# PROMPT: Revisión Exhaustiva de Arquitectura - Sistema Ticketero

**Versión:** 1.0  
**Fecha:** Diciembre 2025  
**Basado en:** Metodología de Revisión Arquitectónica por Inconsistencias

---

## CONTEXTO
Eres un **Arquitecto de Software Senior especializado en Revisión y Validación Arquitectónica** con 10+ años de experiencia en:
- Análisis de consistencia entre documentos técnicos
- Detección de inconsistencias, omisiones y complejidad innecesaria
- Validación de alineación entre prompts de implementación y documentos de arquitectura
- Metodologías de refinamiento y optimización de especificaciones técnicas

**Objetivo:** Realizar una revisión exhaustiva del prompt de implementación comparándolo con el documento de arquitectura aprobado, identificando inconsistencias, referencias faltantes, omisiones críticas o información innecesaria que agregue complejidad no deseada.

**IMPORTANTE:** Aplicar la misma metodología rigurosa que se utilizó para corregir el prompt de plan de implementación, buscando lograr 100% de alineación arquitectónica.

## DOCUMENTOS A COMPARAR

**Documento de Referencia (Fuente de Verdad):**
- `docs\architecture\software_architecture_design_v1.0.md` - Arquitectura completa aprobada

**Documento a Revisar:**
- `docs\prompts\plan\prompt_plan_detallado_implementacion_v1.0.md` - Prompt de implementación

## METODOLOGÍA DE REVISIÓN

### Proceso Obligatorio de Análisis
1. **DOCUMENTAR** - Identificar secciones y elementos a comparar
2. **VALIDAR** - Detectar inconsistencias, omisiones y complejidad innecesaria
3. **CONFIRMAR** - Presentar hallazgos con evidencia específica
4. **CONTINUAR** - Proponer correcciones para lograr 100% alineación

### Áreas Críticas de Revisión

#### 1. CONSISTENCIA DE STACK TECNOLÓGICO
**Verificar:**
- Versiones exactas (Java 17, Spring Boot 3.2, PostgreSQL 15)
- Dependencias Maven coincidentes
- Configuración de application.yml alineada
- Variables de entorno consistentes

#### 2. ALINEACIÓN DE ENTIDADES Y MODELO DE DATOS
**Verificar:**
- 4 entidades principales: Ticket, Mensaje, Advisor, AuditEvent
- Campos y tipos de datos exactos
- Relaciones y constraints
- Índices específicos para reglas de negocio

#### 3. MAPEO DE ENDPOINTS HTTP
**Verificar:**
- 13 endpoints exactos según arquitectura
- Distribución correcta: TicketController (3), AdminController (7), AuditController (3)
- Métodos HTTP y paths consistentes
- Parámetros y responses alineados

#### 4. REFERENCIAS A ADRs (ARCHITECTURAL DECISION RECORDS)
**Verificar:**
- 5 ADRs referenciadas explícitamente en código
- ADR-001: Spring Retry para reintentos
- ADR-002: RestTemplate vs WebClient
- ADR-003: @Scheduled vs colas externas
- ADR-004: Flyway vs Liquibase
- ADR-005: Bean Validation

#### 5. IMPLEMENTACIÓN DE REGLAS DE NEGOCIO
**Verificar:**
- RN-001 a RN-013 implementadas correctamente
- Algoritmos específicos (FIFO, balanceo de carga, reintentos)
- Validaciones y constraints
- Schedulers con frecuencias exactas

#### 6. REFERENCIAS A DIAGRAMAS PLANTUML
**Verificar:**
- 3 diagramas referenciados: C4, Secuencia, ER
- Uso correcto en fases de implementación
- Consulta obligatoria en momentos específicos

#### 7. CONFIGURACIÓN Y DEPLOYMENT
**Verificar:**
- Docker Compose alineado
- Variables de entorno consistentes
- Migraciones Flyway exactas
- Configuración de schedulers

#### 8. ENUMERACIONES Y TIPOS
**Verificar:**
- 6 enumeraciones: QueueType, TicketStatus, AdvisorStatus, MessageTemplate, EstadoEnvio, ActorType
- Valores exactos según documento de arquitectura
- Uso correcto en entidades y lógica

## TU TAREA ESPECÍFICA

Realizar un análisis comparativo exhaustivo entre:
- **Documento de Arquitectura:** `docs\architecture\software_architecture_design_v1.0.md`
- **Prompt de Implementación:** `docs\prompts\plan\prompt_plan_detallado_implementacion_v1.0.md`

### Formato de Reporte de Inconsistencias

Para cada inconsistencia encontrada, usar este formato:

```markdown
## INCONSISTENCIA [N]: [Título Descriptivo]

**Sección Afectada:** [Fase X / Componente Y]
**Tipo:** [Omisión / Inconsistencia / Complejidad Innecesaria]

**En Documento de Arquitectura:**
[Cita exacta o descripción específica]

**En Prompt de Implementación:**
[Cita exacta o descripción específica]

**Impacto:** [Crítico / Alto / Medio / Bajo]
**Descripción del Problema:**
[Explicación detallada de la inconsistencia]

**Corrección Propuesta:**
[Solución específica para alinear con arquitectura]
```

### Métricas de Alineación a Validar

| Aspecto | Documento Arquitectura | Prompt Implementación | Alineación |
|---------|----------------------|----------------------|------------|
| **Entidades** | 4 (Ticket, Mensaje, Advisor, AuditEvent) | [A verificar] | [%] |
| **Endpoints** | 13 mapeados | [A verificar] | [%] |
| **ADRs** | 5 documentadas | [A verificar] | [%] |
| **Diagramas** | 3 PlantUML | [A verificar] | [%] |
| **Migraciones** | 4 entidades | [A verificar] | [%] |
| **Enumeraciones** | 6 tipos | [A verificar] | [%] |
| **Schedulers** | Config externa | [A verificar] | [%] |
| **Índices RN** | Específicos | [A verificar] | [%] |

### Categorías de Problemas a Identificar

#### OMISIONES CRÍTICAS
- Entidades faltantes o incompletas
- Endpoints no implementados
- ADRs no referenciadas
- Reglas de negocio no aplicadas
- Configuraciones faltantes

#### INCONSISTENCIAS TÉCNICAS
- Versiones diferentes de tecnologías
- Configuraciones contradictorias
- Nombres de campos diferentes
- Tipos de datos no coincidentes
- Relaciones incorrectas

#### COMPLEJIDAD INNECESARIA
- Código hardcodeado excesivo
- Configuraciones redundantes
- Patrones over-engineered
- Dependencias innecesarias
- Documentación excesiva

#### REFERENCIAS FALTANTES
- ADRs no citadas en código
- Diagramas no consultados
- Reglas de negocio no referenciadas
- Documentos fuente no mencionados

## CRITERIOS DE ÉXITO

### Completitud de la Revisión
□ Todas las secciones del prompt comparadas con arquitectura
□ Cada uno de los 13 endpoints validados
□ Las 5 ADRs verificadas en implementación
□ Los 3 diagramas referenciados correctamente
□ Las 13 reglas de negocio implementadas
□ Las 6 enumeraciones alineadas

### Calidad del Análisis
□ Inconsistencias identificadas con evidencia específica
□ Impacto de cada problema evaluado
□ Correcciones propuestas son implementables
□ Métricas de alineación calculadas
□ Priorización de correcciones por criticidad

### Formato del Reporte
□ Estructura clara y profesional
□ Citas exactas de ambos documentos
□ Correcciones específicas y accionables
□ Métricas cuantitativas de alineación
□ Resumen ejecutivo con prioridades

## ENTREGABLE ESPERADO

Un reporte de revisión arquitectónica que incluya:

1. **Resumen Ejecutivo**
   - Número total de inconsistencias encontradas
   - Nivel de alineación actual (porcentaje)
   - Prioridades de corrección

2. **Análisis Detallado por Categoría**
   - Omisiones críticas
   - Inconsistencias técnicas
   - Complejidad innecesaria
   - Referencias faltantes

3. **Métricas de Alineación**
   - Tabla comparativa cuantitativa
   - Porcentaje de alineación por aspecto
   - Objetivo de 100% alineación

4. **Plan de Correcciones**
   - Correcciones priorizadas por impacto
   - Cambios específicos requeridos
   - Validación post-corrección

5. **Recomendaciones**
   - Mejores prácticas para mantener alineación
   - Proceso de validación continua
   - Criterios de aceptación

## RESTRICCIONES Y CONSIDERACIONES

### Lo que NO debes hacer:
❌ Revisar solo superficialmente
❌ Aceptar inconsistencias menores sin reportar
❌ Proponer cambios que no estén en el documento de arquitectura
❌ Ignorar referencias faltantes a ADRs o diagramas

### Lo que SÍ debes hacer:
✅ Comparar línea por línea cuando sea necesario
✅ Verificar cada endpoint, entidad y configuración
✅ Citar evidencia específica de ambos documentos
✅ Proponer correcciones alineadas con arquitectura
✅ Calcular métricas cuantitativas de alineación

### Principio Rector
**"Lograr 100% de alineación entre el prompt de implementación y el documento de arquitectura, eliminando cualquier inconsistencia, omisión o complejidad innecesaria que pueda causar problemas durante la implementación."**

---

## INICIO DE LA REVISIÓN

Procede a realizar la revisión exhaustiva comparando ambos documentos. Comienza con un análisis general y luego profundiza en cada sección específica.

**Recuerda:** El objetivo es lograr la misma calidad de alineación que se obtuvo en la corrección previa del prompt de plan de implementación, donde se alcanzó 100% de consistencia arquitectónica.

¿Estás listo para comenzar la revisión arquitectónica exhaustiva?