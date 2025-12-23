package com.example.ticketero.model.dto;

import com.example.ticketero.model.enums.QueueType;

import java.util.List;

/**
 * DTO para estado de colas - RF-003
 * Nomenclatura unificada en español para Sistema Ticketero
 */
public record QueueStatusResponse(
    QueueType tipoCola,
    String descripcion,
    int tiempoPromedio,
    int prioridad,
    String prefijo,
    EstadoActual estadoActual,
    List<TicketEnCola> tickets
) {
    public record EstadoActual(
        int ticketsEnEspera,
        int ticketsNotificados,
        int ticketsEnAtencion,
        int tiempoEstimadoCola,
        String proximoNumero
    ) {}
    
    public record TicketEnCola(
        String numero,
        String estado,
        int posicionEnCola,
        int tiempoEstimadoMinutos
    ) {}
}