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
 * Tests Docker simulados con H2 para evitar problemas de conectividad.
 * Simula comportamiento Docker sin TestContainers.
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-24
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:dockertest;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false",
    "scheduler.message.fixed-rate=2000",
    "scheduler.queue.fixed-rate=1000",
    "telegram.enabled=false"
})
@DisplayName("Docker Tests: Simulación sin TestContainers")
class TicketCreationDockerIT {

    @LocalServerPort
    protected int port;

    protected String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.baseURI = getBaseUrl();
        RestAssured.port = port;
    }

    @Test
    @DisplayName("RF-001: Crear ticket simulando Docker")
    void crearTicket_simulandoDocker_funcionaCorrectamente() {
        String ticketRequest = """
            {
                "titulo": "Consulta gerencial",
                "descripcion": "Consulta de alto nivel que requiere atención gerencial",
                "usuarioId": 4,
                "nationalId": "33333333-3",
                "telefono": "+56933333333",
                "branchOffice": "Sucursal Centro",
                "queueType": "GERENCIA"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(ticketRequest)
        .when()
            .post("/api/tickets")
        .then()
            .statusCode(201)
            .body("numero", matchesPattern("G\\d{2}"))
            .body("status", equalTo("WAITING"))
            .body("nationalId", equalTo("33333333-3"))
            .body("queueType", equalTo("GERENCIA"));
    }

    @Test
    @DisplayName("RN-001: Validación de duplicados simulando Docker")
    void crearTicket_validacionDuplicados_funcionaCorrectamente() {
        String ticketRequest = """
            {
                "titulo": "Operación de caja",
                "descripcion": "Operación bancaria que requiere atención en caja",
                "usuarioId": 5,
                "nationalId": "44444444-4",
                "telefono": "+56944444444",
                "branchOffice": "Sucursal Centro",
                "queueType": "CAJA"
            }
            """;

        // Crear primer ticket
        given()
            .contentType(ContentType.JSON)
            .body(ticketRequest)
        .when()
            .post("/api/tickets")
        .then()
            .statusCode(201);

        // Intentar crear segundo ticket - debe fallar
        given()
            .contentType(ContentType.JSON)
            .body(ticketRequest)
        .when()
            .post("/api/tickets")
        .then()
            .statusCode(409);
    }

    @Test
    @DisplayName("RF-007: Dashboard simulando Docker")
    void dashboard_simulandoDocker_respuestaCorrecta() {
        given()
        .when()
            .get("/api/dashboard/summary")
        .then()
            .statusCode(200)
            .body("timestamp", notNullValue())
            .body("ticketsActivos", greaterThanOrEqualTo(0))
            .body("ejecutivosDisponibles", greaterThanOrEqualTo(0))
            .body("estadoGeneral", notNullValue());
    }
}