package com.example.ticketero.model.entity;

import com.example.ticketero.model.enums.AdvisorStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity Advisor - Asesores/ejecutivos del Sistema Ticketero
 * 
 * Referencia: Plan Detallado Sección 8.2.1 - FASE 2
 * Diagrama ER: docs/architecture/diagrams/03-er-diagram.puml
 * RN-004: Balanceo de carga entre ejecutivos disponibles
 * 
 * @author Sistema Ticketero
 * @version 1.0
 */
@Entity
@Table(name = "advisor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advisor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private AdvisorStatus status = AdvisorStatus.AVAILABLE;
    
    @Column(name = "current_tickets", nullable = false)
    @Builder.Default
    private Integer currentTickets = 0;
    
    @Column(name = "max_concurrent_tickets", nullable = false)
    @Builder.Default
    private Integer maxConcurrentTickets = 3;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Verifica si el asesor puede recibir más tickets según RN-004
     * 
     * @return true si puede recibir tickets
     */
    public boolean canReceiveTickets() {
        return this.status != AdvisorStatus.OFFLINE;
    }
    
    /**
     * Calcula la carga actual del asesor para balanceo según RN-004
     * 
     * @return porcentaje de carga (0.0 a 1.0)
     */
    public double getCurrentLoad() {
        if (maxConcurrentTickets == 0) return 1.0;
        return (double) currentTickets / maxConcurrentTickets;
    }
    
    /**
     * Incrementa el contador de tickets asignados
     */
    public void assignTicket() {
        this.currentTickets++;
        if (this.currentTickets >= this.maxConcurrentTickets) {
            this.status = AdvisorStatus.BUSY;
        }
    }
    
    /**
     * Decrementa el contador de tickets asignados
     */
    public void releaseTicket() {
        if (this.currentTickets > 0) {
            this.currentTickets--;
            if (this.currentTickets < this.maxConcurrentTickets && this.status == AdvisorStatus.BUSY) {
                this.status = AdvisorStatus.AVAILABLE;
            }
        }
    }
}