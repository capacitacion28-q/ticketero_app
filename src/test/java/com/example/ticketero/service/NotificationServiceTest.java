package com.example.ticketero.service;

import com.example.ticketero.model.entity.Mensaje;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.EstadoEnvio;
import com.example.ticketero.model.enums.MessageTemplate;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.repository.MensajeRepository;
import com.example.ticketero.testutil.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para NotificationService
 * Cubre RF-005 y lógica condicional de notificaciones
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private MensajeRepository mensajeRepository;
    
    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendTicketCreatedNotification_shouldCreatePendingMessage() {
        // Given - RF-005: Notificación de creación de ticket
        Ticket ticket = TestDataBuilder.ticketWaiting().build();
        ReflectionTestUtils.setField(notificationService, "telegramEnabled", false);
        
        // When
        notificationService.sendTicketCreatedNotification(ticket);
        
        // Then
        ArgumentCaptor<Mensaje> mensajeCaptor = ArgumentCaptor.forClass(Mensaje.class);
        verify(mensajeRepository).save(mensajeCaptor.capture());
        
        Mensaje savedMensaje = mensajeCaptor.getValue();
        assertThat(savedMensaje.getTicket()).isEqualTo(ticket);
        assertThat(savedMensaje.getTelefono()).isEqualTo(ticket.getTelefono());
        assertThat(savedMensaje.getPlantilla()).isEqualTo(MessageTemplate.TOTEM_TICKET_CREADO);
        assertThat(savedMensaje.getEstadoEnvio()).isEqualTo(EstadoEnvio.PENDIENTE);
        assertThat(savedMensaje.getFechaProgramada()).isNotNull();
        assertThat(savedMensaje.getFechaCreacion()).isNotNull();
    }

    @Test
    void sendStatusChangeNotification_withCalledStatus_shouldSendNotification() {
        // Given - Cambio a estado CALLED debe enviar notificación
        Ticket ticket = TestDataBuilder.ticketWaiting()
            .status(TicketStatus.CALLED)
            .build();
        TicketStatus oldStatus = TicketStatus.WAITING;
        ReflectionTestUtils.setField(notificationService, "telegramEnabled", false);
        
        // When
        notificationService.sendStatusChangeNotification(ticket, oldStatus);
        
        // Then
        ArgumentCaptor<Mensaje> mensajeCaptor = ArgumentCaptor.forClass(Mensaje.class);
        verify(mensajeRepository).save(mensajeCaptor.capture());
        
        Mensaje savedMensaje = mensajeCaptor.getValue();
        assertThat(savedMensaje.getTicket()).isEqualTo(ticket);
        assertThat(savedMensaje.getPlantilla()).isEqualTo(MessageTemplate.TOTEM_ES_TU_TURNO);
        assertThat(savedMensaje.getEstadoEnvio()).isEqualTo(EstadoEnvio.PENDIENTE);
    }

    @Test
    void sendStatusChangeNotification_withInServiceStatus_shouldNotSend() {
        // Given - Cambio a estado IN_SERVICE no debe enviar notificación
        Ticket ticket = TestDataBuilder.ticketInService().build();
        TicketStatus oldStatus = TicketStatus.CALLED;
        
        // When
        notificationService.sendStatusChangeNotification(ticket, oldStatus);
        
        // Then - No debe guardar ningún mensaje
        verify(mensajeRepository, never()).save(any(Mensaje.class));
    }

    @Test
    void sendProximityNotification_shouldScheduleMessage() {
        // Given - Notificación de proximidad programada
        Ticket ticket = TestDataBuilder.ticketWaiting()
            .positionInQueue(2)
            .build();
        
        // When
        notificationService.sendProximityNotification(ticket);
        
        // Then
        ArgumentCaptor<Mensaje> mensajeCaptor = ArgumentCaptor.forClass(Mensaje.class);
        verify(mensajeRepository).save(mensajeCaptor.capture());
        
        Mensaje savedMensaje = mensajeCaptor.getValue();
        assertThat(savedMensaje.getTicket()).isEqualTo(ticket);
        assertThat(savedMensaje.getPlantilla()).isEqualTo(MessageTemplate.TOTEM_PROXIMO_TURNO);
        assertThat(savedMensaje.getEstadoEnvio()).isEqualTo(EstadoEnvio.PENDIENTE);
        
        // Verificar que está programado para 5 minutos después
        assertThat(savedMensaje.getFechaProgramada())
            .isAfter(savedMensaje.getFechaCreacion());
    }
}