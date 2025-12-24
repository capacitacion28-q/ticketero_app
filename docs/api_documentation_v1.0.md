# API REST Documentation - Sistema Ticketero Digital

**Proyecto:** Sistema de Gestión de Tickets para Atención en Sucursales  
**Versión:** 1.0  
**Fecha:** 2025-12-24  
**Base URL:** `http://localhost:8080/api`

---

## Índice
- [Información General](#información-general)
- [Autenticación](#autenticación)
- [Endpoints de Tickets](#endpoints-de-tickets)
- [Endpoints de Administración](#endpoints-de-administración)
- [Endpoints de Auditoría](#endpoints-de-auditoría)
- [Códigos de Estado HTTP](#códigos-de-estado-http)
- [Manejo de Errores](#manejo-de-errores)
- [Ejemplos de Uso](#ejemplos-de-uso)

---

## Información General

### Stack Tecnológico
- **Framework:** Spring Boot 3.2
- **Base de Datos:** PostgreSQL 15
- **Validación:** Bean Validation (JSR-303)
- **Documentación:** OpenAPI 3.0 compatible

### Formatos Soportados
- **Content-Type:** `application/json`
- **Accept:** `application/json`
- **Charset:** UTF-8

### Convenciones
- **Fechas:** ISO 8601 format (`2025-12-24T10:30:00`)
- **UUIDs:** RFC 4122 format
- **Paginación:** Spring Data Pageable
- **Códigos HTTP:** Estándar REST

---

## Autenticación

**Estado Actual:** Sin autenticación (desarrollo)  
**Futuro:** JWT Bearer Token para producción

```bash
# Headers requeridos
Content-Type: application/json
Accept: application/json
```

---

## Endpoints de Tickets

### 1. Crear Ticket
**RF-001:** Creación de tickets con validación automática

```http
POST /api/tickets
```

**Request Body:**
```json
{
  "titulo": "Consulta sobre cuenta corriente",
  "descripcion": "Necesito información sobre los movimientos de mi cuenta corriente del último mes",
  "usuarioId": 12345,
  "nationalId": "12345678-9",
  "telefono": "+56912345678",
  "branchOffice": "Sucursal Centro",
  "queueType": "PERSONAL_BANKER"
}
```

**Response (201 Created):**
```json
{
  "codigoReferencia": "550e8400-e29b-41d4-a716-446655440000",
  "numero": "P01",
  "nationalId": "12345678-9",
  "telefono": "+56912345678",
  "branchOffice": "Sucursal Centro",
  "queueType": "PERSONAL_BANKER",
  "status": "WAITING",
  "positionInQueue": 3,
  "estimatedWaitMinutes": 45,
  "assignedAdvisor": null,
  "assignedModuleNumber": null,
  "createdAt": "2025-12-24T10:30:00",
  "updatedAt": "2025-12-24T10:30:00",
  "statusDescription": "En espera"
}
```

**Validaciones:**
- **RUT:** Formato `^[0-9]{7,8}-[0-9Kk]$`
- **Teléfono:** Formato `^\\+56[0-9]{9}$`
- **Título:** 5-200 caracteres
- **Descripción:** 10-5000 caracteres

**Ejemplo curl:**
```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Consulta cuenta corriente",
    "descripcion": "Información sobre movimientos del último mes",
    "usuarioId": 12345,
    "nationalId": "12345678-9",
    "telefono": "+56912345678",
    "branchOffice": "Sucursal Centro",
    "queueType": "PERSONAL_BANKER"
  }'
```

---

### 2. Consultar Ticket por UUID
**RF-006:** Consulta por código de referencia

```http
GET /api/tickets/{codigoReferencia}
```

**Path Parameters:**
- `codigoReferencia` (UUID): Código único del ticket

**Response (200 OK):**
```json
{
  "codigoReferencia": "550e8400-e29b-41d4-a716-446655440000",
  "numero": "P01",
  "nationalId": "12345678-9",
  "telefono": "+56912345678",
  "branchOffice": "Sucursal Centro",
  "queueType": "PERSONAL_BANKER",
  "status": "CALLED",
  "positionInQueue": 1,
  "estimatedWaitMinutes": 5,
  "assignedAdvisor": "María González",
  "assignedModuleNumber": 3,
  "createdAt": "2025-12-24T10:30:00",
  "updatedAt": "2025-12-24T11:15:00",
  "statusDescription": "Llamado a atención"
}
```

**Ejemplo curl:**
```bash
curl -X GET http://localhost:8080/api/tickets/550e8400-e29b-41d4-a716-446655440000
```

---

### 3. Consultar Ticket por Número
**RF-006:** Consulta por número visible

```http
GET /api/tickets/number/{ticketNumber}
```

**Path Parameters:**
- `ticketNumber` (string): Número del ticket (ej: P01, C05, E03, G02)

**Response (200 OK):** Igual que consulta por UUID

**Ejemplo curl:**
```bash
curl -X GET http://localhost:8080/api/tickets/number/P01
```

---

## Endpoints de Administración

### 4. Consultar Cola Específica
**RF-005:** Estado de cola por tipo

```http
GET /api/queues/{queueType}
```

**Path Parameters:**
- `queueType` (enum): CAJA | PERSONAL_BANKER | EMPRESAS | GERENCIA

**Response (200 OK):**
```json
{
  "queueType": "PERSONAL_BANKER",
  "displayName": "Personal Banker",
  "avgTime": 15,
  "priority": 2
}
```

**Ejemplo curl:**
```bash
curl -X GET http://localhost:8080/api/queues/PERSONAL_BANKER
```

---

### 5. Estadísticas de Colas
**RF-005:** Métricas generales

```http
GET /api/queues/stats
```

**Response (200 OK):**
```json
{
  "totalQueues": 4,
  "activeTickets": 12,
  "avgWaitTime": 15
}
```

**Ejemplo curl:**
```bash
curl -X GET http://localhost:8080/api/queues/stats
```

---

### 6. Resumen de Colas
**RF-005:** Estado consolidado

```http
GET /api/queues/summary
```

**Response (200 OK):**
```json
{
  "CAJA": {
    "waiting": 3,
    "avgTime": 5
  },
  "PERSONAL_BANKER": {
    "waiting": 5,
    "avgTime": 15
  },
  "EMPRESAS": {
    "waiting": 2,
    "avgTime": 20
  },
  "GERENCIA": {
    "waiting": 1,
    "avgTime": 30
  }
}
```

**Ejemplo curl:**
```bash
curl -X GET http://localhost:8080/api/queues/summary
```

---

### 7. Dashboard Principal
**RF-007:** Resumen ejecutivo

```http
GET /api/dashboard/summary
```

**Response (200 OK):**
```json
{
  "timestamp": "2025-12-24T11:30:00",
  "ticketsActivos": 12,
  "ejecutivosDisponibles": 5,
  "estadoGeneral": "NORMAL"
}
```

**Ejemplo curl:**
```bash
curl -X GET http://localhost:8080/api/dashboard/summary
```

---

### 8. Dashboard Tiempo Real
**RF-007:** Métricas actualizadas

```http
GET /api/dashboard/realtime
```

**Response (200 OK):**
```json
{
  "timestamp": "2025-12-24T11:30:15",
  "status": "NORMAL",
  "updateInterval": 5
}
```

**Ejemplo curl:**
```bash
curl -X GET http://localhost:8080/api/dashboard/realtime
```

---

### 9. Alertas Activas
**RF-007:** Sistema de alertas

```http
GET /api/dashboard/alerts
```

**Response (200 OK):**
```json
{
  "alerts": [
    {
      "id": 1,
      "type": "QUEUE_SATURATION",
      "message": "Cola PERSONAL_BANKER saturada (>10 tickets)",
      "priority": "HIGH",
      "timestamp": "2025-12-24T11:25:00"
    }
  ]
}
```

**Ejemplo curl:**
```bash
curl -X GET http://localhost:8080/api/dashboard/alerts
```

---

### 10. Métricas Detalladas
**RF-007:** Análisis avanzado

```http
GET /api/dashboard/metrics
```

**Response (200 OK):**
```json
{
  "metrics": {
    "ticketsCreatedToday": 45,
    "avgProcessingTime": 12.5,
    "advisorUtilization": 0.75,
    "customerSatisfaction": 4.2
  }
}
```

**Ejemplo curl:**
```bash
curl -X GET http://localhost:8080/api/dashboard/metrics
```

---

## Endpoints de Auditoría

### 11. Historial de Ticket
**RF-008:** Trazabilidad completa

```http
GET /api/audit/ticket/{ticketNumber}
```

**Path Parameters:**
- `ticketNumber` (string): Número del ticket

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "timestamp": "2025-12-24T10:30:00.123",
    "eventType": "TICKET_CREATED",
    "actor": "12345678-9",
    "actorType": "CLIENT",
    "ticketId": "550e8400-e29b-41d4-a716-446655440000",
    "ticketNumber": "P01",
    "previousState": null,
    "newState": "WAITING",
    "additionalData": {
      "queueType": "PERSONAL_BANKER",
      "position": 3
    },
    "ipAddress": "192.168.1.100"
  },
  {
    "id": 2,
    "timestamp": "2025-12-24T11:15:00.456",
    "eventType": "TICKET_ASSIGNED",
    "actor": "SYSTEM",
    "actorType": "SYSTEM",
    "ticketId": "550e8400-e29b-41d4-a716-446655440000",
    "ticketNumber": "P01",
    "previousState": "NOTIFIED",
    "newState": "CALLED",
    "additionalData": {
      "assignedAdvisor": "María González",
      "moduleNumber": 3
    },
    "ipAddress": "10.0.0.1"
  }
]
```

**Ejemplo curl:**
```bash
curl -X GET http://localhost:8080/api/audit/ticket/P01
```

---

### 12. Consultar Eventos
**RF-008:** Búsqueda con filtros

```http
GET /api/audit/events
```

**Query Parameters:**
- `eventType` (string, opcional): Tipo de evento
- `actor` (string, opcional): Actor que ejecutó la acción
- `startDate` (datetime, opcional): Fecha inicio (ISO 8601)
- `endDate` (datetime, opcional): Fecha fin (ISO 8601)

**Response (200 OK):** Array de eventos (igual formato que historial)

**Ejemplo curl:**
```bash
curl -X GET "http://localhost:8080/api/audit/events?eventType=TICKET_CREATED&startDate=2025-12-24T00:00:00&endDate=2025-12-24T23:59:59"
```

---

### 13. Resumen de Auditoría
**RF-008:** Estadísticas por período

```http
GET /api/audit/summary
```

**Query Parameters:**
- `startDate` (date, requerido): Fecha inicio (YYYY-MM-DD)
- `endDate` (date, requerido): Fecha fin (YYYY-MM-DD)

**Response (200 OK):**
```json
{
  "period": "2025-12-24 to 2025-12-24",
  "eventTypeStats": [
    ["TICKET_CREATED", 45],
    ["TICKET_ASSIGNED", 42],
    ["STATUS_CHANGED", 38],
    ["NOTIFICATION_SENT", 135]
  ],
  "actorStats": [
    ["SYSTEM", 180],
    ["María González", 15],
    ["Juan Pérez", 12]
  ]
}
```

**Ejemplo curl:**
```bash
curl -X GET "http://localhost:8080/api/audit/summary?startDate=2025-12-24&endDate=2025-12-24"
```

---

## Códigos de Estado HTTP

| Código | Descripción | Uso |
|--------|-------------|-----|
| **200** | OK | Consultas exitosas |
| **201** | Created | Ticket creado exitosamente |
| **400** | Bad Request | Validación fallida, parámetros inválidos |
| **404** | Not Found | Ticket no encontrado |
| **422** | Unprocessable Entity | Reglas de negocio violadas |
| **500** | Internal Server Error | Error interno del servidor |

---

## Manejo de Errores

### Error de Validación (400 Bad Request)
```json
{
  "timestamp": "2025-12-24T11:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/tickets",
  "errors": [
    {
      "field": "nationalId",
      "message": "Formato de RUT inválido"
    },
    {
      "field": "telefono",
      "message": "Teléfono debe tener formato +56XXXXXXXXX"
    }
  ]
}
```

### Ticket No Encontrado (404 Not Found)
```json
{
  "timestamp": "2025-12-24T11:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Ticket not found with id: 550e8400-e29b-41d4-a716-446655440000",
  "path": "/api/tickets/550e8400-e29b-41d4-a716-446655440000"
}
```

### Regla de Negocio Violada (422 Unprocessable Entity)
```json
{
  "timestamp": "2025-12-24T11:30:00",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Cliente ya tiene un ticket activo",
  "path": "/api/tickets",
  "businessRule": "RN-001"
}
```

---

## Ejemplos de Uso

### Flujo Completo: Crear y Consultar Ticket

```bash
# 1. Crear ticket
TICKET_RESPONSE=$(curl -s -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Consulta cuenta corriente",
    "descripcion": "Información sobre movimientos del último mes",
    "usuarioId": 12345,
    "nationalId": "12345678-9",
    "telefono": "+56912345678",
    "branchOffice": "Sucursal Centro",
    "queueType": "PERSONAL_BANKER"
  }')

# 2. Extraer UUID del ticket creado
TICKET_UUID=$(echo $TICKET_RESPONSE | jq -r '.codigoReferencia')

# 3. Consultar ticket por UUID
curl -X GET http://localhost:8080/api/tickets/$TICKET_UUID

# 4. Consultar historial de auditoría
TICKET_NUMBER=$(echo $TICKET_RESPONSE | jq -r '.numero')
curl -X GET http://localhost:8080/api/audit/ticket/$TICKET_NUMBER
```

### Monitoreo del Dashboard

```bash
# Script de monitoreo cada 5 segundos
while true; do
  echo "=== Dashboard $(date) ==="
  curl -s http://localhost:8080/api/dashboard/summary | jq '.'
  echo ""
  curl -s http://localhost:8080/api/queues/stats | jq '.'
  echo ""
  sleep 5
done
```

### Consulta de Auditoría por Período

```bash
# Eventos de hoy
TODAY=$(date +%Y-%m-%d)
curl -X GET "http://localhost:8080/api/audit/summary?startDate=$TODAY&endDate=$TODAY" | jq '.'

# Eventos por tipo
curl -X GET "http://localhost:8080/api/audit/events?eventType=TICKET_CREATED&startDate=${TODAY}T00:00:00&endDate=${TODAY}T23:59:59" | jq '.'
```

---

## Tipos de Datos

### Enumeraciones

**QueueType:**
- `CAJA` (Prioridad: 1, Tiempo: 5 min)
- `PERSONAL_BANKER` (Prioridad: 2, Tiempo: 15 min)
- `EMPRESAS` (Prioridad: 3, Tiempo: 20 min)
- `GERENCIA` (Prioridad: 4, Tiempo: 30 min)

**TicketStatus:**
- `WAITING` - En espera
- `NOTIFIED` - Notificado (posición ≤ 3)
- `CALLED` - Llamado a atención
- `IN_SERVICE` - En atención
- `COMPLETED` - Completado
- `CANCELLED` - Cancelado
- `NO_SHOW` - No se presentó

**ActorType:**
- `SYSTEM` - Sistema automático
- `CLIENT` - Cliente
- `ADVISOR` - Asesor
- `SUPERVISOR` - Supervisor

---

## Notas Técnicas

### Performance
- **Timeout:** 30 segundos por request
- **Rate Limiting:** No implementado (desarrollo)
- **Caching:** No implementado (desarrollo)

### Seguridad
- **HTTPS:** Requerido en producción
- **CORS:** Configurado para desarrollo
- **Input Validation:** Bean Validation automática

### Monitoreo
- **Health Check:** `GET /actuator/health`
- **Metrics:** `GET /actuator/metrics`
- **Info:** `GET /actuator/info`

---

**Documentación generada:** 2025-12-24  
**Versión API:** 1.0  
**Mantenido por:** Equipo Sistema Ticketero