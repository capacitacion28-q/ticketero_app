package com.example.ticketero.exception;

/**
 * Excepción personalizada lanzada cuando no se encuentra un ticket solicitado.
 * 
 * Implementa: RF-006 (Consulta de tickets)
 * 
 * Casos de uso:
 * - Consulta por UUID inexistente
 * - Consulta por número de ticket inexistente
 * - Operaciones sobre tickets eliminados o no válidos
 * 
 * Manejo:
 * - GlobalExceptionHandler convierte a HTTP 404 Not Found
 * - Logging automático del error para trazabilidad
 * 
 * Utilizada por:
 * - TicketService en métodos de consulta
 * - Controllers cuando no encuentran recursos
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
public class TicketNotFoundException extends RuntimeException {
    
    /**
     * Constructor que acepta mensaje descriptivo del error.
     * 
     * @param message Mensaje detallado sobre el ticket no encontrado
     */
    public TicketNotFoundException(String message) {
        super(message);
    }
}