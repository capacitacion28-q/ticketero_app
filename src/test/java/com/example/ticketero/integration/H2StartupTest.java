package com.example.ticketero.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test básico de arranque de aplicación con H2.
 * Valida que la configuración H2 permite arrancar la aplicación sin errores.
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-24
 */
@SpringBootTest
@ActiveProfiles("h2")
@DisplayName("Test de Arranque H2")
class H2StartupTest {

    @Test
    @DisplayName("La aplicación arranca correctamente con H2")
    void aplicacion_arrancaCorrectamenteConH2() {
        // Given: Configuración H2 activa
        // When: Spring Boot arranca la aplicación
        // Then: No hay errores de arranque
        assertTrue(true, "La aplicación arrancó correctamente con H2");
    }
}