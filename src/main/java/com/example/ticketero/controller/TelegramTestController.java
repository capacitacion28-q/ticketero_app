package com.example.ticketero.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Controller de prueba para integración con Telegram Bot API.
 * 
 * Funcionalidades de testing:
 * - Verificación de conectividad con Telegram Bot API
 * - Envío de mensajes de prueba
 * - Obtención de updates y información del bot
 * 
 * Endpoints disponibles:
 * - GET /api/test/telegram/updates: Obtener updates del bot
 * - POST /api/test/telegram/send: Enviar mensaje de prueba
 * - GET /api/test/telegram/bot-info: Información del bot
 * 
 * Configuración requerida:
 * - telegram.bot-token: Token del bot de Telegram
 * - telegram.api-url: URL base de la API de Telegram
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TelegramTestController {

    @Value("${telegram.bot-token}")
    private String botToken;

    @Value("${telegram.api-url}")
    private String telegramApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Obtiene updates recientes del bot de Telegram para verificar conectividad.
     * 
     * @return ResponseEntity con updates del bot o error de conexión
     */
    @GetMapping("/telegram/updates")
    public ResponseEntity<Object> getUpdates() {
        try {
            String url = telegramApiUrl + botToken + "/getUpdates";
            Object response = restTemplate.getForObject(url, Object.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo updates: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Envía mensaje de prueba a chat específico vía Telegram Bot API.
     * 
     * @param request Mapa con chatId y mensaje opcional
     * @return ResponseEntity con respuesta de Telegram API o error
     */
    @PostMapping("/telegram/send")
    public ResponseEntity<Object> sendTestMessage(@RequestBody Map<String, String> request) {
        try {
            String chatId = request.get("chatId");
            String message = request.getOrDefault("message", "🧪 Mensaje de prueba desde Sistema Ticketero!");

            String url = telegramApiUrl + botToken + "/sendMessage";
            
            Map<String, Object> telegramRequest = Map.of(
                "chat_id", chatId,
                "text", message,
                "parse_mode", "Markdown"
            );

            Object response = restTemplate.postForObject(url, telegramRequest, Object.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error enviando mensaje: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtiene información del bot configurado para verificar token.
     * 
     * @return ResponseEntity con datos del bot o error de autenticación
     */
    @GetMapping("/telegram/bot-info")
    public ResponseEntity<Object> getBotInfo() {
        try {
            String url = telegramApiUrl + botToken + "/getMe";
            Object response = restTemplate.getForObject(url, Object.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error obteniendo info del bot: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}