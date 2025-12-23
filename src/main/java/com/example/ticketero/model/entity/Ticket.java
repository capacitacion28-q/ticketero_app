package com.example.ticketero.model.entity;

import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.model.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity Ticket - Tickets del sistema de colas
 * 
 * Referencia: Plan Detallado Sección 8.2.1 - FASE 2
 * Diagrama ER: docs/architecture/diagrams/03-er-diagram.puml
 * RN-001: Validación unicidad ticket activo por cliente
 * RN-005: Numeración secuencial por cola
 * RN-006: Prefijos por tipo de cola
 * 
 * @author Sistema Ticketero
 * @version 1.0
 */
@Entity
@Table(name = "ticket", 
       uniqueConstraints = @UniqueConstraint(name = "uk_active_client", 
                                           columnNames = "client_phone"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "queue_number", nullable = false, unique = true, length = 10)
    private String queueNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "queue_type", nullable = false, length = 20)
    private QueueType queueType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TicketStatus status = TicketStatus.WAITING;
    
    @Column(name = "client_name", nullable = false, length = 100)
    private String clientName;
    
    @Column(name = "client_phone", nullable = false, length = 20)
    private String clientPhone;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advisor_id")
    @ToString.Exclude
    private Advisor advisor;
    
    @Column(name = "position_in_queue")
    private Integer positionInQueue;
    
    @Column(name = "estimated_wait_time")
    private Integer estimatedWaitTime; // en minutos
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    // Relación 1:N con Mensajes
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Mensaje> mensajes = new ArrayList<>();
    
    /**
     * Verifica si el ticket está activo según RN-001
     * 
     * @return true si está en estado activo
     */
    public boolean isActive() {
        return status.isActive();
    }
    
    /**
     * Verifica si el ticket puede ser asignado a asesor
     * 
     * @return true si puede ser asignado
     */
    public boolean canBeAssigned() {
        return status.canAssignAdvisor();
    }
    
    /**
     * Asigna el ticket a un asesor
     * 
     * @param advisor Asesor a asignar
     */
    public void assignToAdvisor(Advisor advisor) {
        this.advisor = advisor;
        this.status = TicketStatus.IN_PROGRESS;
        this.assignedAt = LocalDateTime.now();
        this.positionInQueue = null; // Ya no está en cola
    }
    
    /**
     * Completa el ticket
     */
    public void complete() {
        this.status = TicketStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * Marca el ticket como NO_SHOW según RN-009
     */
    public void markAsNoShow() {
        this.status = TicketStatus.NO_SHOW;
        this.completedAt = LocalDateTime.now();
    }
    
    /**
     * Cancela el ticket
     */
    public void cancel() {
        this.status = TicketStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }
}