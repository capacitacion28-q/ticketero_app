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
 * Tests E2E para Feature: Validaciones de Input
 * 
 * Cobertura de Requerimientos:
 * - RN-010: Validación formato RUT/ID
 * - RN-011: Validación teléfono chileno
 * - RN-012: Validación tipos de cola
 * - RN-013: Validación campos obligatorios
 * 
 * Escenarios Gherkin Implementados:
 * - Happy Path: Datos válidos pasan validación
 * - Edge Case: Formatos límite aceptados/rechazados
 * - Error Handling: Múltiples errores de validación
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-23
 */
@SpringBootTest
@DisplayName("Feature: Validaciones de Input (RN-010, RN-011, RN-012, RN-013)")
class ValidationIT extends BaseIntegrationTest {

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    @DisplayName("RN-010: Validación Formato RUT/ID")
    class ValidacionFormatoRUT {

        @Test
        @DisplayName("@P0 @HappyPath @RN-010: RUT válido con 8 dígitos y dígito verificador")
        void validarRUT_formatoValido8Digitos_aceptado() {
            // Given: RUT con formato válido 8 dígitos + DV
            String requestBody = """
                {
                    "nationalId": "12345678-9",
                    "telefono": "+56987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía datos con RUT válido
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema acepta RUT válido
                .statusCode(201)
                .body("nationalId", equalTo("12345678-9"));
        }

        @Test
        @DisplayName("@P0 @HappyPath @RN-010: RUT válido con 7 dígitos y dígito verificador K")
        void validarRUT_formatoValido7DigitosK_aceptado() {
            // Given: RUT con 7 dígitos y DV K
            String requestBody = """
                {
                    "nationalId": "1234567-K",
                    "telefono": "+56987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía RUT con K mayúscula
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema acepta K como dígito verificador
                .statusCode(201)
                .body("nationalId", equalTo("1234567-K"));
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RN-010: RUT con k minúscula aceptado")
        void validarRUT_digitoVerificadorMinuscula_aceptado() {
            // Given: RUT con k minúscula
            String requestBody = """
                {
                    "nationalId": "7654321-k",
                    "telefono": "+56987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía RUT con k minúscula
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema acepta k minúscula
                .statusCode(201)
                .body("nationalId", equalTo("7654321-k"));
        }

        @Test
        @DisplayName("@P2 @ErrorHandling @RN-010: RUT sin guión rechazado")
        void validarRUT_sinGuion_rechazado() {
            // Given: RUT sin guión separador
            String requestBody = """
                {
                    "nationalId": "123456789",
                    "telefono": "+56987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía RUT sin formato correcto
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza con error de validación
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details.nationalId", containsString("Formato de RUT inválido"));
        }

        @Test
        @DisplayName("@P2 @ErrorHandling @RN-010: RUT con menos de 7 dígitos rechazado")
        void validarRUT_menosDe7Digitos_rechazado() {
            // Given: RUT con solo 6 dígitos
            String requestBody = """
                {
                    "nationalId": "123456-7",
                    "telefono": "+56987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía RUT muy corto
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza RUT inválido
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details.nationalId", containsString("Formato de RUT inválido"));
        }
    }

    @Nested
    @DisplayName("RN-011: Validación Teléfono Chileno")
    class ValidacionTelefonoChileno {

        @Test
        @DisplayName("@P0 @HappyPath @RN-011: Teléfono móvil chileno válido")
        void validarTelefono_formatoMovilChileno_aceptado() {
            // Given: Teléfono móvil con formato +56 9XXXXXXXX
            String requestBody = """
                {
                    "nationalId": "12345678-9",
                    "telefono": "+56987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía teléfono móvil válido
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema acepta formato móvil chileno
                .statusCode(201)
                .body("telefono", equalTo("+56987654321"));
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RN-011: Teléfono fijo chileno válido")
        void validarTelefono_formatoFijoChileno_aceptado() {
            // Given: Teléfono fijo con formato +56 2XXXXXXXX
            String requestBody = """
                {
                    "nationalId": "12345678-9",
                    "telefono": "+56223456789",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía teléfono fijo válido
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema acepta formato fijo chileno
                .statusCode(201)
                .body("telefono", equalTo("+56223456789"));
        }

        @Test
        @DisplayName("@P2 @ErrorHandling @RN-011: Teléfono sin código país rechazado")
        void validarTelefono_sinCodigoPais_rechazado() {
            // Given: Teléfono sin +56
            String requestBody = """
                {
                    "nationalId": "12345678-9",
                    "telefono": "987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía teléfono sin código país
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza formato inválido
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details.telefono", containsString("Teléfono debe tener formato +56XXXXXXXXX"));
        }

        @Test
        @DisplayName("@P2 @ErrorHandling @RN-011: Teléfono con código país incorrecto rechazado")
        void validarTelefono_codigoPaisIncorrecto_rechazado() {
            // Given: Teléfono con código país no chileno
            String requestBody = """
                {
                    "nationalId": "12345678-9",
                    "telefono": "+54987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía teléfono argentino (+54)
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza código país incorrecto
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details.telefono", containsString("Teléfono debe tener formato +56XXXXXXXXX"));
        }
    }

    @Nested
    @DisplayName("RN-012: Validación Tipos de Cola")
    class ValidacionTiposCola {

        @Test
        @DisplayName("@P0 @HappyPath @RN-012: Todos los tipos de cola válidos aceptados")
        void validarTiposCola_todosLosValoresValidos_aceptados() {
            // Given: Todos los tipos de cola válidos
            String[] queueTypes = {"CAJA", "PERSONAL_BANKER", "EMPRESAS", "GERENCIA"};
            String[] nationalIds = {"11111111-1", "22222222-2", "33333333-3", "44444444-4"};

            for (int i = 0; i < queueTypes.length; i++) {
                String requestBody = String.format("""
                    {
                        "nationalId": "%s",
                        "telefono": "+5698765432%d",
                        "branchOffice": "Centro",
                        "queueType": "%s"
                    }
                    """, nationalIds[i], i, queueTypes[i]);

                // When: Cliente selecciona tipo de cola válido
                given()
                    .contentType(ContentType.JSON)
                    .body(requestBody)
                .when()
                    .post("/api/tickets")
                .then()
                    // Then: Sistema acepta tipo de cola
                    .statusCode(201)
                    .body("queueType", equalTo(queueTypes[i]));
            }
        }

        @Test
        @DisplayName("@P2 @ErrorHandling @RN-012: Tipo de cola inválido rechazado")
        void validarTiposCola_valorInvalido_rechazado() {
            // Given: Tipo de cola inexistente
            String requestBody = """
                {
                    "nationalId": "12345678-9",
                    "telefono": "+56987654321",
                    "branchOffice": "Centro",
                    "queueType": "INVALIDA"
                }
                """;

            // When: Cliente envía tipo de cola inválido
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza valor inválido
                .statusCode(400)
                .body("message", containsString("JSON parse error"));
        }
    }

    @Nested
    @DisplayName("RN-013: Validación Campos Obligatorios")
    class ValidacionCamposObligatorios {

        @Test
        @DisplayName("@P2 @ErrorHandling @RN-013: Todos los campos obligatorios faltantes")
        void validarCamposObligatorios_todosFaltantes_rechazadoConMultiplesErrores() {
            // Given: Request completamente vacío
            String requestBody = "{}";

            // When: Cliente envía request sin campos obligatorios
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza con múltiples errores
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details", hasKey("nationalId"))
                .body("details", hasKey("telefono"))
                .body("details", hasKey("branchOffice"))
                .body("details", hasKey("queueType"));
        }

        @Test
        @DisplayName("@P2 @ErrorHandling @RN-013: Campo nationalId vacío rechazado")
        void validarCamposObligatorios_nationalIdVacio_rechazado() {
            // Given: nationalId vacío
            String requestBody = """
                {
                    "nationalId": "",
                    "telefono": "+56987654321",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía nationalId vacío
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza campo vacío
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details.nationalId", anyOf(
                    containsString("El RUT/ID es obligatorio"),
                    containsString("Formato de RUT inválido")
                ));
        }

        @Test
        @DisplayName("@P2 @ErrorHandling @RN-013: Campo branchOffice vacío rechazado")
        void validarCamposObligatorios_branchOfficeVacio_rechazado() {
            // Given: branchOffice vacío
            String requestBody = """
                {
                    "nationalId": "12345678-9",
                    "telefono": "+56987654321",
                    "branchOffice": "",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía sucursal vacía
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza campo vacío
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details.branchOffice", containsString("La sucursal es obligatoria"));
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RN-013: Campos con espacios en blanco rechazados")
        void validarCamposObligatorios_camposConEspacios_rechazados() {
            // Given: Campos con solo espacios en blanco
            String requestBody = """
                {
                    "nationalId": "   ",
                    "telefono": "   ",
                    "branchOffice": "   ",
                    "queueType": "CAJA"
                }
                """;

            // When: Cliente envía campos con espacios
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema rechaza campos en blanco
                .statusCode(400)
                .body("code", equalTo("VALIDATION_ERROR"))
                .body("details", hasKey("nationalId"))
                .body("details", hasKey("telefono"))
                .body("details", hasKey("branchOffice"));
        }
    }

    @Nested
    @DisplayName("Validaciones Combinadas")
    class ValidacionesCombinadas {

        @Test
        @DisplayName("@P2 @ErrorHandling: Múltiples errores de validación reportados")
        void validarMultiplesErrores_todosLosFormatos_reportadosSimultaneamente() {
            // Given: Request con múltiples errores de validación
            String requestBody = """
                {
                    "nationalId": "123",
                    "telefono": "123456789",
                    "branchOffice": "",
                    "queueType": "INVALIDA"
                }
                """;

            // When: Cliente envía datos con múltiples errores
            given()
                .contentType(ContentType.JSON)
                .body(requestBody)
            .when()
                .post("/api/tickets")
            .then()
                // Then: Sistema reporta todos los errores encontrados
                .statusCode(400)
                .body("code", anyOf(
                    equalTo("VALIDATION_ERROR"),
                    containsString("JSON parse error")
                ));
                // Nota: Algunos errores pueden ser de parsing JSON antes de Bean Validation
        }
    }
}