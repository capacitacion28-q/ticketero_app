package com.example.ticketero.exception;

import com.example.ticketero.model.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manejador global de excepciones para respuestas de error estandarizadas.
 * 
 * Funcionalidades:
 * - Manejo centralizado de todas las excepciones del sistema
 * - Conversión automática a respuestas HTTP apropiadas
 * - Logging estructurado para trazabilidad y debugging
 * - Formato consistente usando ErrorResponse DTO
 * 
 * Excepciones manejadas:
 * - TicketActivoExistenteException: HTTP 409 Conflict (RN-001)
 * - TicketNotFoundException: HTTP 404 Not Found
 * - MethodArgumentNotValidException: HTTP 400 Bad Request (Bean Validation)
 * - Exception: HTTP 500 Internal Server Error (fallback)
 * 
 * Características:
 * - @RestControllerAdvice para captura global
 * - Logging diferenciado por tipo de error
 * - Sanitización de mensajes para seguridad
 * - Integración con ErrorResponse para formato consistente
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * RN-001: Maneja violación de unicidad de ticket activo por cliente.
     * Convierte TicketActivoExistenteException a HTTP 409 Conflict.
     * 
     * @param ex Excepción de ticket activo existente
     * @return ResponseEntity con ErrorResponse y código 409
     */
    @ExceptionHandler(TicketActivoExistenteException.class)
    public ResponseEntity<ErrorResponse> handleTicketActivo(TicketActivoExistenteException ex) {
        log.warn("Ticket activo existente: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(new ErrorResponse(ex.getMessage(), 409, "/api/tickets"));
    }
    
    /**
     * Maneja casos donde no se encuentra el ticket solicitado.
     * Convierte TicketNotFoundException a HTTP 404 Not Found.
     * 
     * @param ex Excepción de ticket no encontrado
     * @return ResponseEntity con ErrorResponse y código 404
     */
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTicketNotFound(TicketNotFoundException ex) {
        log.warn("Ticket no encontrado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage(), 404, "/api/tickets"));
    }
    
    /**
     * Maneja errores de validación Bean Validation en requests.
     * Convierte MethodArgumentNotValidException a HTTP 400 Bad Request con detalles.
     * 
     * @param ex Excepción de validación de argumentos
     * @return ResponseEntity con ErrorResponse y lista de errores de validación
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .toList();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse("Datos de entrada inválidos", 400, errors, "/api"));
    }
    
    /**
     * Manejador de fallback para excepciones no controladas específicamente.
     * Convierte cualquier Exception a HTTP 500 Internal Server Error.
     * 
     * @param ex Excepción genérica no manejada
     * @return ResponseEntity con ErrorResponse y código 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Error interno del servidor", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ErrorResponse("Error interno del servidor", 500, "/api"));
    }
}