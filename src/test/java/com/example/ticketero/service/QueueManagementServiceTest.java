package com.example.ticketero.service;

import com.example.ticketero.model.entity.Advisor;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.AdvisorStatus;
import com.example.ticketero.model.enums.MessageTemplate;
import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.repository.AdvisorRepository;
import com.example.ticketero.repository.TicketRepository;
import com.example.ticketero.testutil.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para QueueManagementService
 * Cubre RN-002, RN-003, RN-004, RN-010, RN-012
 */
@ExtendWith(MockitoExtension.class)
class QueueManagementServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    
    @Mock
    private AdvisorRepository advisorRepository;
    
    @Mock
    private TelegramService telegramService;
    
    @Mock
    private AuditService auditService;
    
    @InjectMocks
    private QueueManagementService queueManagementService;

    @Test
    void calcularPosicion_withQueueType_shouldReturnCorrectPosition() {
        // Given
        when(ticketRepository.count()).thenReturn(5L);
        
        // When
        QueueManagementService.PositionInfo result = queueManagementService.calcularPosicion(QueueType.CAJA);
        
        // Then
        assertThat(result.getPosition()).isEqualTo(6);
        assertThat(result.getEstimatedTime()).isEqualTo(6 * QueueType.CAJA.getAvgTimeMinutes());
    }

    @Test
    void asignarSiguienteTicket_withAvailableAdvisor_shouldAssignTicket() {
        // Given
        Ticket ticket = TestDataBuilder.ticketWaiting().build();
        Advisor advisor = TestDataBuilder.advisorAvailable().build();
        
        when(ticketRepository.findNextTicketByPriority()).thenReturn(List.of(ticket));
        when(advisorRepository.findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus.AVAILABLE))
            .thenReturn(Optional.of(advisor));
        
        // When
        queueManagementService.asignarSiguienteTicket();
        
        // Then
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        ArgumentCaptor<Advisor> advisorCaptor = ArgumentCaptor.forClass(Advisor.class);
        
        verify(ticketRepository).save(ticketCaptor.capture());
        verify(advisorRepository).save(advisorCaptor.capture());
        
        Ticket savedTicket = ticketCaptor.getValue();
        Advisor savedAdvisor = advisorCaptor.getValue();
        
        assertThat(savedTicket.getStatus()).isEqualTo(TicketStatus.IN_SERVICE);
        assertThat(savedTicket.getAssignedAdvisor()).isEqualTo("María López");
        assertThat(savedTicket.getAssignedModuleNumber()).isEqualTo(1);
        
        assertThat(savedAdvisor.getStatus()).isEqualTo(AdvisorStatus.BUSY);
        assertThat(savedAdvisor.getAssignedTicketsCount()).isEqualTo(1);
        
        verify(auditService).registrarEvento(eq("TICKET_ASSIGNED"), eq("SYSTEM"), any(), eq("WAITING"), eq("IN_SERVICE"), any());
        verify(telegramService).programarMensaje(ticket, MessageTemplate.TOTEM_ES_TU_TURNO);
    }

    @Test
    void asignarSiguienteTicket_noTicketsPending_shouldDoNothing() {
        // Given
        when(ticketRepository.findNextTicketByPriority()).thenReturn(Collections.emptyList());
        
        // When
        queueManagementService.asignarSiguienteTicket();
        
        // Then
        verify(advisorRepository, never()).findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(any());
        verify(ticketRepository, never()).save(any());
        verify(advisorRepository, never()).save(any());
    }

    @Test
    void asignarSiguienteTicket_noAdvisorsAvailable_shouldDoNothing() {
        // Given
        Ticket ticket = TestDataBuilder.ticketWaiting().build();
        
        when(ticketRepository.findNextTicketByPriority()).thenReturn(List.of(ticket));
        when(advisorRepository.findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus.AVAILABLE))
            .thenReturn(Optional.empty());
        
        // When
        queueManagementService.asignarSiguienteTicket();
        
        // Then
        verify(ticketRepository, never()).save(any());
        verify(advisorRepository, never()).save(any());
        verify(telegramService, never()).programarMensaje(any(), any());
    }

    @Test
    void asignarSiguienteTicket_shouldFollowPriorityOrder() {
        // Given - RN-002: GERENCIA > EMPRESAS > PERSONAL_BANKER > CAJA
        Ticket gerenciaTicket = TestDataBuilder.ticketWaiting()
            .queueType(QueueType.GERENCIA)
            .numero("G001")
            .build();
        Advisor advisor = TestDataBuilder.advisorAvailable().build();
        
        when(ticketRepository.findNextTicketByPriority()).thenReturn(List.of(gerenciaTicket));
        when(advisorRepository.findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus.AVAILABLE))
            .thenReturn(Optional.of(advisor));
        
        // When
        queueManagementService.asignarSiguienteTicket();
        
        // Then
        ArgumentCaptor<Ticket> ticketCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketRepository).save(ticketCaptor.capture());
        
        Ticket savedTicket = ticketCaptor.getValue();
        assertThat(savedTicket.getQueueType()).isEqualTo(QueueType.GERENCIA);
        assertThat(savedTicket.getNumero()).isEqualTo("G001");
    }

    @Test
    void asignarSiguienteTicket_shouldBalanceLoad() {
        // Given - RN-004: Balanceo de carga por menor cantidad de tickets
        Ticket ticket = TestDataBuilder.ticketWaiting().build();
        Advisor leastLoadedAdvisor = TestDataBuilder.advisorAvailable()
            .assignedTicketsCount(0)
            .build();
        
        when(ticketRepository.findNextTicketByPriority()).thenReturn(List.of(ticket));
        when(advisorRepository.findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus.AVAILABLE))
            .thenReturn(Optional.of(leastLoadedAdvisor));
        
        // When
        queueManagementService.asignarSiguienteTicket();
        
        // Then
        verify(advisorRepository).findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus.AVAILABLE);
        
        ArgumentCaptor<Advisor> advisorCaptor = ArgumentCaptor.forClass(Advisor.class);
        verify(advisorRepository).save(advisorCaptor.capture());
        
        Advisor savedAdvisor = advisorCaptor.getValue();
        assertThat(savedAdvisor.getAssignedTicketsCount()).isEqualTo(1);
    }

    @Test
    void recalcularPosiciones_withPositionLessOrEqualThree_shouldNotify() {
        // Given - RN-012: Pre-aviso cuando posición ≤ 3
        Ticket ticket1 = TestDataBuilder.ticketWaiting().numero("C001").build();
        Ticket ticket2 = TestDataBuilder.ticketWaiting().numero("C002").build();
        Ticket ticket3 = TestDataBuilder.ticketWaiting().numero("C003").build();
        
        when(ticketRepository.findByStatusOrderByFechaCreacionAsc(TicketStatus.WAITING))
            .thenReturn(Arrays.asList(ticket1, ticket2, ticket3));
        
        // When
        queueManagementService.recalcularPosiciones(QueueType.CAJA);
        
        // Then
        ArgumentCaptor<List<Ticket>> ticketsCaptor = ArgumentCaptor.forClass(List.class);
        verify(ticketRepository).saveAll(ticketsCaptor.capture());
        
        List<Ticket> savedTickets = ticketsCaptor.getValue();
        assertThat(savedTickets).hasSize(3);
        
        // Verificar que todos los tickets fueron notificados (posición ≤ 3)
        verify(telegramService, times(3)).programarMensaje(any(Ticket.class), eq(MessageTemplate.TOTEM_PROXIMO_TURNO));
        
        // Verificar posiciones calculadas
        assertThat(savedTickets.get(0).getPositionInQueue()).isEqualTo(1);
        assertThat(savedTickets.get(1).getPositionInQueue()).isEqualTo(2);
        assertThat(savedTickets.get(2).getPositionInQueue()).isEqualTo(3);
    }

    @Test
    void recalcularPosiciones_withEmptyQueue_shouldDoNothing() {
        // Given
        when(ticketRepository.findByStatusOrderByFechaCreacionAsc(TicketStatus.WAITING))
            .thenReturn(Collections.emptyList());
        
        // When
        queueManagementService.recalcularPosiciones(QueueType.CAJA);
        
        // Then
        verify(ticketRepository).saveAll(Collections.emptyList());
        verify(telegramService, never()).programarMensaje(any(), any());
    }
}