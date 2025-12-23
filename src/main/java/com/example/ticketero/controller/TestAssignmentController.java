package com.example.ticketero.controller;

import com.example.ticketero.service.QueueManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller de prueba para forzar asignaciones
 */
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Slf4j
public class TestAssignmentController {

    private final QueueManagementService queueManagementService;

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