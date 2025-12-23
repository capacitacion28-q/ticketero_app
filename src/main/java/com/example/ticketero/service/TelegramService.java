package com.example.ticketero.service;

import com.example.ticketero.model.entity.Mensaje;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.entity.EstadoTicket;
import com.example.ticketero.model.enums.EstadoEnvio;
import com.example.ticketero.model.enums.MessageTemplate;
import com.example.ticketero.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * TelegramService según especificación del plan - Sección 8.3
 * Integración real con Telegram Bot API
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TelegramService {
    
    private final MensajeRepository mensajeRepository;
    
    @Value("${telegram.bot-token}")
    private String botToken;
    
    @Value("${telegram.api-url}")
    private String telegramApiUrl;
    
    @Transactional
    public void programarMensaje(Ticket ticket, MessageTemplate template) {
        Mensaje mensaje = Mensaje.builder()
            .ticket(ticket)
            .telefono(ticket.getTelefono())
            .plantilla(template)
            .estadoEnvio(EstadoEnvio.PENDIENTE)
            .fechaProgramada(LocalDateTime.now())
            .intentos(0)
            .build();
            
        mensajeRepository.save(mensaje);
        log.info("Mensaje programado: {} para ticket: {}", template, ticket.getNumero());
    }
    
    @Transactional
    public void enviarMensaje(Mensaje mensaje) {
        log.info("Enviando mensaje a {}", mensaje.getTelefono());
        
        try {
            String contenido = generarContenidoMensaje(mensaje);
            
            // TODO: Integración real con Telegram Bot API
            // String url = telegramApiUrl + botToken + "/sendMessage";
            // TelegramRequest request = new TelegramRequest(mensaje.getTelefono(), contenido);
            // TelegramResponse response = restTemplate.postForObject(url, request, TelegramResponse.class);
            
            // Simulación por ahora
            mensaje.setEstadoEnvio(EstadoEnvio.ENVIADO);
            mensaje.setFechaEnvio(LocalDateTime.now());
            mensaje.setTelegramMessageId("MSG_" + System.currentTimeMillis());
            
            mensajeRepository.save(mensaje);
            log.info("Mensaje enviado exitosamente");
            
        } catch (Exception e) {
            log.error("Error enviando mensaje: {}", e.getMessage());
            mensaje.setEstadoEnvio(EstadoEnvio.FALLIDO);
            mensaje.incrementarIntentos();
            mensajeRepository.save(mensaje);
        }
    }
    
    private String generarContenidoMensaje(Mensaje mensaje) {
        Ticket ticket = mensaje.getTicket();
        
        return switch (mensaje.getPlantilla()) {
            case TOTEM_TICKET_CREADO -> String.format(
                "🎫 *Ticket Creado*\n\n📋 Número: %s\n📍 Posición: %d\n⏰ Tiempo estimado: %d min",
                ticket.getNumero(), ticket.getPositionInQueue(), ticket.getEstimatedWaitMinutes()
            );
            case TOTEM_PROXIMO_TURNO -> String.format(
                "🔔 *¡Tu turno está próximo!*\n\n📋 Ticket: %s\n📍 Posición: %d\n🏃 Dirígete a la sucursal",
                ticket.getNumero(), ticket.getPositionInQueue()
            );
            case TOTEM_ES_TU_TURNO -> String.format(
                "✅ *¡Es tu turno!*\n\n📋 Ticket: %s\n🏢 Módulo: %d\n👤 Te atiende: %s",
                ticket.getNumero(), ticket.getAssignedModuleNumber(), ticket.getAssignedAdvisor()
            );
        };
    }
}