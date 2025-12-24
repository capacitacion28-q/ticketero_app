package com.example.ticketero.repository;

import com.example.ticketero.model.entity.Mensaje;
import com.example.ticketero.model.enums.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository para entidad Mensaje con queries especializadas para cola de notificaciones.
 * 
 * Implementa: RF-002 (Programación de mensajes Telegram)
 * Reglas de Negocio: RN-007 (Máximo 3 reintentos), RN-008 (Backoff exponencial)
 * 
 * Queries críticas para procesamiento asíncrono:
 * - findByEstadoEnvioAndFechaProgramadaBefore: Utilizada por MensajeScheduler cada 60s
 * - findFailedMessagesForRetry: Implementa sistema de reintentos con RN-007, RN-008
 * 
 * Estados de mensaje: PENDIENTE, ENVIADO, FALLIDO
 * Procesamiento: MensajeScheduler ejecuta cada 60 segundos
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    /**
     * Query crítica para MensajeScheduler: obtiene mensajes listos para envío.
     * Utilizada cada 60 segundos para procesamiento asíncrono.
     * 
     * @param estadoEnvio Estado del mensaje (normalmente PENDIENTE)
     * @param fecha Timestamp límite para considerar mensaje listo
     * @return Lista de mensajes programados para envío
     */
    // Mensajes pendientes para procesamiento - SCHEDULER
    List<Mensaje> findByEstadoEnvioAndFechaProgramadaBefore(EstadoEnvio estadoEnvio, LocalDateTime fecha);
    
    /**
     * Obtiene historial de mensajes por ticket para trazabilidad.
     * 
     * @param ticketId ID del ticket
     * @return Lista de mensajes ordenados por fecha de creación descendente
     */
    // Mensajes por ticket
    List<Mensaje> findByTicketIdOrderByFechaCreacionDesc(Long ticketId);
    
    // Mensajes pendientes de envío
    List<Mensaje> findByEstadoEnvioOrderByFechaCreacionAsc(EstadoEnvio estadoEnvio);
    
    // Mensajes por teléfono (para historial cliente)
    List<Mensaje> findByTelefonoOrderByFechaCreacionDesc(String telefono);
    
    /**
     * RN-007, RN-008: Query para sistema de reintentos de mensajes fallidos.
     * Excluye mensajes muy antiguos para evitar reintentos infinitos.
     * 
     * @param cutoffDate Fecha límite para considerar mensaje válido para reintento
     * @return Lista de mensajes fallidos elegibles para reintento
     */
    // Mensajes fallidos para reintento (simplificado)
    @Query("""
        SELECT m FROM Mensaje m 
        WHERE m.estadoEnvio = 'FALLIDO' 
        AND m.fechaCreacion > :cutoffDate
        ORDER BY m.fechaCreacion ASC
        """)
    List<Mensaje> findFailedMessagesForRetry(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Estadísticas de mensajes por estado en período para monitoreo.
     * 
     * @param startDate Fecha de inicio del período
     * @param endDate Fecha de fin del período
     * @return Lista de arrays con [estado, cantidad] por cada EstadoEnvio
     */
    // Contar mensajes por estado en rango de tiempo
    @Query("""
        SELECT m.estadoEnvio, COUNT(m) 
        FROM Mensaje m 
        WHERE m.fechaCreacion BETWEEN :startDate AND :endDate
        GROUP BY m.estadoEnvio
        """)
    List<Object[]> countMessagesByStatusInPeriod(
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
}