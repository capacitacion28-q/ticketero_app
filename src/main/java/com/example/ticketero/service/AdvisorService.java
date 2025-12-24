package com.example.ticketero.service;

import com.example.ticketero.model.entity.Advisor;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.model.enums.AdvisorStatus;
import com.example.ticketero.repository.AdvisorRepository;
import com.example.ticketero.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service para gestión de asesores con balanceo automático de carga.
 * 
 * Implementa: RF-004 (Asignación automática de ejecutivos)
 * Reglas de Negocio: RN-004 (Balanceo de carga entre ejecutivos)
 * 
 * Funcionalidades:
 * - Asignación automática basada en menor carga de trabajo
 * - Liberación automática al completar tickets
 * - Estadísticas de productividad por asesor
 * - Gestión de estados (AVAILABLE, BUSY, OFFLINE)
 * 
 * Algoritmo de balanceo: Selecciona asesor AVAILABLE con menor assignedTicketsCount
 * 
 * Dependencias: AdvisorRepository, TicketRepository, NotificationService
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdvisorService {
    
    private final AdvisorRepository advisorRepository;
    private final TicketRepository ticketRepository;
    private final NotificationService notificationService;
    
    /**
     * RN-004: Asigna próximo ticket al asesor con menor carga de trabajo.
     * Implementa balanceo automático y notificación al cliente.
     * 
     * @return Optional con ticket asignado o empty si no hay tickets/asesores disponibles
     */
    @Transactional
    public Optional<Ticket> assignNextTicket() {
        log.debug("Looking for next ticket to assign");
        
        // Buscar asesor disponible con menor carga
        Optional<Advisor> availableAdvisor = advisorRepository
            .findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus.AVAILABLE);
        
        if (availableAdvisor.isEmpty()) {
            log.warn("No available advisors found");
            return Optional.empty();
        }
        
        // Buscar próximo ticket en espera (por orden de llegada)
        Optional<Ticket> nextTicket = ticketRepository.findByStatusOrderByFechaCreacionAsc(TicketStatus.WAITING)
            .stream()
            .findFirst();
        
        if (nextTicket.isEmpty()) {
            log.debug("No tickets waiting in queue");
            return Optional.empty();
        }
        
        // Asignar ticket al asesor
        Advisor advisor = availableAdvisor.get();
        Ticket ticket = nextTicket.get();
        
        return assignTicketToAdvisor(ticket, advisor);
    }
    
    /**
     * RN-004: Asigna ticket específico a asesor determinado.
     * Actualiza estados, incrementa contador de carga y envía notificación.
     * 
     * @param ticket Ticket a asignar
     * @param advisor Asesor que recibirá el ticket
     * @return Optional con ticket asignado
     */
    @Transactional
    public Optional<Ticket> assignTicketToAdvisor(Ticket ticket, Advisor advisor) {
        log.info("Assigning ticket {} to advisor {} (module {})", 
                ticket.getNumero(), advisor.getName(), advisor.getModuleNumber());
        
        // Actualizar ticket
        ticket.setStatus(TicketStatus.CALLED);
        ticket.setAssignedAdvisor(advisor.getName());
        ticket.setAssignedModuleNumber(advisor.getModuleNumber());
        ticket.setFechaActualizacion(LocalDateTime.now());
        
        // Actualizar asesor
        advisor.incrementAssignedTicketsCount();
        advisor.setStatus(AdvisorStatus.BUSY);
        advisor.setUpdatedAt(LocalDateTime.now());
        
        // Guardar cambios
        Ticket savedTicket = ticketRepository.save(ticket);
        advisorRepository.save(advisor);
        
        // Enviar notificación al cliente
        notificationService.sendStatusChangeNotification(savedTicket, TicketStatus.WAITING);
        
        log.info("Ticket {} assigned successfully to module {}", 
                savedTicket.getNumero(), advisor.getModuleNumber());
        
        return Optional.of(savedTicket);
    }
    
    /**
     * Completa ticket y libera asesor para nueva asignación.
     * Decrementa contador de carga y cambia estado a AVAILABLE.
     * Intenta asignar próximo ticket automáticamente.
     * 
     * @param ticketNumber Número del ticket a completar
     */
    @Transactional
    public void completeTicket(String ticketNumber) {
        Optional<Ticket> ticketOpt = ticketRepository.findByNumero(ticketNumber);
        if (ticketOpt.isEmpty()) {
            throw new RuntimeException("Ticket not found: " + ticketNumber);
        }
        
        Ticket ticket = ticketOpt.get();
        if (ticket.getAssignedAdvisor() == null) {
            throw new RuntimeException("Ticket not assigned to any advisor: " + ticketNumber);
        }
        
        // Actualizar ticket
        ticket.setStatus(TicketStatus.COMPLETED);
        ticket.setFechaActualizacion(LocalDateTime.now());
        
        // Liberar asesor
        Optional<Advisor> advisorOpt = advisorRepository.findByEmail(ticket.getAssignedAdvisor() + "@banco.com");
        if (advisorOpt.isPresent()) {
            Advisor advisor = advisorOpt.get();
            advisor.decrementAssignedTicketsCount();
            advisor.setStatus(AdvisorStatus.AVAILABLE);
            advisor.setUpdatedAt(LocalDateTime.now());
            advisorRepository.save(advisor);
            
            log.info("Advisor {} is now available (module {})", 
                    advisor.getName(), advisor.getModuleNumber());
        }
        
        ticketRepository.save(ticket);
        
        log.info("Ticket {} completed successfully", ticketNumber);
        
        // Intentar asignar próximo ticket automáticamente
        assignNextTicket();
    }
    
    public List<Advisor> getAvailableAdvisors() {
        return advisorRepository.findByStatusOrderByAssignedTicketsCountAsc(AdvisorStatus.AVAILABLE);
    }
    
    public List<Object[]> getProductivityStats() {
        return advisorRepository.getAdvisorProductivityStats();
    }
}