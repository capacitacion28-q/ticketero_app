package com.example.ticketero.model.entity;

import com.example.ticketero.model.enums.EntityType;
import com.example.ticketero.model.enums.EventType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 50)
    private EventType eventType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 50)
    private EntityType entityType;
    
    @Column(name = "entity_id")
    private Long entityId;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "old_values", columnDefinition = "jsonb")
    private Map<String, Object> oldValues;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_values", columnDefinition = "jsonb")
    private Map<String, Object> newValues;
    
    @Column(name = "user_id", length = 100)
    private String userId;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    /**
     * Verifica si el evento es crítico según RN-011
     * 
     * @return true si requiere auditoría obligatoria
     */
    public boolean isCritical() {
        return eventType.isCritical();
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
    
    /**
     * Crea un evento de auditoría para creación de entidad
     * 
     * @param entityType Tipo de entidad
     * @param entityId ID de la entidad
     * @param newValues Valores nuevos
     * @param userId Usuario que ejecutó la acción
     * @return AuditEvent configurado
     */
    public static AuditEvent createEvent(EntityType entityType, Long entityId, 
                                       Map<String, Object> newValues, String userId) {
        EventType eventType = switch (entityType) {
            case TICKET -> EventType.TICKET_CREATED;
            case ADVISOR -> EventType.ADVISOR_STATUS_CHANGED;
            case MENSAJE -> EventType.MESSAGE_SENT;
        };
        
        return AuditEvent.builder()
                .eventType(eventType)
                .entityType(entityType)
                .entityId(entityId)
                .newValues(newValues)
                .userId(userId)
                .build();
    }
    
    /**
     * Crea un evento de auditoría para actualización de entidad
     * 
     * @param eventType Tipo de evento específico
     * @param entityType Tipo de entidad
     * @param entityId ID de la entidad
     * @param oldValues Valores anteriores
     * @param newValues Valores nuevos
     * @param userId Usuario que ejecutó la acción
     * @return AuditEvent configurado
     */
    public static AuditEvent updateEvent(EventType eventType, EntityType entityType, 
                                       Long entityId, Map<String, Object> oldValues,
                                       Map<String, Object> newValues, String userId) {
        return AuditEvent.builder()
                .eventType(eventType)
                .entityType(entityType)
                .entityId(entityId)
                .oldValues(oldValues)
                .newValues(newValues)
                .userId(userId)
                .build();
    }
}