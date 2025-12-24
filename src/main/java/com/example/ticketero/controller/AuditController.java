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

/**
 * Controller REST para auditoría y trazabilidad del sistema.
 * 
 * Implementa: RF-008 (Auditoría y trazabilidad)
 * Reglas de Negocio: RN-011 (Auditoría obligatoria), RN-013 (Retención 7 años)
 * 
 * Endpoints disponibles:
 * - GET /api/audit/ticket/{number}: Historial completo de un ticket
 * - GET /api/audit/events: Consulta de eventos con filtros
 * - GET /api/audit/summary: Resumen de auditoría por período
 * 
 * Funcionalidades:
 * - Trazabilidad completa de eventos críticos
 * - Consultas por ticket, actor, tipo de evento y rango de fechas
 * - Estadísticas de actividad y tipos de eventos
 * - Cumplimiento normativo con retención de 7 años
 * 
 * Eventos auditados: TICKET_CREATED, TICKET_ASSIGNED, STATUS_CHANGED, NOTIFICATION_SENT
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Slf4j
public class AuditController {

    private final AuditService auditService;

    /**
     * RF-008: Obtiene historial completo de auditoría para un ticket específico.
     * Incluye todos los eventos desde creación hasta completación.
     * 
     * @param ticketNumber Número del ticket a consultar
     * @return ResponseEntity con lista de eventos ordenados por timestamp descendente
     */
    @GetMapping("/ticket/{ticketNumber}")
    public ResponseEntity<List<AuditEventResponse>> historialTicket(@PathVariable String ticketNumber) {
        log.info("GET /api/audit/ticket/{}", sanitizeForLog(ticketNumber));
        List<AuditEventResponse> events = auditService.getTicketAuditTrail(ticketNumber);
        return ResponseEntity.ok(events);
    }

    /**
     * RF-008: Consulta eventos de auditoría con filtros opcionales.
     * Permite filtrar por tipo de evento, actor y rango de fechas.
     * 
     * @param eventType Tipo de evento (TICKET_CREATED, STATUS_CHANGED, etc.)
     * @param actor Actor que ejecutó la acción
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @return ResponseEntity con lista de eventos filtrados
     */
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

    /**
     * RF-008: Resumen de auditoría con estadísticas por período.
     * Incluye estadísticas por tipo de evento y actividad por actor.
     * 
     * @param startDate Fecha de inicio del período
     * @param endDate Fecha de fin del período
     * @return ResponseEntity con estadísticas agregadas del período
     */
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
    
    /**
     * Sanitiza entrada para logging seguro, removiendo caracteres de control.
     * 
     * @param input Cadena a sanitizar
     * @return Cadena sanitizada o null si input es null
     */
    private String sanitizeForLog(String input) {
        if (input == null) return null;
        return input.replaceAll("[\\r\\n\\t]", "_");
    }
}