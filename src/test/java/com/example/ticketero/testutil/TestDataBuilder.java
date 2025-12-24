package com.example.ticketero.testutil;

import com.example.ticketero.model.dto.TicketCreateRequest;
import com.example.ticketero.model.entity.Advisor;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.AdvisorStatus;
import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.service.QueueManagementService;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Builder de datos de prueba para tests unitarios
 * Sigue patrones Lombok y estructura real del sistema
 */
public class TestDataBuilder {
    
    // Ticket builders
    public static Ticket.TicketBuilder ticketWaiting() {
        return Ticket.builder()
            .id(1L)
            .codigoReferencia(UUID.randomUUID())
            .numero("C001")
            .nationalId("12345678-9")
            .telefono("+56912345678")
            .branchOffice("Sucursal Centro")
            .queueType(QueueType.CAJA)
            .status(TicketStatus.WAITING)
            .positionInQueue(1)
            .estimatedWaitMinutes(5)
            .fechaCreacion(LocalDateTime.now());
    }
    
    public static Ticket.TicketBuilder ticketInService() {
        return ticketWaiting()
            .status(TicketStatus.IN_SERVICE)
            .assignedAdvisor("María López")
            .assignedModuleNumber(1);
    }
    
    // Advisor builders
    public static Advisor.AdvisorBuilder advisorAvailable() {
        return Advisor.builder()
            .id(1L)
            .name("María López")
            .email("maria.lopez@banco.cl")
            .moduleNumber(1)
            .status(AdvisorStatus.AVAILABLE)
            .assignedTicketsCount(0);
    }
    
    public static Advisor.AdvisorBuilder advisorBusy() {
        return advisorAvailable()
            .status(AdvisorStatus.BUSY)
            .assignedTicketsCount(2);
    }
    
    // DTO builders
    public static TicketCreateRequest validTicketRequest() {
        return new TicketCreateRequest(
            "Consulta sobre cuenta corriente",
            "Necesito información sobre mi cuenta corriente y sus beneficios",
            123L,
            "12345678-9",
            "+56912345678",
            "Sucursal Centro",
            QueueType.CAJA
        );
    }
    
    public static TicketCreateRequest invalidRutRequest() {
        return new TicketCreateRequest(
            "Consulta sobre cuenta corriente",
            "Necesito información sobre mi cuenta corriente y sus beneficios",
            123L,
            "invalid-rut",
            "+56912345678",
            "Sucursal Centro",
            QueueType.CAJA
        );
    }
    
    // Position info builder
    public static QueueManagementService.PositionInfo positionInfo(int position, int estimatedTime) {
        return new QueueManagementService.PositionInfo(position, estimatedTime);
    }
    
    // UUID constants for testing
    public static final UUID VALID_UUID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    public static final UUID NON_EXISTENT_UUID = UUID.fromString("999e9999-e99b-99d9-a999-999999999999");
}