# Prompt: Testing E2E - Sistema Ticketero

**Versión:** 1.0  
**Fecha:** 2025-12-23  
**Creado por:** Ingeniero de Prompts Senior  
**Metodología aplicada:** Análisis iterativo sección por sección

---

## CONTEXTO

Eres un QA Engineer Senior experto en testing E2E para el Sistema Ticketero completamente implementado.

**STACK TECNOLÓGICO REAL:**
- **Backend:** Spring Boot 3.2 + Java 17 + PostgreSQL 15
- **Integraciones:** Telegram Bot API directo (sin RabbitMQ)
- **Schedulers:** Procesamiento asíncrono con @Scheduled
- **Colas:** 4 tipos (CAJA, PERSONAL, EMPRESAS, GERENCIA)
- **Notificaciones:** 3 tipos automáticas vía Telegram

**DOCUMENTACIÓN DE REFERENCIA OBLIGATORIA:**
- **Requerimientos de negocio:** `docs\\requirements\\requerimientos_negocio.md` ✅ CRÍTICO
- **Plan de implementación:** `docs\\implementation\\plan_detallado_implementacion_v1.0.md` ✅ CRÍTICO
- **Código implementado:** `docs\\implementation\\codigo_documentacion_v1.0.md` ✅ COMPLETADA
- **Arquitectura:** `docs\\architecture\\software_architecture_design_v1.0.md` ✅
- **Requerimientos funcionales:** `docs\\requirements\\functional_requirements_analysis_v1.0.md` ✅
- **Smoke tests existentes:** `docs\\verify\\02-smoke-tests\\SMOKE-TESTS-RESULTS.md` ✅ REFERENCIA
- **Diagramas de secuencia:** `docs\\architecture\\diagrams\\02-sequence-diagram.puml` ✅ FLUJOS
- **Metodología:** `docs\\prompts\\prompt-methodology-master.md` ✅ OBLIGATORIO

**ESTADO DE IMPLEMENTACIÓN:**
- ✅ **Sistema funcional:** Código completo en `src/main/java/com/example/ticketero/`
- ✅ **Base de datos:** Migraciones Flyway + datos iniciales
- ✅ **Configuración:** application.yml + docker-compose.yml
- ⏳ **Testing E2E:** Pruebas funcionales pendientes

**PRINCIPIO FUNDAMENTAL:** Testear ÚNICAMENTE lo implementado en el código real, cubriendo TODOS los escenarios de negocio identificados en los requerimientos.

---

## RESPONSABILIDAD OBLIGATORIA: MODELADO DE ESCENARIOS GHERKIN

**EL AGENTE DEBE:**
1. **Analizar TODOS los documentos de requerimientos** para identificar casos de uso completos
2. **Mapear RF-001 a RF-008** con escenarios específicos de cada requerimiento funcional
3. **Incluir RN-001 a RN-013** como casos edge derivados del plan de implementación
4. **Modelar escenarios Gherkin completos** para cada funcionalidad encontrada
5. **Incluir casos Happy Path, Edge Cases y Error Handling** según código real
6. **Usar Gherkin como diseño de tests** antes de implementar código Java
7. **Documentar escenarios** como parte del entregable de cada paso

### **FORMATO OBLIGATORIO POR PASO:**

```markdown
### PASO X: [FEATURE NAME]

#### **ANÁLISIS DE REQUERIMIENTOS:**
[Resultado de fsRead de requerimientos_negocio.md y plan_detallado_implementacion_v1.0.md]

#### **ANÁLISIS DEL CÓDIGO:**
[Resultado de fsRead de controllers/services relevantes]

#### **ESCENARIOS GHERKIN MODELADOS:**
```gherkin
Feature: [Nombre basado en RF-XXX específico]
  Como [usuario según requerimiento de negocio]
  Quiero [funcionalidad RF-XXX identificada]
  Para [valor de negocio según requerimientos]

  @P0 @HappyPath @RF-XXX
  Scenario: [Caso principal según endpoint real]
    Given [precondición basada en requerimientos]
    When [acción según controller implementado]
    Then [resultado según response DTO real]
    And [validación según RN-XXX específica]

  @P1 @EdgeCase @RN-XXX
  Scenario: [Caso borde según reglas de negocio]
    [Escenarios derivados de RN específicas]

  @P2 @ErrorHandling
  Scenario: [Manejo de errores según exception handlers]
    [Casos de error según código implementado]
```

#### **IMPLEMENTACIÓN DE TESTS:**
[Código Java que implementa TODOS los escenarios Gherkin modelados]
```

### **CRITERIOS DE COMPLETITUD:**
- ✅ **Cobertura RF completa:** Cada RF-001 a RF-008 debe tener escenarios
- ✅ **Cobertura RN completa:** Cada RN-001 a RN-013 debe estar cubierta
- ✅ **Casos completos:** Happy Path + Edge Cases + Error Handling
- ✅ **Basado en requerimientos reales:** Escenarios derivados de documentos, no asumidos
- ✅ **Trazabilidad:** Cada test Java mapea a RF/RN específico

---

## METODOLOGÍA

**Proceso específico siguiendo Metodología Universal (`prompt-methodology-master.md`):**

### **PASO 1: ANALIZAR REQUERIMIENTOS COMPLETOS Y DISEÑAR ESCENARIOS**
- **Acción:** Usar `fsRead` para analizar TODOS los documentos de requerimientos
- **Objetivo:** Mapear RF-001 a RF-008 y RN-001 a RN-013 a casos de prueba específicos
- **Formato:** Given/When/Then para cada requerimiento funcional
- **Herramientas:** `fsRead` (requerimientos + plan + código), `fsWrite` (documentar escenarios)
- **Confirmación:** "He definido [X] escenarios Gherkin basados en [Y] RF y [Z] RN. ¿Continúo con implementación?"

### **PASO 2: CONFIGURAR INFRAESTRUCTURA DE TESTING**
- **Acción:** Usar `fsWrite` para crear BaseIntegrationTest + TestContainers
- **Enfoque:** PostgreSQL real + WireMock para Telegram (NO RabbitMQ)
- **Herramientas:** `fsWrite`, `fsReplace`
- **Confirmación:** "Setup de TestContainers completado. ¿Continúo con implementación de tests?"

### **PASO 3: IMPLEMENTAR TESTS POR FEATURE**
- **Acción:** Usar `fsWrite` para crear clases *IT.java por cada funcionalidad
- **Orden:** Creación → Procesamiento → Notificaciones → Validaciones → Admin
- **Herramientas:** `fsWrite` para cada clase de test
- **Confirmación:** "Feature [X] implementada con [Y] tests. ¿Continúo con siguiente?"

### **PASO 4: VALIDAR EJECUCIÓN Y DOCUMENTAR**
- **Acción:** Usar `executeBash` para ejecutar tests y `fsWrite` para documentar resultados
- **Verificar:** Tests pasan correctamente con código real implementado
- **Herramientas:** `executeBash`, `fsWrite`
- **Confirmación:** "Tests E2E ejecutados y documentados. Trabajo completado."

**CRITERIO DE COMPLETITUD:** Cada test debe validar funcionalidad REAL del código, cubriendo TODOS los requerimientos funcionales y reglas de negocio.

**Template de confirmación simplificado:**
```
✅ PASO [X] COMPLETADO
**Acción:** [Descripción breve]
**RF cubiertos:** [Lista de RF-XXX]
**RN cubiertas:** [Lista de RN-XXX]
**Archivos:** [Lista de archivos creados/modificados]
¿Continúo con el siguiente paso?
```

---

## STACK DE TESTING E2E

**Componentes de Testing:**

| Componente | Versión | Propósito | Configuración |
|------------|---------|-----------|---------------|
| JUnit 5 (Jupiter) | 5.10+ | Framework de testing | `@Test`, `@DisplayName`, `@Nested` |
| TestContainers | 1.19+ | PostgreSQL real en Docker | `@Container`, `@Testcontainers` |
| RestAssured | 5.4+ | Testing de APIs REST | `given().when().then()` |
| WireMock | 3.0+ | Mock de Telegram API | Puerto 8089, stubs configurables |
| Awaitility | 4.2+ | Esperas asíncronas | `await().until()` para schedulers |
| Spring Boot Test | 3.2+ | Contexto completo | `@SpringBootTest(webEnvironment = RANDOM_PORT)` |

**Diferencias críticas con Unit Tests:**
- ✅ **@SpringBootTest:** Contexto completo de Spring (vs @ExtendWith(MockitoExtension.class))
- ✅ **Base de datos real:** PostgreSQL en TestContainer (vs @DataJpaTest con H2)
- ✅ **Telegram mockeado:** WireMock server real (vs @MockBean)
- ✅ **Schedulers activos:** Procesamiento asíncrono real (vs mocks)
- ✅ **Transacciones reales:** Commit/rollback completo (vs @Transactional(rollback=true))

**Configuración de profiles:**
```yaml
# application-test.yml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # Usar migraciones Flyway
  flyway:
    enabled: true
    clean-disabled: false  # Permitir clean en tests
telegram:
  api-url: http://localhost:8089/bot  # WireMock
  enabled: true
logging:
  level:
    com.example.ticketero: DEBUG
    org.testcontainers: INFO
```

**Configuración TestContainers:**
```java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
    .withDatabaseName("ticketero_test")
    .withUsername("test")
    .withPassword("test")
    .withReuse(true);  // Reutilizar entre tests para velocidad
```

---

## TU TAREA: 7 PASOS ADAPTATIVOS

**PASO 1:** Análisis completo de requerimientos y setup
**PASO 2:** Feature: Creación de Tickets (RF-001, RF-003, RN-001, RN-005, RN-006)
**PASO 3:** Feature: Procesamiento de Tickets (RF-004, RN-002, RN-003, RN-004)
**PASO 4:** Feature: Notificaciones Telegram (RF-002, RN-007, RN-008, RN-009)
**PASO 5:** Feature: Validaciones de Input (RN-010, RN-011, RN-012, RN-013)
**PASO 6:** Feature: Dashboard Admin (RF-007, RF-008)
**PASO 7:** Ejecución Final, Reporte y Documentación

**PRINCIPIO ADAPTATIVO:** Cada paso debe analizar PRIMERO los requerimientos y código implementado para determinar escenarios exactos necesarios.

### **ESTRUCTURA DE ARCHIVOS A CREAR:**
```
src/test/java/com/example/ticketero/
├── integration/
│   ├── BaseIntegrationTest.java
│   ├── TicketCreationIT.java
│   ├── TicketProcessingIT.java
│   ├── NotificationIT.java
│   ├── ValidationIT.java
│   └── AdminDashboardIT.java
└── config/
    └── WireMockConfig.java
```

### **METODOLOGÍA POR PASO:**
1. **Analizar requerimientos:** `fsRead` de documentos de negocio y plan
2. **Analizar código:** `fsRead` para entender funcionalidad implementada
3. **Diseñar escenarios Gherkin:** Basados en RF/RN específicos
4. **Implementar tests:** `fsWrite` para crear clases IT
5. **Validar ejecución:** `executeBash` para verificar tests
6. **Documentar resultados:** `fsWrite` en `docs\\verify\\04-functional-tests\\`
7. **Confirmar y continuar:** Solicitar aprobación antes del siguiente paso

---

## IMPLEMENTACIÓN POR PASOS

### **PASO 1: ANÁLISIS COMPLETO DE REQUERIMIENTOS Y CÓDIGO**
**Objetivo:** Analizar TODOS los documentos de requerimientos y código implementado.

**Acciones específicas:**
1. **Analizar requerimientos de negocio:** `fsRead` de `docs\\requirements\\requerimientos_negocio.md`
2. **Revisar plan de implementación:** `fsRead` de `docs\\implementation\\plan_detallado_implementacion_v1.0.md`
3. **Estudiar smoke tests existentes:** `fsRead` de `docs\\verify\\02-smoke-tests\\SMOKE-TESTS-RESULTS.md`
4. **Analizar diagramas de flujo:** `fsRead` de `docs\\architecture\\diagrams\\02-sequence-diagram.puml`
5. **Inventariar código implementado:** `listDirectory` + `fsRead` de controllers/services
6. **Mapear RF-001 a RF-008:** Identificar qué está implementado vs especificado
7. **Identificar RN-001 a RN-013:** Reglas de negocio específicas del plan
8. **Crear BaseIntegrationTest:** `fsWrite` con configuración basada en análisis

**Entregables:**
- Inventario completo de RF implementados vs especificados
- Mapeo de RN-001 a RN-013 con ubicación en código
- BaseIntegrationTest.java configurado para el stack real
- WireMockConfig.java para Telegram API

### **PASO 2: FEATURE - CREACIÓN DE TICKETS**
**Objetivo:** Testear RF-001 (Crear Ticket) y RF-003 (Calcular Posición) con RN asociadas.

**Acciones específicas:**
1. **Mapear RF-001:** Analizar endpoint de creación vs requerimiento de negocio
2. **Mapear RF-003:** Verificar cálculo de posición y tiempo estimado
3. **Incluir RN-001:** Validación unicidad ticket activo
4. **Incluir RN-005:** Numeración secuencial con prefijo
5. **Incluir RN-006:** Formato de número de ticket
6. **Diseñar escenarios Gherkin:** Basados en RF y RN específicas
7. **Implementar TicketCreationIT:** `fsWrite` con tests completos

### **PASO 3: FEATURE - PROCESAMIENTO DE TICKETS**
**Objetivo:** Testear RF-004 (Asignación Automática) con RN de procesamiento.

**Acciones específicas:**
1. **Mapear RF-004:** Asignación automática a ejecutivos
2. **Incluir RN-002:** Selección por prioridad de cola
3. **Incluir RN-003:** Balanceo de carga entre ejecutivos
4. **Incluir RN-004:** Orden FIFO dentro de cola
5. **Implementar TicketProcessingIT:** Tests de schedulers y asignación

### **PASO 4: FEATURE - NOTIFICACIONES TELEGRAM**
**Objetivo:** Testear RF-002 (Notificaciones Automáticas) con RN de mensajería.

**Acciones específicas:**
1. **Mapear RF-002:** Los 3 mensajes automáticos especificados
2. **Incluir RN-007:** Mensaje 1 - Confirmación inmediata
3. **Incluir RN-008:** Mensaje 2 - Pre-aviso (posición ≤ 3)
4. **Incluir RN-009:** Mensaje 3 - Turno activo con asesor
5. **Implementar NotificationIT:** WireMock para validar mensajes

### **PASO 5: FEATURE - VALIDACIONES DE INPUT**
**Objetivo:** Testear validaciones de datos según RN específicas.

**Acciones específicas:**
1. **Incluir RN-010:** Validación formato RUT/ID
2. **Incluir RN-011:** Validación teléfono chileno
3. **Incluir RN-012:** Validación tipos de cola
4. **Incluir RN-013:** Validación campos obligatorios
5. **Implementar ValidationIT:** Casos de validación específicos

### **PASO 6: FEATURE - DASHBOARD ADMIN**
**Objetivo:** Testear RF-007 (Panel Monitoreo) y RF-008 (Auditoría).

**Acciones específicas:**
1. **Mapear RF-007:** Dashboard en tiempo real especificado
2. **Mapear RF-008:** Registro de auditoría completo
3. **Implementar AdminDashboardIT:** Tests de endpoints administrativos

### **PASO 7: EJECUCIÓN Y DOCUMENTACIÓN COMPLETA**
**Objetivo:** Ejecutar suite completa y generar documentación en `docs\\verify\\04-functional-tests\\`.

**Acciones específicas:**
1. **Ejecutar todos los tests:** `executeBash` con `mvn test -Dtest="*IT"`
2. **Generar reporte:** `executeBash` con `mvn surefire-report:report`
3. **Validar cobertura:** Verificar que todos los RF/RN están cubiertos
4. **Documentar resultados:** `fsWrite` para crear documentación en `docs\\verify\\04-functional-tests\\`

### **PRINCIPIOS PARA TODOS LOS PASOS:**
- **Análisis de requerimientos primero:** Siempre `fsRead` de documentos de negocio antes de código
- **Mapeo RF/RN específico:** Cada test debe referenciar RF-XXX o RN-XXX específica
- **Basado en código real:** Tests que reflejen funcionalidad implementada
- **Escenarios Gherkin completos:** Incluir TODOS los escenarios identificados en requerimientos
- **Validación continua:** `executeBash` para verificar cada paso
- **Confirmación obligatoria:** Solicitar aprobación antes de continuar

### **VALIDACIÓN OBLIGATORIA:**
Antes de continuar cada paso, el agente debe confirmar:
- "He analizado [X] requerimientos funcionales y [Y] reglas de negocio"
- "He modelado [Z] escenarios Gherkin basados en RF-XXX y RN-XXX específicas"
- "Los escenarios cubren Happy Path, Edge Cases y Error Handling según requerimientos"
- "¿Están completos los escenarios antes de implementar tests?"

---

## DOCUMENTACIÓN OBLIGATORIA

**El agente DEBE crear la siguiente documentación en `docs\\verify\\04-functional-tests\\`:**

### **1. `test-execution-report.md`**
```markdown
# Reporte de Ejecución - Tests E2E Funcionales

## Resumen Ejecutivo
- **Fecha de ejecución:** [timestamp]
- **Total tests:** [número]
- **Tests exitosos:** [número]
- **Tests fallidos:** [número]
- **Cobertura de RF:** [RF-001 a RF-008 cubiertos]
- **Cobertura de RN:** [RN-001 a RN-013 cubiertas]

## Resultados por Feature
### Feature: Creación de Tickets (RF-001, RF-003)
- **Tests ejecutados:** [número]
- **Escenarios Gherkin cubiertos:** [lista]
- **RN validadas:** [RN-001, RN-005, RN-006]
- **Resultado:** ✅/❌

[Repetir para cada feature]

## Métricas de Rendimiento
- **Tiempo total de ejecución:** [duración]
- **Tiempo promedio por test:** [duración]
- **Tests más lentos:** [lista con tiempos]

## Matriz de Trazabilidad
| RF/RN | Escenario Gherkin | Test Java | Estado |
|-------|-------------------|-----------|--------|
| RF-001 | Crear ticket válido | TicketCreationIT.crearTicket_datosValidos | ✅ |

## Issues Encontrados
[Lista de problemas si los hay]

## Recomendaciones
[Sugerencias de mejora]
```

### **2. `user-guide.md`**
```markdown
# Guía de Uso - Tests E2E Funcionales

## Prerrequisitos
- Java 17+
- Maven 3.8+
- Docker Desktop ejecutándose
- 4GB RAM disponible para TestContainers

## Comandos de Ejecución

### Ejecutar todos los tests E2E
```bash
mvn test -Dtest="*IT"
```

### Ejecutar feature específica
```bash
mvn test -Dtest=TicketCreationIT
mvn test -Dtest=TicketProcessingIT
mvn test -Dtest=NotificationIT
mvn test -Dtest=ValidationIT
mvn test -Dtest=AdminDashboardIT
```

### Generar reporte HTML
```bash
mvn surefire-report:report
open target/site/surefire-report.html
```

## Configuración de Entorno
[Detalles de configuración específica]

## Troubleshooting
[Problemas comunes y soluciones]

## Estructura de Tests
[Explicación de la organización de tests]
```

### **3. `gherkin-scenarios.md`**
```markdown
# Escenarios Gherkin - Tests E2E Funcionales

## Feature: Creación de Tickets (RF-001, RF-003)
[Todos los escenarios Gherkin modelados]

## Feature: Procesamiento de Tickets (RF-004)
[Todos los escenarios Gherkin modelados]

[Continuar para todas las features]

## Matriz de Trazabilidad
| Escenario Gherkin | Test Java | RF/RN | Endpoint | Estado |
|-------------------|-----------|-------|----------|--------|
| [nombre] | [clase.método] | RF-001 | POST /api/tickets | ✅/❌ |
```

**INSTRUCCIONES DE CREACIÓN:**
1. **Usar `fsWrite`** para crear cada archivo .md
2. **Completar con datos reales** de la ejecución de tests
3. **Incluir timestamps** y métricas específicas
4. **Documentar TODOS los escenarios Gherkin** modelados
5. **Mapear cada test a RF/RN específica**
6. **Crear guía práctica** para otros desarrolladores

---

## CRITERIOS DE COMPLETITUD

### **Testing E2E Completo:**
- ✅ **Cobertura RF completa:** RF-001 a RF-008 tienen tests específicos
- ✅ **Cobertura RN completa:** RN-001 a RN-013 están validadas
- ✅ **Escenarios Gherkin:** Modelados para cada RF/RN encontrada
- ✅ **Casos completos:** Happy Path + Edge Cases + Error Handling
- ✅ **Infraestructura real:** TestContainers + WireMock configurados
- ✅ **Ejecución exitosa:** Todos los tests pasan con código real
- ✅ **Documentación completa:** Reportes y guías en `docs\\verify\\04-functional-tests\\`

### **Entregables Finales:**
- Suite completa de tests E2E ejecutables
- Escenarios Gherkin documentados por RF/RN
- Configuración de TestContainers funcional
- **Documentación completa en `docs\\verify\\04-functional-tests\\`**
- Matriz de trazabilidad RF/RN → Tests
- Guía de uso para otros desarrolladores

---

**Prompt creado por:** Ingeniero de Prompts Senior  
**Metodología aplicada:** Análisis iterativo con validación por sección  
**Fecha de creación:** 2025-12-23  
**Versión:** 1.0