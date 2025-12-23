package com.example.ticketero.service;

import com.example.ticketero.model.dto.DashboardResponse;
import com.example.ticketero.model.entity.EstadoTicket;
import com.example.ticketero.model.enums.AdvisorStatus;
import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.repository.AdvisorRepository;
import com.example.ticketero.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service para dashboard ejecutivo - RF-004
 * Métricas en tiempo real y alertas del sistema
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {
    
    private final TicketRepository ticketRepository;
    private final AdvisorRepository advisorRepository;
    private final QueueService queueService;
    
    public DashboardResponse getDashboardData() {
        log.debug("Generating dashboard data");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0);
        
        // Resumen ejecutivo
        DashboardResponse.ResumenEjecutivo resumenEjecutivo = generateResumenEjecutivo(startOfDay, now);
        
        // Estado de colas
        Map<String, DashboardResponse.EstadoCola> estadoColas = generateEstadoColas();
        
        // Estado de ejecutivos
        DashboardResponse.EstadoEjecutivos estadoEjecutivos = generateEstadoEjecutivos();
        
        // Alertas
        List<DashboardResponse.Alerta> alertas = generateAlertas();
        
        // Métricas adicionales
        Map<String, Integer> metricas = generateMetricas(startOfDay, now);
        
        return new DashboardResponse(
            now,
            30, // Intervalo de actualización en segundos
            determineEstadoGeneral(alertas),
            resumenEjecutivo,
            estadoColas,
            estadoEjecutivos,
            alertas,
            metricas
        );
    }
    
    private DashboardResponse.ResumenEjecutivo generateResumenEjecutivo(LocalDateTime startOfDay, LocalDateTime now) {
        // Tickets activos (en espera + llamados + en progreso)
        List<EstadoTicket> activeStatuses = List.of(EstadoTicket.WAITING, EstadoTicket.CALLED, EstadoTicket.IN_PROGRESS);
        int ticketsActivos = activeStatuses.stream()
            .mapToInt(status -> (int) ticketRepository.countByStatusAndQueueType(status, QueueType.CAJA) +
                               (int) ticketRepository.countByStatusAndQueueType(status, QueueType.PERSONAL_BANKER) +
                               (int) ticketRepository.countByStatusAndQueueType(status, QueueType.EMPRESAS) +
                               (int) ticketRepository.countByStatusAndQueueType(status, QueueType.GERENCIA))
            .sum();
        
        // Tickets completados hoy
        int ticketsCompletadosHoy = (int) Arrays.stream(QueueType.values())
            .mapToLong(queueType -> ticketRepository.countByStatusAndQueueType(EstadoTicket.COMPLETED, queueType))
            .sum();
        
        // Tiempo promedio global (simulado)
        double tiempoPromedioGlobal = calculateAverageServiceTime();
        
        // Tasa de completación por hora
        long horasTranscurridas = Math.max(1, java.time.Duration.between(startOfDay, now).toHours());
        double tasaCompletacionPorHora = (double) ticketsCompletadosHoy / horasTranscurridas;
        
        return new DashboardResponse.ResumenEjecutivo(
            ticketsActivos,
            ticketsCompletadosHoy,
            tiempoPromedioGlobal,
            tasaCompletacionPorHora
        );
    }
    
    private Map<String, DashboardResponse.EstadoCola> generateEstadoColas() {
        Map<String, DashboardResponse.EstadoCola> estadoColas = new HashMap<>();
        
        for (QueueType queueType : QueueType.values()) {
            int ticketsEnEspera = (int) ticketRepository.countByStatusAndQueueType(EstadoTicket.WAITING, queueType);
            int tiempoEstimadoMaximo = ticketsEnEspera * queueType.getAvgTimeMinutes();
            String estado = determineQueueState(ticketsEnEspera, queueType);
            
            estadoColas.put(queueType.name(), new DashboardResponse.EstadoCola(
                ticketsEnEspera,
                tiempoEstimadoMaximo,
                estado
            ));
        }
        
        return estadoColas;
    }
    
    private DashboardResponse.EstadoEjecutivos generateEstadoEjecutivos() {
        List<Object[]> statusCounts = advisorRepository.countAdvisorsByStatus();
        
        int disponibles = 0;
        int ocupados = 0;
        int offline = 0;
        
        for (Object[] row : statusCounts) {
            AdvisorStatus status = (AdvisorStatus) row[0];
            Long count = (Long) row[1];
            
            switch (status) {
                case AVAILABLE -> disponibles = count.intValue();
                case BUSY -> ocupados = count.intValue();
                case OFFLINE -> offline = count.intValue();
            }
        }
        
        // Asesor más productivo
        DashboardResponse.EjecutivoProductivo masProductivo = getMostProductiveAdvisor();
        
        // Distribución de carga
        String distribucionCarga = calculateLoadDistribution(disponibles, ocupados);
        
        return new DashboardResponse.EstadoEjecutivos(
            disponibles,
            ocupados,
            offline,
            distribucionCarga,
            masProductivo
        );
    }
    
    private List<DashboardResponse.Alerta> generateAlertas() {
        List<DashboardResponse.Alerta> alertas = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Alerta por colas saturadas
        for (QueueType queueType : QueueType.values()) {
            int ticketsEnEspera = (int) ticketRepository.countByStatusAndQueueType(EstadoTicket.WAITING, queueType);
            if (ticketsEnEspera > 10) { // Umbral de saturación
                alertas.add(new DashboardResponse.Alerta(
                    "QUEUE_OVERFLOW_" + queueType.name(),
                    "SATURACION_COLA",
                    "HIGH",
                    String.format("Cola %s tiene %d tickets en espera", queueType.getDisplayName(), ticketsEnEspera),
                    "Asignar más asesores a esta cola",
                    now
                ));
            }
        }
        
        // Alerta por falta de asesores disponibles
        int disponibles = advisorRepository.findByStatusOrderByAssignedTicketsCountAsc(AdvisorStatus.AVAILABLE).size();
        if (disponibles == 0) {
            alertas.add(new DashboardResponse.Alerta(
                "NO_ADVISORS_AVAILABLE",
                "FALTA_ASESORES",
                "CRITICAL",
                "No hay asesores disponibles en el sistema",
                "Verificar estado de asesores y liberar módulos ocupados",
                now
            ));
        }
        
        return alertas;
    }
    
    private Map<String, Integer> generateMetricas(LocalDateTime startOfDay, LocalDateTime now) {
        Map<String, Integer> metricas = new HashMap<>();
        
        // Métricas por cola
        for (QueueType queueType : QueueType.values()) {
            String key = "tickets_" + queueType.name().toLowerCase();
            int count = (int) ticketRepository.countByStatusAndQueueType(EstadoTicket.COMPLETED, queueType);
            metricas.put(key, count);
        }
        
        // Métricas generales
        metricas.put("total_asesores", (int) advisorRepository.count());
        metricas.put("tiempo_promedio_atencion", (int) calculateAverageServiceTime());
        
        return metricas;
    }
    
    private String determineEstadoGeneral(List<DashboardResponse.Alerta> alertas) {
        long criticalAlerts = alertas.stream().filter(a -> "CRITICAL".equals(a.prioridad())).count();
        long highAlerts = alertas.stream().filter(a -> "HIGH".equals(a.prioridad())).count();
        
        if (criticalAlerts > 0) return "CRITICO";
        if (highAlerts > 2) return "ALERTA";
        if (highAlerts > 0) return "ATENCION";
        return "NORMAL";
    }
    
    private String determineQueueState(int ticketsEnEspera, QueueType queueType) {
        if (ticketsEnEspera == 0) return "VACIA";
        if (ticketsEnEspera <= 3) return "NORMAL";
        if (ticketsEnEspera <= 7) return "MODERADA";
        return "SATURADA";
    }
    
    private double calculateAverageServiceTime() {
        // Simulación basada en promedios de tipos de cola
        return Arrays.stream(QueueType.values())
            .mapToInt(QueueType::getAvgTimeMinutes)
            .average()
            .orElse(10.0);
    }
    
    private DashboardResponse.EjecutivoProductivo getMostProductiveAdvisor() {
        return advisorRepository.findFirstByOrderByAssignedTicketsCountDesc()
            .map(advisor -> new DashboardResponse.EjecutivoProductivo(
                advisor.getName(),
                advisor.getAssignedTicketsCount()
            ))
            .orElse(new DashboardResponse.EjecutivoProductivo("N/A", 0));
    }
    
    private String calculateLoadDistribution(int disponibles, int ocupados) {
        int total = disponibles + ocupados;
        if (total == 0) return "Sin asesores";
        
        double porcentajeOcupacion = (double) ocupados / total * 100;
        
        if (porcentajeOcupacion < 30) return "BAJA";
        if (porcentajeOcupacion < 70) return "MODERADA";
        return "ALTA";
    }
}