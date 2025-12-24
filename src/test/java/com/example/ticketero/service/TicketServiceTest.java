package com.example.ticketero.service;

import com.example.ticketero.exception.TicketActivoExistenteException;
import com.example.ticketero.exception.TicketNotFoundException;
import com.example.ticketero.model.dto.TicketCreateRequest;
import com.example.ticketero.model.dto.TicketResponse;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.MessageTemplate;
import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.repository.TicketRepository;
import com.example.ticketero.testutil.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para TicketService
 * Cubre RN-001, RN-005, RN-006 y operaciones CRUD críticas
 */
@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    
    @Mock
    private QueueManagementService queueManagementService;
    
    @Mock
    private TelegramService telegramService;
    
    @InjectMocks
    private TicketService ticketService;

    @Test
    void createTicket_withValidData_shouldCreateTicketAndCalculatePosition() {
        // Given
        TicketCreateRequest request = TestDataBuilder.validTicketRequest();
        QueueManagementService.PositionInfo positionInfo = TestDataBuilder.positionInfo(1, 5);
        Ticket savedTicket = TestDataBuilder.ticketWaiting().build();
        
        when(ticketRepository.findByNationalIdAndStatusIn(eq("12345678-9"), any()))
            .thenReturn(Optional.empty());
        when(queueManagementService.calcularPosicion(QueueType.CAJA))
            .thenReturn(positionInfo);
        when(ticketRepository.save(any(Ticket.class)))
            .thenReturn(savedTicket);
        
        // When
        TicketResponse response = ticketService.createTicket(request);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.numero()).isEqualTo("C001");
        assertThat(response.status()).isEqualTo(TicketStatus.WAITING);
        
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(ticketCaptor.capture());
        
        Ticket capturedTicket = ticketCaptor.getValue();
        assertThat(capturedTicket.getNationalId()).isEqualTo("12345678-9");
        assertThat(capturedTicket.getPositionInQueue()).isEqualTo(1);
        assertThat(capturedTicket.getEstimatedWaitMinutes()).isEqualTo(5);
        
        verify(telegramService).programarMensaje(savedTicket, MessageTemplate.TOTEM_TICKET_CREADO);
    }

    @Test
    void createTicket_withExistingActiveTicket_shouldThrowException() {
        // Given
        TicketCreateRequest request = TestDataBuilder.validTicketRequest();
        Ticket existingTicket = TestDataBuilder.ticketWaiting().build();
        
        when(ticketRepository.findByNationalIdAndStatusIn(eq("12345678-9"), any()))
            .thenReturn(Optional.of(existingTicket));
        
        // When + Then
        assertThatThrownBy(() -> ticketService.createTicket(request))
            .isInstanceOf(TicketActivoExistenteException.class)
            .hasMessageContaining("Ya existe un ticket activo para el RUT: 12345678-9");
        
        verify(ticketRepository, never()).save(any());
        verify(telegramService, never()).programarMensaje(any(), any());
    }

    @Test
    void findByCodigoReferencia_withExistingUuid_shouldReturnTicket() {
        // Given
        UUID uuid = TestDataBuilder.VALID_UUID;
        Ticket ticket = TestDataBuilder.ticketWaiting()
            .codigoReferencia(uuid)
            .build();
        
        when(ticketRepository.findByCodigoReferencia(uuid))
            .thenReturn(Optional.of(ticket));
        
        // When
        Optional<TicketResponse> result = ticketService.findByCodigoReferencia(uuid);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().codigoReferencia()).isEqualTo(uuid);
        assertThat(result.get().numero()).isEqualTo("C001");
    }

    @Test
    void findByCodigoReferencia_withNonExistentUuid_shouldReturnEmpty() {
        // Given
        UUID uuid = TestDataBuilder.NON_EXISTENT_UUID;
        
        when(ticketRepository.findByCodigoReferencia(uuid))
            .thenReturn(Optional.empty());
        
        // When
        Optional<TicketResponse> result = ticketService.findByCodigoReferencia(uuid);
        
        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void updateStatus_withValidTransition_shouldUpdateAndNotify() {
        // Given
        UUID uuid = TestDataBuilder.VALID_UUID;
        Ticket ticket = TestDataBuilder.ticketWaiting()
            .codigoReferencia(uuid)
            .build();
        Ticket updatedTicket = TestDataBuilder.ticketWaiting()
            .codigoReferencia(uuid)
            .status(TicketStatus.CALLED)
            .build();
        
        when(ticketRepository.findByCodigoReferencia(uuid))
            .thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class)))
            .thenReturn(updatedTicket);
        
        // When
        TicketResponse response = ticketService.updateStatus(uuid, TicketStatus.CALLED);
        
        // Then
        assertThat(response.status()).isEqualTo(TicketStatus.CALLED);
        
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(ticketCaptor.capture());
        
        Ticket capturedTicket = ticketCaptor.getValue();
        assertThat(capturedTicket.getStatus()).isEqualTo(TicketStatus.CALLED);
        assertThat(capturedTicket.getFechaActualizacion()).isNotNull();
        
        verify(telegramService).programarMensaje(updatedTicket, MessageTemplate.TOTEM_ES_TU_TURNO);
    }

    @Test
    void updateStatus_withNonExistentTicket_shouldThrowException() {
        // Given
        UUID uuid = TestDataBuilder.NON_EXISTENT_UUID;
        
        when(ticketRepository.findByCodigoReferencia(uuid))
            .thenReturn(Optional.empty());
        
        // When + Then
        assertThatThrownBy(() -> ticketService.updateStatus(uuid, TicketStatus.CALLED))
            .isInstanceOf(TicketNotFoundException.class)
            .hasMessageContaining("Ticket not found: " + uuid);
        
        verify(ticketRepository, never()).save(any());
        verify(telegramService, never()).programarMensaje(any(), any());
    }
}