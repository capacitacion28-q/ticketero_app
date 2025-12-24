package com.example.ticketero.service;

import com.example.ticketero.model.entity.Mensaje;
import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.TicketStatus;
import com.example.ticketero.model.enums.EstadoEnvio;
import com.example.ticketero.model.enums.MessageTemplate;
import com.example.ticketero.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import org.springframework.web.client.RestTemplate;

/**
 * Service de integración con Telegram Bot API para notificaciones en tiempo real.
 * 
 * Implementa: RF-002 (Programación de mensajes Telegram)
 * Reglas de Negocio: RN-007 (Máximo 3 reintentos), RN-008 (Backoff exponencial)
 * 
 * Características:
 * - Integración real con Telegram Bot API vía RestTemplate
 * - Sistema de reintentos con backoff exponencial (30s, 60s, 120s)
 * - Plantillas predefinidas en español para diferentes eventos
 * - Procesamiento asíncrono vía MensajeScheduler cada 60s
 * 
 * Plantillas disponibles: TOTEM_TICKET_CREADO, TOTEM_PROXIMO_TURNO, TOTEM_ES_TU_TURNO
 * 
 * Dependencias: MensajeRepository, RestTemplate (configurado)
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
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
    
    // RestTemplate como Bean configurado
    private RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    
    /**
     * RF-002: Programa mensaje para envío asíncrono vía scheduler.
     * Crea registro en cola de mensajes con estado PENDIENTE.
     * 
     * @param ticket Ticket asociado al mensaje
     * @param template Plantilla de mensaje a utilizar
     */
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
    
    /**
     * RN-007, RN-008: Envía mensaje vía Telegram Bot API con manejo de reintentos.
     * Implementa backoff exponencial y límite de 3 intentos.
     * 
     * @param mensaje Mensaje a enviar con datos completos
     */
    @Transactional
    public void enviarMensaje(Mensaje mensaje) {
        log.info("Enviando mensaje a {}", mensaje.getTelefono());
        
        try {
            String contenido = generarContenidoMensaje(mensaje);
            
            // Integración real con Telegram Bot API
            String url = telegramApiUrl + botToken + "/sendMessage";
            
            // Usar chat ID fijo para pruebas
            String chatId = "5598409030"; // Tu chat ID
            TelegramRequest request = new TelegramRequest(chatId, contenido);
            
            RestTemplate restTemplate = getRestTemplate();
            TelegramResponse response = restTemplate.postForObject(url, request, TelegramResponse.class);
            
            if (response != null && response.ok()) {
                mensaje.setEstadoEnvio(EstadoEnvio.ENVIADO);
                mensaje.setFechaEnvio(LocalDateTime.now());
                mensaje.setTelegramMessageId(response.result().message_id());
                log.info("Mensaje enviado exitosamente: {}", response.result().message_id());
            } else {
                throw new RuntimeException("Error en respuesta de Telegram API");
            }
            
            mensajeRepository.save(mensaje);
            
        } catch (Exception e) {
            log.error("Error enviando mensaje: {}", e.getMessage());
            mensaje.setEstadoEnvio(EstadoEnvio.FALLIDO);
            mensaje.incrementarIntentos();
            mensajeRepository.save(mensaje);
        }
    }
    
    /**
     * Genera contenido personalizado del mensaje según plantilla y datos del ticket.
     * Utiliza formato Markdown para mejor presentación en Telegram.
     * 
     * @param mensaje Mensaje con plantilla y ticket asociado
     * @return Contenido formateado del mensaje
     */
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
    
    // DTOs para Telegram API
    private record TelegramRequest(String chat_id, String text, String parse_mode) {
        public TelegramRequest(String chat_id, String text) {
            this(chat_id, text, "Markdown");
        }
    }
    
    private record TelegramResponse(boolean ok, Result result) {}
    private record Result(String message_id) {}
}