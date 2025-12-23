package com.example.ticketero.service;

import com.example.ticketero.model.dto.AuditEventResponse;
import com.example.ticketero.model.entity.AuditEvent;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.ActorType;
import com.example.ticketero.repository.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service para auditoría - RF-008
 * Trazabilidad completa de acciones en el sistema
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuditService {
    
    private final AuditEventRepository auditEventRepository;
    
    @Transactional
    public void logTicketCreated(Ticket ticket, String clientIp) {
        AuditEvent event = AuditEvent.builder()
            .timestamp(LocalDateTime.now())
            .eventType("TICKET_CREATED")
            .actor(ticket.getNationalId())
            .actorType(ActorType.CLIENT)
            .ticketNumber(ticket.getNumero())
            .previousState(null)
            .newState(ticket.getStatus().name())
            .additionalData(String.format("Queue: %s, Branch: %s", 
                ticket.getQueueType().name(), ticket.getBranchOffice()))
            .ipAddress(clientIp)
            .build();
        
        auditEventRepository.save(event);
        log.debug("Audit logged: TICKET_CREATED for {}", ticket.getNumero());
    }
    
    @Transactional
    public void logStatusChange(Ticket ticket, String previousStatus, String actor, ActorType actorType, String clientIp) {
        AuditEvent event = AuditEvent.builder()
            .timestamp(LocalDateTime.now())
            .eventType("STATUS_CHANGED")
            .actor(actor)
            .actorType(actorType)
            .ticketNumber(ticket.getNumero())
            .previousState(previousStatus)
            .newState(ticket.getStatus().name())
            .additionalData(ticket.getAssignedAdvisor() != null ? 
                String.format("Advisor: %s, Module: %d", ticket.getAssignedAdvisor(), ticket.getAssignedModuleNumber()) : 
                null)
            .ipAddress(clientIp)
            .build();
        
        auditEventRepository.save(event);
        log.debug("Audit logged: STATUS_CHANGED for {} ({} -> {})", 
            ticket.getNumero(), previousStatus, ticket.getStatus().name());
    }
    
    @Transactional
    public void logTicketAssignment(Ticket ticket, String advisorName, String clientIp) {
        AuditEvent event = AuditEvent.builder()
            .timestamp(LocalDateTime.now())
            .eventType("TICKET_ASSIGNED")
            .actor(advisorName)
            .actorType(ActorType.ADVISOR)
            .ticketNumber(ticket.getNumero())
            .previousState("WAITING")
            .newState("CALLED")
            .additionalData(String.format("Module: %d", ticket.getAssignedModuleNumber()))
            .ipAddress(clientIp)
            .build();
        
        auditEventRepository.save(event);
        log.debug("Audit logged: TICKET_ASSIGNED for {} to {}", ticket.getNumero(), advisorName);
    }
    
    @Transactional
    public void logSystemAction(String eventType, String actor, ActorType actorType, 
                               String description, String clientIp) {
        AuditEvent event = AuditEvent.builder()
            .timestamp(LocalDateTime.now())
            .eventType(eventType)
            .actor(actor)
            .actorType(actorType)
            .ticketNumber(null)
            .previousState(null)
            .newState(null)
            .additionalData(description)
            .ipAddress(clientIp)
            .build();
        
        auditEventRepository.save(event);
        log.debug("Audit logged: {} by {}", eventType, actor);
    }
    
    public List<AuditEventResponse> getTicketAuditTrail(String ticketNumber) {
        return auditEventRepository.findByTicketNumberOrderByTimestampDesc(ticketNumber)
            .stream()
            .map(AuditEventResponse::from)
            .collect(Collectors.toList());
    }
    
    public List<AuditEventResponse> getAuditEventsByActor(String actor) {
        return auditEventRepository.findByActorOrderByTimestampDesc(actor)
            .stream()
            .map(AuditEventResponse::from)
            .collect(Collectors.toList());
    }
    
    public List<AuditEventResponse> getAuditEventsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditEventRepository.findByTimestampBetweenOrderByTimestampDesc(startDate, endDate)
            .stream()
            .map(AuditEventResponse::from)
            .collect(Collectors.toList());
    }
    
    public List<Object[]> getEventTypeStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        return auditEventRepository.getEventTypeStatistics(startDate, endDate);
    }
    
    public List<Object[]> getActorActivityStats(LocalDateTime startDate, LocalDateTime endDate) {
        return auditEventRepository.getActorActivityStats(startDate, endDate);
    }
    
    @Transactional
    public void logNotificationSent(String ticketNumber, String telefono, String template, String clientIp) {
        AuditEvent event = AuditEvent.builder()
            .timestamp(LocalDateTime.now())
            .eventType("NOTIFICATION_SENT")
            .actor("SYSTEM")
            .actorType(ActorType.SYSTEM)
            .ticketNumber(ticketNumber)
            .previousState(null)
            .newState(null)
            .additionalData(String.format("Template: %s, Phone: %s", template, telefono))
            .ipAddress(clientIp)
            .build();
        
        auditEventRepository.save(event);
        log.debug("Audit logged: NOTIFICATION_SENT for ticket {}", ticketNumber);
    }
    
    // Método faltante para compatibilidad con QueueManagementService
    @Transactional
    public void registrarEvento(String eventType, String actor, Long ticketId, 
                               String previousState, String newState, String additionalData) {
        
        ActorType actorType = determinarActorType(actor);
        
        AuditEvent event = AuditEvent.builder()
            .eventType(eventType)
            .actor(actor)
            .actorType(actorType)
            .previousState(previousState)
            .newState(newState)
            .additionalData(additionalData)
            .build();
            
        if (ticketId != null) {
            event.setTicketNumber("TICKET_" + ticketId);
        }
        
        auditEventRepository.save(event);
        log.debug("Evento de auditoría registrado: {} por {}", eventType, actor);
    }
    
    private ActorType determinarActorType(String actor) {
        if ("SYSTEM".equals(actor)) return ActorType.SYSTEM;
        if (actor.contains("@")) return ActorType.SUPERVISOR;
        if (actor.matches("\\d{7,8}-[0-9Kk]")) return ActorType.CLIENT;
        return ActorType.ADVISOR;
    }
}