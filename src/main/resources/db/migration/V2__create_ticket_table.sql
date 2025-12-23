-- V2: Crear tabla de tickets
-- Referencia: Plan Detallado Sección 8.1.1 - FASE 1
-- RN-001, RN-005, RN-006: Validación unicidad, numeración secuencial, prefijos

CREATE TABLE ticket (
    id BIGSERIAL PRIMARY KEY,
    queue_number VARCHAR(10) NOT NULL UNIQUE,
    queue_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'WAITING',
    client_name VARCHAR(100) NOT NULL,
    client_phone VARCHAR(20) NOT NULL,
    advisor_id BIGINT,
    position_in_queue INTEGER,
    estimated_wait_time INTEGER, -- en minutos
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_at TIMESTAMP,
    completed_at TIMESTAMP,
    
    -- Foreign key para relación con advisor
    CONSTRAINT fk_ticket_advisor FOREIGN KEY (advisor_id) REFERENCES advisor(id),
    
    -- Constraints para reglas de negocio
    CONSTRAINT chk_ticket_queue_type CHECK (queue_type IN ('CONSULTA', 'PRESTAMO', 'EMPRESA', 'GENERAL')),
    CONSTRAINT chk_ticket_status CHECK (status IN ('WAITING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'NO_SHOW', 'CANCELLED')),
    CONSTRAINT chk_position_positive CHECK (position_in_queue > 0),
    CONSTRAINT chk_wait_time_positive CHECK (estimated_wait_time >= 0)
);

-- Índices para performance según RN-002, RN-003, RN-010
CREATE INDEX idx_ticket_queue_type_status ON ticket(queue_type, status);
CREATE INDEX idx_ticket_position ON ticket(position_in_queue) WHERE status = 'WAITING';
CREATE INDEX idx_ticket_advisor ON ticket(advisor_id);
CREATE INDEX idx_ticket_created_at ON ticket(created_at);

-- Índice único para RN-001: Un ticket activo por cliente
CREATE UNIQUE INDEX idx_unique_active_client ON ticket(client_phone) 
WHERE status IN ('WAITING', 'CONFIRMED', 'IN_PROGRESS');

-- Comentarios para documentación
COMMENT ON TABLE ticket IS 'Tickets del sistema de colas - RN-001 a RN-006';
COMMENT ON COLUMN ticket.queue_number IS 'Número único con prefijo por tipo de cola - RN-005, RN-006';
COMMENT ON COLUMN ticket.position_in_queue IS 'Posición actual en la cola - RN-003 FIFO';
COMMENT ON COLUMN ticket.estimated_wait_time IS 'Tiempo estimado de espera en minutos - RN-010';