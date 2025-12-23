package com.example.ticketero.model.enums;

public enum MessageTemplate {
    TOTEM_TICKET_CREADO("Confirmación de creación"),
    TOTEM_PROXIMO_TURNO("Pre-aviso de proximidad"),
    TOTEM_ES_TU_TURNO("Asignación a ejecutivo");

    private final String description;

    MessageTemplate(String description) {
        this.description = description;
    }

    public String getDescription() { 
        return description; 
    }
}