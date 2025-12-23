package com.example.ticketero.model.enums;

public enum TicketStatus {
    WAITING("Esperando en cola"),
    NOTIFIED("Cliente notificado (posición ≤ 3)"),
    CALLED("Asignado a ejecutivo"),
    IN_SERVICE("En atención"),
    COMPLETED("Atención finalizada"),
    CANCELLED("Cancelado"),
    NO_SHOW("Cliente no se presentó");

    private final String description;

    TicketStatus(String description) {
        this.description = description;
    }

    public String getDescription() { 
        return description; 
    }
}