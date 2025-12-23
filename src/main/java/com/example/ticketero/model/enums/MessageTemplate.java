package com.example.ticketero.model.enums;

public enum MessageTemplate {
    totem_ticket_creado("Confirmación de creación"),
    totem_proximo_turno("Pre-aviso de proximidad"),
    totem_es_tu_turno("Asignación a ejecutivo");

    private final String description;

    MessageTemplate(String description) {
        this.description = description;
    }

    public String getDescription() { 
        return description; 
    }
}