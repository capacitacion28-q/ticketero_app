package com.example.ticketero.model.dto;

import com.example.ticketero.model.enums.QueueType;

import java.util.List;

/**
 * DTO para estado de colas - RF-003
 * Información detallada de cada cola con tickets en espera
 */
public record QueueStatusResponse(
    QueueType queueType,
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
        String status,
        int positionInQueue,
        int estimatedWaitMinutes
    ) {}
}