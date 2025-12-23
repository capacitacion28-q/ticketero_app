package com.example.ticketero.model.enums;

/**
 * Estados de ticket del Sistema Ticketero
 * 
 * Referencia: Plan Detallado Sección 8.1.2 - FASE 1
 * Flujo: WAITING → CONFIRMED → IN_PROGRESS → COMPLETED
 * RN-009: Timeout NO_SHOW después de 5 minutos
 * 
 * @author Sistema Ticketero
 * @version 1.0
 */
public enum TicketStatus {
    
    /**
     * Ticket creado, esperando en cola
     * Estado inicial del ticket
     */
    WAITING("En espera", "Ticket creado y en cola"),
    
    /**
     * Cliente confirmó asistencia
     * Transición desde WAITING
     */
    CONFIRMED("Confirmado", "Cliente confirmó su asistencia"),
    
    /**
     * Ticket asignado a asesor, en atención
     * Transición desde CONFIRMED
     */
    IN_PROGRESS("En progreso", "Siendo atendido por asesor"),
    
    /**
     * Atención completada exitosamente
     * Estado final exitoso
     */
    COMPLETED("Completado", "Atención finalizada exitosamente"),
    
    /**
     * Cliente no se presentó - RN-009
     * Timeout de 5 minutos sin confirmación
     */
    NO_SHOW("No se presentó", "Cliente no confirmó asistencia en tiempo"),
    
    /**
     * Ticket cancelado por cliente o sistema
     * Estado final de cancelación
     */
    CANCELLED("Cancelado", "Ticket cancelado");
    
    private final String displayName;
    private final String description;
    
    /**
     * Constructor de TicketStatus
     * 
     * @param displayName Nombre para mostrar al usuario
     * @param description Descripción del estado
     */
    TicketStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Obtiene el nombre para mostrar al usuario
     * 
     * @return Nombre legible del estado
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Obtiene la descripción del estado
     * 
     * @return Descripción detallada del estado
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Verifica si el ticket está activo (no finalizado)
     * 
     * @return true si el ticket está activo
     */
    public boolean isActive() {
        return this == WAITING || this == CONFIRMED || this == IN_PROGRESS;
    }
    
    /**
     * Verifica si el ticket está finalizado
     * 
     * @return true si el ticket está en estado final
     */
    public boolean isFinal() {
        return this == COMPLETED || this == NO_SHOW || this == CANCELLED;
    }
    
    /**
     * Verifica si el estado permite asignación a asesor
     * 
     * @return true si se puede asignar asesor
     */
    public boolean canAssignAdvisor() {
        return this == CONFIRMED;
    }
}