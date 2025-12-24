package com.example.ticketero.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Clase base alternativa para tests E2E sin Docker.
 * 
 * Configuración:
 * - H2 Database en memoria (sin índices condicionales)
 * - WireMock para Telegram API
 * - Contexto completo de Spring Boot
 * - Schedulers activos para testing asíncrono
 * 
 * Limitaciones:
 * - RN-001 (unicidad) validada por lógica de negocio, no por BD
 * - Menos realista que PostgreSQL real
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-23
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("h2")
@TestPropertySource(properties = {
    "scheduler.message.fixed-rate=2000",
    "scheduler.queue.fixed-rate=1000",
    "telegram.api-url=http://localhost:8089/bot",
    "logging.level.com.example.ticketero=DEBUG"
})
public abstract class BaseH2IntegrationTest {

    @LocalServerPort
    protected int port;

    protected static WireMockServer wireMockServer;

    @BeforeAll
    static void beforeAll() {
        // Iniciar WireMock para Telegram API
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8089);
    }

    @AfterAll
    static void afterAll() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setUp() {
        // Reset WireMock stubs antes de cada test
        wireMockServer.resetAll();
        
        // Stub por defecto para Telegram API
        stubFor(post(urlPathMatching("/bot/test-token/sendMessage"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"ok\":true,\"result\":{\"message_id\":\"123\"}}")));
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}