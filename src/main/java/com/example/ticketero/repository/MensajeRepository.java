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
 * Repository para Mensaje - RF-005
 * Queries con nomenclatura en español según entities
 */
@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    // Mensajes por ticket
    List<Mensaje> findByTicketIdOrderByFechaCreacionDesc(Long ticketId);
    
    // Mensajes pendientes de envío
    List<Mensaje> findByEstadoEnvioOrderByFechaCreacionAsc(EstadoEnvio estadoEnvio);
    
    // Mensajes por teléfono (para historial cliente)
    List<Mensaje> findByTelefonoOrderByFechaCreacionDesc(String telefono);
    
    // Mensajes fallidos para reintento (simplificado)
    @Query("""
        SELECT m FROM Mensaje m 
        WHERE m.estadoEnvio = 'FALLIDO' 
        AND m.fechaCreacion > :cutoffDate
        ORDER BY m.fechaCreacion ASC
        """)
    List<Mensaje> findFailedMessagesForRetry(@Param("cutoffDate") LocalDateTime cutoffDate);
    
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