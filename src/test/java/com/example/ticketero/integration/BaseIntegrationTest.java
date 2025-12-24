package com.example.ticketero.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Clase base para tests de integración E2E del Sistema Ticketero.
 * 
 * Configuración:
 * - PostgreSQL real via TestContainers
 * - WireMock para Telegram API
 * - Contexto completo de Spring Boot
 * - Schedulers activos para testing asíncrono
 * 
 * Stack de Testing:
 * - JUnit 5 + TestContainers + WireMock + RestAssured + Awaitility
 * - Base de datos: PostgreSQL 15 (real)
 * - Telegram API: Mock en puerto 8089
 * - Schedulers: Intervalos reducidos para testing
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-23
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
    "scheduler.message.fixed-rate=2000",  // 2s para testing
    "scheduler.queue.fixed-rate=1000",    // 1s para testing
    "telegram.api-url=http://localhost:8089/bot",
    "logging.level.com.example.ticketero=DEBUG"
})
public abstract class BaseIntegrationTest {

    @LocalServerPort
    protected int port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("ticketero_test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);

    protected static WireMockServer wireMockServer;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("telegram.bot-token", () -> "test-token");
    }

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