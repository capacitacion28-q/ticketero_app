package com.example.ticketero.model.entity;

import com.example.ticketero.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity Mensaje - Mensajes Telegram del sistema
 * 
 * Referencia: Plan Detallado Sección 8.2.1 - FASE 2
 * Diagrama ER: docs/architecture/diagrams/03-er-diagram.puml
 * RN-007: Máximo 3 reintentos de envío
 * RN-008: Backoff exponencial 30s, 60s, 120s
 * 
 * @author Sistema Ticketero
 * @version 1.0
 */
@Entity
@Table(name = "mensaje")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_id", nullable = false)
    @ToString.Exclude
    private Ticket ticket;
    
    @Column(name = "message_text", nullable = false, columnDefinition = "TEXT")
    private String messageText;
    
    @Column(name = "telegram_chat_id", length = 50)
    private String telegramChatId;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    @Builder.Default
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;
    
    @Column(name = "retry_count", nullable = false)
    @Builder.Default
    private Integer retryCount = 0;
    
    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Verifica si el mensaje puede reintentarse según RN-007
     * 
     * @return true si puede reintentarse
     */
    public boolean canRetry() {
        return deliveryStatus.canRetry() && retryCount < 3;
    }
    
    /**
     * Incrementa el contador de reintentos y calcula próximo intento según RN-008
     * Backoff exponencial: 30s, 60s, 120s
     */
    public void incrementRetryCount() {
        this.retryCount++;
        
        if (this.retryCount <= 3) {
            // RN-008: Backoff exponencial
            int delaySeconds = switch (this.retryCount) {
                case 1 -> 30;   // 30 segundos
                case 2 -> 60;   // 60 segundos  
                case 3 -> 120;  // 120 segundos
                default -> 0;
            };
            
            this.nextRetryAt = LocalDateTime.now().plusSeconds(delaySeconds);
        } else {
            // Máximo de reintentos alcanzado
            this.deliveryStatus = DeliveryStatus.FAILED;
            this.nextRetryAt = null;
        }
    }
    
    /**
     * Marca el mensaje como enviado exitosamente
     */
    public void markAsSent() {
        this.deliveryStatus = DeliveryStatus.SENT;
        this.sentAt = LocalDateTime.now();
        this.nextRetryAt = null;
    }
    
    /**
     * Marca el mensaje como fallido con error
     * 
     * @param errorMessage Mensaje de error
     */
    public void markAsFailed(String errorMessage) {
        this.deliveryStatus = DeliveryStatus.FAILED;
        this.errorMessage = errorMessage;
        incrementRetryCount();
    }
    
    /**
     * Cancela el envío del mensaje
     */
    public void cancel() {
        this.deliveryStatus = DeliveryStatus.CANCELLED;
        this.nextRetryAt = null;
    }
    
    /**
     * Verifica si el mensaje necesita procesamiento por scheduler
     * 
     * @return true si necesita procesamiento
     */
    public boolean needsProcessing() {
        return deliveryStatus.needsProcessing() || 
               (nextRetryAt != null && nextRetryAt.isBefore(LocalDateTime.now()));
    }
}