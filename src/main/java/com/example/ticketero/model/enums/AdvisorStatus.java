package com.example.ticketero.model.enums;

/**
 * Estados de asesor del Sistema Ticketero
 * 
 * Referencia: Plan Detallado Sección 8.1.2 - FASE 1
 * RN-004: Balanceo de carga entre ejecutivos disponibles
 * 
 * @author Sistema Ticketero
 * @version 1.0
 */
public enum AdvisorStatus {
    
    /**
     * Asesor disponible para recibir tickets
     * Puede recibir asignaciones según RN-004
     */
    AVAILABLE("Disponible", "Asesor disponible para atender tickets"),
    
    /**
     * Asesor ocupado con tickets asignados
     * Puede recibir más tickets si no alcanzó el máximo
     */
    BUSY("Ocupado", "Asesor atendiendo tickets"),
    
    /**
     * Asesor fuera de línea
     * No puede recibir nuevas asignaciones
     */
    OFFLINE("Fuera de línea", "Asesor no disponible para asignaciones");
    
    private final String displayName;
    private final String description;
    
    /**
     * Constructor de AdvisorStatus
     * 
     * @param displayName Nombre para mostrar al usuario
     * @param description Descripción del estado
     */
    AdvisorStatus(String displayName, String description) {
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
     * Verifica si el asesor puede recibir asignaciones
     * 
     * @return true si puede recibir tickets según RN-004
     */
    public boolean canReceiveTickets() {
        return this == AVAILABLE || this == BUSY;
    }
    
    /**
     * Verifica si el asesor está activo en el sistema
     * 
     * @return true si está activo (no offline)
     */
    public boolean isActive() {
        return this != OFFLINE;
    }
}