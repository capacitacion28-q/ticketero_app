package com.example.ticketero.repository;

import com.example.ticketero.model.entity.Ticket;
import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.model.entity.EstadoTicket;
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
    Optional<Ticket> findByNationalIdAndStatusIn(String nationalId, List<EstadoTicket> statuses);
    
    // Tickets por estado - ESPECIFICACIÓN EXACTA DEL PLAN
    List<Ticket> findByStatusOrderByFechaCreacionAsc(EstadoTicket status);
    
    // Contar tickets en cola por tipo - RN-003
    long countByStatusAndQueueType(EstadoTicket status, QueueType queueType);
    
    // Tickets activos por sucursal
    List<Ticket> findByBranchOfficeAndStatusInOrderByFechaCreacionAsc(String branchOffice, List<EstadoTicket> statuses);
    
    // Query para calcular posición en cola - RN-002
    @Query("""
        SELECT COUNT(t) FROM Ticket t 
        WHERE t.queueType = :queueType 
        AND t.status = :status 
        AND t.fechaCreacion < :fechaCreacion
        """)
    int calculatePositionInQueue(
        @Param("queueType") QueueType queueType,
        @Param("status") EstadoTicket status,
        @Param("fechaCreacion") LocalDateTime fechaCreacion
    );
    
    // Próximo ticket por prioridad - ESPECIFICACIÓN EXACTA DEL PLAN
    Optional<Ticket> findNextTicketByPriority(QueueType queueType);
    
    // Tickets por asesor asignado
    List<Ticket> findByAssignedAdvisorAndStatusOrderByFechaActualizacionDesc(String advisor, EstadoTicket status);
    
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