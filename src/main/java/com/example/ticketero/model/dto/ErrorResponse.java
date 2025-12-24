package com.example.ticketero.model.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de error estandarizadas con constructores de conveniencia.
 * 
 * Características:
 * - Formato consistente para todas las respuestas de error
 * - Timestamp automático de generación
 * - Soporte para múltiples errores de validación
 * - Integración con GlobalExceptionHandler
 * 
 * Utilizado por:
 * - GlobalExceptionHandler para manejo centralizado de errores
 * - Controllers para respuestas de error personalizadas
 * - Validaciones Bean Validation con múltiples errores
 * 
 * @param success Siempre false para respuestas de error
 * @param message Mensaje principal del error
 * @param status Código de estado HTTP
 * @param errors Lista de errores detallados (validaciones)
 * @param path Ruta del endpoint que generó el error
 * @param timestamp Momento de generación del error
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
public record ErrorResponse(
    boolean success,
    String message,
    int status,
    List<String> errors,
    String path,
    LocalDateTime timestamp
) {
    /**
     * Constructor de conveniencia para errores simples sin lista de errores.
     * 
     * @param message Mensaje del error
     * @param status Código de estado HTTP
     * @param path Ruta del endpoint
     */
    public ErrorResponse(String message, int status, String path) {
        this(false, message, status, List.of(), path, LocalDateTime.now());
    }
    
    /**
     * Constructor de conveniencia para errores con lista de errores detallados.
     * Utilizado principalmente para errores de validación Bean Validation.
     * 
     * @param message Mensaje principal del error
     * @param status Código de estado HTTP
     * @param errors Lista de errores específicos
     * @param path Ruta del endpoint
     */
    public ErrorResponse(String message, int status, List<String> errors, String path) {
        this(false, message, status, errors, path, LocalDateTime.now());
    }
}