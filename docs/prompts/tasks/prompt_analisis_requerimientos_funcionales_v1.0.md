# PROMPT REFINADO: Análisis - Requerimientos Funcionales del Sistema Ticketero

**Versión:** 1.0  
**Fecha:** Diciembre 2025  
**Iteraciones:** 10 secciones refinadas  
**Ingeniero de Prompts:** Amazon Q Developer

---

## CONTEXTO

Eres un **Analista de Requerimientos Senior** especializado en sistemas financieros digitales con 5+ años de experiencia en:
- Transformación digital de procesos bancarios
- Documentación de requerimientos según estándares IEEE 830
- Integración de sistemas de notificaciones en tiempo real
- Cumplimiento normativo en instituciones financieras

**Objetivo:** Transformar el documento de negocio `docs/requirements/requerimientos_negocio.md` en un documento de **Requerimientos Funcionales** que cumpla:
- Estándar IEEE 830 para especificaciones de software
- Criterios de aceptación verificables en formato Gherkin
- Trazabilidad completa entre requerimientos y beneficios de negocio
- Compatibilidad con metodologías ágiles (Scrum/Kanban)

**IMPORTANTE:** Después de completar CADA paso, debes DETENERTE y solicitar revisión exhaustiva antes de continuar.

---

## DOCUMENTO DE ENTRADA

**Archivo fuente:** `docs/requirements/requerimientos_negocio.md`

**Contenido del documento:**
- Contexto del negocio y problema a resolver
- 8 Requerimientos Funcionales (RF-001 a RF-008) en formato narrativo
- Flujo detallado del proceso en 20 pasos
- 7 Requerimientos No Funcionales (RNF-001 a RNF-007)
- Métricas de éxito: NPS 45→65, abandonos 15%→5%, +20% productividad

**Validación previa:** Confirma que el archivo existe en la ruta especificada antes de iniciar el proceso.

---

## METODOLOGÍA DE TRABAJO

### Principio Fundamental
**"Documentar → Validar → Confirmar → Continuar"**

### Proceso Obligatorio por Paso

**1. DOCUMENTAR**
- Completar requerimiento funcional según template establecido
- Incluir todos los elementos obligatorios (modelo de datos, Gherkin, reglas de negocio)

**2. VALIDAR**
- Verificar criterios cuantitativos específicos
- Revisar formato y consistencia técnica
- Aplicar checklist de calidad

**3. CONFIRMAR**
- Solicitar revisión con formato estándar
- Esperar aprobación explícita antes de continuar

**4. CONTINUAR**
- Avanzar solo tras confirmación positiva

---

### Template de Solicitud de Revisión

```
✅ PASO [X] COMPLETADO

**Requerimiento:** RF-XXX: [Nombre del Requerimiento]

**Criterios Validados:**
□ Escenarios Gherkin: [cantidad] incluidos
□ Modelo de datos: [cantidad] campos definidos  
□ Reglas de negocio: [RN-XXX, RN-YYY] aplicadas
□ Ejemplos JSON: Sintaxis válida verificada
□ Formato: Consistente con template

🔍 **¿APROBADO PARA CONTINUAR?**
```

### Criterios de Validación Específicos

**Para cada RF debe incluir:**
- Mínimo 5 escenarios Gherkin (happy path + errores + edge cases)
- Modelo de datos con campos tipificados
- Referencias específicas a reglas de negocio (RN-XXX)
- Ejemplos JSON sintácticamente correctos
- Precondiciones y postcondiciones claras

---

## TU TAREA

Transformar `docs/requirements/requerimientos_negocio.md` en un documento de **Requerimientos Funcionales** profesional siguiendo el estándar IEEE 830.

### Estructura de Implementación

**10 pasos secuenciales con revisión obligatoria:**

| Paso | Componente | Entregable |
|------|-----------|------------|
| 1 | Introducción y Reglas de Negocio | 13 RN + 4 Enumeraciones |
| 2 | RF-001: Crear Ticket Digital | 7+ escenarios Gherkin |
| 3 | RF-002: Enviar Notificaciones Telegram | 6+ escenarios Gherkin |
| 4 | RF-003: Calcular Posición y Tiempo | 5+ escenarios Gherkin |
| 5 | RF-004: Asignar Ticket a Ejecutivo | 7+ escenarios Gherkin |
| 6 | RF-005: Gestionar Múltiples Colas | 5+ escenarios Gherkin |
| 7 | RF-006: Consultar Estado del Ticket | 5+ escenarios Gherkin |
| 8 | RF-007: Panel de Monitoreo | 6+ escenarios Gherkin |
| 9 | RF-008: Registrar Auditoría | 5+ escenarios Gherkin |
| 10 | Matrices de Trazabilidad | Validación final |

**Total esperado:** 46+ escenarios Gherkin, 13 RN, 8 RF, 11 endpoints HTTP

### Criterio de Completitud por Paso

Cada paso se considera completo cuando incluye:
- ✅ Todos los elementos obligatorios del template
- ✅ Criterios cuantitativos cumplidos (cantidad de escenarios, campos, etc.)
- ✅ Formato Gherkin sintácticamente correcto
- ✅ Referencias a reglas de negocio aplicables
- ✅ Aprobación explícita del revisor

---

## PASO 1: Introducción y Reglas de Negocio

### Objetivo
Crear la estructura base del documento con introducción profesional y documentar las 13 reglas de negocio transversales.

### Tareas Específicas

**1.1 Introducción del Documento**
- Propósito: Definir objetivo del sistema ticketero
- Alcance: Especificar qué incluye/excluye el documento  
- Definiciones: Glosario de 6 términos clave mínimo

**1.2 Reglas de Negocio (13 obligatorias)**
- RN-001: Unicidad de ticket activo por cliente
- RN-002: Prioridad de colas (GERENCIA=4, EMPRESAS=3, PERSONAL_BANKER=2, CAJA=1)
- RN-003: Orden FIFO dentro de cada cola
- RN-004: Balanceo de carga entre asesores
- RN-005: Formato número ticket [Prefijo][01-99]
- RN-006: Prefijos por cola (C, P, E, G)
- RN-007: 3 reintentos automáticos para mensajes
- RN-008: Backoff exponencial (30s, 60s, 120s)
- RN-009: Estados de ticket (6 valores)
- RN-010: Fórmula tiempo estimado = posición × tiempo_promedio
- RN-011: Auditoría obligatoria de eventos críticos
- RN-012: Pre-aviso cuando posición ≤ 3
- RN-013: Estados de asesor (AVAILABLE, BUSY, OFFLINE)

**1.3 Enumeraciones (4 obligatorias)**
- QueueType: 4 valores con tiempos y prioridades
- TicketStatus: 6 estados con indicador "activo"
- AdvisorStatus: 3 estados con indicador "recibe asignaciones"
- MessageTemplate: 3 plantillas con momento de envío

### Criterios de Validación

**Cuantitativos:**
□ 13 reglas de negocio documentadas (RN-001 a RN-013)
□ 4 enumeraciones completas con todos sus valores
□ 6+ términos en glosario de definiciones
□ Tablas bien formateadas para enumeraciones

**Cualitativos:**
□ Cada RN tiene descripción clara y sin ambigüedades
□ RN-010 incluye fórmula matemática específica
□ Introducción sigue estructura: propósito → alcance → definiciones
□ Formato profesional consistente con numeración RN-XXX

### Template de Implementación

Usar la estructura exacta del ejemplo proporcionado:
- Encabezado con metadatos del proyecto
- Sección 1: Introducción (1.1, 1.2, 1.3)
- Sección 2: Reglas de Negocio (RN-001 a RN-013)
- Sección 3: Enumeraciones (tablas con valores completos)

---

## PASO 2: RF-001 (Crear Ticket Digital)

### Objetivo
Documentar RF-001 transformando la descripción narrativa del documento fuente en especificación técnica completa.

### Fuente de Información
**Del documento:** `docs/requirements/requerimientos_negocio.md`
- RF-001: "El sistema debe permitir al cliente obtener un ticket digital ingresando su RUT/ID y seleccionando el tipo de atención requerida..."
- Flujo detallado: Pasos 1-7 (Emisión de Ticket)
- Tipos de cola: Caja, Personal Banker, Empresas, Gerencia

### Elementos Obligatorios

**2.1 Especificación Técnica**
- Descripción: Basada en RF-001 del documento fuente
- Prioridad: Alta
- Actor Principal: Cliente
- Precondiciones: 3 mínimo
- Postcondiciones: 3 mínimo

**2.2 Modelo de Datos (12 campos obligatorios)**
- codigoReferencia: UUID único
- numero: String formato [Prefijo][01-99]
- nationalId: String (RUT/ID del cliente)
- telefono: String (formato +56XXXXXXXXX)
- branchOffice: String
- queueType: Enum (4 valores)
- status: Enum (6 valores)
- positionInQueue: Integer
- estimatedWaitMinutes: Integer
- createdAt: Timestamp
- assignedAdvisor: Relación (null inicial)
- assignedModuleNumber: Integer 1-5 (null inicial)

**2.3 Reglas de Negocio Aplicables**
- RN-001: Unicidad de ticket activo
- RN-005: Formato número ticket
- RN-006: Prefijos por cola
- RN-010: Cálculo tiempo estimado

**2.4 Escenarios Gherkin (7 mínimo)**
1. Creación exitosa para cada tipo de cola (4 escenarios)
2. Error: Cliente con ticket activo (HTTP 409)
3. Validación: RUT/ID inválido (HTTP 400)
4. Validación: Teléfono inválido (HTTP 400)
5. Edge case: Primera persona en cola
6. Edge case: Cola con tickets existentes
7. Edge case: Creación sin teléfono

**2.5 Ejemplos JSON**
- Respuesta exitosa (HTTP 201)
- Error ticket activo (HTTP 409)
- Error validación (HTTP 400)

### Criterios de Validación

**Cuantitativos:**
□ 7+ escenarios Gherkin documentados
□ 12 campos del modelo de datos definidos
□ 4 reglas de negocio referenciadas (RN-001, RN-005, RN-006, RN-010)
□ 3+ ejemplos JSON sintácticamente válidos

**Cualitativos:**
□ Escenarios cubren happy path + errores + edge cases
□ Formato Gherkin correcto (Given/When/Then/And)
□ Ejemplos JSON incluyen códigos HTTP apropiados
□ Descripción adaptada del documento fuente, no copiada

### Referencia de Formato
Seguir la estructura exacta del ejemplo RF-001 proporcionado, adaptando el contenido al contexto específico del documento fuente.

---

## PASO 3: RF-002 (Enviar Notificaciones Automáticas vía Telegram)

### Objetivo
Documentar RF-002 transformando la descripción de notificaciones automáticas en especificación técnica con modelo de mensajería.

### Fuente de Información
**Del documento:** `docs/requirements/requerimientos_negocio.md`
- RF-002: "El sistema debe enviar tres mensajes automáticos vía Telegram..."
- Mensaje 1: Confirmación inmediata con número, posición y tiempo
- Mensaje 2: Pre-aviso cuando quedan 3 personas adelante
- Mensaje 3: Turno activo con módulo y nombre del asesor
- RNF-004: 99.9% mensajes entregados, 3 reintentos (30s, 60s, 120s)

### Elementos Obligatorios

**3.1 Especificación Técnica**
- Descripción: Sistema automatizado de notificaciones
- Prioridad: Alta
- Actor Principal: Sistema (proceso automatizado)
- Precondiciones: Ticket creado, Bot Telegram activo, Cliente con cuenta Telegram

**3.2 Modelo de Datos Mensaje (8 campos obligatorios)**
- id: BIGSERIAL (primary key)
- ticket_id: BIGINT (foreign key)
- plantilla: String (3 valores posibles)
- estadoEnvio: Enum (PENDIENTE, ENVIADO, FALLIDO)
- fechaProgramada: Timestamp
- fechaEnvio: Timestamp (nullable)
- telegramMessageId: String (nullable)
- intentos: Integer (default 0, máximo 3)

**3.3 Plantillas de Mensajes (3 obligatorias)**
- totem_ticket_creado: Confirmación con número, posición, tiempo estimado
- totem_proximo_turno: Pre-aviso para acercarse a sucursal
- totem_es_tu_turno: Asignación con módulo y nombre asesor

**3.4 Reglas de Negocio Aplicables**
- RN-007: 3 reintentos automáticos para mensajes fallidos
- RN-008: Backoff exponencial (30s, 60s, 120s)
- RN-011: Auditoría obligatoria de envíos
- RN-012: Mensaje 2 cuando posición ≤ 3

**3.5 Escenarios Gherkin (6 mínimo)**
1. Envío exitoso Mensaje 1 (confirmación)
2. Envío exitoso Mensaje 2 (pre-aviso)
3. Envío exitoso Mensaje 3 (turno activo)
4. Fallo de red con reintento exitoso
5. 3 reintentos fallidos → estado FALLIDO
6. Backoff exponencial entre reintentos

### Criterios de Validación

**Cuantitativos:**
□ 8 campos del modelo Mensaje definidos
□ 3 plantillas documentadas con formato HTML
□ 6+ escenarios Gherkin (éxito + fallos + reintentos)
□ 4 reglas de negocio aplicadas (RN-007, RN-008, RN-011, RN-012)

**Cualitativos:**
□ Plantillas incluyen variables dinámicas ({numero}, {posicion}, etc.)
□ Escenarios cubren proceso completo de reintentos
□ Modelo soporta auditoría de intentos y estados
□ Actor principal correctamente identificado como "Sistema"

**Postcondiciones:**
□ Mensaje insertado en BD con estado correspondiente
□ telegram_message_id almacenado si éxito
□ Contador de intentos actualizado
□ Evento de auditoría registrado

### Consideraciones Técnicas
- Proceso interno automatizado (sin endpoints HTTP públicos)
- Integración con Telegram Bot API
- Manejo de errores de conectividad
- Persistencia de estado para reintentos

---

## PASOS 4-9: Requerimientos Funcionales Restantes

### Estructura Uniforme para Todos los Pasos

Cada paso (4-9) debe seguir el **mismo template** que PASO 2 y PASO 3:

**Template Estándar:**
1. **Objetivo:** Transformar RF-XXX del documento fuente
2. **Fuente de Información:** Referencia específica al documento
3. **Elementos Obligatorios:** Especificación, modelo de datos, reglas de negocio, escenarios
4. **Criterios de Validación:** Cuantitativos y cualitativos
5. **Consideraciones Técnicas:** Aspectos únicos del RF

---

### PASO 4: RF-003 (Calcular Posición y Tiempo Estimado)

**Fuente:** RF-003 del documento + Flujo paso 5 + RNF-002 (performance)
**Elementos clave:** Algoritmos de cálculo, tiempos por cola, actualización en tiempo real
**Escenarios mínimos:** 5 (cálculo inicial, recálculo, cola vacía, múltiples colas, performance)
**Reglas aplicables:** RN-003 (FIFO), RN-010 (fórmula tiempo estimado)

### PASO 5: RF-004 (Asignar Ticket a Ejecutivo Automáticamente)

**Fuente:** RF-004 del documento + Flujo pasos 12-13 + Sección 3.3
**Elementos clave:** Modelo Advisor, algoritmo de asignación, balanceo de carga
**Escenarios mínimos:** 7 (asignación exitosa, prioridades, balanceo, ejecutivo ocupado)
**Reglas aplicables:** RN-002 (prioridad colas), RN-003 (FIFO), RN-004 (balanceo)

### PASO 6: RF-005 (Gestionar Múltiples Colas)

**Fuente:** RF-005 del documento + 4 tipos de cola especificados
**Elementos clave:** 4 colas con características, endpoints administrativos
**Escenarios mínimos:** 5 (consulta por cola, estadísticas, colas vacías, múltiples tickets)
**Reglas aplicables:** RN-002 (prioridades), RN-006 (prefijos)

### PASO 7: RF-006 (Consultar Estado del Ticket)

**Fuente:** RF-006 del documento + necesidad de consulta en tiempo real
**Elementos clave:** Consulta por UUID/número, estados actualizados
**Escenarios mínimos:** 5 (consulta exitosa, ticket no existe, diferentes estados)
**Reglas aplicables:** RN-009 (estados ticket)

### PASO 8: RF-007 (Panel de Monitoreo para Supervisor)

**Fuente:** RF-007 del documento + Sección 3.4 (Supervisión) + RNF-002
**Elementos clave:** Dashboard tiempo real, métricas, alertas, actualización cada 5s
**Escenarios mínimos:** 6 (dashboard, alertas, métricas por cola, estado ejecutivos)
**Reglas aplicables:** RN-013 (estados asesor), alertas personalizadas

### PASO 9: RF-008 (Registrar Auditoría de Eventos)

**Fuente:** RF-008 del documento + RNF-005 (seguridad) + necesidad de trazabilidad
**Elementos clave:** Eventos críticos, modelo de auditoría, compliance
**Escenarios mínimos:** 5 (registro eventos, consulta auditoría, integridad datos)
**Reglas aplicables:** RN-011 (auditoría obligatoria)

---

### Criterios de Consistencia

**Para TODOS los pasos 4-9:**
□ Mismo formato de template que pasos 2-3
□ Referencia específica al documento fuente
□ Cantidad de escenarios justificada por complejidad
□ Reglas de negocio aplicables identificadas
□ Revisión obligatoria antes de continuar

**Total esperado pasos 4-9:** 32+ escenarios Gherkin adicionales
**Gran total (pasos 1-9):** 46+ escenarios Gherkin

---

## PASO 10: Matrices de Trazabilidad y Validación Final

### Objetivo
Completar el documento con matrices de trazabilidad que conecten requerimientos con beneficios de negocio y realizar validación integral de completitud.

### Fuente de Información
**Del documento:** `docs/requirements/requerimientos_negocio.md`
- Beneficios esperados: NPS 45→65, abandonos 15%→5%, +20% productividad
- Flujo completo: 20 pasos del proceso end-to-end
- RNF: 7 requerimientos no funcionales para validar cumplimiento

### Elementos Obligatorios

**10.1 Matriz de Trazabilidad RF → Beneficios**
| RF | Beneficio de Negocio | Métrica de Impacto |
|----|---------------------|-------------------|
| RF-001 | Mejora experiencia cliente | NPS +20 puntos |
| RF-002 | Reduce abandonos | Abandonos 15%→5% |
| ... | ... | ... |

**10.2 Matriz de Endpoints HTTP**
| Método | Endpoint | RF Asociado | Propósito |
|--------|----------|-------------|-----------|
| POST | /api/tickets | RF-001 | Crear ticket |
| GET | /api/tickets/{uuid} | RF-006 | Consultar estado |
| ... | ... | ... | ... |

**Total esperado:** 11 endpoints HTTP mapeados

**10.3 Casos de Uso Principales**
- CU-001: Cliente crea ticket y recibe notificaciones
- CU-002: Supervisor monitorea operación en tiempo real  
- CU-003: Sistema asigna automáticamente tickets a ejecutivos

**10.4 Matriz de Dependencias entre RFs**
- RF-002 depende de RF-001 (necesita ticket creado)
- RF-003 depende de RF-001 (necesita tickets en cola)
- RF-004 depende de RF-003 (necesita cálculo de posición)

**10.5 Checklist de Validación Final**

**Completitud Cuantitativa:**
□ 8 RF documentados con nivel de detalle requerido
□ 46+ escenarios Gherkin totales distribuidos
□ 13 reglas de negocio numeradas (RN-001 a RN-013)
□ 4 enumeraciones completas con valores
□ 11 endpoints HTTP mapeados
□ 3 entidades de datos definidas (Ticket, Mensaje, Advisor)

**Completitud Cualitativa:**
□ Cada RF tiene descripción, modelo de datos, reglas aplicables
□ Escenarios Gherkin sintácticamente correctos
□ Ejemplos JSON válidos en respuestas HTTP
□ Trazabilidad clara RF → Beneficio → Endpoint
□ Formato profesional consistente
□ Sin ambigüedades en especificaciones

**Alineación con Documento Fuente:**
□ Todos los RF del documento fuente cubiertos
□ Flujo de 20 pasos reflejado en especificaciones
□ RNF considerados en criterios de aceptación
□ Beneficios de negocio trazables a RFs específicos

### Criterios de Validación

**Cuantitativos:**
□ 4 matrices completas (trazabilidad, endpoints, dependencias, casos de uso)
□ 3 casos de uso principales documentados
□ 15+ elementos en checklist de validación final

**Cualitativos:**
□ Matrices muestran relaciones claras entre elementos
□ Casos de uso cubren flujos principales del sistema
□ Checklist permite verificación objetiva de completitud
□ Documento listo para revisión de stakeholders

### Entregable Final
**Archivo:** `REQUERIMIENTOS-FUNCIONALES.md`
**Ubicación sugerida:** `docs/requirements/`
**Longitud esperada:** 50-70 páginas (12,000-15,000 palabras)
**Propósito:** Base contractual para desarrollo y validación por stakeholders

---

## CRITERIOS DE CALIDAD CONSOLIDADOS

### Métricas Cuantitativas Obligatorias
| Elemento | Cantidad Mínima | Validación |
|----------|-----------------|------------|
| Requerimientos Funcionales | 8 RF completos | RF-001 a RF-008 |
| Escenarios Gherkin | 46+ total | Distribuidos según complejidad |
| Reglas de Negocio | 13 numeradas | RN-001 a RN-013 |
| Endpoints HTTP | 11 mapeados | Todos los RF cubiertos |
| Enumeraciones | 4 completas | Con todos los valores |
| Entidades de Datos | 3 definidas | Ticket, Mensaje, Advisor |

### Estándares de Calidad Técnica
**Formato Gherkin:**
□ Sintaxis correcta (Given/When/Then/And)
□ Escenarios verificables y específicos
□ Cobertura: happy path + errores + edge cases

**Ejemplos JSON:**
□ Sintaxis válida verificada
□ Códigos HTTP apropiados (200, 201, 400, 409, 500)
□ Estructura consistente con modelo de datos

**Trazabilidad:**
□ Cada RF conectado con beneficio de negocio
□ Reglas de negocio aplicables identificadas
□ Dependencias entre RFs documentadas

### Restricciones de Contenido
**❌ NO INCLUIR:**
- Tecnologías específicas (Java, Spring Boot, PostgreSQL, Docker)
- Arquitectura de software (capas, patrones de diseño)
- Código fuente o pseudocódigo
- Detalles de implementación técnica

**✅ SÍ INCLUIR:**
- QUÉ debe hacer el sistema (funcionalidad)
- CUÁNDO debe ejecutarse (triggers, condiciones)
- CON QUÉ datos trabaja (modelo de datos)
- CÓMO se valida (criterios de aceptación)

---

## ENTREGABLE FINAL

### Especificaciones del Archivo
**Nombre:** `REQUERIMIENTOS-FUNCIONALES.md`
**Ubicación:** `docs/requirements/` (junto al documento fuente)
**Formato:** Markdown con tablas y código Gherkin
**Longitud:** 50-70 páginas (12,000-15,000 palabras)

### Estructura del Documento
```
# Requerimientos Funcionales - Sistema Ticketero Digital
1. Introducción (propósito, alcance, definiciones)
2. Reglas de Negocio (RN-001 a RN-013)
3. Enumeraciones (4 tipos)
4. Requerimientos Funcionales (RF-001 a RF-008)
5. Matriz de Trazabilidad
6. Matriz de Endpoints HTTP
7. Casos de Uso Principales
8. Checklist de Validación Final
```

### Propósito del Documento
- **Entrada para:** Diseño de arquitectura técnica
- **Validación por:** Stakeholders de negocio y técnicos
- **Base contractual:** Para desarrollo y testing
- **Referencia:** Para documentación de usuario final

---

## RESUMEN DE CAMBIOS APLICADOS

### Optimizaciones Realizadas:
1. **Eliminación de duplicación masiva** - Removidas 500+ líneas de contenido repetitivo
2. **Estructura uniforme** - Template consistente para todos los pasos
3. **Criterios medibles** - Validaciones cuantitativas específicas
4. **Conexión con documento fuente** - Referencias específicas al archivo del proyecto
5. **Consolidación de secciones** - Información dispersa unificada en secciones coherentes

### Mejoras de Usabilidad:
- Template de revisión conciso y estándar
- Criterios de validación específicos por paso
- Métricas cuantitativas claras (46+ escenarios, 13 RN, 11 endpoints)
- Eliminación de ambigüedades y términos vagos
- Proceso de guardado con ubicación específica

**Resultado:** Prompt optimizado de 2,500 palabras vs. 8,000+ palabras original (reducción 70% manteniendo funcionalidad completa)