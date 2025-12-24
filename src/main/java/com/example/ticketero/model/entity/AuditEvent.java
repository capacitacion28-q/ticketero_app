package com.example.ticketero.model.entity;

import com.example.ticketero.model.enums.ActorType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad de auditoría que registra todos los eventos críticos del sistema.
 * 
 * Implementa: RF-008 (Auditoría y trazabilidad)
 * Reglas de Negocio: RN-011 (Auditoría obligatoria), RN-013 (Retención 7 años)
 * 
 * Características de seguridad:
 * - Hash de integridad automático en @PrePersist
 * - Almacenamiento JSONB para datos adicionales
 * - Trazabilidad completa de cambios de estado
 * 
 * Eventos auditados: TICKET_CREATED, TICKET_ASSIGNED, STATUS_CHANGED, etc.
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
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
    
    @Column(name = "additional_data", columnDefinition = "TEXT")
    private String additionalData;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "integrity_hash", nullable = false, length = 64)
    private String integrityHash;
    
    /**
     * RN-011: Callback JPA que genera hash de integridad automáticamente.
     * Ejecutado antes de persistir para garantizar trazabilidad.
     */
    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        // RN-011: Generar hash de integridad (simplificado para el ejemplo)
        integrityHash = generateIntegrityHash();
    }
    
    /**
     * RN-011: Genera hash de integridad para validación de auditoría.
     * Implementación simplificada - en producción usar SHA-256.
     * 
     * @return Hash de integridad basado en campos críticos
     */
    private String generateIntegrityHash() {
        // Implementación simplificada - en producción usar SHA-256
        return String.valueOf((eventType + actor + timestamp.toString()).hashCode());
    }
}