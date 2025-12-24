package com.example.ticketero.service;

import com.example.ticketero.model.dto.QueueStatusResponse;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.repository.TicketRepository;
import com.example.ticketero.testutil.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para QueueService
 * Cubre RN-002, RN-003, RF-003
 */
@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    
    @InjectMocks
    private QueueService queueService;

    @Test
    void calculateQueuePosition_shouldReturnCorrectPosition() {
        // Given - RN-002: Cálculo de posición basado en tickets en espera
        QueueType queueType = QueueType.CAJA;
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, queueType))
            .thenReturn(5L);
        
        // When
        int position = queueService.calculateQueuePosition(queueType);
        
        // Then
        assertThat(position).isEqualTo(6); // 5 tickets esperando + 1 = posición 6
        verify(ticketRepository).countByStatusAndQueueType(TicketStatus.WAITING, queueType);
    }

    @Test
    void calculateEstimatedWaitTime_shouldCalculateBasedOnPosition() {
        // Given - RN-003: Tiempo estimado basado en posición y tipo de cola
        QueueType queueType = QueueType.PERSONAL_BANKER; // 15 min promedio
        int position = 4;
        
        // When
        int estimatedTime = queueService.calculateEstimatedWaitTime(queueType, position);
        
        // Then
        assertThat(estimatedTime).isEqualTo(45); // (4-1) * 15 = 45 minutos
    }

    @Test
    void getQueueStatus_shouldReturnCompleteStatus() {
        // Given - RF-003: Estado completo de cola
        QueueType queueType = QueueType.CAJA;
        
        // Mock conteos por estado
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, queueType))
            .thenReturn(3L);
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.CALLED, queueType))
            .thenReturn(1L);
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.IN_SERVICE, queueType))
            .thenReturn(2L);
        
        // Mock tickets en espera
        Ticket ticket1 = TestDataBuilder.ticketWaiting()
            .numero("C001")
            .queueType(queueType)
            .positionInQueue(1)
            .estimatedWaitMinutes(5)
            .build();
        Ticket ticket2 = TestDataBuilder.ticketWaiting()
            .numero("C002")
            .queueType(queueType)
            .positionInQueue(2)
            .estimatedWaitMinutes(10)
            .build();
        
        when(ticketRepository.findByStatusOrderByFechaCreacionAsc(TicketStatus.WAITING))
            .thenReturn(Arrays.asList(ticket1, ticket2));
        
        // When
        QueueStatusResponse result = queueService.getQueueStatus(queueType);
        
        // Then
        assertThat(result.tipoCola()).isEqualTo(queueType);
        assertThat(result.descripcion()).isEqualTo(queueType.getDisplayName());
        assertThat(result.tiempoPromedio()).isEqualTo(queueType.getAvgTimeMinutes());
        assertThat(result.prioridad()).isEqualTo(queueType.getPriority());
        assertThat(result.prefijo()).isEqualTo(queueType.getPrefix());
        
        // Verificar estado actual
        QueueStatusResponse.EstadoActual estadoActual = result.estadoActual();
        assertThat(estadoActual.ticketsEnEspera()).isEqualTo(3);
        assertThat(estadoActual.ticketsNotificados()).isEqualTo(1);
        assertThat(estadoActual.ticketsEnAtencion()).isEqualTo(2);
        assertThat(estadoActual.tiempoEstimadoCola()).isEqualTo(3 * queueType.getAvgTimeMinutes());
        assertThat(estadoActual.proximoNumero()).isEqualTo("C001");
        
        // Verificar tickets en cola
        assertThat(result.tickets()).hasSize(2);
        assertThat(result.tickets().get(0).numero()).isEqualTo("C001");
        assertThat(result.tickets().get(1).numero()).isEqualTo("C002");
    }

    @Test
    void getNextTicketNumber_withWaitingTickets_shouldReturnFirst() {
        // Given - Próximo ticket en cola
        QueueType queueType = QueueType.EMPRESAS;
        
        Ticket firstTicket = TestDataBuilder.ticketWaiting()
            .numero("E001")
            .queueType(queueType)
            .build();
        Ticket secondTicket = TestDataBuilder.ticketWaiting()
            .numero("E002")
            .queueType(queueType)
            .build();
        Ticket differentQueue = TestDataBuilder.ticketWaiting()
            .numero("C001")
            .queueType(QueueType.CAJA)
            .build();
        
        when(ticketRepository.findByStatusOrderByFechaCreacionAsc(TicketStatus.WAITING))
            .thenReturn(Arrays.asList(firstTicket, differentQueue, secondTicket));
        
        // Mock other counts for getQueueStatus
        when(ticketRepository.countByStatusAndQueueType(eq(TicketStatus.WAITING), eq(queueType)))
            .thenReturn(2L);
        when(ticketRepository.countByStatusAndQueueType(eq(TicketStatus.CALLED), eq(queueType)))
            .thenReturn(0L);
        when(ticketRepository.countByStatusAndQueueType(eq(TicketStatus.IN_SERVICE), eq(queueType)))
            .thenReturn(0L);
        
        // When
        QueueStatusResponse result = queueService.getQueueStatus(queueType);
        
        // Then
        assertThat(result.estadoActual().proximoNumero()).isEqualTo("E001");
    }

    @Test
    void getTicketsInQueue_shouldReturnLimitedList() {
        // Given - Lista limitada de tickets (máximo 10)
        QueueType queueType = QueueType.GERENCIA;
        
        // Crear 12 tickets (más del límite de 10)
        List<Ticket> manyTickets = Arrays.asList(
            TestDataBuilder.ticketWaiting().numero("G001").queueType(queueType).build(),
            TestDataBuilder.ticketWaiting().numero("G002").queueType(queueType).build(),
            TestDataBuilder.ticketWaiting().numero("G003").queueType(queueType).build(),
            TestDataBuilder.ticketWaiting().numero("G004").queueType(queueType).build(),
            TestDataBuilder.ticketWaiting().numero("G005").queueType(queueType).build(),
            TestDataBuilder.ticketWaiting().numero("G006").queueType(queueType).build(),
            TestDataBuilder.ticketWaiting().numero("G007").queueType(queueType).build(),
            TestDataBuilder.ticketWaiting().numero("G008").queueType(queueType).build(),
            TestDataBuilder.ticketWaiting().numero("G009").queueType(queueType).build(),
            TestDataBuilder.ticketWaiting().numero("G010").queueType(queueType).build(),
            TestDataBuilder.ticketWaiting().numero("G011").queueType(queueType).build(),
            TestDataBuilder.ticketWaiting().numero("G012").queueType(queueType).build()
        );
        
        when(ticketRepository.findByStatusOrderByFechaCreacionAsc(TicketStatus.WAITING))
            .thenReturn(manyTickets);
        
        // Mock counts
        when(ticketRepository.countByStatusAndQueueType(eq(TicketStatus.WAITING), eq(queueType)))
            .thenReturn(12L);
        when(ticketRepository.countByStatusAndQueueType(eq(TicketStatus.CALLED), eq(queueType)))
            .thenReturn(0L);
        when(ticketRepository.countByStatusAndQueueType(eq(TicketStatus.IN_SERVICE), eq(queueType)))
            .thenReturn(0L);
        
        // When
        QueueStatusResponse result = queueService.getQueueStatus(queueType);
        
        // Then - Solo debe retornar 10 tickets máximo
        assertThat(result.tickets()).hasSize(10);
        assertThat(result.tickets().get(0).numero()).isEqualTo("G001");
        assertThat(result.tickets().get(9).numero()).isEqualTo("G010");
        
        // G011 y G012 no deben aparecer (límite de 10)
        assertThat(result.tickets().stream()
            .anyMatch(t -> t.numero().equals("G011") || t.numero().equals("G012")))
            .isFalse();
    }
}