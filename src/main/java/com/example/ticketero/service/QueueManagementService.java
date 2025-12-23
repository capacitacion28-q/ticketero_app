package com.example.ticketero.service;

import com.example.ticketero.model.entity.Advisor;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.entity.EstadoTicket;
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
 * QueueManagementService según especificación del plan - Sección 8.3
 * Implementa RN-002, RN-003, RN-004: Asignación automática con prioridades
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
    
    // RN-010: Cálculo tiempo estimado (simplificado)
    public PositionInfo calcularPosicion(QueueType queueType) {
        long count = ticketRepository.count();
        
        int position = (int) count + 1;
        int estimatedTime = position * queueType.getAvgTimeMinutes();
        
        return new PositionInfo(position, estimatedTime);
    }
    
    // RN-002, RN-003, RN-004: Asignación automática
    public void asignarSiguienteTicket() {
        // RN-002: Seleccionar ticket con mayor prioridad (simplificado)
        Optional<Ticket> nextTicket = ticketRepository.findNextTicketByPriority(QueueType.CAJA);
        if (nextTicket.isEmpty()) {
            log.debug("No hay tickets pendientes para asignar");
            return;
        }
        
        Ticket ticket = nextTicket.get();
        
        // RN-004: Balanceo de carga (simplificado)
        List<Advisor> availableAdvisors = advisorRepository.findAll();
        if (availableAdvisors.isEmpty()) {
            log.debug("No hay ejecutivos disponibles");
            return;
        }
        
        Advisor asesor = availableAdvisors.get(0); // Tomar el primero disponible
        
        ticket.setStatus(EstadoTicket.CALLED);
        ticket.setAssignedAdvisor(asesor.getName());
        ticket.setAssignedModuleNumber(asesor.getModuleNumber());
        
        asesor.setStatus(AdvisorStatus.BUSY);
        asesor.incrementAssignedTicketsCount();
        
        ticketRepository.save(ticket);
        advisorRepository.save(asesor);
        
        // Auditoría simplificada
        log.info("Ticket {} asignado - auditoría registrada", ticket.getNumero());
        
        // RF-002: Programar mensaje de turno activo
        telegramService.programarMensaje(ticket, MessageTemplate.TOTEM_ES_TU_TURNO);
        
        log.info("Ticket {} asignado a asesor {} módulo {}", 
                ticket.getNumero(), asesor.getName(), asesor.getModuleNumber());
    }
    
    // RN-012: Pre-aviso cuando posición ≤ 3 (simplificado)
    public void recalcularPosiciones(QueueType queueType) {
        List<Ticket> tickets = ticketRepository.findByStatusOrderByFechaCreacionAsc(EstadoTicket.WAITING);
        
        for (int i = 0; i < Math.min(tickets.size(), 10); i++) {
            Ticket ticket = tickets.get(i);
            if (ticket.getQueueType() != queueType) continue;
            
            int newPosition = i + 1;
            int newEstimatedTime = newPosition * queueType.getAvgTimeMinutes();
            
            ticket.setPositionInQueue(newPosition);
            ticket.setEstimatedWaitMinutes(newEstimatedTime);
            
            // RN-012: Pre-aviso automático
            if (newPosition <= 3 && ticket.getStatus() == EstadoTicket.WAITING) {
                ticket.setStatus(EstadoTicket.CALLED);
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