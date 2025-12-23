package com.example.ticketero.model.dto;

import com.example.ticketero.model.entity.AuditEvent;
import com.example.ticketero.model.enums.ActorType;

import java.time.LocalDateTime;

/**
 * DTO para eventos de auditoría - RF-008
 * Trazabilidad completa de acciones en el sistema
 */
public record AuditEventResponse(
    Long id,
    LocalDateTime timestamp,
    String eventType,
    String actor,
    ActorType actorType,
    String ticketNumber,
    String previousState,
    String newState,
    String additionalData,
    String ipAddress
) {
    public static AuditEventResponse from(AuditEvent auditEvent) {
        return new AuditEventResponse(
            auditEvent.getId(),
            auditEvent.getTimestamp(),
            auditEvent.getEventType(),
            auditEvent.getActor(),
            auditEvent.getActorType(),
            auditEvent.getTicketNumber(),
            auditEvent.getPreviousState(),
            auditEvent.getNewState(),
            auditEvent.getAdditionalData(),
            auditEvent.getIpAddress()
        );
    }
}