# PROMPT REFINADO: Arquitectura - Diseño de Alto Nivel del Sistema Ticketero

**Versión:** 1.0  
**Fecha:** Diciembre 2025  
**Metodología:** Basada en Prompt Refinement Master v1.0  
**Ingeniero de Prompts:** Amazon Q Developer

---

## CONTEXTO

Eres un **Arquitecto de Software Senior** especializado en sistemas empresariales financieros con 10+ años de experiencia en:
- Arquitecturas de microservicios y sistemas distribuidos
- Integración de APIs externas (Telegram, servicios de mensajería)
- Bases de datos transaccionales de alta disponibilidad
- Sistemas de colas y procesamiento asíncrono
- Cumplimiento normativo en instituciones financieras

**Objetivo:** Diseñar la arquitectura de alto nivel del Sistema Ticketero basándote en el documento de Requerimientos Funcionales aprobado, garantizando escalabilidad, confiabilidad y alineación con las 13 reglas de negocio definidas.

**IMPORTANTE:** Después de completar CADA paso, debes DETENERTE y solicitar revisión exhaustiva antes de continuar.

---

## DOCUMENTOS DE ENTRADA

**Archivo principal:** `docs/requirements/functional_requirements_analysis_v1.0.md`

**Contenido clave del documento:**
- 8 Requerimientos Funcionales (RF-001 a RF-008) con 48+ escenarios Gherkin
- 13 Reglas de Negocio transversales (RN-001 a RN-013)
- 4 Enumeraciones del sistema (QueueType, TicketStatus, AdvisorStatus, MessageTemplate)
- 4 Entidades principales (Ticket, Mensaje, Advisor, AuditEvent)
- 13 Endpoints HTTP especificados
- Beneficios de negocio: NPS 45→65, abandonos 15%→5%, +20% productividad

**Archivo de contexto:** `docs/requirements/requerimientos_negocio.md`

**Contenido de contexto:**
- Descripción del problema de negocio y solución propuesta
- Flujo detallado del proceso en 20 pasos
- 7 Requerimientos No Funcionales (RNF-001 a RNF-007)
- Escalabilidad: Fase Piloto (500-800 tickets/día) → Fase Nacional (25,000+ tickets/día)

**Validación previa:** Confirma que ambos archivos existen y revisa las RN y enumeraciones antes de iniciar el diseño.

---

## METODOLOGÍA DE TRABAJO

### Principio Fundamental
**"Diseñar → Validar → Confirmar → Continuar"**

### Proceso Obligatorio por Paso

**1. DISEÑAR**
- Crear componente arquitectónico según requerimientos funcionales
- Alinear con reglas de negocio específicas del documento

**2. VALIDAR**
- Verificar que es renderizable/técnicamente correcto
- Revisar alineación con RF y RN del documento fuente

**3. CONFIRMAR**
- Solicitar revisión con formato estándar
- Esperar aprobación explícita antes de continuar

**4. CONTINUAR**
- Avanzar solo tras confirmación positiva

---

### Template de Solicitud de Revisión

```
✅ PASO [X] COMPLETADO

**Componente:** [Nombre del componente arquitectónico]

**Alineación validada:**
□ Reglas de negocio: [RN-XXX, RN-YYY] aplicadas
□ Requerimientos: [RF-XXX] cubiertos
□ Renderizable: Diagramas PlantUML válidos
□ Justificación: Decisiones técnicas fundamentadas

🔍 **¿APROBADO PARA CONTINUAR?**
```

### Criterios de Validación Arquitectónica

**Para cada componente debe incluir:**
- Alineación específica con RN del documento fuente
- Justificación técnica sólida con alternativas consideradas
- Diagramas renderizables (PlantUML válido cuando aplique)
- Decisiones coherentes con volumen y contexto financiero
- Referencias explícitas a RF cubiertos

---

## TU TAREA

Crear un documento de **Arquitectura de Software** profesional basado en el análisis de requerimientos funcionales completado.

### Estructura de Implementación

**7 pasos secuenciales con revisión obligatoria:**

| Paso | Componente | Entregable | Alineación con RF |
|------|-----------|------------|-------------------|
| 1 | Stack Tecnológico | Justificaciones técnicas | Volumen esperado (25K tickets/día) |
| 2 | Diagrama C4 Contexto | PlantUML renderizable | RF-001, RF-002, RF-007 |
| 3 | Diagrama Secuencia | Flujo end-to-end | RF-001→RF-002→RF-004 |
| 4 | Modelo Datos ER | 4 entidades + relaciones | Ticket, Mensaje, Advisor, AuditEvent |
| 5 | Arquitectura Capas | 5 capas + componentes | Separación responsabilidades |
| 6 | Decisiones ADR | 5 ADRs documentadas | Justificación técnica vs alternativas |
| 7 | Configuración Final | Variables + validación | Deployment y ubicación documento |

### Criterio de Completitud por Paso

Cada paso se considera completo cuando incluye:
- ✅ Alineación específica con RN del documento fuente
- ✅ Justificación técnica sólida con alternativas consideradas
- ✅ Diagramas renderizables (PlantUML válido)
- ✅ Decisiones coherentes con volumen y contexto financiero
- ✅ Aprobación explícita del revisor

### Uso de Documentos de Entrada

**Documento principal:** Usar `functional_requirements_analysis_v1.0.md` para:
- Extraer 13 RN específicas para aplicar en decisiones arquitectónicas
- Mapear 4 entidades de datos a modelo ER
- Alinear 13 endpoints HTTP con componentes
- Considerar 48+ escenarios Gherkin para casos de uso

**Documento de contexto:** Usar `requerimientos_negocio.md` para:
- Entender escalabilidad por fases (500 → 25,000 tickets/día)
- Considerar RNF en decisiones técnicas
- Validar flujo de 20 pasos en diagrama de secuencia

---

## PASO 1: Stack Tecnológico con Justificaciones

### Objetivo
Seleccionar y justificar todas las tecnologías del proyecto considerando el volumen esperado (25,000 tickets/día), contexto financiero y requerimientos no funcionales.

### Fuente de Información
**Del documento:** `docs/requirements/functional_requirements_analysis_v1.0.md`
- Volumen: 25,000+ tickets/día en fase nacional
- RNF-004: 99.9% mensajes entregados, 3 reintentos
- RNF-002: Creación ticket <3s, envío mensaje <5s
- Contexto: Institución financiera (compliance, seguridad)

### Tecnologías a Justificar

**1.1 Backend Framework**
- Evaluar: Java + Spring Boot vs Node.js vs .NET Core
- Criterios: Madurez ecosistema, soporte transaccional, adopción financiera

**1.2 Base de Datos**
- Evaluar: PostgreSQL vs MySQL vs Oracle
- Criterios: ACID compliance, JSONB, escalabilidad, costos

**1.3 Migraciones de BD**
- Evaluar: Flyway vs Liquibase
- Criterios: Simplicidad, integración, versionado

**1.4 Integración Telegram**
- Evaluar: RestTemplate vs WebClient vs HTTP Client nativo
- Criterios: Simplicidad vs performance para volumen esperado

**1.5 Containerización**
- Evaluar: Docker vs alternativas
- Criterios: Paridad dev/prod, adopción, complejidad

**1.6 Build Tool**
- Evaluar: Maven vs Gradle
- Criterios: Convención, ecosistema, curva aprendizaje

### Criterios de Validación

**Cuantitativos:**
□ 6 tecnologías seleccionadas y justificadas
□ Tabla de alternativas con pros/contras para cada una
□ Justificaciones alineadas con volumen (25K tickets/día)
□ Consideración de contexto financiero (compliance, seguridad)

**Cualitativos:**
□ Decisiones técnicamente sólidas
□ Principio 80/20 aplicado (simplicidad vs complejidad)
□ Alineación con RNF del documento fuente
□ Justificaciones específicas, no genéricas

### Reglas de Negocio Aplicables
- **RN-007, RN-008:** Reintentos y backoff exponencial (afecta integración Telegram)
- **RN-011:** Auditoría obligatoria (afecta selección de BD y logging)
- **Volumen esperado:** 25K tickets/día → 75K mensajes/día (0.9 msg/segundo)

---

## PASO 2: Diagrama de Contexto C4

### Objetivo
Crear diagrama C4 Level 1 mostrando el sistema en su contexto con actores y sistemas externos, basado estrictamente en los RF del documento `functional_requirements_analysis_v1.0.md`.

### Fuente de Información Específica
**Del documento:** `docs/requirements/functional_requirements_analysis_v1.0.md`

**RF-001 (Crear Ticket Digital):**
- Actor: Cliente ingresa RUT/ID y selecciona tipo de atención
- Sistema: Terminal Autoservicio para emisión
- Endpoint: POST /api/tickets

**RF-002 (Notificaciones Telegram):**
- Sistema Externo: Telegram Bot API
- 3 mensajes automáticos: confirmación, pre-aviso, turno activo
- Plantillas: totem_ticket_creado, totem_proximo_turno, totem_es_tu_turno

**RF-007 (Panel de Monitoreo):**
- Actor: Supervisor de Sucursal
- Endpoints: GET /api/dashboard/*, GET /api/admin/*
- Actualización cada 5 segundos

### Elementos Obligatorios (Basados en RF)

**2.1 Actores (Person) - Según RF del documento:**
- **Cliente/Socio:** "Persona que requiere atención en sucursal" (RF-001, RF-006)
- **Supervisor de Sucursal:** "Monitorea operación en tiempo real" (RF-007)

**2.2 Sistema Principal:**
- **API Ticketero:** "Sistema de gestión de tickets con notificaciones en tiempo real"
- Cubre: RF-001 a RF-008 completos

**2.3 Sistemas Externos - Según RF del documento:**
- **Telegram Bot API:** "Servicio de mensajería para notificaciones push" (RF-002)
- **Terminal Autoservicio:** "Kiosco para emisión de tickets" (RF-001)

**2.4 Relaciones Específicas - Según endpoints del documento:**
- Cliente → Terminal: "Ingresa RUT y selecciona servicio"
- Terminal → API: "POST /api/tickets" (RF-001)
- API → Telegram: "Envía 3 notificaciones automáticas" (RF-002)
- Telegram → Cliente: "Recibe mensajes de estado"
- Supervisor → API: "GET /api/dashboard/*, GET /api/admin/*" (RF-007)

### Criterios de Validación Estricta

**Alineación con Documento:**
□ Actores exactos según RF-001 y RF-007
□ Sistemas externos según RF-001 y RF-002
□ Relaciones basadas en 13 endpoints HTTP del documento
□ Descripciones consistentes con terminología del documento

**Técnicos:**
□ Diagrama PlantUML renderizable
□ Biblioteca C4-PlantUML estándar
□ SHOW_LEGEND() incluida
□ Protocolos específicos (HTTPS/JSON, REST API)

### Ubicación de Archivos
- **Archivo fuente:** `docs/architecture/diagrams/01-context-diagram.puml`
- **Embebido en:** Sección 3.1 del documento de arquitectura

### Reglas de Negocio del Documento Aplicables
- **RN-002:** Prioridad de colas (GERENCIA, EMPRESAS, PERSONAL_BANKER, CAJA)
- **RN-012:** Pre-aviso cuando posición ≤ 3 (flujo Telegram)
- **4 Enumeraciones:** QueueType, TicketStatus, AdvisorStatus, MessageTemplate

### Instrucciones de Implementación
1. **Generar código PlantUML completo** usando elementos definidos arriba
2. **Crear archivo:** `docs/architecture/diagrams/01-context-diagram.puml`
3. **Embeber en documento:** Sección 3.1 Diagrama de Contexto C4
4. **Verificar renderizado:** http://www.plantuml.com/plantuml/
5. **Incluir nota:** Referencias a archivo fuente y herramientas de visualización

---

## PASO 3: Diagrama de Secuencia del Flujo Completo

### Objetivo
Crear diagrama de secuencia mostrando el flujo end-to-end desde creación hasta completar atención, basado estrictamente en los RF del documento `functional_requirements_analysis_v1.0.md`.

### Fuente de Información Específica
**Del documento:** `docs/requirements/functional_requirements_analysis_v1.0.md`

**Flujo RF-001→RF-002→RF-003→RF-004:**
- RF-001: Cliente crea ticket → sistema calcula posición
- RF-002: 3 mensajes automáticos (confirmación, pre-aviso, turno activo)
- RF-003: Recálculo de posiciones en tiempo real
- RF-004: Asignación automática a ejecutivo disponible

### Fases Obligatorias (Basadas en RF del Documento)

**3.1 Creación de Ticket (RF-001)**
- Cliente → Terminal → Controller → Service → DB
- Aplicar RN-001 (unicidad ticket activo)
- Generar número según RN-005 (formato [Prefijo][01-99]) y RN-006 (prefijos por cola)
- Calcular posición según RN-010 (fórmula tiempo estimado)

**3.2 Mensaje 1 - Confirmación (RF-002)**
- Scheduler → DB → TelegramService → Telegram API
- Plantilla: totem_ticket_creado (según documento)
- Aplicar RN-007 (3 reintentos) y RN-008 (backoff exponencial 30s, 60s, 120s)

**3.3 Progreso de Cola (RF-003)**
- QueueProcessor → recálculo posiciones según RN-003 (FIFO)
- Mensaje 2 cuando posición ≤ 3 según RN-012
- Plantilla: totem_proximo_turno

**3.4 Asignación Automática (RF-004)**
- Selección asesor según RN-004 (balanceo de carga)
- Prioridad colas según RN-002 (GERENCIA=4, EMPRESAS=3, PERSONAL_BANKER=2, CAJA=1)
- Estados según RN-013 (AVAILABLE, BUSY, OFFLINE)
- Mensaje 3 con plantilla: totem_es_tu_turno

**3.5 Completar Atención**
- Asesor → Controller → Service → actualización estados
- Auditoría según RN-011 (registro obligatorio de eventos)
- Estados finales según documento: COMPLETED

### Criterios de Validación Estricta

**Alineación con Documento:**
□ 5 fases basadas en RF-001, RF-002, RF-003, RF-004
□ Estados exactos según TicketStatus del documento (WAITING, NOTIFIED, CALLED, IN_SERVICE, COMPLETED)
□ Plantillas exactas según MessageTemplate del documento
□ RN específicas aplicadas en cada fase

**Técnicos:**
□ 8+ participantes (Cliente, Terminal, Controllers, Services, DB, Schedulers, Asesor)
□ 25+ interacciones documentadas
□ Notas explicativas en schedulers
□ Diagrama PlantUML renderizable

### Ubicación de Archivos
- **Archivo fuente:** `docs/architecture/diagrams/02-sequence-diagram.puml`
- **Embebido en:** Sección 3.2 del documento de arquitectura

### Reglas de Negocio del Documento Aplicables
- **RN-001:** Unicidad ticket activo por cliente
- **RN-002:** Prioridad colas (GERENCIA > EMPRESAS > PERSONAL_BANKER > CAJA)
- **RN-003:** Orden FIFO dentro de cada cola
- **RN-004:** Balanceo de carga entre asesores
- **RN-005, RN-006:** Formato y prefijos de números
- **RN-007, RN-008:** Reintentos y backoff exponencial
- **RN-010:** Fórmula tiempo estimado = posición × tiempo_promedio
- **RN-011:** Auditoría obligatoria
- **RN-012:** Pre-aviso cuando posición ≤ 3
- **RN-013:** Estados de asesor

### Instrucciones de Implementación
1. **Generar código PlantUML completo** usando fases y RN definidas arriba
2. **Crear archivo:** `docs/architecture/diagrams/02-sequence-diagram.puml`
3. **Embeber en documento:** Sección 3.2 Diagrama de Secuencia
4. **Incluir descripción de fases** después del diagrama
5. **Verificar renderizado:** http://www.plantuml.com/plantuml/

---

## PASO 4: Modelo de Datos ER

### Objetivo
Crear diagrama ER con las 4 entidades del sistema y sus relaciones, basado estrictamente en el modelo de datos del documento `functional_requirements_analysis_v1.0.md`.

### Fuente de Información Específica
**Del documento:** `docs/requirements/functional_requirements_analysis_v1.0.md`

**Entidades definidas en RF:**
- **Ticket (RF-001):** 12 campos especificados
- **Mensaje (RF-002):** 8 campos especificados  
- **Advisor (RF-004):** Campos para asignación y balanceo
- **AuditEvent (RF-008):** Campos para auditoría obligatoria

### Entidades Obligatorias (Según Documento)

**4.1 Ticket (RF-001 - 12+ campos)**
- codigoReferencia: UUID único
- numero: String formato [Prefijo][01-99] según RN-005
- nationalId: String (RUT/ID del cliente)
- telefono: String (formato +56XXXXXXXXX)
- branchOffice: String
- queueType: Enum según QueueType del documento
- status: Enum según TicketStatus del documento
- positionInQueue: Integer
- estimatedWaitMinutes: Integer
- createdAt: Timestamp
- assignedAdvisor: String (null inicial)
- assignedModuleNumber: Integer 1-5 (null inicial)

**4.2 Mensaje (RF-002 - 8 campos)**
- id: BIGSERIAL (primary key)
- ticket_id: BIGINT (foreign key)
- plantilla: String según MessageTemplate del documento
- estadoEnvio: Enum (PENDIENTE, ENVIADO, FALLIDO)
- fechaProgramada: Timestamp
- fechaEnvio: Timestamp (nullable)
- telegramMessageId: String (nullable)
- intentos: Integer (default 0, máximo 3 según RN-007)

**4.3 Advisor (RF-004)**
- id: BIGSERIAL (primary key)
- nombre: String
- status: Enum según AdvisorStatus del documento
- moduleNumber: Integer 1-5
- ticketsAtendidos: Integer (para RN-004 balanceo)
- createdAt: Timestamp
- updatedAt: Timestamp

**4.4 AuditEvent (RF-008)**
- id: BIGSERIAL (primary key)
- timestamp: Timestamp con precisión milisegundos
- eventType: String (según RN-011)
- actor: String
- ticketId: BIGINT (nullable)
- previousState: String (nullable)
- newState: String (nullable)
- additionalData: JSONB (nullable)

### Relaciones (Según RF del Documento)
- **ticket ← mensaje (1:N):** Un ticket tiene múltiples mensajes programados (RF-002)
- **advisor ← ticket (1:N):** Un asesor puede atender múltiples tickets (RF-004)
- **ticket ← audit_event (1:N):** Un ticket genera múltiples eventos de auditoría (RF-008)

### Enumeraciones del Documento
**Según sección 3 del documento:**
- **QueueType:** CAJA, PERSONAL_BANKER, EMPRESAS, GERENCIA
- **TicketStatus:** WAITING, NOTIFIED, CALLED, IN_SERVICE, COMPLETED, CANCELLED, NO_SHOW
- **AdvisorStatus:** AVAILABLE, BUSY, OFFLINE
- **MessageTemplate:** totem_ticket_creado, totem_proximo_turno, totem_es_tu_turno

### Criterios de Validación Estricta

**Alineación con Documento:**
□ 4 entidades documentadas (Ticket, Mensaje, Advisor, AuditEvent)
□ Campos exactos según RF-001, RF-002, RF-004, RF-008
□ Enumeraciones exactas según sección 3 del documento
□ Relaciones basadas en RF específicos

**Técnicos:**
□ 30+ campos totales con tipos PostgreSQL
□ 3+ relaciones (1:N) documentadas
□ Constraints (PK, FK, UQ) marcadas
□ Notas con enumeraciones del documento

### Ubicación de Archivos
- **Archivo fuente:** `docs/architecture/diagrams/03-er-diagram.puml`
- **Embebido en:** Sección 3.3 del documento de arquitectura

### Reglas de Negocio del Documento Aplicables
- **RN-001:** Unicidad ticket activo (afecta índices)
- **RN-005:** Formato número ticket (afecta campo numero)
- **RN-007:** Reintentos mensajes (afecta campo intentos)
- **RN-011:** Auditoría obligatoria (justifica entidad AuditEvent)

### Instrucciones de Implementación
1. **Generar código PlantUML completo** usando entidades y campos definidos arriba
2. **Crear archivo:** `docs/architecture/diagrams/03-er-diagram.puml`
3. **Embeber en documento:** Sección 3.3 Modelo de Datos ER
4. **Incluir descripción de relaciones** e índices importantes
5. **Verificar renderizado:** http://www.plantuml.com/plantuml/

---

## PASO 5: Arquitectura en Capas y Componentes Principales

### Objetivo
Documentar la arquitectura en capas del sistema y los componentes principales, alineados estrictamente con los 13 endpoints HTTP del documento `functional_requirements_analysis_v1.0.md`.

### Fuente de Información Específica
**Del documento:** `docs/requirements/functional_requirements_analysis_v1.0.md`

**13 Endpoints HTTP especificados:**
- POST /api/tickets (RF-001)
- GET /api/tickets/{uuid}, GET /api/tickets/number/{ticketNumber} (RF-006)
- GET /api/queues/{queueType}, GET /api/queues/stats, GET /api/queues/summary (RF-005)
- GET /api/dashboard/*, GET /api/admin/* (RF-007)
- GET /api/audit/* (RF-008)

### Capas Obligatorias (Basadas en RF)

**5.1 Capa de Presentación (Controllers)**
- **TicketController:** Endpoints RF-001, RF-006
  - POST /api/tickets → crearTicket() aplicando RN-001, RN-005, RN-006, RN-010
  - GET /api/tickets/{uuid} → consultarTicket()
- **AdminController:** Endpoints RF-005, RF-007, RF-008
  - GET /api/dashboard/* → dashboard tiempo real (actualización 5s según documento)
  - GET /api/queues/* → gestión múltiples colas
  - GET /api/audit/* → consulta auditoría

**5.2 Capa de Negocio (Services)**
- **TicketService:** Lógica RF-001, RF-003
  - Aplicar RN-001 (unicidad), RN-005/RN-006 (formato), RN-010 (cálculo tiempo)
- **TelegramService:** Integración RF-002
  - Manejar RN-007 (3 reintentos), RN-008 (backoff exponencial)
  - Plantillas según MessageTemplate del documento
- **QueueManagementService:** RF-004, RF-005
  - Aplicar RN-002 (prioridad), RN-003 (FIFO), RN-004 (balanceo)
- **AuditService:** RF-008
  - Aplicar RN-011 (auditoría obligatoria)

**5.3 Capa de Datos (Repositories)**
- TicketRepository, MensajeRepository, AdvisorRepository, AuditEventRepository
- Queries custom para cálculos RF-003 (posición en cola)

**5.4 Capa Asíncrona (Schedulers)**
- **MessageScheduler:** Procesamiento RF-002 cada 60s
- **QueueProcessorScheduler:** RF-003, RF-004 cada 5s según documento

### Criterios de Validación Estricta

**Alineación con Documento:**
□ Mapeo completo a 13 endpoints HTTP del documento
□ Componentes cubren RF-001 a RF-008
□ Referencias específicas a RN aplicables por componente
□ Schedulers con frecuencias según documento (60s, 5s)

**Arquitectónicos:**
□ 5 capas documentadas con responsabilidades claras
□ 8+ componentes principales especificados
□ Separación clara de responsabilidades
□ Dependencias entre componentes especificadas

### Reglas de Negocio del Documento por Componente

**TicketService:**
- RN-001: Unicidad ticket activo
- RN-005, RN-006: Formato y prefijos números
- RN-010: Fórmula tiempo estimado
- RN-011: Auditoría de creación

**TelegramService:**
- RN-007: 3 reintentos automáticos
- RN-008: Backoff exponencial (30s, 60s, 120s)
- RN-012: Pre-aviso cuando posición ≤ 3

**QueueManagementService:**
- RN-002: Prioridad colas (GERENCIA=4, EMPRESAS=3, PERSONAL_BANKER=2, CAJA=1)
- RN-003: Orden FIFO dentro de cola
- RN-004: Balanceo de carga entre asesores
- RN-013: Estados asesor (AVAILABLE, BUSY, OFFLINE)

### Instrucciones de Implementación

**5.1 Crear Diagrama ASCII de Capas**
- Mostrar 5 capas con flujo de datos
- Incluir capa asíncrona (schedulers)

**5.2 Documentar Componentes**
- Responsabilidad específica por componente
- Métodos principales con referencia a RF
- Dependencias entre componentes
- Ejemplos de código para componentes clave

**5.3 Mapeo a Endpoints**
- Cada endpoint mapeado a controller y método específico
- Validaciones según Bean Validation del documento

### Consideraciones Técnicas del Documento
- **Volumen:** 25,000 tickets/día → 75,000 mensajes/día
- **Performance:** Creación ticket <3s, envío mensaje <5s
- **Schedulers:** MessageScheduler 60s, QueueProcessor 5s
- **Transaccionalidad:** @Transactional para operaciones críticas

---

## PASO 6: Decisiones Arquitectónicas (ADRs)

### Objetivo
Documentar las 5 decisiones arquitectónicas clave con formato ADR estándar, justificadas específicamente por el contexto del documento `functional_requirements_analysis_v1.0.md`.

### Fuente de Información Específica
**Del documento:** `docs/requirements/functional_requirements_analysis_v1.0.md`

**Contexto para decisiones:**
- **Volumen:** 25,000+ tickets/día → 75,000 mensajes/día (0.9 msg/segundo)
- **RNF-004:** 99.9% mensajes entregados, 3 reintentos automáticos
- **RNF-002:** Performance: creación <3s, envío <5s, dashboard cada 5s
- **Contexto financiero:** Institución financiera, compliance, seguridad
- **Escalabilidad:** Fase Piloto (500 tickets/día) → Nacional (25,000+ tickets/día)

### Decisiones Obligatorias a Documentar

**6.1 ADR-001: Estrategia de Reintentos para Mensajes**
- **Contexto:** RF-002 requiere 99.9% entrega, RN-007 y RN-008 especifican reintentos
- **Opciones:** Reintentos simples vs Circuit Breaker vs Queue externa (RabbitMQ)
- **Criterios:** Volumen (0.9 msg/seg), complejidad, RN-007 (3 reintentos), RN-008 (backoff)

**6.2 ADR-002: API Síncrona vs Reactiva para Telegram**
- **Contexto:** Integración Telegram Bot API, volumen esperado
- **Opciones:** RestTemplate vs WebClient vs HTTP Client nativo
- **Criterios:** Simplicidad vs performance, curva aprendizaje, volumen 0.9 msg/seg

**6.3 ADR-003: Procesamiento de Colas y Schedulers**
- **Contexto:** RF-003, RF-004 requieren procesamiento cada 5s según documento
- **Opciones:** @Scheduled vs RabbitMQ vs Kafka vs SQS
- **Criterios:** Complejidad infraestructura, volumen, latencia requerida

**6.4 ADR-004: Migraciones de Base de Datos**
- **Contexto:** 4 entidades (Ticket, Mensaje, Advisor, AuditEvent), evolución esquema
- **Opciones:** Flyway vs Liquibase vs migraciones manuales
- **Criterios:** Simplicidad, versionado, rollback, integración

**6.5 ADR-005: Validación de Datos de Entrada**
- **Contexto:** RF-001 requiere validación RUT, teléfono, RN-001 unicidad
- **Opciones:** Bean Validation vs validación manual vs validación en BD
- **Criterios:** Declarativo vs control, mensajes error, performance

### Formato ADR Estándar

**Para cada ADR incluir:**
```
## ADR-XXX: [Título de la Decisión]

### Contexto
[Situación específica del documento que requiere decisión]

### Decisión
[Tecnología/enfoque seleccionado]

### Razones
- [Justificación basada en RN específicas]
- [Consideración de volumen del documento]
- [Alineación con contexto financiero]
- [Principio 80/20 aplicado]

### Consecuencias
**Positivas:**
- [Beneficios específicos]

**Negativas:**
- [Trade-offs aceptados]

### Alternativas Consideradas
| Opción | Pros | Contras | Decisión |
|--------|------|---------|----------|
| [Alt 1] | [Pros] | [Contras] | ✅/❌ |

### Futuro
[Cuándo reevaluar la decisión]
```

### Criterios de Validación Estricta

**Alineación con Documento:**
□ 5 ADRs documentadas con formato estándar
□ Contexto basado en RN específicas del documento
□ Volumen y performance según RNF del documento
□ Consideración de escalabilidad por fases

**Técnicos:**
□ Cada ADR con: Contexto, Decisión, Razones, Consecuencias, Futuro
□ Alternativas consideradas con tabla comparativa
□ Justificaciones técnicamente sólidas
□ Trade-offs claramente explicados

### Reglas de Negocio del Documento Aplicables
- **RN-007, RN-008:** Afectan decisión de reintentos (ADR-001)
- **RN-011:** Auditoría obligatoria afecta selección BD (ADR-004)
- **RN-001:** Validación unicidad afecta estrategia validación (ADR-005)
- **Volumen documento:** 25K tickets/día afecta todas las decisiones

### Instrucciones de Implementación
1. **Generar 5 ADRs completos** usando formato y criterios definidos arriba
2. **Basar contexto** en RN y RNF específicos del documento
3. **Incluir tabla de alternativas** para cada decisión
4. **Justificar con volumen** y contexto financiero del documento
5. **Considerar escalabilidad** por fases según documento de negocio

---

## PASO 7: Configuración y Validación Final

### Objetivo
Completar el documento con configuración de deployment y realizar validación integral de completitud basada en el documento `functional_requirements_analysis_v1.0.md`.

### Fuente de Información Específica
**Del documento:** `docs/requirements/functional_requirements_analysis_v1.0.md`

**Configuración requerida:**
- **Telegram Bot:** RF-002 requiere integración con Bot API
- **PostgreSQL:** 4 entidades (Ticket, Mensaje, Advisor, AuditEvent)
- **Schedulers:** MessageScheduler (60s), QueueProcessor (5s) según documento
- **Profiles:** dev/staging/prod para escalabilidad por fases

### Elementos Finales Obligatorios

**7.1 Variables de Entorno**
- Variables críticas para RF-002 (Telegram), conexión BD, profiles
- Configuración por ambiente (dev/staging/prod)
- Seguridad: no hardcodear tokens en código

**7.2 Docker Compose para Desarrollo**
- Configuración PostgreSQL + API
- Variables de entorno apropiadas
- Volúmenes para persistencia de datos

**7.3 Application Properties**
- Configuración Spring Boot para schedulers
- Configuración JPA para 4 entidades
- Logging apropiado para auditoría (RN-011)
- Profiles por ambiente

**7.4 Checklist de Completitud Específico**

**Alineación con Documento RF:**
□ Stack tecnológico justificado con volumen 25K tickets/día
□ 3 diagramas PlantUML renderizables (C4, Secuencia, ER)
□ 4 entidades mapeadas (Ticket, Mensaje, Advisor, AuditEvent)
□ 13 endpoints HTTP mapeados a componentes
□ 5 ADRs con contexto específico del documento
□ 13 RN aplicadas en decisiones arquitectónicas

**Completitud Técnica:**
□ Diagramas todos renderizables en PlantUML
□ Componentes mapeados a RF específicos
□ Schedulers con frecuencias según documento (60s, 5s)
□ Configuración completa para deployment
□ Variables de entorno documentadas

**Calidad Profesional:**
□ Justificaciones técnicas sólidas
□ Decisiones alineadas con contexto financiero
□ Formato profesional consistente
□ Referencias específicas a RN del documento

### Estructura del Documento Final

**El agente definirá la estructura más coherente, incluyendo mínimo:**
- Resumen Ejecutivo
- Stack Tecnológico (con justificaciones)
- Diagramas de Arquitectura (C4, Secuencia, ER)
- Arquitectura en Capas
- Componentes Principales
- Decisiones Arquitectónicas (ADRs)
- Configuración y Deployment

### Ubicación del Entregable Final

Al completar el documento, preguntar:
**"¿En qué directorio específico debo guardar el documento de arquitectura?"**

Esperar respuesta del usuario antes de proceder con el guardado.

### Criterios de Validación Final

**Cuantitativos:**
□ 3 diagramas PlantUML en `docs/architecture/diagrams/`
□ 5+ ADRs con formato estándar
□ Variables de entorno documentadas (5+ variables)
□ Configuración completa (Docker Compose + application.yml)

**Cualitativos:**
□ Alineación 100% con documento de RF
□ Decisiones coherentes con volumen y contexto
□ Configuración lista para deployment
□ Documento profesional y completo

### Reglas de Negocio del Documento Aplicables
- **RN-007, RN-008:** Afectan configuración de reintentos
- **RN-011:** Auditoría requiere configuración de logging
- **Volumen:** 25K tickets/día afecta configuración de performance
- **Escalabilidad:** Fases requieren profiles configurables

### Consideraciones de Deployment
- **Fase Piloto:** 500-800 tickets/día, configuración básica
- **Fase Nacional:** 25,000+ tickets/día, optimizaciones necesarias
- **Seguridad:** Institución financiera, encriptación, compliance
- **Monitoreo:** Métricas para dashboard RF-007

### Instrucciones de Implementación
1. **Generar configuración completa** basada en decisiones arquitectónicas
2. **Crear checklist específico** alineado con documento de RF
3. **Validar completitud** contra 13 RN y 8 RF del documento
4. **Consultar ubicación** antes de guardar documento final
5. **Confirmar renderizado** de todos los diagramas PlantUML

---

## CRITERIOS DE CALIDAD CONSOLIDADOS

### Alineación con Documento Fuente
**Obligatorio verificar:**
□ 13 Reglas de Negocio (RN-001 a RN-013) aplicadas correctamente
□ 4 Enumeraciones del sistema reflejadas en diseño
□ 8 Requerimientos Funcionales cubiertos arquitectónicamente
□ 13 Endpoints HTTP mapeados a componentes
□ Volumen esperado (25K tickets/día) considerado en decisiones
□ 4 Entidades (Ticket, Mensaje, Advisor, AuditEvent) modeladas

### Estándares Técnicos
**Diagramas:**
□ 3 diagramas PlantUML renderizables
□ Archivos fuente en `docs/architecture/diagrams/`
□ Embebidos en documento de arquitectura
□ Verificados en http://www.plantuml.com/plantuml/

**Decisiones:**
□ 5 ADRs con formato estándar
□ Justificaciones técnicas basadas en RN específicas
□ Alternativas consideradas con pros/contras
□ Contexto específico del documento de RF

**Configuración:**
□ Variables de entorno completas
□ Docker Compose funcional
□ Application properties por ambiente
□ Consideración de escalabilidad por fases

### Restricciones de Contenido
**❌ NO INCLUIR:**
- Implementación de código Java específico (eso es para fase de implementación)
- Scripts SQL de migraciones detallados (eso es para fase de implementación)
- Configuraciones de infraestructura cloud específicas (AWS, Azure, etc.)

**✅ SÍ INCLUIR:**
- Nombres de clases y métodos principales
- Estructura de paquetes sugerida (com.ticketero.controller, etc.)
- Decisiones técnicas justificadas con contexto del proyecto
- Ejemplos de código conceptual para componentes clave
- Referencias específicas a RN aplicables por componente

---

## ENTREGABLE FINAL

### Especificaciones del Documento
**Nombre:** El agente definirá el nombre más coherente para el documento de arquitectura
**Formato:** Markdown con diagramas PlantUML embebidos
**Longitud esperada:** 30-40 páginas (8,000-10,000 palabras)

### Ubicación de Archivos
**Documento principal:** Consultar al usuario antes de guardar
**Diagramas:** `docs/architecture/diagrams/`
- 01-context-diagram.puml
- 02-sequence-diagram.puml
- 03-er-diagram.puml

### Propósito del Documento
- **Entrada para:** Plan detallado de implementación
- **Validación por:** Equipo de desarrollo y arquitectos senior
- **Base técnica:** Para decisiones de implementación
- **Referencia:** Para configuración de ambientes

### Beneficios de Negocio Garantizados
**Según documento de RF:**
- **NPS:** 45 → 65 puntos (+44%)
- **Abandonos:** 15% → 5% (-67%)
- **Productividad:** +20% tickets por ejecutivo
- **Trazabilidad:** 100% eventos auditados

---

## RESUMEN DE CAMBIOS APLICADOS

### Optimizaciones Realizadas:
1. **Referencias corregidas** - Documentos reales del proyecto
2. **Alineación con RN** - 13 reglas de negocio específicas
3. **Eliminación de duplicación** - Removido contenido hardcodeado masivo
4. **Consulta de ubicación** - Template para preguntar dónde guardar
5. **Validación específica** - Criterios alineados con documento fuente
6. **Metodología Master** - Incorporada metodología universal de refinamiento

### Mejoras de Coherencia:
- Volumen esperado (25K tickets/día) considerado en decisiones
- 4 Enumeraciones del documento fuente reflejadas
- 13 Endpoints HTTP mapeados a componentes
- Beneficios de negocio (NPS, abandonos, productividad) como contexto
- Metodología iterativa con validación paso a paso

**Resultado:** Prompt arquitectónico alineado con el análisis de requerimientos funcionales completado, eliminando inconsistencias y garantizando coherencia técnica con el documento `functional_requirements_analysis_v1.0.md`, siguiendo la Metodología Master de Refinamiento de Prompts.