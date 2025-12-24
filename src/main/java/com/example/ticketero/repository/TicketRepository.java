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
 * Repository para entidad Ticket con queries especializadas para reglas de negocio críticas.
 * 
 * Implementa: RF-001 (Creación de tickets), RF-006 (Consulta de tickets)
 * Reglas de Negocio: RN-001 (Unicidad), RN-002 (Prioridades), RN-003 (FIFO), RN-009 (Timeouts)
 * 
 * Queries críticas implementadas:
 * - findByNationalIdAndStatusIn: Valida RN-001 (unicidad ticket activo)
 * - findNextTicketByPriority: Implementa RN-002 (orden por prioridad GERENCIA>EMPRESAS>PERSONAL_BANKER>CAJA)
 * - findCalledOlderThan: Implementa RN-009 (timeout NO_SHOW 5 minutos)
 * - calculatePositionInQueue: Calcula posición dinámica en cola
 * 
 * Índices de base de datos requeridos:
 * - idx_ticket_active_unique: Garantiza RN-001
 * - idx_ticket_queue_fifo: Optimiza RN-003
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    /**
     * RF-006: Busca ticket por código de referencia UUID único.
     * 
     * @param codigoReferencia UUID del ticket
     * @return Optional con ticket encontrado
     */
    // Búsqueda por código de referencia único
    Optional<Ticket> findByCodigoReferencia(UUID codigoReferencia);
    
    /**
     * RF-006: Busca ticket por número generado (formato: C01, P02, etc.).
     * 
     * @param numero Número de ticket con prefijo
     * @return Optional con ticket encontrado
     */
    // Búsqueda por número de ticket
    Optional<Ticket> findByNumero(String numero);
    
    /**
     * RN-001: Query crítica para validar unicidad de ticket activo por nationalId.
     * Utilizada para prevenir múltiples tickets activos del mismo cliente.
     * 
     * @param nationalId RUT del cliente
     * @param statuses Lista de estados activos (WAITING, CALLED, IN_SERVICE)
     * @return Optional con ticket activo si existe
     */
    // RN-001: Ticket activo por nationalId - ESPECIFICACIÓN EXACTA DEL PLAN
    Optional<Ticket> findByNationalIdAndStatusIn(String nationalId, List<TicketStatus> statuses);
    
    /**
     * RN-003: Obtiene tickets por estado ordenados por FIFO (fecha de creación ascendente).
     * 
     * @param status Estado del ticket a filtrar
     * @return Lista de tickets ordenados por llegada
     */
    // Tickets por estado - ESPECIFICACIÓN EXACTA DEL PLAN
    List<Ticket> findByStatusOrderByFechaCreacionAsc(TicketStatus status);
    
    /**
     * Cuenta tickets en cola por estado y tipo para cálculos de posición y métricas.
     * 
     * @param status Estado del ticket
     * @param queueType Tipo de cola
     * @return Cantidad de tickets que coinciden con los criterios
     */
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
    
    /**
     * RN-002: Query crítica que implementa selección por prioridad de cola.
     * Orden: GERENCIA(4) > EMPRESAS(3) > PERSONAL_BANKER(2) > CAJA(1), luego FIFO.
     * 
     * @return Lista de tickets ordenados por prioridad y fecha de creación
     */
    // Próximo ticket por prioridad - ESPECIFICACIÓN EXACTA DEL PLAN - CORREGIDA
    @Query("""
        SELECT t FROM Ticket t 
        WHERE t.status IN ('WAITING', 'NOTIFIED') 
        ORDER BY 
            CASE t.queueType 
                WHEN 'GERENCIA' THEN 4
                WHEN 'EMPRESAS' THEN 3
                WHEN 'PERSONAL_BANKER' THEN 2
                WHEN 'CAJA' THEN 1
            END DESC,
            t.fechaCreacion ASC
        """)
    List<Ticket> findNextTicketByPriority();
    
    // Tickets por asesor asignado
    List<Ticket> findByAssignedAdvisorAndStatusOrderByFechaActualizacionDesc(String advisor, TicketStatus status);
    
    /**
     * RN-009: Query para detectar tickets con timeout de NO_SHOW (5 minutos).
     * Utilizada por QueueProcessorScheduler para procesamiento automático.
     * 
     * @param timeoutThreshold Timestamp límite para considerar timeout
     * @return Lista de tickets que excedieron el tiempo límite
     */
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