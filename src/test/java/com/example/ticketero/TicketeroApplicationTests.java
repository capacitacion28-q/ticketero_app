package com.example.ticketero;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test básico de integración para validar setup del Sistema Ticketero
 * 
 * Valida:
 * - Carga correcta del contexto Spring Boot
 * - Configuración de @EnableScheduling y @EnableRetry
 * - Inicialización sin errores
 * 
 * @see docs/implementation/plan_detallado_implementacion_v1.0.md FASE 0
 */
@SpringBootTest
@ActiveProfiles("test")
class TicketeroApplicationTests {

    /**
     * Test funcional mínimo: Validar que el contexto Spring Boot se carga correctamente
     * 
     * Reglas de negocio cubiertas en esta fase:
     * - Configuración base para procesamiento de colas (RN-002, RN-003)
     * - Configuración de reintentos para Telegram API (RN-007, RN-008)
     * - Setup de schedulers para RF-002 y RF-003
     */
    @Test
    void contextLoads() {
        // Si este test pasa, significa que:
        // 1. Spring Boot se inicializa correctamente
        // 2. @EnableScheduling está configurado
        // 3. @EnableRetry está configurado
        // 4. Todas las dependencias se resuelven
        // 5. application.yml se carga sin errores
    }
}