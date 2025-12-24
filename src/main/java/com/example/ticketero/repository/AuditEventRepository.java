package com.example.ticketero.repository;

import com.example.ticketero.model.entity.AuditEvent;
import com.example.ticketero.model.enums.ActorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para entidad AuditEvent con queries especializadas para trazabilidad y cumplimiento.
 * 
 * Implementa: RF-008 (Auditoría y trazabilidad)
 * Reglas de Negocio: RN-011 (Auditoría obligatoria), RN-013 (Retención 7 años)
 * 
 * Queries críticas para auditoría:
 * - findByTicketNumberOrderByTimestampDesc: Trazabilidad completa por ticket
 * - getEventTypeStatistics: Estadísticas de eventos para cumplimiento
 * - getActorActivityStats: Monitoreo de actividad por actor
 * 
 * Eventos auditados: TICKET_CREATED, TICKET_ASSIGNED, STATUS_CHANGED, NOTIFICATION_SENT
 * Retención: 7 años según configuración audit.retention-days: 2555
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    
    /**
     * RF-008: Query crítica para trazabilidad completa de un ticket.
     * Utilizada por AuditController para historial detallado.
     * 
     * @param ticketNumber Número del ticket
     * @return Lista de eventos ordenados por timestamp descendente (más reciente primero)
     */
    // Eventos por ticket
    List<AuditEvent> findByTicketNumberOrderByTimestampDesc(String ticketNumber);
    
    /**
     * RF-008: Consulta eventos por actor para monitoreo de actividad.
     * 
     * @param actor Identificador del actor (RUT, email, SYSTEM)
     * @return Lista de eventos del actor ordenados por timestamp descendente
     */
    // Eventos por actor
    List<AuditEvent> findByActorOrderByTimestampDesc(String actor);
    
    // Eventos por tipo de actor
    List<AuditEvent> findByActorTypeOrderByTimestampDesc(ActorType actorType);
    
    // Eventos por tipo de evento
    List<AuditEvent> findByEventTypeOrderByTimestampDesc(String eventType);
    
    /**
     * RN-013: Consulta eventos en rango de fechas para cumplimiento normativo.
     * Soporta retención de 7 años según RN-013.
     * 
     * @param startDate Fecha de inicio del rango
     * @param endDate Fecha de fin del rango
     * @return Lista de eventos en el período ordenados por timestamp descendente
     */
    // Eventos en rango de tiempo - RF-008
    List<AuditEvent> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
    /**
     * RF-008: Estadísticas de eventos por tipo para reportes de cumplimiento.
     * Utilizada por AuditController para resumen de auditoría.
     * 
     * @param startDate Fecha de inicio del período
     * @param endDate Fecha de fin del período
     * @return Lista de arrays con [tipoEvento, cantidad] ordenados por frecuencia
     */
    // Estadísticas de eventos por tipo - RF-008
    @Query("""
        SELECT a.eventType, COUNT(a) 
        FROM AuditEvent a 
        WHERE a.timestamp BETWEEN :startDate AND :endDate
        GROUP BY a.eventType
        ORDER BY COUNT(a) DESC
        """)
    List<Object[]> getEventTypeStatistics(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * RF-008: Estadísticas de actividad por actor para monitoreo y cumplimiento.
     * 
     * @param startDate Fecha de inicio del período
     * @param endDate Fecha de fin del período
     * @return Lista de arrays con [actor, tipoActor, cantidad] ordenados por actividad
     */
    // Actividad por actor en período
    @Query("""
        SELECT a.actor, a.actorType, COUNT(a) 
        FROM AuditEvent a 
        WHERE a.timestamp BETWEEN :startDate AND :endDate
        GROUP BY a.actor, a.actorType
        ORDER BY COUNT(a) DESC
        """)
    List<Object[]> getActorActivityStats(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}