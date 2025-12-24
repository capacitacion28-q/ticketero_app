package com.example.ticketero.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Clase base para tests E2E contra aplicación en Docker Compose.
 * 
 * Configuración:
 * - Conecta a PostgreSQL en Docker Compose (puerto 5432)
 * - Conecta a aplicación en Docker Compose (puerto 8080)
 * - Sin TestContainers (usa servicios externos)
 * - Health checks antes de ejecutar tests
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-24
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test-docker")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5432/ticketero_db",
    "spring.datasource.username=ticketero_user",
    "spring.datasource.password=ticketero_pass"
})
public abstract class BaseDockerComposeTest {

    protected static final String APP_BASE_URL = "http://localhost:8080";
    protected static final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @BeforeAll
    static void validateDockerServices() throws Exception {
        // Validar que la aplicación esté corriendo
        waitForApplicationHealth();
    }

    @BeforeEach
    void setUp() {
        // Setup específico por test si es necesario
    }

    private static void waitForApplicationHealth() throws Exception {
        int maxRetries = 30;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(APP_BASE_URL + "/actuator/health"))
                        .timeout(Duration.ofSeconds(5))
                        .GET()
                        .build();
                
                HttpResponse<String> response = httpClient.send(request, 
                        HttpResponse.BodyHandlers.ofString());
                
                if (response.statusCode() == 200 && response.body().contains("UP")) {
                    System.out.println("✅ Aplicación Docker lista para tests");
                    return;
                }
            } catch (Exception e) {
                // Continuar intentando
            }
            
            retryCount++;
            Thread.sleep(2000); // Esperar 2 segundos
        }
        
        throw new RuntimeException("❌ Aplicación Docker no responde después de " + maxRetries + " intentos");
    }

    protected String getBaseUrl() {
        return APP_BASE_URL;
    }
}