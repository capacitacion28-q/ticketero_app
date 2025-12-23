package com.example.ticketero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Aplicación principal del Sistema Ticketero
 * 
 * Sistema de gestión de tickets para institución financiera que implementa:
 * - Gestión de colas por prioridad (C, P, E, G)
 * - Asignación automática de asesores
 * - Notificaciones vía Telegram Bot API
 * - Procesamiento asíncrono con schedulers
 * 
 * Configuraciones habilitadas:
 * - @EnableScheduling: Para procesamiento de colas cada 5s y mensajes cada 60s (RF-002, RF-003)
 * - @EnableRetry: Para estrategia de reintentos según ADR-001 (RN-007, RN-008)
 * 
 * @see docs/architecture/software_architecture_design_v1.0.md
 * @see docs/implementation/plan_detallado_implementacion_v1.0.md
 * 
 * @author Sistema Ticketero
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling  // OBLIGATORIO: ADR-003 Procesamiento de Colas
@EnableRetry       // OBLIGATORIO: ADR-001 Estrategia de Reintentos
public class TicketeroApplication {

    /**
     * Punto de entrada principal del Sistema Ticketero
     * 
     * Inicializa:
     * - Spring Boot context con todas las configuraciones
     * - Schedulers para procesamiento automático
     * - Configuración de reintentos para Telegram API
     * - Conexión a PostgreSQL con Flyway migrations
     * 
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        SpringApplication.run(TicketeroApplication.class, args);
    }
}