-- V4: Crear tabla de auditoría
-- Referencia: Plan Detallado Sección 8.1.1 - FASE 1
-- RN-011, RN-013: Auditoría obligatoria y retención 7 años

CREATE TABLE audit_event (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT,
    old_values JSONB,
    new_values JSONB,
    user_id VARCHAR(100),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details TEXT,
    
    -- Constraints para auditoría
    CONSTRAINT chk_audit_event_type CHECK (event_type IN (
        'TICKET_CREATED', 'TICKET_CONFIRMED', 'TICKET_ASSIGNED', 
        'TICKET_COMPLETED', 'TICKET_CANCELLED', 'TICKET_NO_SHOW',
        'ADVISOR_STATUS_CHANGED', 'MESSAGE_SENT', 'MESSAGE_FAILED'
    )),
    CONSTRAINT chk_audit_entity_type CHECK (entity_type IN ('TICKET', 'ADVISOR', 'MENSAJE'))
);

-- Índices para consultas de auditoría y performance
CREATE INDEX idx_audit_event_type ON audit_event(event_type);
CREATE INDEX idx_audit_entity ON audit_event(entity_type, entity_id);
CREATE INDEX idx_audit_timestamp ON audit_event(timestamp);
CREATE INDEX idx_audit_user ON audit_event(user_id);

-- Índice para limpieza automática según RN-013 (retención 7 años)
CREATE INDEX idx_audit_cleanup ON audit_event(timestamp) WHERE timestamp < CURRENT_TIMESTAMP - INTERVAL '7 years';

-- Comentarios para documentación
COMMENT ON TABLE audit_event IS 'Eventos de auditoría del sistema - RN-011, RN-013';
COMMENT ON COLUMN audit_event.old_values IS 'Valores anteriores en formato JSON para cambios';
COMMENT ON COLUMN audit_event.new_values IS 'Valores nuevos en formato JSON para cambios';
COMMENT ON COLUMN audit_event.timestamp IS 'Timestamp del evento - retención 7 años según RN-013';