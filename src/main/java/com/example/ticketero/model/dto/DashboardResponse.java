package com.example.ticketero.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para dashboard ejecutivo - RF-004
 * Métricas en tiempo real y alertas del sistema
 */
public record DashboardResponse(
    LocalDateTime timestamp,
    int updateInterval,
    String estadoGeneral,
    ResumenEjecutivo resumenEjecutivo,
    Map<String, EstadoCola> estadoColas,
    EstadoEjecutivos estadoEjecutivos,
    List<Alerta> alertas,
    Map<String, Integer> metricas
) {
    public record ResumenEjecutivo(
        int ticketsActivos,
        int ticketsCompletadosHoy,
        double tiempoPromedioGlobal,
        double tasaCompletacionPorHora
    ) {}
    
    public record EstadoCola(
        int ticketsEnEspera,
        int tiempoEstimadoMaximo,
        String estado
    ) {}
    
    public record EstadoEjecutivos(
        int disponibles,
        int ocupados,
        int offline,
        String distribucionCarga,
        EjecutivoProductivo masProductivo
    ) {}
    
    public record EjecutivoProductivo(
        String nombre,
        int ticketsAtendidos
    ) {}
    
    public record Alerta(
        String id,
        String tipo,
        String prioridad,
        String descripcion,
        String accionSugerida,
        LocalDateTime timestamp
    ) {}
}