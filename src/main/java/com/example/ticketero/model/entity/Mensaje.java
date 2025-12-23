package com.example.ticketero.model.entity;

import com.example.ticketero.model.enums.EstadoEnvio;
import com.example.ticketero.model.enums.MessageTemplate;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;
    
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plantilla", nullable = false)
    private MessageTemplate plantilla;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_envio", nullable = false)
    @Builder.Default
    private EstadoEnvio estadoEnvio = EstadoEnvio.PENDIENTE;
    
    @Column(name = "fecha_programada", nullable = false)
    private LocalDateTime fechaProgramada;
    
    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
    
    @Column(name = "telegram_message_id", length = 50)
    private String telegramMessageId;
    
    @Column(name = "intentos", nullable = false)
    @Builder.Default
    private Integer intentos = 0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (fechaProgramada == null) {
            fechaProgramada = LocalDateTime.now();
        }
    }
    
    public void incrementarIntentos() {
        this.intentos++;
    }
}