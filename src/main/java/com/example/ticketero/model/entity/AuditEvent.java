package com.example.ticketero.model.entity;

import com.example.ticketero.model.enums.ActorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "timestamp", nullable = false)
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
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        // Generar hash de integridad (simplificado para el ejemplo)
        integrityHash = generateIntegrityHash();
    }
    
    private String generateIntegrityHash() {
        // Implementación simplificada - en producción usar SHA-256
        return String.valueOf((eventType + actor + timestamp.toString()).hashCode());
    }
}