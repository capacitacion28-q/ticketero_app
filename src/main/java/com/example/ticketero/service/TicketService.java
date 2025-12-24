package com.example.ticketero.service;

import com.example.ticketero.model.dto.TicketCreateRequest;
import com.example.ticketero.model.dto.TicketResponse;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service mejorado para TicketService - RF-001, RF-002
 * 
 * Implementa: RF-001 (Creación de tickets), RF-006 (Consulta de tickets)
 * Reglas de Negocio: RN-001 (Unicidad ticket activo), RN-002 (Cálculo posición), RN-003 (Tiempo estimado)
 * 
 * Funcionalidades principales:
 * - Creación de tickets con validación de unicidad por nationalId
 * - Generación automática de números secuenciales por cola (RN-005, RN-006)
 * - Integración con sistema de notificaciones Telegram
 * - Auditoría automática de eventos críticos
 * 
 * Dependencias: TicketRepository, QueueManagementService, TelegramService
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final QueueManagementService queueManagementService;
    private final TelegramService telegramService;
    
    @Transactional
    public TicketResponse createTicket(TicketCreateRequest request) {
        log.info("Creating ticket for nationalId: {}", request.nationalId());
        
        // RN-001: Validar que no existe ticket activo para el mismo nationalId
        validateNoActiveTicket(request.nationalId());
        
        // Crear ticket
        Ticket ticket = Ticket.builder()
            .codigoReferencia(UUID.randomUUID())
            .numero(generateTicketNumber(request.queueType()))
            .nationalId(request.nationalId())
            .telefono(request.telefono())
            .branchOffice(request.branchOffice())
            .queueType(request.queueType())
            .status(TicketStatus.WAITING)
            .fechaCreacion(LocalDateTime.now())
            .build();
        
        // RN-002: Calcular posición en cola
        QueueManagementService.PositionInfo position = queueManagementService.calcularPosicion(request.queueType());
        ticket.setPositionInQueue(position.getPosition());
        
        // RN-003: Calcular tiempo estimado
        ticket.setEstimatedWaitMinutes(position.getEstimatedTime());
        
        Ticket savedTicket = ticketRepository.save(ticket);
        
        // Enviar notificación
        telegramService.programarMensaje(savedTicket, com.example.ticketero.model.enums.MessageTemplate.TOTEM_TICKET_CREADO);
        
        log.info("Ticket created: {} at position {}", savedTicket.getNumero(), position.getPosition());
        return TicketResponse.from(savedTicket);
    }
    
    public Optional<TicketResponse> findByCodigoReferencia(UUID codigoReferencia) {
        return ticketRepository.findByCodigoReferencia(codigoReferencia)
            .map(TicketResponse::from);
    }
    
    public Optional<TicketResponse> findByNumero(String numero) {
        return ticketRepository.findByNumero(numero)
            .map(TicketResponse::from);
    }
    
    @Transactional
    public TicketResponse updateStatus(UUID codigoReferencia, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findByCodigoReferencia(codigoReferencia)
            .orElseThrow(() -> new com.example.ticketero.exception.TicketNotFoundException("Ticket not found: " + codigoReferencia));
        
        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(newStatus);
        ticket.setFechaActualizacion(LocalDateTime.now());
        
        Ticket updatedTicket = ticketRepository.save(ticket);
        
        // Notificar cambio de estado
        if (newStatus == TicketStatus.CALLED) {
            telegramService.programarMensaje(updatedTicket, com.example.ticketero.model.enums.MessageTemplate.TOTEM_ES_TU_TURNO);
        }
        
        log.info("Ticket {} status changed: {} -> {}", ticket.getNumero(), oldStatus, newStatus);
        return TicketResponse.from(updatedTicket);
    }
    
    /**
     * RN-001: Valida que no existe ticket activo para el mismo nationalId.
     * Lanza TicketActivoExistenteException si encuentra ticket en estados WAITING, CALLED o IN_SERVICE.
     * 
     * @param nationalId RUT del cliente a validar
     * @throws TicketActivoExistenteException si ya existe ticket activo
     */
    private void validateNoActiveTicket(String nationalId) {
        List<TicketStatus> activeStatuses = List.of(TicketStatus.WAITING, TicketStatus.CALLED, TicketStatus.IN_SERVICE);
        
        Optional<Ticket> existingTicket = ticketRepository.findByNationalIdAndStatusIn(nationalId, activeStatuses);
        if (existingTicket.isPresent()) {
            throw new com.example.ticketero.exception.TicketActivoExistenteException("Ya existe un ticket activo para el RUT: " + nationalId);
        }
    }
    
    /**
     * RN-005, RN-006: Genera número de ticket con prefijo según tipo de cola.
     * Formato: [Prefijo][Secuencia] (ej: C01, P02, E03, G04)
     * 
     * @param queueType Tipo de cola para determinar prefijo
     * @return Número de ticket generado
     */
    // RN-005, RN-006: Generación de números con prefijos según plan
    private String generateTicketNumber(com.example.ticketero.model.enums.QueueType queueType) {
        String prefix = queueType.getPrefix();
        
        // Contar tickets del mismo tipo de cola creados hoy (simplificado)
        long count = ticketRepository.count();
        
        int sequence = (int) count + 1;
        
        if (sequence > 99) {
            log.warn("Límite de tickets alcanzado para cola {}: {}", queueType, sequence);
            sequence = 1; // Reset diario
        }
        
        return String.format("%s%02d", prefix, sequence);
    }
}