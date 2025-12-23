package com.example.ticketero.model.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para respuestas de error estandarizadas
 * Formato consistente para manejo de errores en API REST
 */
public record ErrorResponse(
    boolean success,
    String code,
    String message,
    Map<String, String> details,
    LocalDateTime timestamp
) {
    public ErrorResponse(String code, String message) {
        this(false, code, message, null, LocalDateTime.now());
    }
    
    public ErrorResponse(String code, String message, Map<String, String> details) {
        this(false, code, message, details, LocalDateTime.now());
    }
}