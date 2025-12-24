package com.example.ticketero.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO complejo para dashboard ejecutivo con records anidados para organización de datos.
 * 
 * Implementa: RF-007 (Dashboard ejecutivo)
 * 
 * Estructura de datos:
 * - Resumen ejecutivo con métricas clave del día
 * - Estado de colas con detección de saturación
 * - Estado de ejecutivos con distribución de carga
 * - Sistema de alertas con prioridades y recomendaciones
 * - Métricas detalladas por tipo de cola
 * 
 * Utilizado por:
 * - DashboardService para generar datos completos
 * - AdminController en endpoints de dashboard
 * - Actualización cada 30 segundos (configurable)
 * 
 * @param timestamp Momento de generación de los datos
 * @param updateInterval Intervalo de actualización en segundos
 * @param estadoGeneral Estado general del sistema (NORMAL, ATENCION, ALERTA, CRITICO)
 * @param resumenEjecutivo Métricas principales del día
 * @param estadoColas Estado detallado por tipo de cola
 * @param estadoEjecutivos Información de asesores y distribución
 * @param alertas Lista de alertas activas con prioridades
 * @param metricas Métricas adicionales por cola y sistema
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
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
    /**
     * Record para resumen ejecutivo con métricas clave del día.
     * 
     * @param ticketsActivos Tickets en estados WAITING, CALLED, IN_SERVICE
     * @param ticketsCompletadosHoy Tickets completados desde inicio del día
     * @param tiempoPromedioGlobal Tiempo promedio de atención en minutos
     * @param tasaCompletacionPorHora Tickets completados por hora
     */
    public record ResumenEjecutivo(
        int ticketsActivos,
        int ticketsCompletadosHoy,
        double tiempoPromedioGlobal,
        double tasaCompletacionPorHora
    ) {}
    
    /**
     * Record para estado de una cola específica.
     * 
     * @param ticketsEnEspera Cantidad de tickets en estado WAITING
     * @param tiempoEstimadoMaximo Tiempo máximo estimado para el último ticket
     * @param estado Estado de la cola (VACIA, NORMAL, MODERADA, SATURADA)
     */
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
    
    /**
     * Record para alertas del sistema con prioridades y recomendaciones.
     * 
     * @param id Identificador único de la alerta
     * @param tipo Tipo de alerta (SATURACION_COLA, FALTA_ASESORES, etc.)
     * @param prioridad Prioridad (INFO, LOW, MEDIUM, HIGH, CRITICAL)
     * @param descripcion Descripción detallada del problema
     * @param accionSugerida Recomendación para resolver la alerta
     * @param timestamp Momento de generación de la alerta
     */
    public record Alerta(
        String id,
        String tipo,
        String prioridad,
        String descripcion,
        String accionSugerida,
        LocalDateTime timestamp
    ) {}
}