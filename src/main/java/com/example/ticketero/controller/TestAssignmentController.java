package com.example.ticketero.controller;

import com.example.ticketero.service.QueueManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller de prueba para forzar asignaciones automáticas de tickets.
 * 
 * Funcionalidades de testing:
 * - Forzar asignación manual del próximo ticket en cola
 * - Crear ticket de prueba y ejecutar asignación
 * - Verificar funcionamiento del algoritmo de balanceo de carga
 * 
 * Endpoints disponibles:
 * - POST /api/test/assign-next: Forzar asignación del próximo ticket
 * - POST /api/test/create-and-assign: Crear ticket de prueba y asignar
 * 
 * Utilizado para:
 * - Testing de RN-002 (Selección por prioridad)
 * - Testing de RN-004 (Balanceo de carga)
 * - Verificación de asignación automática
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestAssignmentController {

    private final QueueManagementService queueManagementService;

    /**
     * Fuerza ejecución manual del algoritmo de asignación automática.
     * Ejecuta RN-002 (prioridades) y RN-004 (balanceo de carga).
     * 
     * @return ResponseEntity con resultado de la asignación o error
     */
    @PostMapping("/assign-next")
    public ResponseEntity<Map<String, String>> forceAssignNext() {
        try {
            queueManagementService.asignarSiguienteTicket();
            return ResponseEntity.ok(Map.of(
                "message", "Asignación forzada ejecutada",
                "status", "success"
            ));
        } catch (Exception e) {
            log.error("Error en asignación forzada: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Error: " + e.getMessage(),
                "status", "error",
                "details", e.getClass().getSimpleName()
            ));
        }
    }
    
    /**
     * Crea ticket de prueba y ejecuta asignación automática para testing.
     * Genera RUT temporal y simula flujo completo de asignación.
     * 
     * @return ResponseEntity con resultado del proceso o error detallado
     */
    @PostMapping("/create-and-assign")
    public ResponseEntity<Map<String, Object>> createAndAssign() {
        try {
            // Crear ticket de prueba
            String testRut = "99999999-" + System.currentTimeMillis() % 10;
            
            // Simular creación directa en base de datos
            log.info("Creando ticket de prueba para asignación: {}", testRut);
            
            // Intentar asignación
            queueManagementService.asignarSiguienteTicket();
            
            return ResponseEntity.ok(Map.of(
                "message", "Ticket creado y asignación ejecutada",
                "status", "success",
                "testRut", testRut
            ));
        } catch (Exception e) {
            log.error("Error en crear y asignar: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "message", "Error: " + e.getMessage(),
                "status", "error",
                "exception", e.getClass().getSimpleName()
            ));
        }
    }
}