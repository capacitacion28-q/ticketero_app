package com.example.ticketero.model.entity;

import com.example.ticketero.model.enums.ActorType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity AuditEvent - Eventos de auditoría del sistema
 * 
 * Referencia: Plan Detallado Sección 8.2.1 - FASE 2
 * Diagrama ER: docs/architecture/diagrams/03-er-diagram.puml
 * RN-011: Auditoría obligatoria de eventos críticos
 * RN-013: Retención de auditoría por 7 años
 * 
 * @author Sistema Ticketero
 * @version 1.0
 */
@Entity
@Table(name = "audit_event")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;
    
    @Column(name = "actor", nullable = false, length = 100)
    private String actor;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false)
    private ActorType actorType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    @ToString.Exclude
    private Ticket ticket;
    
    @Column(name = "ticket_number", length = 10)
    private String ticketNumber;
    
    @Column(name = "previous_state", length = 20)
    private String previousState;
    
    @Column(name = "new_state", length = 20)
    private String newState;
    
    @Column(name = "additional_data", columnDefinition = "jsonb")
    private String additionalData;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "integrity_hash", nullable = false, length = 64)
    private String integrityHash;
    
    @PrePersist
    protected void onCreate() {
        if (integrityHash == null) {
            integrityHash = generateIntegrityHash();
        }
    }
    
    private String generateIntegrityHash() {
        return String.valueOf((eventType + actor + timestamp.toString()).hashCode());
    }
    
    /**
     * Verifica si el evento debe ser retenido según RN-013
     * 
     * @return true si debe mantenerse (menos de 7 años)
     */
    public boolean shouldBeRetained() {
        LocalDateTime retentionLimit = LocalDateTime.now().minusYears(7);
        return timestamp.isAfter(retentionLimit);
    }
}