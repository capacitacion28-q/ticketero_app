-- V1: Crear tabla de asesores/ejecutivos
-- Referencia: Plan Detallado Sección 8.1.1 - FASE 1
-- ADR-004: Migraciones Flyway con comentarios explicativos

CREATE TABLE advisor (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    current_tickets INTEGER NOT NULL DEFAULT 0,
    max_concurrent_tickets INTEGER NOT NULL DEFAULT 3,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints para RN-004: Balanceo de carga
    CONSTRAINT chk_advisor_status CHECK (status IN ('AVAILABLE', 'BUSY', 'OFFLINE')),
    CONSTRAINT chk_current_tickets CHECK (current_tickets >= 0),
    CONSTRAINT chk_max_tickets CHECK (max_concurrent_tickets > 0)
);

-- Índices para performance según RN-004
CREATE INDEX idx_advisor_status ON advisor(status);
CREATE INDEX idx_advisor_load ON advisor(current_tickets, max_concurrent_tickets);

-- Comentarios para documentación
COMMENT ON TABLE advisor IS 'Asesores/ejecutivos del sistema - RN-004 balanceo de carga';
COMMENT ON COLUMN advisor.current_tickets IS 'Tickets actualmente asignados al asesor';
COMMENT ON COLUMN advisor.max_concurrent_tickets IS 'Máximo de tickets concurrentes permitidos';