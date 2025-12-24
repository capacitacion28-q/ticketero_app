package com.example.ticketero.model.dto;

import com.example.ticketero.model.entity.AuditEvent;
import com.example.ticketero.model.enums.ActorType;

import java.time.LocalDateTime;

/**
 * DTO para eventos de auditoría con método factory para trazabilidad completa.
 * 
 * Implementa: RF-008 (Auditoría y trazabilidad)
 * Reglas de Negocio: RN-011 (Auditoría obligatoria), RN-013 (Retención 7 años)
 * 
 * Características:
 * - Mapeo completo de entidad AuditEvent
 * - Incluye hash de integridad (omitido en DTO por seguridad)
 * - Soporte para datos adicionales en formato JSON
 * - Trazabilidad de cambios de estado con before/after
 * 
 * Utilizado por:
 * - AuditController para consultas de trazabilidad
 * - Reportes de cumplimiento normativo
 * - Investigación de incidentes
 * 
 * @param id Identificador único del evento
 * @param timestamp Momento exacto del evento
 * @param eventType Tipo de evento (TICKET_CREATED, STATUS_CHANGED, etc.)
 * @param actor Identificador del actor (RUT, email, SYSTEM)
 * @param actorType Tipo de actor (CLIENT, ADVISOR, SYSTEM, SUPERVISOR)
 * @param ticketNumber Número del ticket afectado
 * @param previousState Estado anterior (para cambios de estado)
 * @param newState Nuevo estado (para cambios de estado)
 * @param additionalData Datos adicionales en formato JSON
 * @param ipAddress Dirección IP desde donde se ejecutó la acción
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
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
    /**
     * Método factory estático para conversión de entidad AuditEvent a DTO.
     * Omite hash de integridad por razones de seguridad en respuestas API.
     * 
     * @param auditEvent Entidad AuditEvent a convertir
     * @return AuditEventResponse con datos de auditoría
     */
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