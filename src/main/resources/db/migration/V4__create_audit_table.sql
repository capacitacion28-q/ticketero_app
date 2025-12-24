-- V4__create_audit_table.sql
-- Tabla de auditoría para RF-008

CREATE TABLE audit_event (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP(3) WITH TIME ZONE NOT NULL DEFAULT NOW(),
    event_type VARCHAR(50) NOT NULL,
    actor VARCHAR(100) NOT NULL,
    actor_type VARCHAR(20) NOT NULL,
    ticket_id BIGINT REFERENCES ticket(id),
    ticket_number VARCHAR(10),
    previous_state VARCHAR(20),
    new_state VARCHAR(20),
    additional_data TEXT,
    ip_address VARCHAR(45),
    integrity_hash VARCHAR(64) NOT NULL
);

-- Índices para consultas de auditoría (RF-008)
CREATE INDEX idx_audit_ticket_lookup ON audit_event (ticket_id, timestamp);
CREATE INDEX idx_audit_event_type ON audit_event (event_type, timestamp);
CREATE INDEX idx_audit_actor ON audit_event (actor, timestamp);

-- Comentarios
COMMENT ON TABLE audit_event IS 'Registro inmutable de eventos críticos del sistema';
COMMENT ON COLUMN audit_event.timestamp IS 'Timestamp con precisión de milisegundos';
COMMENT ON COLUMN audit_event.additional_data IS 'Información variable en formato JSONB';
COMMENT ON COLUMN audit_event.integrity_hash IS 'Hash SHA-256 para prevenir alteraciones';