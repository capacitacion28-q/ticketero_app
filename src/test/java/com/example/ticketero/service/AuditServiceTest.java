package com.example.ticketero.service;

import com.example.ticketero.model.dto.AuditEventResponse;
import com.example.ticketero.model.entity.AuditEvent;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.ActorType;
import com.example.ticketero.repository.AuditEventRepository;
import com.example.ticketero.testutil.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AuditService
 * Cubre RN-011, RN-013 y trazabilidad de auditoría
 */
@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditEventRepository auditEventRepository;
    
    @InjectMocks
    private AuditService auditService;

    @Test
    void logTicketCreated_shouldCreateAuditEvent() {
        // Given - RN-011: Auditoría obligatoria de eventos críticos
        Ticket ticket = TestDataBuilder.ticketWaiting().build();
        String clientIp = "192.168.1.100";
        
        // When
        auditService.logTicketCreated(ticket, clientIp);
        
        // Then
        ArgumentCaptor<AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository).save(eventCaptor.capture());
        
        AuditEvent savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getEventType()).isEqualTo("TICKET_CREATED");
        assertThat(savedEvent.getActor()).isEqualTo(ticket.getNationalId());
        assertThat(savedEvent.getActorType()).isEqualTo(ActorType.CLIENT);
        assertThat(savedEvent.getTicketNumber()).isEqualTo(ticket.getNumero());
        assertThat(savedEvent.getPreviousState()).isNull();
        assertThat(savedEvent.getNewState()).isEqualTo(ticket.getStatus().name());
        assertThat(savedEvent.getIpAddress()).isEqualTo(clientIp);
        assertThat(savedEvent.getTimestamp()).isNotNull();
        assertThat(savedEvent.getAdditionalData()).contains("Queue: CAJA");
        assertThat(savedEvent.getAdditionalData()).contains("Branch: Sucursal Centro");
    }

    @Test
    void logStatusChange_shouldRecordStateTransition() {
        // Given - RN-011: Registro de cambios de estado
        Ticket ticket = TestDataBuilder.ticketInService().build();
        String previousStatus = "WAITING";
        String actor = "María López";
        ActorType actorType = ActorType.ADVISOR;
        String clientIp = "192.168.1.101";
        
        // When
        auditService.logStatusChange(ticket, previousStatus, actor, actorType, clientIp);
        
        // Then
        ArgumentCaptor<AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository).save(eventCaptor.capture());
        
        AuditEvent savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getEventType()).isEqualTo("STATUS_CHANGED");
        assertThat(savedEvent.getActor()).isEqualTo(actor);
        assertThat(savedEvent.getActorType()).isEqualTo(actorType);
        assertThat(savedEvent.getTicketNumber()).isEqualTo(ticket.getNumero());
        assertThat(savedEvent.getPreviousState()).isEqualTo(previousStatus);
        assertThat(savedEvent.getNewState()).isEqualTo(ticket.getStatus().name());
        assertThat(savedEvent.getIpAddress()).isEqualTo(clientIp);
        assertThat(savedEvent.getAdditionalData()).contains("Advisor: María López");
        assertThat(savedEvent.getAdditionalData()).contains("Module: 1");
    }

    @Test
    void registrarEvento_shouldCreateGenericEvent() {
        // Given - Método genérico para auditoría
        String eventType = "TICKET_ASSIGNED";
        String actor = "SYSTEM";
        Long ticketId = 123L;
        String previousState = "WAITING";
        String newState = "IN_SERVICE";
        String additionalData = "{\"advisorId\":1,\"advisorName\":\"María López\",\"moduleNumber\":1}";
        
        // When
        auditService.registrarEvento(eventType, actor, ticketId, previousState, newState, additionalData);
        
        // Then
        ArgumentCaptor<AuditEvent> eventCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository).save(eventCaptor.capture());
        
        AuditEvent savedEvent = eventCaptor.getValue();
        assertThat(savedEvent.getEventType()).isEqualTo(eventType);
        assertThat(savedEvent.getActor()).isEqualTo(actor);
        assertThat(savedEvent.getActorType()).isEqualTo(ActorType.SYSTEM);
        assertThat(savedEvent.getTicketNumber()).isEqualTo("TICKET_123");
        assertThat(savedEvent.getPreviousState()).isEqualTo(previousState);
        assertThat(savedEvent.getNewState()).isEqualTo(newState);
        assertThat(savedEvent.getAdditionalData()).isEqualTo(additionalData);
    }

    @Test
    void getTicketAuditTrail_shouldReturnOrderedEvents() {
        // Given - RF-008: Consulta de trazabilidad
        String ticketNumber = "C001";
        AuditEvent event1 = AuditEvent.builder()
            .eventType("TICKET_CREATED")
            .ticketNumber(ticketNumber)
            .timestamp(LocalDateTime.now().minusMinutes(10))
            .build();
        AuditEvent event2 = AuditEvent.builder()
            .eventType("STATUS_CHANGED")
            .ticketNumber(ticketNumber)
            .timestamp(LocalDateTime.now().minusMinutes(5))
            .build();
        
        when(auditEventRepository.findByTicketNumberOrderByTimestampDesc(ticketNumber))
            .thenReturn(Arrays.asList(event2, event1)); // Más reciente primero
        
        // When
        List<AuditEventResponse> result = auditService.getTicketAuditTrail(ticketNumber);
        
        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).eventType()).isEqualTo("STATUS_CHANGED");
        assertThat(result.get(1).eventType()).isEqualTo("TICKET_CREATED");
    }

    @Test
    void determinarActorType_shouldIdentifyCorrectType() {
        // Given - Lógica de determinación de tipo de actor
        AuditService spyService = spy(auditService);
        
        // When & Then - SYSTEM
        spyService.registrarEvento("TEST", "SYSTEM", null, null, null, null);
        ArgumentCaptor<AuditEvent> systemCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository).save(systemCaptor.capture());
        assertThat(systemCaptor.getValue().getActorType()).isEqualTo(ActorType.SYSTEM);
        
        // When & Then - SUPERVISOR (email)
        spyService.registrarEvento("TEST", "supervisor@banco.cl", null, null, null, null);
        ArgumentCaptor<AuditEvent> supervisorCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository, times(2)).save(supervisorCaptor.capture());
        List<AuditEvent> allEvents = supervisorCaptor.getAllValues();
        assertThat(allEvents.get(1).getActorType()).isEqualTo(ActorType.SUPERVISOR);
        
        // When & Then - CLIENT (RUT format)
        spyService.registrarEvento("TEST", "12345678-9", null, null, null, null);
        ArgumentCaptor<AuditEvent> clientCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository, times(3)).save(clientCaptor.capture());
        List<AuditEvent> allClientEvents = clientCaptor.getAllValues();
        assertThat(allClientEvents.get(2).getActorType()).isEqualTo(ActorType.CLIENT);
        
        // When & Then - ADVISOR (default)
        spyService.registrarEvento("TEST", "María López", null, null, null, null);
        ArgumentCaptor<AuditEvent> advisorCaptor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(auditEventRepository, times(4)).save(advisorCaptor.capture());
        List<AuditEvent> allAdvisorEvents = advisorCaptor.getAllValues();
        assertThat(allAdvisorEvents.get(3).getActorType()).isEqualTo(ActorType.ADVISOR);
    }
}