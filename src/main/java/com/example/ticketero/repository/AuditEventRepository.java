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
 * Repository para AuditEvent - RF-008
 * Queries con nomenclatura en español según entities
 */
@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {
    
    // Eventos por ticket
    List<AuditEvent> findByTicketNumberOrderByTimestampDesc(String ticketNumber);
    
    // Eventos por actor
    List<AuditEvent> findByActorOrderByTimestampDesc(String actor);
    
    // Eventos por tipo de actor
    List<AuditEvent> findByActorTypeOrderByTimestampDesc(ActorType actorType);
    
    // Eventos por tipo de evento
    List<AuditEvent> findByEventTypeOrderByTimestampDesc(String eventType);
    
    // Eventos en rango de tiempo - RF-008
    List<AuditEvent> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime startDate, 
        LocalDateTime endDate
    );
    
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