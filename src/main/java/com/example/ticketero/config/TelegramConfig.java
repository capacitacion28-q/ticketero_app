package com.example.ticketero.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración para integración con Telegram Bot API
 * Según especificación del plan - Sección 8.3
 */
@Configuration
public class TelegramConfig {
    
    @Value("${telegram.bot-token}")
    private String botToken;
    
    @Value("${telegram.api-url}")
    private String apiUrl;
    
    @Bean
    public RestTemplate telegramRestTemplate() {
        return new RestTemplate();
    }
    
    public String getBotToken() {
        return botToken;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public String getSendMessageUrl() {
        return apiUrl + botToken + "/sendMessage";
    }
}