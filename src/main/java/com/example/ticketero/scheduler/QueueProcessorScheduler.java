package com.example.ticketero.scheduler;

import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.repository.TicketRepository;
import com.example.ticketero.service.QueueManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class QueueProcessorScheduler {

    private final QueueManagementService queueManagementService;
    private final TicketRepository ticketRepository;

    // RF-003: Procesamiento cada 5s según plan
    @Scheduled(fixedRateString = "${scheduler.queue.fixed-rate:5000}")
    @Transactional
    public void procesarColas() {
        log.debug("Ejecutando procesamiento de colas");
        
        // Recalcular posiciones tras cambios de estado
        for (QueueType queueType : QueueType.values()) {
            queueManagementService.recalcularPosiciones(queueType);
        }
        
        // Asignar tickets cuando hay ejecutivos disponibles
        queueManagementService.asignarSiguienteTicket();
        
        // Procesar timeouts de NO_SHOW (5 minutos)
        procesarTimeouts();
    }
    
    private void procesarTimeouts() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(5);
        
        List<Ticket> timedOutTickets = ticketRepository.findCalledOlderThan(timeoutThreshold);
        
        for (Ticket ticket : timedOutTickets) {
            log.warn("Ticket {} marcado como NO_SHOW por timeout", ticket.getNumero());
            ticket.setStatus(TicketStatus.NO_SHOW);
            ticket.setFechaActualizacion(LocalDateTime.now());
        }
        
        if (!timedOutTickets.isEmpty()) {
            ticketRepository.saveAll(timedOutTickets);
            log.info("Procesados {} tickets con timeout", timedOutTickets.size());
        }
    }
}