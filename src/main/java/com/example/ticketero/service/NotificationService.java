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
 * Service para notificaciones - RF-005
 * Integración con Telegram Bot API para envío de mensajes
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {
    
    private final MensajeRepository mensajeRepository;
    
    @Value("${telegram.bot.enabled:false}")
    private boolean telegramEnabled;
    
    @Transactional
    public void sendTicketCreatedNotification(Ticket ticket) {
        log.info("Sending ticket created notification for: {}", ticket.getNumero());
        
        Mensaje mensaje = Mensaje.builder()
            .ticket(ticket)
            .telefono(ticket.getTelefono())
            .plantilla(MessageTemplate.TOTEM_TICKET_CREADO)
            .estadoEnvio(EstadoEnvio.PENDIENTE)
            .fechaProgramada(LocalDateTime.now())
            .fechaCreacion(LocalDateTime.now())
            .build();
        
        mensajeRepository.save(mensaje);
        
        // Enviar inmediatamente si Telegram está habilitado
        if (telegramEnabled) {
            sendTelegramMessage(mensaje);
        }
    }
    
    @Transactional
    public void sendStatusChangeNotification(Ticket ticket, EstadoTicket oldStatus) {
        log.info("Sending status change notification for ticket: {} ({} -> {})", 
                ticket.getNumero(), oldStatus, ticket.getStatus());
        
        MessageTemplate template = getTemplateForStatusChange(ticket.getStatus());
        if (template == null) {
            return; // No enviar notificación para este cambio de estado
        }
        
        Mensaje mensaje = Mensaje.builder()
            .ticket(ticket)
            .telefono(ticket.getTelefono())
            .plantilla(template)
            .estadoEnvio(EstadoEnvio.PENDIENTE)
            .fechaProgramada(LocalDateTime.now())
            .fechaCreacion(LocalDateTime.now())
            .build();
        
        mensajeRepository.save(mensaje);
        
        if (telegramEnabled) {
            sendTelegramMessage(mensaje);
        }
    }
    
    @Transactional
    public void sendProximityNotification(Ticket ticket) {
        log.info("Sending proximity notification for ticket: {}", ticket.getNumero());
        
        Mensaje mensaje = Mensaje.builder()
            .ticket(ticket)
            .telefono(ticket.getTelefono())
            .plantilla(MessageTemplate.TOTEM_PROXIMO_TURNO)
            .estadoEnvio(EstadoEnvio.PENDIENTE)
            .fechaProgramada(LocalDateTime.now().plusMinutes(5)) // Programar para 5 min después
            .fechaCreacion(LocalDateTime.now())
            .build();
        
        mensajeRepository.save(mensaje);
    }
    
    private void sendTelegramMessage(Mensaje mensaje) {
        try {
            // Simular envío a Telegram Bot API
            log.info("Sending Telegram message to: {} with template: {}", 
                    mensaje.getTelefono(), mensaje.getPlantilla());
            
            // Aquí iría la integración real con Telegram Bot API
            // Por ahora simulamos éxito
            mensaje.setEstadoEnvio(EstadoEnvio.ENVIADO);
            mensaje.setFechaEnvio(LocalDateTime.now());
            mensaje.setTelegramMessageId("MSG_" + System.currentTimeMillis());
            
            mensajeRepository.save(mensaje);
            
        } catch (Exception e) {
            log.error("Error sending Telegram message: {}", e.getMessage());
            mensaje.setEstadoEnvio(EstadoEnvio.FALLIDO);
            mensaje.incrementarIntentos();
            mensajeRepository.save(mensaje);
        }
    }
    
    private MessageTemplate getTemplateForStatusChange(EstadoTicket newStatus) {
        return switch (newStatus) {
            case CALLED -> MessageTemplate.TOTEM_ES_TU_TURNO;
            case IN_PROGRESS -> null; // No notificar cuando está en atención
            case COMPLETED, CANCELLED -> null; // No notificar al completar/cancelar
            default -> null;
        };
    }
}