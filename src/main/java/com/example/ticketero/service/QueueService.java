package com.example.ticketero.service;

import com.example.ticketero.model.dto.QueueStatusResponse;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service especializado para gestión y consulta de colas por tipo.
 * 
 * Implementa: RF-005 (Gestión de múltiples colas)
 * Reglas de Negocio: RN-002 (Posicionamiento), RN-003 (Tiempos estimados)
 * 
 * Funcionalidades:
 * - Cálculo de posiciones dinámicas por cola
 * - Estimación de tiempos basada en tipo de cola
 * - Estado detallado de colas con tickets en espera
 * - Información de próximo ticket por cola
 * 
 * Tipos de cola soportados: CAJA, PERSONAL_BANKER, EMPRESAS, GERENCIA
 * 
 * Dependencias: TicketRepository
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QueueService {
    
    private final TicketRepository ticketRepository;
    
    /**
     * RN-002: Calcula posición dinámica en cola basada en tickets en espera.
     * 
     * @param queueType Tipo de cola para cálculo
     * @return Posición siguiente en la cola
     */
    public int calculateQueuePosition(QueueType queueType) {
        // RN-002: Calcular posición basada en tickets en espera
        long waitingTickets = ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, queueType);
        return (int) waitingTickets + 1;
    }
    
    /**
     * RN-003: Calcula tiempo estimado de espera basado en posición y tipo de cola.
     * 
     * @param queueType Tipo de cola con tiempo promedio definido
     * @param position Posición actual en la cola
     * @return Tiempo estimado en minutos
     */
    public int calculateEstimatedWaitTime(QueueType queueType, int position) {
        // RN-003: Tiempo estimado basado en tipo de cola y posición
        int avgTimePerTicket = queueType.getAvgTimeMinutes();
        return (position - 1) * avgTimePerTicket;
    }
    
    /**
     * RF-005: Obtiene estado completo de una cola específica.
     * Incluye tickets en espera, notificados, en atención y próximo número.
     * 
     * @param queueType Tipo de cola a consultar
     * @return QueueStatusResponse con estado detallado
     */
    public QueueStatusResponse getQueueStatus(QueueType queueType) {
        log.debug("Getting queue status for: {}", queueType);
        
        // Contar tickets por estado
        int ticketsEnEspera = (int) ticketRepository.countByStatusAndQueueType(TicketStatus.WAITING, queueType);
        int ticketsNotificados = (int) ticketRepository.countByStatusAndQueueType(TicketStatus.CALLED, queueType);
        int ticketsEnAtencion = (int) ticketRepository.countByStatusAndQueueType(TicketStatus.IN_SERVICE, queueType);
        
        // Calcular tiempo estimado total de la cola
        int tiempoEstimadoCola = ticketsEnEspera * queueType.getAvgTimeMinutes();
        
        // Obtener próximo número
        String proximoNumero = getNextTicketNumber(queueType);
        
        // Estado actual
        QueueStatusResponse.EstadoActual estadoActual = new QueueStatusResponse.EstadoActual(
            ticketsEnEspera,
            ticketsNotificados,
            ticketsEnAtencion,
            tiempoEstimadoCola,
            proximoNumero
        );
        
        // Tickets en cola
        List<QueueStatusResponse.TicketEnCola> ticketsEnCola = getTicketsInQueue(queueType);
        
        return new QueueStatusResponse(
            queueType,
            queueType.getDisplayName(),
            queueType.getAvgTimeMinutes(),
            queueType.getPriority(),
            queueType.getPrefix(),
            estadoActual,
            ticketsEnCola
        );
    }
    
    private String getNextTicketNumber(QueueType queueType) {
        return ticketRepository.findByStatusOrderByFechaCreacionAsc(TicketStatus.WAITING)
            .stream()
            .filter(ticket -> ticket.getQueueType() == queueType)
            .findFirst()
            .map(ticket -> ticket.getNumero())
            .orElse("N/A");
    }
    
    private List<QueueStatusResponse.TicketEnCola> getTicketsInQueue(QueueType queueType) {
        return ticketRepository.findByStatusOrderByFechaCreacionAsc(TicketStatus.WAITING)
            .stream()
            .filter(ticket -> ticket.getQueueType() == queueType)
            .limit(10) // Mostrar solo los primeros 10
            .map(ticket -> new QueueStatusResponse.TicketEnCola(
                ticket.getNumero(),
                ticket.getStatus().name(),
                ticket.getPositionInQueue(),
                ticket.getEstimatedWaitMinutes()
            ))
            .collect(Collectors.toList());
    }
}