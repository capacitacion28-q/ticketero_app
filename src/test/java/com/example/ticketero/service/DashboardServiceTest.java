package com.example.ticketero.service;

import com.example.ticketero.model.dto.DashboardResponse;
import com.example.ticketero.model.entity.Advisor;
import com.example.ticketero.model.enums.AdvisorStatus;
import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.repository.AdvisorRepository;
import com.example.ticketero.repository.TicketRepository;
import com.example.ticketero.testutil.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para DashboardService
 * Cubre RF-007 y métricas en tiempo real
 */
@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TicketRepository ticketRepository;
    
    @Mock
    private AdvisorRepository advisorRepository;
    
    @Mock
    private QueueService queueService;
    
    @InjectMocks
    private DashboardService dashboardService;

    @Test
    void getDashboardData_shouldReturnCompleteData() {
        // Given - RF-007: Dashboard ejecutivo con métricas
        setupBasicMocks();
        
        // When
        DashboardResponse result = dashboardService.getDashboardData();
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.timestamp()).isNotNull();
        assertThat(result.updateInterval()).isEqualTo(30);
        assertThat(result.estadoGeneral()).isIn("NORMAL", "ATENCION", "ALERTA", "CRITICO");
        
        // Verificar resumen ejecutivo
        assertThat(result.resumenEjecutivo()).isNotNull();
        assertThat(result.resumenEjecutivo().ticketsActivos()).isGreaterThanOrEqualTo(0);
        assertThat(result.resumenEjecutivo().ticketsCompletadosHoy()).isGreaterThanOrEqualTo(0);
        assertThat(result.resumenEjecutivo().tiempoPromedioGlobal()).isGreaterThan(0);
        
        // Verificar estado de colas
        assertThat(result.estadoColas()).isNotEmpty();
        assertThat(result.estadoColas()).containsKeys("CAJA", "PERSONAL_BANKER", "EMPRESAS", "GERENCIA");
        
        // Verificar estado de ejecutivos
        assertThat(result.estadoEjecutivos()).isNotNull();
        assertThat(result.estadoEjecutivos().disponibles()).isGreaterThanOrEqualTo(0);
        assertThat(result.estadoEjecutivos().ocupados()).isGreaterThanOrEqualTo(0);
        
        // Verificar métricas
        assertThat(result.metricas()).isNotEmpty();
        assertThat(result.metricas()).containsKey("total_asesores");
    }

    @Test
    void generateAlertas_withSaturatedQueue_shouldCreateAlert() {
        // Given - Sistema de alertas por saturación
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, QueueType.CAJA))
            .thenReturn(15L); // Más de 10 = saturado
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, QueueType.PERSONAL_BANKER))
            .thenReturn(2L);
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, QueueType.EMPRESAS))
            .thenReturn(1L);
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, QueueType.GERENCIA))
            .thenReturn(0L);
        
        // Mock asesores disponibles
        when(advisorRepository.findByStatusOrderByAssignedTicketsCountAsc(AdvisorStatus.AVAILABLE))
            .thenReturn(List.of(TestDataBuilder.advisorAvailable().build()));
        
        setupOtherMocks();
        
        // When
        DashboardResponse result = dashboardService.getDashboardData();
        
        // Then
        assertThat(result.alertas()).isNotEmpty();
        
        Optional<DashboardResponse.Alerta> alertaSaturacion = result.alertas().stream()
            .filter(alerta -> alerta.tipo().equals("SATURACION_COLA"))
            .findFirst();
        
        assertThat(alertaSaturacion).isPresent();
        assertThat(alertaSaturacion.get().prioridad()).isEqualTo("HIGH");
        assertThat(alertaSaturacion.get().descripcion()).contains("Cola Caja tiene 15 tickets");
        assertThat(alertaSaturacion.get().accionSugerida()).contains("Asignar más asesores");
    }

    @Test
    void determineEstadoGeneral_withCriticalAlerts_shouldReturnCritico() {
        // Given - Estado crítico por falta de asesores
        when(ticketRepository.countByStatusAndQueueType(any(TicketStatus.class), any(QueueType.class)))
            .thenReturn(5L);
        
        // Sin asesores disponibles = alerta crítica
        when(advisorRepository.findByStatusOrderByAssignedTicketsCountAsc(AdvisorStatus.AVAILABLE))
            .thenReturn(List.of()); // Lista vacía
        
        setupOtherMocks();
        
        // When
        DashboardResponse result = dashboardService.getDashboardData();
        
        // Then
        assertThat(result.estadoGeneral()).isEqualTo("CRITICO");
        
        Optional<DashboardResponse.Alerta> alertaCritica = result.alertas().stream()
            .filter(alerta -> alerta.prioridad().equals("CRITICAL"))
            .findFirst();
        
        assertThat(alertaCritica).isPresent();
        assertThat(alertaCritica.get().tipo()).isEqualTo("FALTA_ASESORES");
        assertThat(alertaCritica.get().descripcion()).contains("No hay asesores disponibles");
    }

    @Test
    void generateEstadoColas_shouldCalculateCorrectStates() {
        // Given - Estados de cola según cantidad de tickets
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, QueueType.CAJA))
            .thenReturn(0L); // VACIA
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, QueueType.PERSONAL_BANKER))
            .thenReturn(2L); // NORMAL (≤3)
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, QueueType.EMPRESAS))
            .thenReturn(5L); // MODERADA (4-7)
        when(ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, QueueType.GERENCIA))
            .thenReturn(12L); // SATURADA (>7)
        
        setupOtherMocks();
        
        // When
        DashboardResponse result = dashboardService.getDashboardData();
        
        // Then
        assertThat(result.estadoColas().get("CAJA").estado()).isEqualTo("VACIA");
        assertThat(result.estadoColas().get("CAJA").ticketsEnEspera()).isEqualTo(0);
        assertThat(result.estadoColas().get("CAJA").tiempoEstimadoMaximo()).isEqualTo(0);
        
        assertThat(result.estadoColas().get("PERSONAL_BANKER").estado()).isEqualTo("NORMAL");
        assertThat(result.estadoColas().get("PERSONAL_BANKER").ticketsEnEspera()).isEqualTo(2);
        
        assertThat(result.estadoColas().get("EMPRESAS").estado()).isEqualTo("MODERADA");
        assertThat(result.estadoColas().get("EMPRESAS").ticketsEnEspera()).isEqualTo(5);
        
        assertThat(result.estadoColas().get("GERENCIA").estado()).isEqualTo("SATURADA");
        assertThat(result.estadoColas().get("GERENCIA").ticketsEnEspera()).isEqualTo(12);
        assertThat(result.estadoColas().get("GERENCIA").tiempoEstimadoMaximo())
            .isEqualTo(12 * QueueType.GERENCIA.getAvgTimeMinutes());
    }
    
    private void setupBasicMocks() {
        // Mock conteos básicos
        when(ticketRepository.countByStatusAndQueueType(any(TicketStatus.class), any(QueueType.class)))
            .thenReturn(5L);
        
        setupOtherMocks();
    }
    
    private void setupOtherMocks() {
        // Mock advisor counts
        Object[] availableCount = {AdvisorStatus.AVAILABLE, 3L};
        Object[] busyCount = {AdvisorStatus.BUSY, 2L};
        Object[] offlineCount = {AdvisorStatus.OFFLINE, 1L};
        
        when(advisorRepository.countAdvisorsByStatus())
            .thenReturn(Arrays.asList(availableCount, busyCount, offlineCount));
        
        // Mock most productive advisor
        Advisor productiveAdvisor = TestDataBuilder.advisorBusy()
            .name("Top Performer")
            .assignedTicketsCount(10)
            .build();
        
        when(advisorRepository.findFirstByOrderByAssignedTicketsCountDesc())
            .thenReturn(Optional.of(productiveAdvisor));
        
        // Mock total advisor count
        when(advisorRepository.count()).thenReturn(6L);
        
        // Mock available advisors for alerts
        if (advisorRepository.findByStatusOrderByAssignedTicketsCountAsc(AdvisorStatus.AVAILABLE) == null) {
            when(advisorRepository.findByStatusOrderByAssignedTicketsCountAsc(AdvisorStatus.AVAILABLE))
                .thenReturn(List.of(TestDataBuilder.advisorAvailable().build()));
        }
    }
}