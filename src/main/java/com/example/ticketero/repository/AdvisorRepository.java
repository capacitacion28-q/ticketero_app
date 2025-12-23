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
 * Repository para Advisor - RN-004
 * Queries para balanceo de carga y asignación de asesores
 */
@Repository
public interface AdvisorRepository extends JpaRepository<Advisor, Long> {
    
    // Buscar por email
    Optional<Advisor> findByEmail(String email);
    
    // Asesores disponibles por estado - RN-004 (simplificado)
    List<Advisor> findByStatusOrderByAssignedTicketsCountAsc(AdvisorStatus status);
    
    // Asesor con menor carga disponible - RN-004 (query derivada sin LIMIT)
    Optional<Advisor> findFirstByStatusOrderByAssignedTicketsCountAscUpdatedAtAsc(AdvisorStatus status);
    
    // Asesores por estado
    List<Advisor> findByStatusOrderByNameAsc(AdvisorStatus status);
    
    // Estadísticas de productividad - RF-004 (simplificado)
    @Query("""
        SELECT a.name, a.assignedTicketsCount
        FROM Advisor a 
        WHERE a.status IN ('AVAILABLE', 'BUSY')
        ORDER BY a.assignedTicketsCount DESC
        """)
    List<Object[]> getAdvisorProductivityStats();
    
    // Contar asesores por estado
    @Query("""
        SELECT a.status, COUNT(a) 
        FROM Advisor a 
        GROUP BY a.status
        """)
    List<Object[]> countAdvisorsByStatus();
    
    // Asesor con mayor cantidad de tickets asignados
    Optional<Advisor> findFirstByOrderByAssignedTicketsCountDesc();
}