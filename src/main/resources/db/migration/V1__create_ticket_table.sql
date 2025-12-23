-- V1__create_ticket_table.sql
-- Basado en ADR-004: Migraciones de Base de Datos
-- Decisión: Flyway vs Liquibase
-- Justificación: SQL puro, rollback controlado, contexto financiero

CREATE TABLE ticket (
    id BIGSERIAL PRIMARY KEY,
    codigo_referencia UUID NOT NULL UNIQUE,
    numero VARCHAR(10) NOT NULL UNIQUE,
    national_id VARCHAR(20) NOT NULL,
    telefono VARCHAR(20),
    branch_office VARCHAR(100) NOT NULL,
    queue_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    position_in_queue INTEGER NOT NULL,
    estimated_wait_minutes INTEGER NOT NULL,
    assigned_advisor VARCHAR(50),
    assigned_module_number INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices específicos para reglas de negocio según documento de arquitectura

-- RN-001: Unicidad de ticket activo por cliente (CRÍTICO)
CREATE UNIQUE INDEX idx_ticket_active_unique 
ON ticket (national_id) 
WHERE status IN ('WAITING', 'NOTIFIED', 'CALLED');

-- RN-003: Orden FIFO por cola
CREATE INDEX idx_ticket_queue_fifo 
ON ticket (queue_type, created_at);

-- RF-004: Asignación automática
CREATE INDEX idx_ticket_assignment 
ON ticket (status, position_in_queue);

-- Performance general
CREATE INDEX idx_ticket_created_at ON ticket(created_at DESC);

COMMENT ON TABLE ticket IS 'Tickets de atención en sucursales';
COMMENT ON COLUMN ticket.codigo_referencia IS 'UUID único para referencias externas';
COMMENT ON COLUMN ticket.numero IS 'Número visible del ticket (C01, P15, etc.)';