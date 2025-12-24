package com.example.ticketero.model.dto;

import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.model.enums.QueueType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de respuesta para tickets con método factory estático.
 * 
 * Implementa: RF-001 (Creación de tickets), RF-006 (Consulta de tickets)
 * 
 * Características:
 * - Mapeo completo de entidad Ticket a DTO
 * - Método factory from() para conversión estándar
 * - Incluye información de posición y tiempo estimado
 * - Datos de asignación de asesor y módulo
 * - Descripción de estado legible
 * 
 * Utilizado por:
 * - TicketController en respuestas de creación y consulta
 * - Servicios que requieren exposición de datos de ticket
 * 
 * @param codigoReferencia UUID único del ticket
 * @param numero Número con prefijo (C01, P02, etc.)
 * @param nationalId RUT del cliente
 * @param telefono Teléfono de contacto
 * @param branchOffice Sucursal de atención
 * @param queueType Tipo de cola asignada
 * @param status Estado actual del ticket
 * @param positionInQueue Posición actual en la cola
 * @param estimatedWaitMinutes Tiempo estimado de espera
 * @param assignedAdvisor Nombre del asesor asignado
 * @param assignedModuleNumber Número de módulo asignado
 * @param createdAt Fecha de creación
 * @param updatedAt Fecha de última actualización
 * @param statusDescription Descripción legible del estado
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
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
    /**
     * Método factory estático para conversión de entidad Ticket a DTO.
     * Mapea todos los campos incluyendo descripción de estado.
     * 
     * @param ticket Entidad Ticket a convertir
     * @return TicketResponse con todos los datos mapeados
     */
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