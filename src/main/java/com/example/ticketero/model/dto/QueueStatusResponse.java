package com.example.ticketero.model.dto;

import com.example.ticketero.model.enums.QueueType;

import java.util.List;

/**
 * DTO para estado detallado de colas con records anidados para organización.
 * 
 * Implementa: RF-005 (Gestión de múltiples colas)
 * Reglas de Negocio: RN-002 (Posicionamiento), RN-003 (Tiempos estimados)
 * 
 * Información proporcionada:
 * - Configuración de la cola (tipo, prioridad, tiempo promedio)
 * - Estado actual con contadores por estado de ticket
 * - Lista de tickets en cola con posiciones y tiempos
 * - Próximo número a ser llamado
 * 
 * Utilizado por:
 * - QueueService para consultas de estado
 * - AdminController en endpoints de colas
 * - Dashboard para visualización de colas
 * 
 * @param tipoCola Tipo de cola (CAJA, PERSONAL_BANKER, EMPRESAS, GERENCIA)
 * @param descripcion Nombre descriptivo de la cola
 * @param tiempoPromedio Tiempo promedio de atención en minutos
 * @param prioridad Nivel de prioridad numérico
 * @param prefijo Prefijo para numeración (C, P, E, G)
 * @param estadoActual Estado actual con contadores
 * @param tickets Lista de tickets en cola (limitada a primeros 10)
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
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
    /**
     * Record para estado actual de la cola con contadores por estado.
     * 
     * @param ticketsEnEspera Tickets en estado WAITING
     * @param ticketsNotificados Tickets en estado CALLED
     * @param ticketsEnAtencion Tickets en estado IN_SERVICE
     * @param tiempoEstimadoCola Tiempo total estimado para procesar toda la cola
     * @param proximoNumero Número del próximo ticket a ser llamado
     */
    public record EstadoActual(
        int ticketsEnEspera,
        int ticketsNotificados,
        int ticketsEnAtencion,
        int tiempoEstimadoCola,
        String proximoNumero
    ) {}
    
    /**
     * Record para ticket individual en cola con información de posición.
     * 
     * @param numero Número del ticket con prefijo
     * @param estado Estado actual del ticket
     * @param posicionEnCola Posición actual en la cola (1-based)
     * @param tiempoEstimadoMinutos Tiempo estimado de espera en minutos
     */
    public record TicketEnCola(
        String numero,
        String estado,
        int posicionEnCola,
        int tiempoEstimadoMinutos
    ) {}
}