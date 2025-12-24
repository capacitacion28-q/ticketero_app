package com.example.ticketero.controller;

import com.example.ticketero.model.dto.TicketCreateRequest;
import com.example.ticketero.model.dto.TicketResponse;
import com.example.ticketero.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Controller REST para gestión de tickets del sistema.
 * 
 * Implementa: RF-001 (Creación de tickets), RF-006 (Consulta de tickets)
 * 
 * Endpoints disponibles:
 * - POST /api/tickets: Crear nuevo ticket con validación automática
 * - GET /api/tickets/{uuid}: Consultar ticket por código de referencia
 * - GET /api/tickets/number/{number}: Consultar ticket por número
 * 
 * Validaciones:
 * - Bean Validation automática en TicketCreateRequest
 * - Formato RUT chileno: ^[0-9]{7,8}-[0-9Kk]$
 * - Formato teléfono: ^\+56[0-9]{9}$
 * 
 * Códigos HTTP: 201 (Created), 200 (OK), 404 (Not Found), 400 (Bad Request)
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;

    /**
     * RF-001: Crea nuevo ticket con validación automática y notificación.
     * 
     * Proceso:
     * 1. Valida formato RUT y teléfono vía Bean Validation
     * 2. Verifica unicidad de ticket activo (RN-001)
     * 3. Genera número secuencial con prefijo (RN-005, RN-006)
     * 4. Calcula posición y tiempo estimado (RN-010)
     * 5. Programa notificación Telegram (RF-002)
     * 
     * @param request Datos del ticket con validaciones aplicadas
     * @return ResponseEntity con TicketResponse (201 Created)
     */
    @PostMapping
    public ResponseEntity<TicketResponse> crearTicket(@Valid @RequestBody TicketCreateRequest request) {
        log.info("POST /api/tickets - Creando ticket para {}", request.nationalId());
        TicketResponse response = ticketService.createTicket(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * RF-006: Consulta ticket por código de referencia UUID.
     * 
     * @param codigoReferencia UUID único del ticket
     * @return ResponseEntity con TicketResponse (200 OK) o 404 Not Found
     */
    @GetMapping("/{codigoReferencia}")
    public ResponseEntity<TicketResponse> obtenerTicket(@PathVariable UUID codigoReferencia) {
        log.info("GET /api/tickets/{}", codigoReferencia);
        return ticketService.findByCodigoReferencia(codigoReferencia)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * RF-006: Consulta ticket por número generado (formato: C01, P02, etc.).
     * 
     * @param ticketNumber Número de ticket con prefijo
     * @return ResponseEntity con TicketResponse (200 OK) o 404 Not Found
     */
    @GetMapping("/number/{ticketNumber}")
    public ResponseEntity<TicketResponse> consultarPorNumero(@PathVariable String ticketNumber) {
        log.info("GET /api/tickets/number/{}", ticketNumber);
        return ticketService.findByNumero(ticketNumber)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}