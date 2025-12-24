package com.example.ticketero.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * Clase base alternativa para tests E2E sin Docker.
 * 
 * Configuración:
 * - H2 Database en memoria (sin índices condicionales)
 * - Telegram deshabilitado para evitar errores
 * - Contexto completo de Spring Boot
 * - Schedulers deshabilitados para testing simple
 * 
 * Limitaciones:
 * - RN-001 (unicidad) validada por lógica de negocio, no por BD
 * - Sin notificaciones Telegram reales
 * - Menos realista que PostgreSQL real
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-23
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2")
@TestPropertySource(properties = {
    "scheduler.message.enabled=false",
    "scheduler.queue.enabled=false", 
    "telegram.enabled=false",
    "logging.level.com.example.ticketero=DEBUG"
})
public abstract class BaseH2SimpleTest {

    @LocalServerPort
    protected int port;

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}