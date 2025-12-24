package com.example.ticketero.integration;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Clase base para tests de integración E2E con Docker.
 * 
 * Configuración:
 * - PostgreSQL real via TestContainers
 * - Sin WireMock (evita conflictos)
 * - Contexto completo de Spring Boot
 * - Schedulers con intervalos reducidos
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-24
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
    "scheduler.message.fixed-rate=2000",
    "scheduler.queue.fixed-rate=1000",
    "telegram.enabled=false",
    "logging.level.com.example.ticketero=DEBUG"
})
public abstract class BaseDockerTest {

    @LocalServerPort
    protected int port;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("ticketero_test")
            .withUsername("test")
            .withPassword("test")
            .withExposedPorts(5432)
            .withEnv("POSTGRES_DB", "ticketero_test")
            .withEnv("POSTGRES_USER", "test")
            .withEnv("POSTGRES_PASSWORD", "test")
            .withReuse(false);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        if (!postgres.isRunning()) {
            postgres.start();
        }
    }

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }
}