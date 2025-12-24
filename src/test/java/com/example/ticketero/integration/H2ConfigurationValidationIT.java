package com.example.ticketero.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Test de validación de configuración H2.
 * Verifica que la infraestructura básica funcione sin Docker.
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-23
 */
@DisplayName("Validación de Configuración H2")
class H2ConfigurationValidationIT extends BaseH2SimpleTest {

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.baseURI = getBaseUrl();
        RestAssured.port = port;
    }

    @Test
    @DisplayName("Crear ticket básico con H2 - Validar que no falla por índice condicional")
    void crearTicket_configuracionH2_noFallaPorIndice() {
        // Given: Datos válidos de ticket
        String ticketRequest = """
            {
                "nationalId": "12345678-9",
                "telefono": "+56987654321",
                "branchOffice": "Sucursal Centro",
                "queueType": "CAJA"
            }
            """;

        // When: Crear ticket
        given()
            .contentType(ContentType.JSON)
            .body(ticketRequest)
        .when()
            .post("/api/tickets")
        .then()
            .statusCode(201)
            .body("numero", matchesPattern("C\\d{3}"))
            .body("status", equalTo("WAITING"))
            .body("nationalId", equalTo("12345678-9"))
            .body("queueType", equalTo("CAJA"));
    }

    @Test
    @DisplayName("Validar que endpoints básicos responden")
    void endpoints_basicos_responden() {
        // Dashboard summary
        given()
        .when()
            .get("/api/dashboard/summary")
        .then()
            .statusCode(200)
            .body("totalTickets", greaterThanOrEqualTo(0));

        // Queue status
        given()
        .when()
            .get("/api/queues/CAJA")
        .then()
            .statusCode(200)
            .body("queueType", equalTo("CAJA"));
    }
}