package com.example.ticketero.model.enums;

public enum EstadoEnvio {
    PENDIENTE("Mensaje programado, pendiente de envío"),
    ENVIADO("Mensaje enviado exitosamente"),
    FALLIDO("Mensaje falló tras reintentos");

    private final String description;

    EstadoEnvio(String description) {
        this.description = description;
    }

    public String getDescription() { 
        return description; 
    }
}