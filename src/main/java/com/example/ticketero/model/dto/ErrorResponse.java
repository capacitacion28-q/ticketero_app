package com.example.ticketero.model.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de error estandarizadas - Sistema Ticketero
 * Formato consistente según especificaciones del plan
 */
public record ErrorResponse(
    boolean success,
    String message,
    int status,
    List<String> errors,
    String path,
    LocalDateTime timestamp
) {
    public ErrorResponse(String message, int status, String path) {
        this(false, message, status, List.of(), path, LocalDateTime.now());
    }
    
    public ErrorResponse(String message, int status, List<String> errors, String path) {
        this(false, message, status, errors, path, LocalDateTime.now());
    }
}