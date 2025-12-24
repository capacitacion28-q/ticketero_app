# Database Documentation - Sistema Ticketero Digital

**Proyecto:** Sistema de Gestión de Tickets para Atención en Sucursales  
**Base de Datos:** PostgreSQL 15  
**Versión:** 1.0  
**Fecha:** 2025-12-24  
**Herramienta de Migración:** Flyway 9.x

---

## Índice
- [Información General](#información-general)
- [Modelo de Datos](#modelo-de-datos)
- [Tablas del Sistema](#tablas-del-sistema)
- [Relaciones y Constraints](#relaciones-y-constraints)
- [Índices y Performance](#índices-y-performance)
- [Migraciones Flyway](#migraciones-flyway)
- [Tipos de Datos y Validaciones](#tipos-de-datos-y-validaciones)
- [Consultas Frecuentes](#consultas-frecuentes)

---

## Información General

### Stack de Base de Datos
- **SGBD:** PostgreSQL 15
- **Migraciones:** Flyway 9.x
- **ORM:** Spring Data JPA + Hibernate
- **Pool de Conexiones:** HikariCP

### Configuración
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ticketero_db
    username: ticketero_user
    password: ${DATABASE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### Características
- **Transacciones ACID:** Garantizadas por PostgreSQL
- **Integridad Referencial:** Foreign Keys y Constraints
- **Auditoría:** Tabla audit_event inmutable
- **Performance:** Índices optimizados para consultas frecuentes

---

## Modelo de Datos

### Diagrama ER Conceptual

```
┌─────────────┐       ┌─────────────┐       ┌─────────────┐
│   TICKET    │──────▶│   MENSAJE   │       │   ADVISOR   │
│             │ 1:N   │             │       │             │
│ - id (PK)   │       │ - id (PK)   │       │ - id (PK)   │
│ - codigo_   │       │ - ticket_id │       │ - name      │
│   referencia│       │   (FK)      │       │ - status    │
│ - numero    │       │ - plantilla │       │ - module_   │
│ - status    │       │ - estado_   │       │   number    │
│ - queue_type│       │   envio     │       └─────────────┘
│ - position_ │       │ - intentos  │              │
│   in_queue  │       └─────────────┘              │
│ - assigned_ │                                    │
│   advisor   │◀───────────────────────────────────┘
└─────────────┘                                1:N
       │
       │ 1:N
       ▼
┌─────────────┐
│ AUDIT_EVENT │
│             │
│ - id (PK)   │
│ - ticket_id │
│   (FK)      │
│ - event_type│
│ - timestamp │
│ - actor     │
└─────────────┘
```

### Entidades Principales

| Entidad | Propósito | Registros Estimados |
|---------|-----------|-------------------|
| **ticket** | Tickets de atención | 25,000/día |
| **mensaje** | Notificaciones Telegram | 75,000/día |
| **advisor** | Asesores de sucursal | 5-10 por sucursal |
| **audit_event** | Trazabilidad completa | 200,000/día |

---

## Tablas del Sistema

### 1. Tabla: ticket
**Propósito:** Gestión de tickets de atención  
**Requerimientos:** RF-001, RF-003, RF-004, RF-006

```sql
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
```

**Campos Clave:**
- `codigo_referencia`: UUID para consultas externas (RF-006)
- `numero`: Número visible con prefijo (C01, P15, etc.)
- `national_id`: RUT del cliente para validar unicidad (RN-001)
- `queue_type`: Tipo de cola (CAJA, PERSONAL_BANKER, EMPRESAS, GERENCIA)
- `status`: Estado del ticket (WAITING, NOTIFIED, CALLED, etc.)
- `position_in_queue`: Posición actual en la cola (RF-003)

**Estados Válidos:**
- `WAITING`: En espera
- `NOTIFIED`: Notificado (posición ≤ 3)
- `CALLED`: Llamado a atención
- `IN_SERVICE`: En atención
- `COMPLETED`: Completado
- `CANCELLED`: Cancelado
- `NO_SHOW`: No se presentó

---

### 2. Tabla: mensaje
**Propósito:** Gestión de notificaciones Telegram  
**Requerimientos:** RF-002, RN-007, RN-008

```sql
CREATE TABLE mensaje (
    id BIGSERIAL PRIMARY KEY,
    ticket_id BIGINT NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    plantilla VARCHAR(50) NOT NULL,
    estado_envio VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_programada TIMESTAMP NOT NULL,
    fecha_envio TIMESTAMP,
    telegram_message_id VARCHAR(50),
    intentos INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_mensaje_ticket 
        FOREIGN KEY (ticket_id) 
        REFERENCES ticket(id) 
        ON DELETE CASCADE
);
```

**Campos Clave:**
- `plantilla`: Tipo de mensaje (totem_ticket_creado, totem_proximo_turno, totem_es_tu_turno)
- `estado_envio`: Control de envío (PENDIENTE, ENVIADO, FALLIDO)
- `intentos`: Contador para reintentos (máximo 4 según RN-007)
- `telegram_message_id`: ID de confirmación de Telegram API

**Plantillas de Mensaje:**
- `totem_ticket_creado`: Confirmación de creación
- `totem_proximo_turno`: Pre-aviso (posición ≤ 3)
- `totem_es_tu_turno`: Llamado a atención

---

### 3. Tabla: advisor
**Propósito:** Gestión de asesores de sucursal  
**Requerimientos:** RF-004, RN-004

```sql
CREATE TABLE advisor (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    module_number INTEGER NOT NULL,
    assigned_tickets_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT chk_module_number CHECK (module_number BETWEEN 1 AND 5),
    CONSTRAINT chk_assigned_count CHECK (assigned_tickets_count >= 0)
);
```

**Campos Clave:**
- `status`: Estado del asesor (AVAILABLE, BUSY, OFFLINE)
- `module_number`: Número de módulo físico (1-5)
- `assigned_tickets_count`: Contador para balanceo de carga (RN-004)

**Estados de Asesor:**
- `AVAILABLE`: Disponible para asignación
- `BUSY`: Atendiendo cliente
- `OFFLINE`: No disponible

---

### 4. Tabla: audit_event
**Propósito:** Auditoría y trazabilidad completa  
**Requerimientos:** RF-008, RN-011

```sql
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
```

**Campos Clave:**
- `timestamp`: Precisión de milisegundos para auditoría
- `event_type`: Tipo de evento (TICKET_CREATED, STATUS_CHANGED, etc.)
- `actor`: Quien ejecutó la acción
- `additional_data`: Información variable en formato JSON
- `integrity_hash`: SHA-256 para prevenir alteraciones

**Tipos de Evento:**
- `TICKET_CREATED`: Creación de ticket
- `TICKET_ASSIGNED`: Asignación a asesor
- `STATUS_CHANGED`: Cambio de estado
- `NOTIFICATION_SENT`: Envío de notificación
- `TICKET_COMPLETED`: Completación de atención

---

## Relaciones y Constraints

### Relaciones Principales

```sql
-- 1:N - Un ticket tiene múltiples mensajes
ticket (1) ──── (N) mensaje
  id    ←────    ticket_id

-- 1:N - Un asesor atiende múltiples tickets
advisor (1) ──── (N) ticket
  name   ←────    assigned_advisor

-- 1:N - Un ticket genera múltiples eventos de auditoría
ticket (1) ──── (N) audit_event
  id    ←────    ticket_id
```

### Constraints de Integridad

```sql
-- Unicidad de ticket activo por cliente (RN-001)
CREATE UNIQUE INDEX idx_ticket_active_unique 
ON ticket (national_id) 
WHERE status IN ('WAITING', 'NOTIFIED', 'CALLED');

-- Validación de módulos (1-5)
ALTER TABLE advisor ADD CONSTRAINT chk_module_number 
CHECK (module_number BETWEEN 1 AND 5);

-- Validación de intentos de mensaje (0-4)
ALTER TABLE mensaje ADD CONSTRAINT chk_intentos 
CHECK (intentos >= 0 AND intentos <= 4);

-- Integridad referencial
ALTER TABLE mensaje ADD CONSTRAINT fk_mensaje_ticket 
FOREIGN KEY (ticket_id) REFERENCES ticket(id) ON DELETE CASCADE;

ALTER TABLE audit_event ADD CONSTRAINT fk_audit_ticket 
FOREIGN KEY (ticket_id) REFERENCES ticket(id) ON DELETE SET NULL;
```

---

## Índices y Performance

### Índices Críticos para Reglas de Negocio

```sql
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

-- RN-004: Balanceo de carga entre asesores
CREATE INDEX idx_advisor_load_balancing 
ON advisor (status, assigned_tickets_count, created_at);
```

### Índices de Performance

```sql
-- Consultas frecuentes de tickets
CREATE INDEX idx_ticket_created_at ON ticket(created_at DESC);
CREATE INDEX idx_ticket_status ON ticket(status);
CREATE INDEX idx_ticket_codigo_referencia ON ticket(codigo_referencia);

-- Procesamiento de mensajes (RF-002)
CREATE INDEX idx_mensaje_estado_fecha ON mensaje(estado_envio, fecha_programada);
CREATE INDEX idx_mensaje_ticket_id ON mensaje(ticket_id);

-- Consultas de auditoría (RF-008)
CREATE INDEX idx_audit_ticket_lookup ON audit_event (ticket_id, timestamp);
CREATE INDEX idx_audit_event_type ON audit_event (event_type, timestamp);
CREATE INDEX idx_audit_actor ON audit_event (actor, timestamp);

-- Performance de asesores
CREATE INDEX idx_advisor_status ON advisor(status);
CREATE INDEX idx_advisor_module ON advisor(module_number);
```

### Justificación de Índices

| Índice | Consulta Optimizada | Frecuencia | Impacto |
|--------|-------------------|------------|---------|
| `idx_ticket_active_unique` | Validación RN-001 | Cada creación | CRÍTICO |
| `idx_ticket_queue_fifo` | Orden FIFO (RN-003) | Cada 5 segundos | ALTO |
| `idx_advisor_load_balancing` | Balanceo carga (RN-004) | Cada asignación | ALTO |
| `idx_mensaje_estado_fecha` | Procesamiento mensajes | Cada 60 segundos | MEDIO |
| `idx_audit_*` | Consultas auditoría | Bajo demanda | BAJO |

---

## Migraciones Flyway

### Estructura de Migraciones

```
src/main/resources/db/migration/
├── V1__create_ticket_table.sql      # Tabla principal de tickets
├── V2__create_mensaje_table.sql     # Tabla de mensajes Telegram
├── V3__create_advisor_table.sql     # Tabla de asesores
├── V4__create_audit_table.sql       # Tabla de auditoría
└── V5__insert_initial_data.sql      # Datos iniciales (5 asesores)
```

### Convenciones de Naming

```
V{version}__{description}.sql

Ejemplos:
V1__create_ticket_table.sql
V2__create_mensaje_table.sql
V6__add_priority_column.sql
V7__update_queue_types.sql
```

### Comandos de Migración

```bash
# Aplicar migraciones
mvn flyway:migrate

# Validar migraciones
mvn flyway:validate

# Ver información de migraciones
mvn flyway:info

# Limpiar base de datos (solo desarrollo)
mvn flyway:clean
```

### Rollback Manual

```sql
-- Ejemplo: Rollback de V2 (crear tabla mensaje)
-- Crear V6__rollback_mensaje_table.sql

DROP TABLE IF EXISTS mensaje CASCADE;
DROP INDEX IF EXISTS idx_mensaje_estado_fecha;
DROP INDEX IF EXISTS idx_mensaje_ticket_id;
```

---

## Tipos de Datos y Validaciones

### Tipos de Datos Estándar

| Campo | Tipo PostgreSQL | Tamaño | Validación |
|-------|----------------|--------|------------|
| `id` | BIGSERIAL | 8 bytes | Auto-increment |
| `codigo_referencia` | UUID | 16 bytes | RFC 4122 |
| `numero` | VARCHAR(10) | Variable | Formato [CPEG][01-99] |
| `national_id` | VARCHAR(20) | Variable | RUT chileno |
| `telefono` | VARCHAR(20) | Variable | +56XXXXXXXXX |
| `timestamp` | TIMESTAMP(3) | 8 bytes | Precisión milisegundos |
| `status` | VARCHAR(20) | Variable | Enum values |

### Validaciones a Nivel de Base de Datos

```sql
-- Formato de número de ticket
ALTER TABLE ticket ADD CONSTRAINT chk_numero_format 
CHECK (numero ~ '^[CPEG][0-9]{2}$');

-- Rango de módulos válidos
ALTER TABLE advisor ADD CONSTRAINT chk_module_range 
CHECK (module_number BETWEEN 1 AND 5);

-- Contador de intentos válido
ALTER TABLE mensaje ADD CONSTRAINT chk_intentos_range 
CHECK (intentos >= 0 AND intentos <= 4);

-- Estados válidos de ticket
ALTER TABLE ticket ADD CONSTRAINT chk_status_valid 
CHECK (status IN ('WAITING', 'NOTIFIED', 'CALLED', 'IN_SERVICE', 'COMPLETED', 'CANCELLED', 'NO_SHOW'));

-- Estados válidos de asesor
ALTER TABLE advisor ADD CONSTRAINT chk_advisor_status_valid 
CHECK (status IN ('AVAILABLE', 'BUSY', 'OFFLINE'));
```

### Validaciones a Nivel de Aplicación

```java
// Bean Validation en DTOs
@Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$", message = "Formato de RUT inválido")
private String nationalId;

@Pattern(regexp = "^\\+56[0-9]{9}$", message = "Teléfono debe tener formato +56XXXXXXXXX")
private String telefono;

@NotNull(message = "El tipo de cola es obligatorio")
private QueueType queueType;
```

---

## Consultas Frecuentes

### 1. Validar Unicidad de Ticket Activo (RN-001)

```sql
-- Verificar si cliente tiene ticket activo
SELECT COUNT(*) 
FROM ticket 
WHERE national_id = '12345678-9' 
  AND status IN ('WAITING', 'NOTIFIED', 'CALLED');
```

### 2. Obtener Siguiente Ticket por Prioridad (RN-002, RN-003)

```sql
-- Siguiente ticket con mayor prioridad y más antiguo
SELECT t.* 
FROM ticket t
WHERE t.status = 'WAITING'
ORDER BY 
  CASE t.queue_type 
    WHEN 'GERENCIA' THEN 4
    WHEN 'EMPRESAS' THEN 3
    WHEN 'PERSONAL_BANKER' THEN 2
    WHEN 'CAJA' THEN 1
  END DESC,
  t.created_at ASC
LIMIT 1;
```

### 3. Balanceo de Carga de Asesores (RN-004)

```sql
-- Asesor disponible con menor carga
SELECT * 
FROM advisor 
WHERE status = 'AVAILABLE'
ORDER BY assigned_tickets_count ASC, created_at ASC
LIMIT 1;
```

### 4. Recalcular Posiciones FIFO (RN-003)

```sql
-- Actualizar posiciones por cola
WITH ranked_tickets AS (
  SELECT id, 
         ROW_NUMBER() OVER (ORDER BY created_at ASC) as new_position
  FROM ticket 
  WHERE queue_type = 'PERSONAL_BANKER' 
    AND status IN ('WAITING', 'NOTIFIED')
)
UPDATE ticket 
SET position_in_queue = rt.new_position,
    estimated_wait_minutes = rt.new_position * 15
FROM ranked_tickets rt 
WHERE ticket.id = rt.id;
```

### 5. Mensajes Pendientes de Envío

```sql
-- Mensajes listos para procesar
SELECT m.*, t.numero as ticket_number
FROM mensaje m
JOIN ticket t ON m.ticket_id = t.id
WHERE m.estado_envio = 'PENDIENTE'
  AND m.fecha_programada <= NOW()
  AND m.intentos < 4
ORDER BY m.fecha_programada ASC;
```

### 6. Estadísticas de Auditoría

```sql
-- Eventos por tipo en las últimas 24 horas
SELECT event_type, COUNT(*) as total
FROM audit_event 
WHERE timestamp >= NOW() - INTERVAL '24 hours'
GROUP BY event_type
ORDER BY total DESC;
```

### 7. Dashboard de Colas

```sql
-- Estado actual de todas las colas
SELECT 
  queue_type,
  COUNT(*) FILTER (WHERE status = 'WAITING') as waiting,
  COUNT(*) FILTER (WHERE status = 'CALLED') as called,
  AVG(estimated_wait_minutes) as avg_wait
FROM ticket 
WHERE status IN ('WAITING', 'NOTIFIED', 'CALLED')
GROUP BY queue_type;
```

---

## Mantenimiento y Optimización

### Limpieza de Datos Históricos

```sql
-- Archivar tickets completados > 30 días
INSERT INTO ticket_archive 
SELECT * FROM ticket 
WHERE status IN ('COMPLETED', 'CANCELLED', 'NO_SHOW')
  AND updated_at < NOW() - INTERVAL '30 days';

DELETE FROM ticket 
WHERE status IN ('COMPLETED', 'CANCELLED', 'NO_SHOW')
  AND updated_at < NOW() - INTERVAL '30 days';
```

### Particionado de Auditoría (Futuro)

```sql
-- Particionado por mes para tabla audit_event
CREATE TABLE audit_event_2025_12 PARTITION OF audit_event
FOR VALUES FROM ('2025-12-01') TO ('2026-01-01');
```

### Monitoreo de Performance

```sql
-- Consultas lentas
SELECT query, mean_time, calls 
FROM pg_stat_statements 
WHERE query LIKE '%ticket%'
ORDER BY mean_time DESC;

-- Uso de índices
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read
FROM pg_stat_user_indexes 
WHERE schemaname = 'public'
ORDER BY idx_scan DESC;
```

---

## Backup y Recuperación

### Backup Diario

```bash
# Backup completo
pg_dump -h localhost -U ticketero_user -d ticketero_db > backup_$(date +%Y%m%d).sql

# Backup solo datos
pg_dump -h localhost -U ticketero_user -d ticketero_db --data-only > data_backup_$(date +%Y%m%d).sql
```

### Restauración

```bash
# Restaurar backup completo
psql -h localhost -U ticketero_user -d ticketero_db < backup_20251224.sql

# Restaurar solo datos
psql -h localhost -U ticketero_user -d ticketero_db < data_backup_20251224.sql
```

---

## Configuración de Desarrollo

### Docker Compose para PostgreSQL

```yaml
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_DB: ticketero_db
      POSTGRES_USER: ticketero_user
      POSTGRES_PASSWORD: dev_password
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
```

### Conexión desde Aplicación

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ticketero_db
    username: ticketero_user
    password: ${DATABASE_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

---

**Documentación generada:** 2025-12-24  
**Versión BD:** 1.0  
**Mantenido por:** Equipo Sistema Ticketero