package com.example.ticketero.model.dto;

import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.model.enums.QueueType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para tickets - RF-001, RF-002
 * Usa EstadoTicket del package entity según especificaciones
 */
public record TicketResponse(
    UUID codigoReferencia,
    String numero,
    String nationalId,
    String telefono,
    String branchOffice,
    QueueType queueType,
    TicketStatus status,
    Integer positionInQueue,
    Integer estimatedWaitMinutes,
    String assignedAdvisor,
    Integer assignedModuleNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String statusDescription
) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
            ticket.getCodigoReferencia(),
            ticket.getNumero(),
            ticket.getNationalId(),
            ticket.getTelefono(),
            ticket.getBranchOffice(),
            ticket.getQueueType(),
            ticket.getStatus(),
            ticket.getPositionInQueue(),
            ticket.getEstimatedWaitMinutes(),
            ticket.getAssignedAdvisor(),
            ticket.getAssignedModuleNumber(),
            ticket.getFechaCreacion(),
            ticket.getFechaActualizacion(),
            ticket.getStatus().getDescription()
        );
    }
}