package com.example.ticketero.exception;

/**
 * Excepción personalizada lanzada cuando se intenta crear un ticket para un cliente que ya tiene uno activo.
 * 
 * Implementa: RF-001 (Creación de tickets)
 * Reglas de Negocio: RN-001 (Unicidad ticket activo por cliente)
 * 
 * Casos de uso:
 * - Cliente intenta crear segundo ticket sin completar el primero
 * - Validación de unicidad por nationalId en estados WAITING, CALLED, IN_SERVICE
 * - Prevención de duplicación de tickets activos
 * 
 * Manejo:
 * - GlobalExceptionHandler convierte a HTTP 409 Conflict
 * - Mensaje indica que ya existe ticket activo
 * - Logging de advertencia para monitoreo
 * 
 * Utilizada por:
 * - TicketService.validateNoActiveTicket() en creación de tickets
 * - Validaciones de integridad de datos
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
public class TicketActivoExistenteException extends RuntimeException {
    
    /**
     * Constructor que acepta mensaje descriptivo del conflicto.
     * 
     * @param message Mensaje detallado sobre el ticket activo existente
     */
    public TicketActivoExistenteException(String message) {
        super(message);
    }
}