package com.example.ticketero.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Controller de prueba para integración con Telegram
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