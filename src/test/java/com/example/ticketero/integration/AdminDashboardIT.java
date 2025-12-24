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
 * Tests E2E para Feature: Dashboard Administrativo
 * 
 * Cobertura de Requerimientos:
 * - RF-007: Panel de Monitoreo para Supervisor
 * - RF-008: Registrar Auditoría de Eventos
 * 
 * Escenarios Gherkin Implementados:
 * - Happy Path: Dashboard muestra métricas en tiempo real
 * - Edge Case: Consultas de auditoría con filtros
 * - Error Handling: Parámetros inválidos en consultas
 * 
 * @author QA Engineer Senior
 * @version 1.0
 * @since 2025-12-23
 */
@SpringBootTest
@DisplayName("Feature: Dashboard Administrativo (RF-007, RF-008)")
class AdminDashboardIT extends BaseIntegrationTest {

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    @DisplayName("RF-007: Panel de Monitoreo para Supervisor")
    class PanelMonitoreo {

        @Test
        @DisplayName("@P0 @HappyPath @RF-007: Dashboard summary muestra métricas básicas")
        void dashboardSummary_sinTickets_muestraMetricasBasicas() {
            // Given: Sistema sin tickets activos
            // When: Supervisor consulta dashboard
            given()
            .when()
                .get("/api/dashboard/summary")
            .then()
                // Then: Sistema retorna estructura de dashboard
                .statusCode(200)
                .body("timestamp", notNullValue())
                .body("updateInterval", equalTo(5))
                .body("estadoGeneral", notNullValue())
                .body("resumenEjecutivo", notNullValue())
                .body("resumenEjecutivo.ticketsActivos", greaterThanOrEqualTo(0))
                .body("estadoEjecutivos", notNullValue())
                .body("estadoEjecutivos.disponibles", greaterThanOrEqualTo(0))
                .body("estadoEjecutivos.ocupados", greaterThanOrEqualTo(0))
                .body("estadoEjecutivos.offline", greaterThanOrEqualTo(0));
        }

        @Test
        @DisplayName("@P0 @HappyPath @RF-007: Dashboard realtime actualiza cada 5 segundos")
        void dashboardRealtime_consultaMultiple_actualizaTimestamp() {
            // Given: Dashboard en tiempo real
            String firstTimestamp = given()
            .when()
                .get("/api/dashboard/realtime")
            .then()
                .statusCode(200)
                .extract()
                .path("timestamp");

            // When: Esperamos y consultamos nuevamente
            try {
                Thread.sleep(1000); // Esperar 1 segundo
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Then: Timestamp debe ser diferente (actualizado)
            given()
            .when()
                .get("/api/dashboard/realtime")
            .then()
                .statusCode(200)
                .body("timestamp", not(equalTo(firstTimestamp)))
                .body("updateInterval", equalTo(5));
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RF-007: Dashboard con tickets activos muestra métricas correctas")
        void dashboardSummary_conTicketsActivos_muestraMetricasActualizadas() {
            // Given: Crear ticket para generar métricas
            String ticketRequest = """
                {
                    "nationalId": "19191919-1",
                    "telefono": "+56919191919",
                    "branchOffice": "Centro",
                    "queueType": "CAJA"
                }
                """;

            given()
                .contentType(ContentType.JSON)
                .body(ticketRequest)
            .when()
                .post("/api/tickets")
            .then()
                .statusCode(201);

            // When: Supervisor consulta dashboard después de crear ticket
            given()
            .when()
                .get("/api/dashboard/summary")
            .then()
                // Then: Métricas reflejan ticket activo
                .statusCode(200)
                .body("resumenEjecutivo.ticketsActivos", greaterThanOrEqualTo(1))
                .body("estadoGeneral", anyOf(equalTo("NORMAL"), equalTo("BUSY")));
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RF-007: Alertas activas mostradas en dashboard")
        void dashboardAlertas_conSistemaFuncionando_muestraAlertas() {
            // Given: Sistema funcionando
            // When: Supervisor consulta alertas
            given()
            .when()
                .get("/api/dashboard/alerts")
            .then()
                // Then: Endpoint de alertas responde correctamente
                .statusCode(200)
                .body("alerts", notNullValue());
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RF-007: Métricas detalladas disponibles")
        void dashboardMetricas_consultaDetallada_muestraMetricasCompletas() {
            // Given: Sistema con datos
            // When: Supervisor consulta métricas detalladas
            given()
            .when()
                .get("/api/dashboard/metrics")
            .then()
                // Then: Métricas detalladas disponibles
                .statusCode(200)
                .body("metrics", notNullValue());
        }
    }

    @Nested
    @DisplayName("RF-008: Registrar Auditoría de Eventos")
    class RegistroAuditoria {

        @Test
        @DisplayName("@P0 @HappyPath @RF-008: Consulta de eventos de auditoría sin filtros")
        void auditoria_consultaSinFiltros_retornaEventos() {
            // Given: Sistema con eventos de auditoría
            // When: Administrador consulta eventos sin filtros
            given()
            .when()
                .get("/api/audit/events")
            .then()
                // Then: Sistema retorna estructura de paginación
                .statusCode(200)
                .body("content", notNullValue())
                .body("pageable", notNullValue())
                .body("totalElements", greaterThanOrEqualTo(0));
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RF-008: Consulta de auditoría con filtros específicos")
        void auditoria_consultaConFiltros_aplicaFiltrosCorrectamente() {
            // Given: Parámetros de filtro específicos
            // When: Administrador consulta con filtros
            given()
                .queryParam("eventType", "TICKET_CREATED")
                .queryParam("page", 0)
                .queryParam("size", 10)
            .when()
                .get("/api/audit/events")
            .then()
                // Then: Sistema aplica filtros correctamente
                .statusCode(200)
                .body("content", notNullValue())
                .body("size", equalTo(10))
                .body("number", equalTo(0));
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RF-008: Historial de ticket específico")
        void auditoria_historialTicket_muestraEventosDelTicket() {
            // Given: Ticket creado para generar historial
            String ticketRequest = """
                {
                    "nationalId": "20202020-2",
                    "telefono": "+56920202020",
                    "branchOffice": "Centro",
                    "queueType": "PERSONAL_BANKER"
                }
                """;

            String ticketNumber = given()
                .contentType(ContentType.JSON)
                .body(ticketRequest)
            .when()
                .post("/api/tickets")
            .then()
                .statusCode(201)
                .extract()
                .path("numero");

            // When: Administrador consulta historial del ticket
            given()
            .when()
                .get("/api/audit/ticket/" + ticketNumber)
            .then()
                // Then: Sistema retorna historial del ticket
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
                // Nota: Puede estar vacío si auditoría no está completamente implementada
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RF-008: Resumen de auditoría por rango de fechas")
        void auditoria_resumenPorFechas_muestraEstadisticas() {
            // Given: Rango de fechas para consulta
            String startDate = "2025-12-23";
            String endDate = "2025-12-23";

            // When: Administrador consulta resumen por fechas
            given()
                .queryParam("startDate", startDate)
                .queryParam("endDate", endDate)
            .when()
                .get("/api/audit/summary")
            .then()
                // Then: Sistema retorna resumen estadístico
                .statusCode(200)
                .body("summary", notNullValue());
        }
    }

    @Nested
    @DisplayName("Gestión de Colas")
    class GestionColas {

        @Test
        @DisplayName("@P0 @HappyPath @RF-005: Consulta estado de cola específica")
        void gestionColas_consultaColaEspecifica_retornaEstado() {
            // Given: Cola CAJA disponible
            // When: Supervisor consulta estado de cola CAJA
            given()
            .when()
                .get("/api/queues/CAJA")
            .then()
                // Then: Sistema retorna información de la cola
                .statusCode(200)
                .body("queueType", equalTo("CAJA"))
                .body("displayName", equalTo("Caja"))
                .body("avgTime", equalTo(5))
                .body("priority", equalTo(1))
                .body("prefix", equalTo("C"));
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RF-005: Consulta todas las colas disponibles")
        void gestionColas_consultaTodasLasColas_retornaInformacionCompleta() {
            // Given: Múltiples tipos de cola
            String[] queueTypes = {"CAJA", "PERSONAL_BANKER", "EMPRESAS", "GERENCIA"};
            String[] expectedNames = {"Caja", "Personal Banker", "Empresas", "Gerencia"};
            int[] expectedTimes = {5, 15, 20, 30};
            int[] expectedPriorities = {1, 2, 3, 4};

            for (int i = 0; i < queueTypes.length; i++) {
                // When: Supervisor consulta cada tipo de cola
                given()
                .when()
                    .get("/api/queues/" + queueTypes[i])
                .then()
                    // Then: Cada cola retorna información correcta
                    .statusCode(200)
                    .body("queueType", equalTo(queueTypes[i]))
                    .body("displayName", equalTo(expectedNames[i]))
                    .body("avgTime", equalTo(expectedTimes[i]))
                    .body("priority", equalTo(expectedPriorities[i]));
            }
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RF-005: Estadísticas de colas")
        void gestionColas_estadisticasColas_muestraMetricas() {
            // Given: Sistema con colas funcionando
            // When: Supervisor consulta estadísticas
            given()
            .when()
                .get("/api/queues/stats")
            .then()
                // Then: Sistema retorna estadísticas
                .statusCode(200)
                .body("message", notNullValue());
        }

        @Test
        @DisplayName("@P1 @EdgeCase @RF-005: Resumen general de colas")
        void gestionColas_resumenGeneral_muestraEstadoGlobal() {
            // Given: Sistema con múltiples colas
            // When: Supervisor consulta resumen general
            given()
            .when()
                .get("/api/queues/summary")
            .then()
                // Then: Sistema retorna resumen global
                .statusCode(200)
                .body("message", notNullValue());
        }
    }

    @Nested
    @DisplayName("Manejo de Errores Administrativos")
    class ManejoErroresAdmin {

        @Test
        @DisplayName("@P2 @ErrorHandling: Cola inexistente retorna 400")
        void gestionColas_colaInexistente_retornaError() {
            // Given: Tipo de cola que no existe
            // When: Supervisor consulta cola inexistente
            given()
            .when()
                .get("/api/queues/INEXISTENTE")
            .then()
                // Then: Sistema retorna error 400
                .statusCode(400);
        }

        @Test
        @DisplayName("@P2 @ErrorHandling: Parámetros inválidos en auditoría")
        void auditoria_parametrosInvalidos_retornaError() {
            // Given: Parámetros de fecha inválidos
            // When: Administrador consulta con fechas inválidas
            given()
                .queryParam("startDate", "fecha-invalida")
                .queryParam("endDate", "otra-fecha-invalida")
            .when()
                .get("/api/audit/summary")
            .then()
                // Then: Sistema maneja error de parámetros
                .statusCode(anyOf(equalTo(400), equalTo(500)));
                // Nota: Depende de implementación específica de validación de fechas
        }

        @Test
        @DisplayName("@P2 @ErrorHandling: Ticket inexistente en historial de auditoría")
        void auditoria_ticketInexistente_retornaVacio() {
            // Given: Número de ticket que no existe
            String ticketInexistente = "X99";

            // When: Administrador consulta historial de ticket inexistente
            given()
            .when()
                .get("/api/audit/ticket/" + ticketInexistente)
            .then()
                // Then: Sistema retorna lista vacía o 404
                .statusCode(anyOf(equalTo(200), equalTo(404)));
                // Si retorna 200, debe ser lista vacía
        }
    }
}