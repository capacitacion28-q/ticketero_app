-- V3: Crear tabla de mensajes
-- Referencia: Plan Detallado Sección 8.1.1 - FASE 1
-- RN-007, RN-008: Reintentos y backoff exponencial

CREATE TABLE mensaje (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    message_text TEXT NOT NULL,
    telegram_chat_id VARCHAR(50),
    sent_at TIMESTAMP,
    delivery_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INTEGER NOT NULL DEFAULT 0,
    next_retry_at TIMESTAMP,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key para relación con ticket
    CONSTRAINT fk_mensaje_ticket FOREIGN KEY (ticket_id) REFERENCES ticket(id) ON DELETE CASCADE,
    
    -- Constraints para reglas de negocio RN-007, RN-008
    CONSTRAINT chk_mensaje_status CHECK (delivery_status IN ('PENDING', 'SENT', 'FAILED', 'CANCELLED')),
    CONSTRAINT chk_retry_count CHECK (retry_count >= 0 AND retry_count <= 3), -- RN-007: máximo 3 reintentos
    CONSTRAINT chk_message_not_empty CHECK (LENGTH(TRIM(message_text)) > 0)
);

-- Índices para performance de schedulers
CREATE INDEX idx_mensaje_ticket ON mensaje(ticket_id);
CREATE INDEX idx_mensaje_status ON mensaje(delivery_status);
CREATE INDEX idx_mensaje_retry ON mensaje(next_retry_at) WHERE delivery_status = 'PENDING' AND retry_count < 3;
CREATE INDEX idx_mensaje_created_at ON mensaje(created_at);

-- Comentarios para documentación
COMMENT ON TABLE mensaje IS 'Mensajes Telegram del sistema - RN-007, RN-008';
COMMENT ON COLUMN mensaje.retry_count IS 'Contador de reintentos - máximo 3 según RN-007';
COMMENT ON COLUMN mensaje.next_retry_at IS 'Próximo intento con backoff exponencial - RN-008';
COMMENT ON COLUMN mensaje.delivery_status IS 'Estado de entrega del mensaje Telegram';