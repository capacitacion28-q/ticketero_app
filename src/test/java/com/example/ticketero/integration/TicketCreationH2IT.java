package com.example.ticketero.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests H2 para creación de tickets sin Docker.
 * Valida funcionalidad básica con H2 en memoria.
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-24
 */
@DisplayName("H2 Tests: Creación de Tickets")
class TicketCreationH2IT extends BaseH2SimpleTest {

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.baseURI = getBaseUrl();
        RestAssured.port = port;
    }

    @Test
    @DisplayName("RF-001: Crear ticket válido genera número con prefijo")
    void crearTicket_datosValidos_generaNumeroConPrefijo() {
        String ticketRequest = """
            {
                "titulo": "Consulta sobre cuenta corriente",
                "descripcion": "Necesito información sobre mi cuenta corriente y movimientos recientes",
                "usuarioId": 1,
                "nationalId": "12345678-9",
                "telefono": "+56987654321",
                "branchOffice": "Sucursal Centro",
                "queueType": "CAJA"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(ticketRequest)
        .when()
            .post("/api/tickets")
        .then()
            .statusCode(201)
            .body("numero", matchesPattern("C\\d{2}"))
            .body("status", equalTo("WAITING"))
            .body("nationalId", equalTo("12345678-9"))
            .body("queueType", equalTo("CAJA"))
            .body("codigoReferencia", notNullValue())
            .body("createdAt", notNullValue());
    }

    @Test
    @DisplayName("RF-003: Calcular posición y tiempo estimado")
    void crearTicket_calculaPosicionYTiempo() {
        String ticketRequest = """
            {
                "titulo": "Asesoría financiera personal",
                "descripcion": "Requiero asesoría para planificación financiera y productos de inversión",
                "usuarioId": 2,
                "nationalId": "11111111-1",
                "telefono": "+56911111111",
                "branchOffice": "Sucursal Centro",
                "queueType": "PERSONAL_BANKER"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(ticketRequest)
        .when()
            .post("/api/tickets")
        .then()
            .statusCode(201)
            .body("numero", matchesPattern("P\\d{2}"))
            .body("positionInQueue", notNullValue())
            .body("estimatedWaitMinutes", notNullValue());
    }

    @Test
    @DisplayName("RN-001: Rechazar ticket duplicado para mismo RUT")
    void crearTicket_rutConTicketActivo_rechazaDuplicado() {
        String ticketRequest = """
            {
                "titulo": "Consulta empresarial",
                "descripcion": "Consulta sobre productos y servicios para empresas",
                "usuarioId": 3,
                "nationalId": "22222222-2",
                "telefono": "+56922222222",
                "branchOffice": "Sucursal Centro",
                "queueType": "EMPRESAS"
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

        // Intentar crear segundo ticket con mismo RUT
        given()
            .contentType(ContentType.JSON)
            .body(ticketRequest)
        .when()
            .post("/api/tickets")
        .then()
            .statusCode(409);
    }
}