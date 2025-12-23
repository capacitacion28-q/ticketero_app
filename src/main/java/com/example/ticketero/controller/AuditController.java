package com.example.ticketero.controller;

import com.example.ticketero.model.dto.AuditEventResponse;
import com.example.ticketero.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Slf4j
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/ticket/{ticketNumber}")
    public ResponseEntity<List<AuditEventResponse>> historialTicket(@PathVariable String ticketNumber) {
        log.info("GET /api/audit/ticket/{}", sanitizeForLog(ticketNumber));
        List<AuditEventResponse> events = auditService.getTicketAuditTrail(ticketNumber);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/events")
    public ResponseEntity<List<AuditEventResponse>> consultarEventos(
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String actor,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate) {
        log.info("GET /api/audit/events - eventType: {}, actor: {}", 
                sanitizeForLog(eventType), sanitizeForLog(actor));
        
        List<AuditEventResponse> events;
        if (startDate != null && endDate != null) {
            events = auditService.getAuditEventsByDateRange(startDate, endDate);
        } else if (actor != null) {
            events = auditService.getAuditEventsByActor(actor);
        } else {
            events = List.of(); // Evitar cargar todos los eventos
        }
        
        return ResponseEntity.ok(events);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> resumenAuditoria(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        log.info("GET /api/audit/summary - period: {} to {}", startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<Object[]> eventStats = auditService.getEventTypeStatistics(start, end);
        List<Object[]> actorStats = auditService.getActorActivityStats(start, end);
        
        return ResponseEntity.ok(Map.of(
            "period", startDate + " to " + endDate,
            "eventTypeStats", eventStats,
            "actorStats", actorStats
        ));
    }
    
    private String sanitizeForLog(String input) {
        if (input == null) return null;
        return input.replaceAll("[\\r\\n\\t]", "_");
    }
}