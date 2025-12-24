package com.example.ticketero.model.entity;

import com.example.ticketero.model.enums.AdvisorStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un asesor/ejecutivo del sistema de atención al cliente.
 * 
 * Implementa: RF-004 (Asignación automática de ejecutivos)
 * Reglas de Negocio: RN-004 (Balanceo de carga entre ejecutivos)
 * 
 * Funcionalidades clave:
 * - Balanceo automático de carga por assignedTicketsCount
 * - Control de disponibilidad por AdvisorStatus
 * - Asignación a módulos físicos (1-5)
 * 
 * Query crítica: findFirstByStatusOrderByAssignedTicketsCountAsc() para RN-004
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@Entity
@Table(name = "advisor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advisor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AdvisorStatus status = AdvisorStatus.AVAILABLE;
    
    @Column(name = "module_number", nullable = false)
    private Integer moduleNumber;
    
    @Column(name = "assigned_tickets_count", nullable = false)
    @Builder.Default
    private Integer assignedTicketsCount = 0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Callback JPA ejecutado antes de persistir la entidad.
     * Inicializa timestamps automáticos.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Callback JPA ejecutado antes de actualizar la entidad.
     * Actualiza timestamp de modificación.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * RN-004: Incrementa contador de tickets asignados para balanceo de carga.
     * Utilizado en asignación automática de tickets.
     */
    public void incrementAssignedTicketsCount() {
        this.assignedTicketsCount++;
    }
    
    /**
     * RN-004: Decrementa contador de tickets asignados para balanceo de carga.
     * Utilizado cuando se completa o cancela un ticket.
     */
    public void decrementAssignedTicketsCount() {
        if (this.assignedTicketsCount > 0) {
            this.assignedTicketsCount--;
        }
    }
}