package com.example.ticketero.model.enums;

/**
 * Tipos de entidad para auditoría del Sistema Ticketero
 * 
 * Referencia: Plan Detallado Sección 8.1.2 - FASE 1
 * RN-011: Auditoría obligatoria de eventos críticos
 * 
 * @author Sistema Ticketero
 * @version 1.0
 */
public enum EntityType {
    
    /**
     * Entidad Ticket
     * Representa tickets del sistema de colas
     */
    TICKET("Ticket", "com.example.ticketero.model.entity.Ticket"),
    
    /**
     * Entidad Advisor
     * Representa asesores/ejecutivos del sistema
     */
    ADVISOR("Advisor", "com.example.ticketero.model.entity.Advisor"),
    
    /**
     * Entidad Mensaje
     * Representa mensajes Telegram del sistema
     */
    MENSAJE("Mensaje", "com.example.ticketero.model.entity.Mensaje");
    
    private final String displayName;
    private final String className;
    
    /**
     * Constructor de EntityType
     * 
     * @param displayName Nombre para mostrar al usuario
     * @param className Nombre completo de la clase Java
     */
    EntityType(String displayName, String className) {
        this.displayName = displayName;
        this.className = className;
    }
    
    /**
     * Obtiene el nombre para mostrar al usuario
     * 
     * @return Nombre legible del tipo de entidad
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Obtiene el nombre completo de la clase Java
     * 
     * @return Nombre completo de la clase
     */
    public String getClassName() {
        return className;
    }
    
    /**
     * Convierte string a EntityType de forma segura
     * 
     * @param value String a convertir
     * @return EntityType correspondiente o null si no existe
     */
    public static EntityType fromString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        
        try {
            return EntityType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Obtiene EntityType desde nombre de clase
     * 
     * @param className Nombre de la clase
     * @return EntityType correspondiente o null si no existe
     */
    public static EntityType fromClassName(String className) {
        if (className == null) {
            return null;
        }
        
        for (EntityType type : values()) {
            if (type.className.equals(className) || 
                type.className.endsWith("." + className)) {
                return type;
            }
        }
        
        return null;
    }
}