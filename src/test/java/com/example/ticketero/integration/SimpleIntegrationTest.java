package com.example.ticketero.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests E2E simplificados para Sistema Ticketero
 * Usa H2 en lugar de TestContainers para ejecución rápida
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "telegram.enabled=false",
    "scheduler.message.fixed-rate=60000",
    "scheduler.queue.fixed-rate=60000"
})
@DisplayName("Tests E2E Simplificados - Sistema Ticketero")
class SimpleIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    @DisplayName("RF-001: Crear ticket con datos válidos")
    void crearTicket_datosValidos_exitoso() {
        String requestBody = """
            {
                "nationalId": "12345678-9",
                "telefono": "+56987654321",
                "branchOffice": "Centro",
                "queueType": "CAJA"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/tickets")
        .then()
            .statusCode(201)
            .body("numero", matchesPattern("C\\d{2}"))
            .body("queueType", equalTo("CAJA"))
            .body("status", equalTo("WAITING"))
            .body("nationalId", equalTo("12345678-9"));
    }

    @Test
    @DisplayName("RN-010: Validación RUT inválido")
    void crearTicket_rutInvalido_error400() {
        String requestBody = """
            {
                "nationalId": "123456789",
                "telefono": "+56987654321",
                "branchOffice": "Centro",
                "queueType": "CAJA"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(requestBody)
        .when()
            .post("/api/tickets")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("RF-007: Dashboard summary disponible")
    void dashboard_summary_disponible() {
        given()
        .when()
            .get("/api/dashboard/summary")
        .then()
            .statusCode(200)
            .body("timestamp", notNullValue())
            .body("updateInterval", equalTo(5));
    }

    @Test
    @DisplayName("RF-005: Consulta información de cola")
    void queues_consultaCaja_informacionCorrecta() {
        given()
        .when()
            .get("/api/queues/CAJA")
        .then()
            .statusCode(200)
            .body("queueType", equalTo("CAJA"))
            .body("displayName", equalTo("Caja"))
            .body("avgTime", equalTo(5));
    }

    @Test
    @DisplayName("RF-008: Consulta eventos de auditoría")
    void audit_events_disponible() {
        given()
        .when()
            .get("/api/audit/events")
        .then()
            .statusCode(200)
            .body("content", notNullValue());
    }
}