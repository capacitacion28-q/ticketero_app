package com.example.ticketero.model.enums;

/**
 * Tipos de eventos de auditoría del Sistema Ticketero
 * 
 * Referencia: Plan Detallado Sección 8.1.2 - FASE 1
 * RN-011: Auditoría obligatoria de eventos críticos
 * 
 * @author Sistema Ticketero
 * @version 1.0
 */
public enum EventType {
    
    // Eventos de Ticket
    /**
     * Ticket creado por cliente
     * Evento crítico para auditoría
     */
    TICKET_CREATED("Ticket Creado", "TICKET", "Nuevo ticket generado en el sistema"),
    
    /**
     * Cliente confirmó asistencia
     * Transición de estado crítica
     */
    TICKET_CONFIRMED("Ticket Confirmado", "TICKET", "Cliente confirmó su asistencia"),
    
    /**
     * Ticket asignado a asesor
     * Evento de asignación crítico
     */
    TICKET_ASSIGNED("Ticket Asignado", "TICKET", "Ticket asignado a asesor disponible"),
    
    /**
     * Atención completada
     * Finalización exitosa del proceso
     */
    TICKET_COMPLETED("Ticket Completado", "TICKET", "Atención finalizada exitosamente"),
    
    /**
     * Ticket cancelado
     * Cancelación por cliente o sistema
     */
    TICKET_CANCELLED("Ticket Cancelado", "TICKET", "Ticket cancelado antes de completar"),
    
    /**
     * Cliente no se presentó - RN-009
     * Timeout de confirmación
     */
    TICKET_NO_SHOW("Ticket No Show", "TICKET", "Cliente no confirmó asistencia en tiempo"),
    
    // Eventos de Asesor
    /**
     * Cambio de estado de asesor
     * Disponibilidad modificada
     */
    ADVISOR_STATUS_CHANGED("Estado Asesor Cambiado", "ADVISOR", "Cambio en disponibilidad del asesor"),
    
    // Eventos de Mensaje
    /**
     * Mensaje enviado exitosamente
     * Confirmación de entrega
     */
    MESSAGE_SENT("Mensaje Enviado", "MENSAJE", "Mensaje Telegram entregado correctamente"),
    
    /**
     * Fallo en envío de mensaje
     * Error en comunicación Telegram
     */
    MESSAGE_FAILED("Mensaje Fallido", "MENSAJE", "Error en envío de mensaje Telegram");
    
    private final String displayName;
    private final String entityType;
    private final String description;
    
    /**
     * Constructor de EventType
     * 
     * @param displayName Nombre para mostrar al usuario
     * @param entityType Tipo de entidad afectada
     * @param description Descripción del evento
     */
    EventType(String displayName, String entityType, String description) {
        this.displayName = displayName;
        this.entityType = entityType;
        this.description = description;
    }
    
    /**
     * Obtiene el nombre para mostrar al usuario
     * 
     * @return Nombre legible del evento
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Obtiene el tipo de entidad afectada
     * 
     * @return Tipo de entidad (TICKET, ADVISOR, MENSAJE)
     */
    public String getEntityType() {
        return entityType;
    }
    
    /**
     * Obtiene la descripción del evento
     * 
     * @return Descripción detallada del evento
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Verifica si es un evento crítico que requiere auditoría
     * 
     * @return true si requiere auditoría según RN-011
     */
    public boolean isCritical() {
        return this == TICKET_CREATED || this == TICKET_ASSIGNED || 
               this == TICKET_COMPLETED || this == ADVISOR_STATUS_CHANGED;
    }
}