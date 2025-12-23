package com.example.ticketero.model.entity;

/**
 * Estados del ticket en el Sistema Ticketero
 * Ubicado en package entity según especificaciones del plan
 */
public enum EstadoTicket {
    WAITING("En espera de atención"),
    CALLED("Llamado para atención"),
    IN_PROGRESS("En atención"),
    COMPLETED("Completado"),
    CANCELLED("Cancelado");

    private final String description;

    EstadoTicket(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}