package com.example.ticketero.scheduler;

import com.example.ticketero.model.entity.Mensaje;
import com.example.ticketero.model.enums.EstadoEnvio;
import com.example.ticketero.repository.MensajeRepository;
import com.example.ticketero.service.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler para procesamiento asíncrono de mensajes Telegram cada 60 segundos.
 * 
 * Implementa: RF-002 (Programación de mensajes Telegram)
 * Reglas de Negocio: RN-007 (Máximo 3 reintentos), RN-008 (Backoff exponencial)
 * 
 * Funcionalidades:
 * - Procesamiento de cola de mensajes pendientes
 * - Sistema de reintentos con backoff exponencial (30s, 60s, 120s)
 * - Manejo de fallos con límite de 3 reintentos
 * - Ejecución cada 60 segundos (configurable)
 * 
 * Algoritmo de reintentos:
 * - Intento 1: Inmediato
 * - Intento 2: +30 segundos
 * - Intento 3: +60 segundos
 * - Intento 4: +120 segundos
 * - Después: FALLIDO
 * 
 * Configuración:
 * - Intervalo: scheduler.message.fixed-rate (default: 60000ms)
 * 
 * Dependencias: MensajeRepository, TelegramService
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MensajeScheduler {

    private final MensajeRepository mensajeRepository;
    private final TelegramService telegramService;

    /**
     * RF-002: Método principal de procesamiento ejecutado cada 60 segundos.
     * Obtiene mensajes pendientes y los envía vía TelegramService.
     */
    // RF-002: Procesamiento cada 60s según plan
    @Scheduled(fixedRateString = "${scheduler.message.fixed-rate:60000}")
    @Transactional
    public void procesarMensajesPendientes() {
        LocalDateTime ahora = LocalDateTime.now();

        List<Mensaje> mensajesPendientes = mensajeRepository.findByEstadoEnvioAndFechaProgramadaBefore(
            EstadoEnvio.PENDIENTE, ahora);

        if (mensajesPendientes.isEmpty()) {
            log.debug("No hay mensajes pendientes");
            return;
        }

        log.info("Procesando {} mensajes pendientes", mensajesPendientes.size());

        for (Mensaje mensaje : mensajesPendientes) {
            try {
                telegramService.enviarMensaje(mensaje);
            } catch (Exception e) {
                manejarFalloEnvio(mensaje, e);
            }
        }
    }

    /**
     * RN-007, RN-008: Maneja fallos de envío con sistema de reintentos y backoff exponencial.
     * 
     * Lógica:
     * - Incrementa contador de intentos
     * - Si >= 4 intentos: marca como FALLIDO (RN-007)
     * - Si < 4 intentos: programa reintento con backoff exponencial (RN-008)
     * 
     * @param mensaje Mensaje que falló en el envío
     * @param e Excepción que causó el fallo
     */
    // RN-007, RN-008: Manejo de fallos con backoff exponencial
    private void manejarFalloEnvio(Mensaje mensaje, Exception e) {
        log.warn("Fallo enviando mensaje {}: {}", mensaje.getId(), e.getMessage());
        
        mensaje.incrementarIntentos();
        
        if (mensaje.getIntentos() >= 4) { // RN-007: máximo 3 reintentos
            mensaje.setEstadoEnvio(EstadoEnvio.FALLIDO);
            log.error("Mensaje {} marcado como FALLIDO tras {} intentos", 
                     mensaje.getId(), mensaje.getIntentos());
        } else {
            // RN-008: Backoff exponencial 30s, 60s, 120s
            long delaySeconds = 30L * (long) Math.pow(2, mensaje.getIntentos() - 1);
            mensaje.setFechaProgramada(LocalDateTime.now().plusSeconds(delaySeconds));
            log.info("Reintento {} programado para mensaje {} en {} segundos", 
                    mensaje.getIntentos(), mensaje.getId(), delaySeconds);
        }
        
        mensajeRepository.save(mensaje);
    }
}