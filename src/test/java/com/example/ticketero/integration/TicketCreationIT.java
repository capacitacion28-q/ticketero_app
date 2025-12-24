package com.example.ticketero.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests E2E para Feature: Creación de Tickets
 * 
 * Cobertura de Requerimientos:
 * - RF-001: Crear Ticket Digital
 * - RF-003: Calcular Posición y Tiempo Estimado
 * - RN-001: Unicidad ticket activo por cliente
 * - RN-005: Numeración secuencial con prefijo
 * - RN-006: Formato de número de ticket
 * 
 * Escenarios Gherkin Implementados:
 * - Happy Path: Creación exitosa con datos válidos
 * - Edge Case: Ticket duplicado para mismo RUT
 * - Error Handling: Validaciones de formato
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-23
 */
@SpringBootTest
@DisplayName("Feature: Creación de Tickets (RF-001, RF-003, RN-001, RN-005, RN-006)")
class TicketCreationIT extends BaseIntegrationTest {

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    @DisplayName("RF-001: Crear Ticket Digital")
    class CrearTicketDigital {

        @Test
        @DisplayName("@P0 @HappyPath @RF-001: Crear ticket con datos válidos genera número con prefijo")
        void crearTicket_datosValidos_generaNumeroConPrefijo() {
            // Given: Cliente con datos válidos quiere ticket para CAJA
            String requestBody = """
                {
                    "nationalId": "12345678-9",
                    "telefono": "+56987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente solicita crear ticket
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                .statusCode(201)
                // Then: Sistema genera ticket con prefijo C y número secuencial
                .body("numero", matchesPattern("C\\d{2}"))
                .body("queueType", equalTo("CAJA"))
                .body("status", equalTo("WAITING"))
                .body("positionInQueue", greaterThan(0))
                .body("estimatedWaitMinutes", greaterThan(0))
                .body("codigoReferencia", notNullValue())
                // And: Datos del cliente se almacenan correctamente
                .body("nationalId", equalTo("12345678-9"))
                .body("telefono", equalTo("+56987654321"))
                .body("branchOffice", equalTo("Centro"));
        }

        @Test
        @DisplayName("@P0 @HappyPath @RF-003: Cálculo de posición y tiempo estimado correcto")
        void crearTicket_calculaPosicionYTiempo_correctamente() {
            // Given: Cola PERSONAL_BANKER con tiempo promedio 15 minutos
            String requestBody = """
                {
                    "nationalId": "87654321-K",
                    "telefono": "+56912345678",
                    "branchOffice": "Norte",
                    "queueType": "PERSONAL_BANKER"
                }
                """;

            // When: Cliente solicita ticket
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                .statusCode(201)
                // Then: Sistema calcula posición y tiempo según RN-010
                .body("numero", matchesPattern("P\\d{2}"))
                .body("positionInQueue", greaterThan(0))
                // And: Tiempo estimado = posición × tiempo promedio cola
                .body("estimatedWaitMinutes", greaterThan(0));
        }
    }

    @Nested
    @DisplayName("RN-001: Unicidad Ticket Activo por Cliente")
    class UnicidadTicketActivo {

        @Test
        @DisplayName("@P1 @EdgeCase @RN-001: Rechazar ticket duplicado para mismo RUT")
        void crearTicket_rutConTicketActivo_rechazaConflicto() {
            // Given: Cliente ya tiene ticket activo
            String primerTicket = """
                {
                    "nationalId": "11111111-1",
                    "telefono": "+56911111111",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;
            
            given()
                .contentType(ContentType.JSON)
                .body(primerTicket)
            .when()
                .post("/api/tickets")
            .then()
                .statusCode(201);

            // When: Mismo cliente intenta crear segundo ticket
            String segundoTicket = """
                {
                    "nationalId": "11111111-1",
                    "telefono": "+56911111111",
                    "branchOffice": "Norte",
                    "queueType": "PERSONAL_BANKER"
                }
                """;

            given()
                .contentType(ContentType.JSON)
                .body(segundoTicket)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza con código 409 Conflict
                .statusCode(409)
                .body("code", equalTo("ACTIVE_TICKET_EXISTS"))
                .body("message", containsString("Ya existe un ticket activo para el RUT: 11111111-1"));
        }
    }

    @Nested
    @DisplayName("RN-005/006: Numeración Secuencial con Prefijos")
    class NumeracionConPrefijos {

        @Test
        @DisplayName("@P1 @EdgeCase @RN-005: Numeración secuencial por tipo de cola")
        void crearTickets_diferentesColas_numeracionSecuencial() {
            // Given: Diferentes tipos de cola
            String[] requests = {
                """
                {
                    "nationalId": "22222222-2",
                    "telefono": "+56922222222",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """,
                """
                {
                    "nationalId": "33333333-3",
                    "telefono": "+56933333333",
                    "branchOffice": "Centro",
                    "queueType": "EMPRESAS"
                }
                """,
                """
                {
                    "nationalId": "44444444-4",
                    "telefono": "+56944444444",
                    "branchOffice": "Centro",
                    "queueType": "GERENCIA"
                }
                """
            };

            String[] expectedPrefixes = {"C", "E", "G"};

            // When: Se crean tickets en diferentes colas
            for (int i = 0; i < requests.length; i++) {
                given()
                    .contentType(ContentType.JSON)
                    .body(requests[i])
                .when()
                    .post("/api/tickets")
                .then()
                    .statusCode(201)
                    // Then: Cada cola mantiene su prefijo específico
                    .body("numero", matchesPattern(expectedPrefixes[i] + "\\d{2}"));
            }
        }
    }

    @Nested
    @DisplayName("Validaciones de Input")
    class ValidacionesInput {

        @Test
        @DisplayName("@P2 @ErrorHandling: RUT inválido rechazado con 400")
        void crearTicket_rutInvalido_rechazaBadRequest() {
            // Given: RUT con formato inválido
            String requestBody = """
                {
                    "nationalId": "123456789",
                    "telefono": "+56987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente intenta crear ticket con RUT inválido
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza con validación de formato
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details.nationalId", containsString("Formato de RUT inválido"));
        }

        @Test
        @DisplayName("@P2 @ErrorHandling: Teléfono inválido rechazado con 400")
        void crearTicket_telefonoInvalido_rechazaBadRequest() {
            // Given: Teléfono sin formato chileno
            String requestBody = """
                {
                    "nationalId": "12345678-9",
                    "telefono": "987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente intenta crear ticket con teléfono inválido
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza con validación de formato
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details.telefono", containsString("Teléfono debe tener formato +56XXXXXXXXX"));
        }

        @Test
        @DisplayName("@P2 @ErrorHandling: Campos obligatorios faltantes rechazados")
        void crearTicket_camposFaltantes_rechazaBadRequest() {
            // Given: Request sin campos obligatorios
            String requestBody = """
                {
                    "nationalId": "",
                    "branchOffice": "Centro"
                }
                """;

            // When: Cliente intenta crear ticket incompleto
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza con múltiples errores de validación
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details", hasKey("nationalId"))
                .body("details", hasKey("telefono"))
                .body("details", hasKey("queueType"));
        }
    }
}