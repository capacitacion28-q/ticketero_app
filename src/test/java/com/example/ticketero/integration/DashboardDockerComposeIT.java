package com.example.ticketero.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test E2E para Dashboard contra aplicación en Docker Compose.
 * 
 * Ejecuta tests contra:
 * - Aplicación Spring Boot en Docker (puerto 8080)
 * - PostgreSQL en Docker (puerto 5432)
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-24
 */
@DisplayName("Dashboard E2E - Docker Compose")
class DashboardDockerComposeIT extends BaseDockerComposeTest {

    @Test
    @DisplayName("Health Check - Aplicación responde correctamente")
    void testHealthCheck() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/actuator/health"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("UP"));
        System.out.println("✅ Health check exitoso");
    }

    @Test
    @DisplayName("Dashboard Summary - Endpoint responde con datos")
    void testDashboardSummary() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/api/dashboard/summary"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
        assertTrue(response.body().length() > 0);
        System.out.println("✅ Dashboard summary exitoso");
    }

    @Test
    @DisplayName("Queue Stats - Endpoint responde con estadísticas")
    void testQueueStats() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getBaseUrl() + "/api/queues/stats"))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, 
                HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertNotNull(response.body());
        System.out.println("✅ Queue stats exitoso");
    }
}