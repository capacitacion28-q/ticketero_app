-- V1__create_ticket_table_h2.sql
-- Versión compatible con H2 Database para tests sin Docker
-- Basado en ADR-004: Migraciones de Base de Datos
-- Modificación: Índice condicional simulado con trigger

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

-- Índices básicos (sin condicionales para H2)
CREATE INDEX idx_ticket_queue_fifo ON ticket (queue_type, created_at);
CREATE INDEX idx_ticket_assignment ON ticket (status, position_in_queue);
CREATE INDEX idx_ticket_created_at ON ticket(created_at DESC);
CREATE INDEX idx_ticket_national_id_status ON ticket(national_id, status);

COMMENT ON TABLE ticket IS 'Tickets de atención en sucursales';
COMMENT ON COLUMN ticket.codigo_referencia IS 'UUID único para referencias externas';
COMMENT ON COLUMN ticket.numero IS 'Número visible del ticket (C01, P15, etc.)';