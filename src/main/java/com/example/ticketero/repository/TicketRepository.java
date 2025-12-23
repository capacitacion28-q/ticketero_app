package com.example.ticketero.repository;

import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.model.enums.TicketStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository para Ticket - RF-001, RF-002
 * Métodos según especificaciones exactas del plan - Sección 7.3
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Búsqueda por código de referencia único
    Optional<Ticket> findByCodigoReferencia(UUID codigoReferencia);
    
    // Búsqueda por número de ticket
    Optional<Ticket> findByNumero(String numero);
    
    // RN-001: Ticket activo por nationalId - ESPECIFICACIÓN EXACTA DEL PLAN
    Optional<Ticket> findByNationalIdAndStatusIn(String nationalId, List<TicketStatus> statuses);
    
    // Tickets por estado - ESPECIFICACIÓN EXACTA DEL PLAN
    List<Ticket> findByStatusOrderByFechaCreacionAsc(TicketStatus status);
    
    // Contar tickets en cola por tipo - RN-003
    long countByStatusAndQueueType(TicketStatus status, QueueType queueType);
    
    // Tickets activos por sucursal
    List<Ticket> findByBranchOfficeAndStatusInOrderByFechaCreacionAsc(String branchOffice, List<TicketStatus> statuses);
    
    // Query para calcular posición en cola - RN-002
    @Query("""
        SELECT COUNT(t) FROM Ticket t 
        WHERE t.queueType = :queueType 
        AND t.status = :status 
        AND t.fechaCreacion < :fechaCreacion
        """)
    int calculatePositionInQueue(
        @Param("queueType") QueueType queueType,
        @Param("status") TicketStatus status,
        @Param("fechaCreacion") LocalDateTime fechaCreacion
    );
    
    // Próximo ticket por prioridad - ESPECIFICACIÓN EXACTA DEL PLAN
    @Query("""
        SELECT t FROM Ticket t 
        WHERE t.status = 'WAITING' 
        AND t.queueType = :queueType
        ORDER BY t.fechaCreacion ASC
        """)
    Optional<Ticket> findNextTicketByPriority(@Param("queueType") QueueType queueType);
    
    // Tickets por asesor asignado
    List<Ticket> findByAssignedAdvisorAndStatusOrderByFechaActualizacionDesc(String advisor, TicketStatus status);
    
    // Tickets con timeout para NO_SHOW - SCHEDULER - ESPECIFICACIÓN EXACTA DEL PLAN
    @Query("SELECT t FROM Ticket t WHERE t.status = 'CALLED' AND t.fechaActualizacion < :timeoutThreshold")
    List<Ticket> findCalledOlderThan(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);
    
    // Estadísticas por rango de fechas - RF-004 (simplificada)
    @Query("""
        SELECT t.queueType, COUNT(t)
        FROM Ticket t 
        WHERE t.fechaCreacion BETWEEN :startDate AND :endDate
        AND t.status IN ('COMPLETED', 'CANCELLED')
        GROUP BY t.queueType
        """)
    List<Object[]> getQueueStatistics(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}