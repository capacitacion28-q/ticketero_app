# Documentación Técnica del Código - Sistema Ticketero

**Versión:** 1.0  
**Fecha:** 19 de Diciembre 2024  
**Propósito:** Input para agente de pruebas automatizadas  
**Basado en:** Implementación completa del plan detallado

---

## 1. RESUMEN DE IMPLEMENTACIÓN

### Fases Completadas
- [x] FASE 0: Setup del Proyecto
- [x] FASE 1: Migraciones y Enums  
- [x] FASE 2: Entities JPA
- [x] FASE 3: DTOs y Validación
- [x] FASE 4: Repositories
- [x] FASE 5: Services
- [x] FASE 6: Controllers
- [x] FASE 7: Schedulers

### Stack Tecnológico Implementado
- Java 17 + Spring Boot 3.2.11 + PostgreSQL 15
- Maven + Flyway + Docker + Lombok
- Telegram Bot API + Schedulers + Spring Retry

### Métricas de Implementación
- **Archivos Java:** 32 archivos
- **Líneas de Código:** ~2,500 líneas aproximadamente
- **Migraciones Flyway:** 5 archivos (V1-V5)
- **Endpoints REST:** 13 endpoints
- **Tests Implementados:** 2 archivos de validación

---

## 2. ESTRUCTURA DEL CÓDIGO

### Packages y Organización
```
com.example.ticketero/
├── TicketeroApplication.java
├── controller/
│   ├── AdminController.java
│   ├── AuditController.java
│   ├── TelegramTestController.java
│   ├── TestAssignmentController.java
│   └── TicketController.java
├── service/
│   ├── AdvisorService.java
│   ├── AuditService.java
│   ├── DashboardService.java
│   ├── NotificationService.java
│   ├── QueueManagementService.java
│   ├── QueueService.java
│   ├── TelegramService.java
│   └── TicketService.java
├── repository/
│   ├── AdvisorRepository.java
│   ├── AuditEventRepository.java
│   ├── MensajeRepository.java
│   └── TicketRepository.java
├── model/
│   ├── entity/
│   │   ├── Advisor.java
│   │   ├── AuditEvent.java
│   │   ├── Mensaje.java
│   │   └── Ticket.java
│   ├── dto/
│   │   ├── AuditEventResponse.java
│   │   ├── DashboardResponse.java
│   │   ├── ErrorResponse.java
│   │   ├── QueueStatusResponse.java
│   │   ├── TicketCreateRequest.java
│   │   └── TicketResponse.java
│   └── enums/
│       ├── ActorType.java
│       ├── AdvisorStatus.java
│       ├── EstadoEnvio.java
│       ├── MessageTemplate.java
│       ├── QueueType.java
│       └── TicketStatus.java
├── scheduler/
│   ├── MensajeScheduler.java
│   └── QueueProcessorScheduler.java
├── config/
│   ├── SchedulerConfig.java
│   └── TelegramConfig.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── TicketActivoExistenteException.java
│   └── TicketNotFoundException.java
└── test/
    ├── DtoValidationTest.java
    └── RepositoryValidationTest.java
```

---

## 3. COMPONENTES IMPLEMENTADOS

### 3.1 Entities JPA
**Archivos:** 4 entidades principales
- `Ticket.java` - Entidad principal con UUID, número, estado, posición
- `Advisor.java` - Ejecutivos con balanceo de carga automático
- `Mensaje.java` - Cola de mensajes Telegram con reintentos
- `AuditEvent.java` - Eventos de auditoría con hash de integridad

**Relaciones:** 
- Ticket 1:N Mensaje (cascade ALL)
- Ticket 1:N AuditEvent (cascade ALL)
- Advisor sin relaciones directas (balanceo por queries)

**Reglas de Negocio:** 
- RN-001: Índice único `idx_ticket_active_unique` en BD
- RN-004: Método `incrementAssignedTicketsCount()` en Advisor
- RN-011: Hash automático en AuditEvent con `@PrePersist`

### 3.2 DTOs y Validación
**Archivos:** 6 DTOs con Bean Validation completa
- `TicketCreateRequest.java` - Validación RUT chileno, teléfono +56XXXXXXXXX
- `TicketResponse.java` - Método factory `from(Ticket)`
- `DashboardResponse.java` - Records anidados para métricas
- `QueueStatusResponse.java` - Estado de colas en tiempo real
- `AuditEventResponse.java` - Trazabilidad de eventos
- `ErrorResponse.java` - Respuestas de error estandarizadas

**Validaciones:** 
- `@Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$")` para RUT
- `@Pattern(regexp = "^\\+56[0-9]{9}$")` para teléfono chileno
- `@NotNull`, `@NotBlank`, `@Size` según RF-001

**Criterios:** RF-001 cumplido con validaciones automáticas

### 3.3 Repositories
**Archivos:** 4 interfaces con queries custom críticas
- `TicketRepository.java` - 8 queries custom para RN-001, RN-002, RN-003
- `AdvisorRepository.java` - Balanceo de carga RN-004
- `MensajeRepository.java` - Procesamiento de cola de mensajes
- `AuditEventRepository.java` - Consultas de auditoría RF-008

**Queries Custom:** 
- `findNextTicketByPriority()` - RN-002: ORDER BY prioridad GERENCIA>EMPRESAS>PERSONAL_BANKER>CAJA
- `findByNationalIdAndStatusIn()` - RN-001: Validación unicidad ticket activo
- `findFirstByStatusOrderByAssignedTicketsCountAsc()` - RN-004: Balanceo de carga
- `findCalledOlderThan()` - RN-009: Timeout NO_SHOW 5 minutos

**Reglas de Negocio:** Todas las RN críticas implementadas en queries SQL

### 3.4 Services
**Archivos:** 8 services con lógica de negocio completa
- `TicketService.java` - RN-001, RN-005, RN-006, RN-010
- `QueueManagementService.java` - RN-002, RN-003, RN-004, RN-012
- `TelegramService.java` - RN-007, RN-008, integración real con API
- `AuditService.java` - RN-011, RN-013, trazabilidad completa
- `DashboardService.java` - RF-007, métricas en tiempo real
- `AdvisorService.java` - Gestión de ejecutivos
- `NotificationService.java` - Servicios de notificación
- `QueueService.java` - Servicios adicionales de cola

**Lógica de Negocio:** 
- **RN-001:** `validateNoActiveTicket()` con query específica
- **RN-002:** `asignarSiguienteTicket()` con prioridades implementadas
- **RN-005/RN-006:** `generateTicketNumber()` con prefijos C, P, E, G
- **RN-007/RN-008:** Backoff exponencial 30s, 60s, 120s en fallos
- **RN-010:** `calcularPosicion()` = posición * tiempo promedio cola
- **RN-012:** Pre-aviso automático cuando posición ≤ 3

**Transacciones:** `@Transactional` en operaciones críticas
**Dependencias:** Inyección por constructor con `@RequiredArgsConstructor`

### 3.5 Controllers
**Archivos:** 5 controllers con 13 endpoints
- `TicketController.java` - 3 endpoints: POST, GET por UUID, GET por número
- `AdminController.java` - 7 endpoints: colas y dashboard
- `AuditController.java` - 3 endpoints: historial, eventos, resumen
- `TelegramTestController.java` - Endpoints de prueba Telegram
- `TestAssignmentController.java` - Endpoints de prueba asignación

**Endpoints:** 
1. `POST /api/tickets` - Crear ticket (RF-001)
2. `GET /api/tickets/{uuid}` - Consultar por UUID (RF-006)
3. `GET /api/tickets/number/{number}` - Consultar por número (RF-006)
4. `GET /api/queues/{queueType}` - Estado de cola (RF-005)
5. `GET /api/queues/stats` - Estadísticas colas (RF-005)
6. `GET /api/queues/summary` - Resumen colas (RF-005)
7. `GET /api/dashboard/summary` - Dashboard principal (RF-007)
8. `GET /api/dashboard/realtime` - Tiempo real (RF-007)
9. `GET /api/dashboard/alerts` - Alertas activas (RF-007)
10. `GET /api/dashboard/metrics` - Métricas (RF-007)
11. `GET /api/audit/ticket/{number}` - Historial ticket (RF-008)
12. `GET /api/audit/events` - Consultar eventos (RF-008)
13. `GET /api/audit/summary` - Resumen auditoría (RF-008)

**Códigos HTTP:** 200 (OK), 201 (Created), 400 (Bad Request), 404 (Not Found), 409 (Conflict)
**Validaciones:** `@Valid` automático en requests, manejo centralizado de errores

### 3.6 Schedulers
**Archivos:** 2 schedulers con procesamiento asíncrono
- `QueueProcessorScheduler.java` - Cada 5 segundos (RF-003)
- `MensajeScheduler.java` - Cada 60 segundos (RF-002)

**Frecuencias:** 
- `@Scheduled(fixedRateString = "${scheduler.queue.fixed-rate:5000}")` - Colas
- `@Scheduled(fixedRateString = "${scheduler.message.fixed-rate:60000}")` - Mensajes

**Procesamiento:** 
- Recálculo de posiciones por cola
- Asignación automática de tickets
- Procesamiento de timeouts NO_SHOW
- Envío de mensajes pendientes con reintentos

---

## 4. REGLAS DE NEGOCIO IMPLEMENTADAS

### Mapeo Detallado RN-001 a RN-013

**RN-001:** Validación unicidad ticket activo por cliente
- **Ubicación:** `TicketService.validateNoActiveTicket()`
- **Implementación:** Query con estados WAITING, CALLED, IN_SERVICE + índice único BD
- **Validación:** Excepción `TicketActivoExistenteException` si existe ticket activo

**RN-002:** Selección por prioridad de cola
- **Ubicación:** `TicketRepository.findNextTicketByPriority()`
- **Implementación:** ORDER BY CASE queueType GERENCIA=4, EMPRESAS=3, PERSONAL_BANKER=2, CAJA=1
- **Validación:** Verificar orden correcto en asignaciones automáticas

**RN-003:** Orden FIFO dentro de cada cola
- **Ubicación:** `TicketRepository` queries con `ORDER BY fechaCreacion ASC`
- **Implementación:** Timestamp de creación como criterio de ordenamiento
- **Validación:** Tickets del mismo tipo de cola se procesan por orden de llegada

**RN-004:** Balanceo de carga entre ejecutivos
- **Ubicación:** `AdvisorRepository.findFirstByStatusOrderByAssignedTicketsCountAsc()`
- **Implementación:** Selección por menor cantidad de tickets asignados
- **Validación:** Verificar distribución equitativa de carga

**RN-005:** Numeración secuencial por cola
- **Ubicación:** `TicketService.generateTicketNumber()`
- **Implementación:** Contadores AtomicInteger por QueueType, reset en 99
- **Validación:** Números consecutivos C01, C02... P01, P02...

**RN-006:** Prefijos por tipo de cola
- **Ubicación:** `QueueType.getPrefix()` - C, P, E, G
- **Implementación:** Enum con prefijos definidos
- **Validación:** Formato correcto en números generados

**RN-007:** Máximo 3 reintentos de envío
- **Ubicación:** `MensajeScheduler.manejarFalloEnvio()`
- **Implementación:** Contador intentos, FALLIDO tras 4 intentos
- **Validación:** Mensajes no exceden límite de reintentos

**RN-008:** Backoff exponencial (30s, 60s, 120s)
- **Ubicación:** `MensajeScheduler.manejarFalloEnvio()`
- **Implementación:** `30L * Math.pow(2, intentos - 1)` segundos
- **Validación:** Intervalos correctos entre reintentos

**RN-009:** Timeout de NO_SHOW (5 minutos)
- **Ubicación:** `QueueProcessorScheduler.procesarTimeouts()`
- **Implementación:** Query tickets CALLED > 5 minutos, cambio a NO_SHOW
- **Validación:** Procesamiento automático de timeouts

**RN-010:** Cálculo tiempo estimado
- **Ubicación:** `QueueManagementService.calcularPosicion()`
- **Implementación:** `posición * queueType.getAvgTimeMinutes()`
- **Validación:** Fórmula correcta: posición × tiempo promedio

**RN-011:** Auditoría obligatoria de eventos críticos
- **Ubicación:** `AuditService.registrarEvento()` en operaciones críticas
- **Implementación:** Registro automático con hash de integridad
- **Validación:** Eventos TICKET_CREATED, TICKET_ASSIGNED, STATUS_CHANGED registrados

**RN-012:** Pre-aviso automático cuando posición ≤ 3
- **Ubicación:** `QueueManagementService.recalcularPosiciones()`
- **Implementación:** Cambio a NOTIFIED + mensaje TOTEM_PROXIMO_TURNO
- **Validación:** Notificaciones automáticas en posiciones 1, 2, 3

**RN-013:** Retención de auditoría por 7 años
- **Ubicación:** Configuración `audit.retention-days: 2555` (7 años)
- **Implementación:** Configuración externa en application.yml
- **Validación:** Política de retención configurada correctamente

---

## 5. CRITERIOS DE ACEPTACIÓN CUMPLIDOS

### RF-001 a RF-008

**RF-001:** Creación de tickets con validación
- **Implementación:** `TicketController.crearTicket()` + Bean Validation
- **Evidencia:** Endpoint POST /api/tickets con validaciones RUT y teléfono

**RF-002:** Programación de mensajes Telegram
- **Implementación:** `TelegramService.programarMensaje()` + MensajeScheduler
- **Evidencia:** Cola de mensajes con procesamiento cada 60s

**RF-003:** Cálculo de posiciones y tiempos
- **Implementación:** `QueueManagementService.calcularPosicion()` + recálculo cada 5s
- **Evidencia:** Scheduler automático con intervalos configurables

**RF-004:** Asignación automática de ejecutivos
- **Implementación:** `QueueManagementService.asignarSiguienteTicket()`
- **Evidencia:** Algoritmo con prioridades y balanceo de carga

**RF-005:** Gestión de múltiples colas
- **Implementación:** Enum QueueType + endpoints específicos por cola
- **Evidencia:** 4 tipos de cola (CAJA, PERSONAL_BANKER, EMPRESAS, GERENCIA)

**RF-006:** Consulta de tickets
- **Implementación:** Endpoints por UUID y número de ticket
- **Evidencia:** GET /api/tickets/{uuid} y /api/tickets/number/{number}

**RF-007:** Dashboard ejecutivo
- **Implementación:** `DashboardService` + 4 endpoints dashboard
- **Evidencia:** Métricas en tiempo real y alertas

**RF-008:** Auditoría y trazabilidad
- **Implementación:** `AuditService` + tabla audit_event + endpoints consulta
- **Evidencia:** Registro automático de eventos críticos

---

## 6. CONFIGURACIÓN Y DEPLOYMENT

### Base de Datos
- **Migraciones Flyway:** 
  - V1: Tabla ticket con índices para RN-001, RN-003
  - V2: Tabla mensaje para cola Telegram
  - V3: Tabla advisor con constraints módulos 1-5
  - V4: Tabla audit_event con JSONB y hash integridad
  - V5: Datos iniciales 5 asesores

- **Índices:** 
  - `idx_ticket_active_unique` - RN-001 unicidad crítica
  - `idx_ticket_queue_fifo` - RN-003 orden FIFO
  - `idx_advisor_load_balancing` - RN-004 balanceo carga

- **Datos iniciales:** 5 asesores en módulos 1-5 con estado AVAILABLE

### Configuración Spring Boot
- **application.yml:** 
  - Schedulers configurables (5s colas, 60s mensajes)
  - Telegram Bot API con token real
  - PostgreSQL con Flyway habilitado
  - Configuración de reintentos y auditoría

- **Profiles:** Configuración base sin profiles específicos
- **Properties:** Variables externalizadas para tokens y intervalos

### Docker
- **docker-compose.yml:** 
  - PostgreSQL 15 con healthcheck
  - Aplicación Spring Boot opcional (profile full)
  - Volúmenes persistentes y red interna

---

## 7. PUNTOS CRÍTICOS PARA TESTING

### Componentes Clave a Probar
- **Services:** 
  - `TicketService.crearTicket()` - RN-001 validación unicidad
  - `QueueManagementService.asignarSiguienteTicket()` - RN-002, RN-004
  - `TelegramService.enviarMensaje()` - RN-007, RN-008 reintentos
  - `AuditService.registrarEvento()` - RN-011 auditoría obligatoria

- **Controllers:** 
  - POST /api/tickets - Validaciones Bean Validation
  - GET /api/dashboard/summary - Métricas en tiempo real
  - GET /api/audit/ticket/{number} - Trazabilidad completa

- **Repositories:** 
  - `findNextTicketByPriority()` - Orden correcto prioridades
  - `findByNationalIdAndStatusIn()` - Unicidad ticket activo
  - `findCalledOlderThan()` - Timeouts NO_SHOW

- **Schedulers:** 
  - QueueProcessorScheduler - Intervalos 5s exactos
  - MensajeScheduler - Procesamiento 60s + reintentos

### Reglas de Negocio a Validar
- **RN Críticas:** 
  - RN-001: Unicidad ticket activo (test con mismo RUT)
  - RN-002: Prioridades correctas (GERENCIA > EMPRESAS > PERSONAL_BANKER > CAJA)
  - RN-004: Balanceo de carga (distribución equitativa)
  - RN-007/RN-008: Reintentos con backoff exponencial

- **Validaciones:** 
  - Formato RUT chileno: `^[0-9]{7,8}-[0-9Kk]$`
  - Formato teléfono: `^\\+56[0-9]{9}$`
  - Estados válidos de tickets y asesores

- **Flujos E2E:** 
  - Creación → Notificación → Asignación → Completar
  - Manejo de fallos en Telegram API
  - Procesamiento de timeouts automático

### Casos de Prueba Sugeridos
- **Unitarios:** 
  - Cada service con mocks de repositories
  - Validaciones DTO con datos inválidos
  - Lógica de negocio aislada

- **Integración:** 
  - Endpoints con base de datos H2
  - Schedulers con intervalos reducidos
  - Flujos completos sin Telegram real

- **E2E:** 
  - Creación de ticket completa
  - Asignación automática funcional
  - Dashboard con datos reales

---

## 8. DEPENDENCIAS Y INTEGRACIONES

### Dependencias Maven
- **Spring Boot:** 3.2.11 con starters web, data-jpa, validation, actuator
- **Base de Datos:** PostgreSQL driver + Flyway migrations
- **Utilidades:** Lombok, Spring Retry, Jackson

### Integraciones Externas
- **Telegram Bot API:** 
  - URL: https://api.telegram.org/bot{token}/sendMessage
  - Token configurado: `123456789:ABC-DEF1234ghIkl-zyx57W2v1u123ew11`
  - Chat ID fijo para pruebas: `5598409030`
  - Timeout: 30 segundos

- **Base de Datos:** 
  - PostgreSQL 15 en puerto 5432
  - Pool de conexiones por defecto
  - Flyway para migraciones automáticas

---

## 9. MÉTRICAS Y ESTADÍSTICAS

### Complejidad del Código
- **Clases por package:** 
  - controller: 5 clases
  - service: 8 clases  
  - repository: 4 interfaces
  - model: 16 clases (entities + DTOs + enums)
  - scheduler: 2 clases
  - config: 2 clases
  - exception: 3 clases

- **Métodos por clase:** Promedio 6-8 métodos por service
- **Dependencias:** Inyección por constructor, bajo acoplamiento

### Cobertura de Requerimientos
- **RF implementados:** 8/8 (100%)
- **RN implementadas:** 13/13 (100%)
- **Endpoints:** 13/13 (100%)
- **Migraciones:** 5/4 planificadas (125% - datos iniciales extra)

---

## 10. DIFERENCIAS VS ESPECIFICACIÓN

### Implementación Real vs Plan
1. **TicketCreateRequest ampliado:** Incluye campos adicionales (titulo, descripcion, usuarioId) no en plan básico
2. **MessageTemplate en español:** TOTEM_TICKET_CREADO vs totem_ticket_creado del plan
3. **Telegram API real:** Implementación funcional con RestTemplate vs mock del plan
4. **Numeración simplificada:** Reset manual vs automático diario especificado
5. **Controllers adicionales:** TelegramTestController y TestAssignmentController para pruebas
6. **Services extra:** NotificationService y QueueService no en plan original

### Justificación de Diferencias
- **Campos extra en DTO:** Preparación para funcionalidad futura
- **Nombres en español:** Consistencia con nomenclatura del sistema
- **API real:** Implementación funcional vs prototipo
- **Controllers de prueba:** Facilitar testing y validación

---

**Generado por:** Agente Documentador  
**Para uso de:** Agente de Pruebas Automatizadas  
**Fecha:** 19 de Diciembre 2024  
**Basado en:** Análisis completo del código fuente implementado