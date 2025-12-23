package com.example.ticketero.model.enums;

public enum ActorType {
    SYSTEM("Sistema automático"),
    CLIENT("Cliente/Usuario"),
    ADVISOR("Ejecutivo/Asesor"),
    SUPERVISOR("Supervisor de sucursal");

    private final String description;

    ActorType(String description) {
        this.description = description;
    }

    public String getDescription() { 
        return description; 
    }
}