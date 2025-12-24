package com.example.ticketero.service;

import com.example.ticketero.model.entity.Advisor;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.model.enums.*;
import com.example.ticketero.repository.AdvisorRepository;
import com.example.ticketero.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service de gestión avanzada de colas con asignación automática inteligente.
 * 
 * Implementa: RF-003 (Cálculo de posiciones), RF-004 (Asignación automática)
 * Reglas de Negocio: RN-002 (Prioridades), RN-003 (FIFO), RN-004 (Balanceo), RN-010 (Tiempo estimado), RN-012 (Pre-aviso)
 * 
 * Algoritmos implementados:
 * - Selección por prioridad: GERENCIA > EMPRESAS > PERSONAL_BANKER > CAJA
 * - Balanceo de carga por assignedTicketsCount ascendente
 * - Pre-aviso automático cuando posición ≤ 3
 * - Recálculo de posiciones cada 5 segundos vía scheduler
 * 
 * Dependencias: TicketRepository, AdvisorRepository, TelegramService, AuditService
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class QueueManagementService {
    
    private final TicketRepository ticketRepository;
    private final AdvisorRepository advisorRepository;
    private final TelegramService telegramService;
    private final AuditService auditService;
    
    /**
     * RN-010: Calcula posición en cola y tiempo estimado de espera.
     * Fórmula: posición * tiempo promedio por tipo de cola
     * 
     * @param queueType Tipo de cola para cálculo
     * @return PositionInfo con posición y tiempo estimado
     */
    // RN-010: Cálculo tiempo estimado (simplificado)
    public PositionInfo calcularPosicion(QueueType queueType) {
        long count = ticketRepository.count();
        
        int position = (int) count + 1;
        int estimatedTime = position * queueType.getAvgTimeMinutes();
        
        return new PositionInfo(position, estimatedTime);
    }
    
    /**
     * RN-002, RN-003, RN-004: Asignación automática de tickets con prioridades.
     * 
     * Proceso:
     * 1. Selecciona ticket con mayor prioridad (RN-002)
     * 2. Busca asesor disponible con menor carga (RN-004)
     * 3. Asigna ticket y actualiza estados
     * 4. Registra evento de auditoría (RN-011)
     * 5. Programa notificación Telegram (RF-002)
     */
    // RN-002, RN-003, RN-004: Asignación automática - CORREGIDA
    public void asignarSiguienteTicket() {
        // RN-002: Seleccionar ticket con mayor prioridad
        List<Ticket> nextTickets = ticketRepository.findNextTicketByPriority();
        if (nextTickets.isEmpty()) {
            log.debug("No hay tickets pendientes para asignar");
            return;
        }
        
        Ticket ticket = nextTickets.get(0);
        
        // RN-004: Balanceo de carga - CORREGIDO
        Optional<Advisor> advisor = advisorRepository.findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus.AVAILABLE);
        if (advisor.isEmpty()) {
            log.debug("No hay ejecutivos disponibles");
            return;
        }
        
        Advisor asesor = advisor.get();
        
        ticket.setStatus(TicketStatus.IN_SERVICE);
        ticket.setAssignedAdvisor(asesor.getName());
        ticket.setAssignedModuleNumber(asesor.getModuleNumber());
        
        asesor.setStatus(AdvisorStatus.BUSY);
        asesor.incrementAssignedTicketsCount();
        
        ticketRepository.save(ticket);
        advisorRepository.save(asesor);
        
        // RN-011: Auditoría de asignación
        auditService.registrarEvento("TICKET_ASSIGNED", "SYSTEM", ticket.getId(),
                                    "WAITING", "IN_SERVICE", buildAssignmentData(asesor));
        
        // RF-002: Programar mensaje de turno activo
        telegramService.programarMensaje(ticket, MessageTemplate.TOTEM_ES_TU_TURNO);
        
        log.info("Ticket {} asignado a asesor {} módulo {}", 
                ticket.getNumero(), asesor.getName(), asesor.getModuleNumber());
    }
    
    /**
     * RN-012: Recálculo de posiciones con pre-aviso automático.
     * Envía notificación cuando posición ≤ 3 y cambia estado a CALLED.
     * 
     * @param queueType Tipo de cola a recalcular
     */
    // RN-012: Pre-aviso cuando posición ≤ 3 (simplificado)
    public void recalcularPosiciones(QueueType queueType) {
        List<Ticket> tickets = ticketRepository.findByStatusOrderByFechaCreacionAsc(TicketStatus.WAITING);
        
        for (int i = 0; i < Math.min(tickets.size(), 10); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket.getQueueType() != queueType) continue;
            
            int newPosition = i + 1;
            int newEstimatedTime = newPosition * queueType.getAvgTimeMinutes();
            
            ticket.setPositionInQueue(newPosition);
            ticket.setEstimatedWaitMinutes(newEstimatedTime);
            
            // RN-012: Pre-aviso automático
            if (newPosition <= 3 && ticket.getStatus() == TicketStatus.WAITING) {
                ticket.setStatus(TicketStatus.CALLED);
                telegramService.programarMensaje(ticket, MessageTemplate.TOTEM_PROXIMO_TURNO);
                log.info("Pre-aviso enviado para ticket {} en posición {}", 
                        ticket.getNumero(), newPosition);
            }
        }
        
        ticketRepository.saveAll(tickets.subList(0, Math.min(tickets.size(), 10)));
    }
    
    private String buildAssignmentData(Advisor advisor) {
        return String.format("{\"advisorId\":%d,\"advisorName\":\"%s\",\"moduleNumber\":%d}", 
                           advisor.getId(), advisor.getName(), advisor.getModuleNumber());
    }
    
    // Clase interna para información de posición
    public static class PositionInfo {
        private final int position;
        private final int estimatedTime;
        
        public PositionInfo(int position, int estimatedTime) {
            this.position = position;
            this.estimatedTime = estimatedTime;
        }
        
        public int getPosition() { return position; }
        public int getEstimatedTime() { return estimatedTime; }
    }
}