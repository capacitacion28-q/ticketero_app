package com.example.ticketero.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests H2 para dashboard sin Docker.
 * Valida endpoints de dashboard con H2 en memoria.
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-24
 */
@DisplayName("H2 Tests: Dashboard")
class DashboardH2IT extends BaseH2SimpleTest {

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.baseURI = getBaseUrl();
        RestAssured.port = port;
    }

    @Test
    @DisplayName("RF-007: Dashboard summary responde correctamente")
    void dashboardSummary_respuestaCorrecta() {
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

    @Test
    @DisplayName("RF-007: Queue status por tipo de cola")
    void queueStatus_porTipoCola_respuestaCorrecta() {
        given()
        .when()
            .get("/api/queues/CAJA")
        .then()
            .statusCode(200)
            .body("queueType", equalTo("CAJA"))
            .body("displayName", notNullValue())
            .body("avgTime", greaterThanOrEqualTo(0))
            .body("priority", greaterThanOrEqualTo(0));
    }

    @Test
    @DisplayName("RF-008: Eventos de auditoría disponibles")
    void auditEvents_consultaBasica_respuestaCorrecta() {
        given()
            .queryParam("actor", "SYSTEM")
        .when()
            .get("/api/audit/events")
        .then()
            .statusCode(200)
            .body("$", notNullValue());
    }
}