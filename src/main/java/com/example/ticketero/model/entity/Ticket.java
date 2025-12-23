package com.example.ticketero.model.entity;

import com.example.ticketero.model.enums.QueueType;
import com.example.ticketero.model.entity.EstadoTicket;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ticket")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo_referencia", nullable = false, unique = true)
    private UUID codigoReferencia;
    
    @Column(name = "numero", nullable = false, unique = true, length = 10)
    private String numero;
    
    @Column(name = "national_id", nullable = false, length = 20)
    private String nationalId;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "branch_office", nullable = false, length = 100)
    private String branchOffice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "queue_type", nullable = false)
    private QueueType queueType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EstadoTicket status;
    
    @Column(name = "position_in_queue", nullable = false)
    private Integer positionInQueue;
    
    @Column(name = "estimated_wait_minutes", nullable = false)
    private Integer estimatedWaitMinutes;
    
    @Column(name = "assigned_advisor", length = 50)
    private String assignedAdvisor;
    
    @Column(name = "assigned_module_number")
    private Integer assignedModuleNumber;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "updated_at")
    private LocalDateTime fechaActualizacion;
    
    // Relación con mensajes
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mensaje> mensajes;
    
    // Relación con eventos de auditoría
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuditEvent> auditEvents;
    
    @PrePersist
    protected void onCreate() {
        if (codigoReferencia == null) {
            codigoReferencia = UUID.randomUUID();
        }
        fechaCreacion = LocalDateTime.now();
        fechaActualizacion = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}