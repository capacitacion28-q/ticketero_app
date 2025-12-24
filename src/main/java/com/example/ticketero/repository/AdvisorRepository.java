package com.example.ticketero.repository;

import com.example.ticketero.model.entity.Advisor;
import com.example.ticketero.model.enums.AdvisorStatus;
import com.example.ticketero.model.enums.QueueType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository para entidad Advisor con queries especializadas para balanceo de carga.
 * 
 * Implementa: RF-004 (Asignación automática de ejecutivos)
 * Reglas de Negocio: RN-004 (Balanceo de carga entre ejecutivos)
 * 
 * Queries críticas para balanceo:
 * - findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc: Implementa RN-004 (menor carga)
 * - getAdvisorProductivityStats: Estadísticas de productividad
 * - countAdvisorsByStatus: Métricas de disponibilidad
 * 
 * Algoritmo de balanceo: Selecciona asesor AVAILABLE con menor assignedTicketsCount,
 * en caso de empate usa updatedAt más antiguo.
 * 
 * @author Sistema Ticketero
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface AdvisorRepository extends JpaRepository<Advisor, Long> {
    
    // Buscar por email
    Optional<Advisor> findByEmail(String email);
    
    /**
     * Obtiene asesores por estado ordenados por carga de trabajo ascendente.
     * 
     * @param status Estado del asesor a filtrar
     * @return Lista de asesores ordenados por assignedTicketsCount
     */
    // Asesores disponibles por estado - RN-004 (simplificado)
    List<Advisor> findByStatusOrderByAssignedTicketsCountAsc(AdvisorStatus status);
    
    /**
     * RN-004: Query crítica para balanceo de carga automático.
     * Selecciona asesor disponible con menor cantidad de tickets asignados.
     * En caso de empate, usa updatedAt más antiguo (FIFO entre asesores).
     * 
     * @param status Estado del asesor (normalmente AVAILABLE)
     * @return Optional con asesor con menor carga o empty si no hay disponibles
     */
    // Asesor con menor carga disponible - RN-004 (query derivada sin LIMIT)
    Optional<Advisor> findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus status);
    
    // Asesores por estado
    List<Advisor> findByStatusOrderByNameAsc(AdvisorStatus status);
    
    /**
     * RF-007: Estadísticas de productividad de asesores para dashboard.
     * 
     * @return Lista de arrays con [nombre, ticketsAsignados] ordenados por productividad
     */
    // Estadísticas de productividad - RF-004 (simplificado)
    @Query("""
        SELECT a.name, a.assignedTicketsCount
        FROM Advisor a 
        WHERE a.status IN ('AVAILABLE', 'BUSY')
        ORDER BY a.assignedTicketsCount DESC
        """)
    List<Object[]> getAdvisorProductivityStats();
    
    /**
     * RF-007: Cuenta asesores agrupados por estado para métricas de dashboard.
     * 
     * @return Lista de arrays con [estado, cantidad] para cada AdvisorStatus
     */
    // Contar asesores por estado
    @Query("""
        SELECT a.status, COUNT(a) 
        FROM Advisor a 
        GROUP BY a.status
        """)
    List<Object[]> countAdvisorsByStatus();
    
    /**
     * RF-007: Encuentra asesor más productivo para dashboard ejecutivo.
     * 
     * @return Optional con asesor con mayor cantidad de tickets asignados
     */
    // Asesor con mayor cantidad de tickets asignados
    Optional<Advisor> findFirstByOrderByAssignedTicketsCountDesc();
}