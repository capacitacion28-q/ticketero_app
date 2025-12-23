package com.example.ticketero.model.enums;

/**
 * Estados de entrega de mensajes Telegram
 * 
 * Referencia: Plan Detallado Sección 8.1.2 - FASE 1
 * RN-007: Máximo 3 reintentos de envío
 * RN-008: Backoff exponencial 30s, 60s, 120s
 * 
 * @author Sistema Ticketero
 * @version 1.0
 */
public enum DeliveryStatus {
    
    /**
     * Mensaje pendiente de envío
     * Estado inicial del mensaje
     */
    PENDING("Pendiente", "Mensaje en cola para envío"),
    
    /**
     * Mensaje enviado exitosamente
     * Estado final exitoso
     */
    SENT("Enviado", "Mensaje entregado correctamente"),
    
    /**
     * Fallo en el envío del mensaje
     * Puede reintentarse según RN-007
     */
    FAILED("Fallido", "Error en el envío del mensaje"),
    
    /**
     * Mensaje cancelado
     * No se intentará enviar más
     */
    CANCELLED("Cancelado", "Envío cancelado por el sistema");
    
    private final String displayName;
    private final String description;
    
    /**
     * Constructor de DeliveryStatus
     * 
     * @param displayName Nombre para mostrar al usuario
     * @param description Descripción del estado
     */
    DeliveryStatus(String displayName, String description) {
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
     * Verifica si el mensaje puede reintentarse
     * 
     * @return true si permite reintentos según RN-007
     */
    public boolean canRetry() {
        return this == PENDING || this == FAILED;
    }
    
    /**
     * Verifica si el mensaje está en estado final
     * 
     * @return true si no se procesará más
     */
    public boolean isFinal() {
        return this == SENT || this == CANCELLED;
    }
    
    /**
     * Verifica si el mensaje requiere procesamiento
     * 
     * @return true si necesita ser procesado por scheduler
     */
    public boolean needsProcessing() {
        return this == PENDING;
    }
}