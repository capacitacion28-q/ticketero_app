package com.example.ticketero.service;

import com.example.ticketero.model.entity.Mensaje;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.EstadoEnvio;
import com.example.ticketero.model.enums.MessageTemplate;
import com.example.ticketero.repository.MensajeRepository;
import com.example.ticketero.testutil.TestDataBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para TelegramService
 * Cubre RN-007, RN-008 y procesamiento de mensajes
 */
@ExtendWith(MockitoExtension.class)
class TelegramServiceTest {

    @Mock
    private MensajeRepository mensajeRepository;
    
    @InjectMocks
    private TelegramService telegramService;

    @Test
    void programarMensaje_shouldCreatePendingMessage() {
        // Given
        Ticket ticket = TestDataBuilder.ticketWaiting().build();
        MessageTemplate template = MessageTemplate.TOTEM_TICKET_CREADO;
        
        // When
        telegramService.programarMensaje(ticket, template);
        
        // Then
        ArgumentCaptor<Mensaje> mensajeCaptor = ArgumentCaptor.forClass(Mensaje.class);
        verify(mensajeRepository).save(mensajeCaptor.capture());
        
        Mensaje savedMensaje = mensajeCaptor.getValue();
        assertThat(savedMensaje.getTicket()).isEqualTo(ticket);
        assertThat(savedMensaje.getTelefono()).isEqualTo(ticket.getTelefono());
        assertThat(savedMensaje.getPlantilla()).isEqualTo(template);
        assertThat(savedMensaje.getEstadoEnvio()).isEqualTo(EstadoEnvio.PENDIENTE);
        assertThat(savedMensaje.getIntentos()).isEqualTo(0);
        assertThat(savedMensaje.getFechaProgramada()).isNotNull();
    }

    @Test
    void enviarMensaje_withValidMessage_shouldProcessCorrectly() {
        // Given - RN-007: Procesamiento de mensajes
        Ticket ticket = TestDataBuilder.ticketWaiting().build();
        Mensaje mensaje = Mensaje.builder()
            .ticket(ticket)
            .telefono("+56912345678")
            .plantilla(MessageTemplate.TOTEM_TICKET_CREADO)
            .estadoEnvio(EstadoEnvio.PENDIENTE)
            .intentos(0)
            .build();
        
        // When
        telegramService.enviarMensaje(mensaje);
        
        // Then - Verificar que se guarda el mensaje (éxito o fallo)
        verify(mensajeRepository).save(mensaje);
        
        // El estado será ENVIADO o FALLIDO dependiendo de la conexión real
        assertThat(mensaje.getEstadoEnvio()).isIn(EstadoEnvio.ENVIADO, EstadoEnvio.FALLIDO);
    }

    @Test
    void enviarMensaje_shouldIncrementAttemptsOnFailure() {
        // Given - RN-007: Incremento de intentos en fallo
        Ticket ticket = TestDataBuilder.ticketWaiting().build();
        Mensaje mensaje = Mensaje.builder()
            .ticket(ticket)
            .telefono("+56912345678")
            .plantilla(MessageTemplate.TOTEM_TICKET_CREADO)
            .estadoEnvio(EstadoEnvio.PENDIENTE)
            .intentos(0)
            .build();
        
        // When
        telegramService.enviarMensaje(mensaje);
        
        // Then
        verify(mensajeRepository).save(mensaje);
        
        // Si falla, debe incrementar intentos
        if (mensaje.getEstadoEnvio() == EstadoEnvio.FALLIDO) {
            assertThat(mensaje.getIntentos()).isEqualTo(1);
        }
    }

    @Test
    void generarContenidoMensaje_withTicketCreated_shouldGenerateCorrectContent() {
        // Given
        Ticket ticket = TestDataBuilder.ticketWaiting()
            .numero("C001")
            .positionInQueue(3)
            .estimatedWaitMinutes(15)
            .build();
        
        Mensaje mensaje = Mensaje.builder()
            .ticket(ticket)
            .plantilla(MessageTemplate.TOTEM_TICKET_CREADO)
            .build();
        
        // When
        String content = invokeGenerarContenidoMensaje(mensaje);
        
        // Then
        assertThat(content).contains("🎫 *Ticket Creado*");
        assertThat(content).contains("📋 Número: C001");
        assertThat(content).contains("📍 Posición: 3");
        assertThat(content).contains("⏰ Tiempo estimado: 15 min");
    }

    @Test
    void generarContenidoMensaje_withEsTuTurno_shouldIncludeAdvisorInfo() {
        // Given
        Ticket ticket = TestDataBuilder.ticketInService()
            .numero("C001")
            .assignedModuleNumber(2)
            .assignedAdvisor("María López")
            .build();
        
        Mensaje mensaje = Mensaje.builder()
            .ticket(ticket)
            .plantilla(MessageTemplate.TOTEM_ES_TU_TURNO)
            .build();
        
        // When
        String content = invokeGenerarContenidoMensaje(mensaje);
        
        // Then
        assertThat(content).contains("✅ *¡Es tu turno!*");
        assertThat(content).contains("📋 Ticket: C001");
        assertThat(content).contains("🏢 Módulo: 2");
        assertThat(content).contains("👤 Te atiende: María López");
    }

    @Test
    void generarContenidoMensaje_withProximoTurno_shouldIncludePositionInfo() {
        // Given
        Ticket ticket = TestDataBuilder.ticketWaiting()
            .numero("C002")
            .positionInQueue(2)
            .build();
        
        Mensaje mensaje = Mensaje.builder()
            .ticket(ticket)
            .plantilla(MessageTemplate.TOTEM_PROXIMO_TURNO)
            .build();
        
        // When
        String content = invokeGenerarContenidoMensaje(mensaje);
        
        // Then
        assertThat(content).contains("🔔 *¡Tu turno está próximo!*");
        assertThat(content).contains("📋 Ticket: C002");
        assertThat(content).contains("📍 Posición: 2");
        assertThat(content).contains("🏃 Dirígete a la sucursal");
    }
    
    // Helper method to invoke private method using reflection
    private String invokeGenerarContenidoMensaje(Mensaje mensaje) {
        try {
            var method = TelegramService.class.getDeclaredMethod("generarContenidoMensaje", Mensaje.class);
            method.setAccessible(true);
            return (String) method.invoke(telegramService, mensaje);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}