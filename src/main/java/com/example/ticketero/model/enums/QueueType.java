package com.example.ticketero.model.enums;

/**
 * Tipos de cola del Sistema Ticketero
 * 
 * Referencia: Plan Detallado Sección 8.1.2 - FASE 1
 * RN-006: Prefijos por tipo de cola (C, P, E, G)
 * 
 * @author Sistema Ticketero
 * @version 1.0
 */
public enum QueueType {
    
    /**
     * Cola de consultas generales - Prefijo C
     * Prioridad: 4 (más baja)
     */
    CONSULTA("C", "Consultas Generales", 4),
    
    /**
     * Cola de préstamos - Prefijo P  
     * Prioridad: 2 (alta)
     */
    PRESTAMO("P", "Préstamos", 2),
    
    /**
     * Cola empresarial - Prefijo E
     * Prioridad: 1 (más alta)
     */
    EMPRESA("E", "Empresarial", 1),
    
    /**
     * Cola general - Prefijo G
     * Prioridad: 3 (media)
     */
    GENERAL("G", "General", 3);
    
    private final String prefix;
    private final String description;
    private final int priority; // 1 = más alta, 4 = más baja
    
    /**
     * Constructor de QueueType
     * 
     * @param prefix Prefijo para numeración según RN-006
     * @param description Descripción legible del tipo de cola
     * @param priority Prioridad numérica (1=alta, 4=baja) para RN-002
     */
    QueueType(String prefix, String description, int priority) {
        this.prefix = prefix;
        this.description = description;
        this.priority = priority;
    }
    
    /**
     * Obtiene el prefijo para numeración de tickets
     * 
     * @return Prefijo según RN-006 (C, P, E, G)
     */
    public String getPrefix() {
        return prefix;
    }
    
    /**
     * Obtiene la descripción legible del tipo de cola
     * 
     * @return Descripción del tipo de cola
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Obtiene la prioridad numérica para ordenamiento
     * 
     * @return Prioridad (1=más alta, 4=más baja) según RN-002
     */
    public int getPriority() {
        return priority;
    }
    
    /**
     * Convierte string a QueueType de forma segura
     * 
     * @param value String a convertir
     * @return QueueType correspondiente o GENERAL por defecto
     */
    public static QueueType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return GENERAL;
        }
        
        try {
            return QueueType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return GENERAL;
        }
    }
}