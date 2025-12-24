package com.example.ticketero.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Tests E2E para dashboard administrativo corregidos.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test-docker")
@DisplayName("E2E Tests: Dashboard Administrativo")
class AdminDashboardE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @DisplayName("Dashboard summary - Endpoint corregido")
    void dashboardSummary_endpointCorregido() {
        given()
        .when()
            .get("/api/dashboard/summary")
        .then()
            .statusCode(200)
            .body(notNullValue());
    }

    @Test
    @DisplayName("Health check - Validación básica")
    void healthCheck_validacionBasica() {
        given()
        .when()
            .get("/actuator/health")
        .then()
            .statusCode(200)
            .body("status", equalTo("UP"));
    }
}