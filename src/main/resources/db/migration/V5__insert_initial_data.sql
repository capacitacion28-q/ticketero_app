-- V5: Insertar datos iniciales
-- Referencia: Plan Detallado Sección 8.1.1 - FASE 1
-- Datos iniciales: 5 asesores para testing

INSERT INTO advisor (name, status, current_tickets, max_concurrent_tickets) VALUES
('Ana García', 'AVAILABLE', 0, 3),
('Carlos Rodríguez', 'AVAILABLE', 0, 3),
('María López', 'AVAILABLE', 0, 3),
('Juan Martínez', 'AVAILABLE', 0, 3),
('Laura Fernández', 'AVAILABLE', 0, 3);

-- Comentario para documentación
-- Datos iniciales para testing del sistema de colas
-- Todos los asesores inician disponibles con capacidad para 3 tickets concurrentes