# Prompt: Implementación Completa del Sistema Ticketero

**Versión:** 1.1  
**Fecha:** Diciembre 2025
**Tipo:** Prompt de Implementación de Código  
**Basado en:** Plan Detallado de Implementación v1.0

---

## ROL

Desarrollador Senior Full-Stack especializado en Implementación de Sistemas Spring Boot, con 7+ años de experiencia en:
- Desarrollo de aplicaciones Java 17 + Spring Boot 3.2 + PostgreSQL
- Implementación de arquitecturas en capas con patrones empresariales
- Desarrollo guiado por planes de implementación detallados
- Integración de sistemas con APIs externas (Telegram Bot API)
- Metodologías de desarrollo iterativo con validación paso a paso

---

## ESTADO ACTUAL DE LA IMPLEMENTACIÓN

**Estado Actual: FASE 0 COMPLETADA ✅**

Has completado exitosamente la FASE 0: SETUP DEL PROYECTO con los siguientes elementos implementados:

**✅ Completado:**
- `pom.xml` - Configuración Maven completa con todas las dependencias
- `application.yml` - Configuración Spring Boot con PostgreSQL, Flyway, schedulers
- `TicketeroApplication.java` - Clase principal con anotaciones necesarias (@EnableScheduling, @EnableRetry)
- Estructura de directorios - Package structure básica creada
- Carpeta de migraciones - `src/main/resources/db/migration/` creada pero vacía

**❌ Pendiente de FASE 1:**
- Migraciones Flyway (V1__create_ticket_table.sql, V2__create_mensaje_table.sql, etc.)
- Enumeraciones Java (QueueType.java, TicketStatus.java, AdvisorStatus.java, etc.)
- docker-compose.yml y .env para infraestructura

**Próximo Paso: FASE 1: MIGRACIONES Y ENUMERACIONES**

Necesitas crear:
- 4 archivos de migración SQL (V1-V4)
- 6 enumeraciones Java
- Configuración Docker

---

## ACCIÓN

Continuar la implementación del Sistema Ticketero desde la FASE 1 siguiendo el plan de implementación mediante desarrollo iterativo paso a paso para:

**Objetivos de Implementación:**
- Ejecutar las 7 fases restantes del plan en secuencia (Migraciones → Entities → DTOs → Repositories → Services → Controllers → Schedulers)
- Generar código Java funcional y compilable en cada paso
- Validar cumplimiento de reglas de negocio y criterios de aceptación definidos en `docs\\implementation\\plan_detallado_implementacion_v1.0.md`
- Mantener trazabilidad completa con el plan de implementación aprobado

**Metodología de Desarrollo:**
1. **Implementar**: Crear código completo del paso según especificaciones del plan
2. **Validar**: Ejecutar pruebas de compilación Y funcionales para verificar cumplimiento de reglas de negocio
3. **Probar**: Realizar prueba mínima funcional para validar funcionamiento y reglas de negocio cubiertas hasta el momento
4. **Confirmar**: Solicitar revisión exhaustiva con evidencia de pruebas antes de continuar
5. **Continuar**: Avanzar solo tras aprobación explícita del usuario

**Validaciones Obligatorias por Paso:**
- **Compilación**: `mvn clean compile` sin errores
- **Funcionales**: Verificar que el componente cumple criterios de aceptación específicos
- **Prueba Mínima**: Ejecutar test básico para validar funcionamiento hasta la fase actual
- **Conformidad con Documentos**: OBLIGATORIO validar contra documentos específicos:
  - Plan: Verificar que implementación sigue especificaciones exactas del paso
  - Arquitectura: Confirmar que respeta patrones, stack y decisiones arquitectónicas
  - Requerimientos: Validar cumplimiento de RF y RN correspondientes
- **Trazabilidad**: Citar sección específica del documento que respalda cada implementación

---

## CONTEXTO

Implementación del Sistema Ticketero siguiendo documentación técnica aprobada:

**Documentos de Referencia Obligatorios:**
- **Plan de Implementación:** `docs\\implementation\\plan_detallado_implementacion_v1.0.md`
- **Arquitectura del Sistema:** `docs\\architecture\\software_architecture_design_v1.0.md`
- **Requerimientos Funcionales:** `docs\\requirements\\functional_requirements_analysis_v1.0.md`
- **Reglas del Desarrollador:** `.amazonq\\rules\\` (aplicación automática durante desarrollo)

**Stack Tecnológico Definido:**
- **Backend:** Java 17 + Spring Boot 3.2 + PostgreSQL 15
- **Herramientas:** Maven + Flyway + Docker + Lombok
- **Integración:** Telegram Bot API para notificaciones
- **Arquitectura:** Capas (Controller → Service → Repository → Entity)

**Criterios de Implementación:**
- Seguir exactamente las 7 fases restantes definidas en el plan (10.5 horas estimadas restantes)
- Implementar reglas de negocio RN-001 a RN-013 según mapeo específico del plan
- Cumplir criterios de aceptación de RF-001 a RF-008
- Aplicar patrones y estándares definidos en reglas automáticas
- **Consultar diagramas obligatorios:** C4 Context, Sequence End-to-End, ER Database
- **Referenciar ADRs:** ADR-001 a ADR-005 en código según especificación

**Validación Continua:**
- Cada componente debe compilar sin errores
- Cada fase debe cumplir criterios específicos del plan
- Reglas de negocio deben ser verificables funcionalmente
- Trazabilidad completa con documentos de referencia
- **Prueba funcional mínima** después de cada fase implementada

---

## RESULTADO

Para cada paso de implementación, entregar:

**Entregables por Paso:**
- **Código Funcional:** Archivos Java compilables y ejecutables con documentación técnica completa
- **Validaciones Ejecutadas:** Evidencia de pruebas de compilación y funcionales
- **Prueba Mínima Funcional:** Test básico que demuestre funcionamiento hasta la fase actual
- **Reporte de Cumplimiento:** Verificación de criterios específicos del plan
- **Solicitud de Revisión:** Confirmación estructurada antes de continuar

**Template de Confirmación Obligatorio:**
```
✅ PASO [X.Y] COMPLETADO
**Fase:** [Nombre de la fase]
**Archivos creados/modificados:**
- [lista específica de archivos]

**Validaciones realizadas:**
- ✅ Compilación: mvn clean compile exitoso
- ✅ Funcional: [prueba específica del componente]
- ✅ Prueba Mínima: [test básico ejecutado con resultado]
- ✅ Documentación: Comentarios técnicos agregados/actualizados en todo el código
- ✅ Plan de Implementación: [sección específica validada]
- ✅ Arquitectura: [patrón/estándar aplicado según documento]
- ✅ Requerimientos: [RF/RN específicos cumplidos]
- ✅ Trazabilidad: [cita textual del documento que justifica implementación]

🔍 SOLICITO REVISIÓN EXHAUSTIVA:
1. ¿El código cumple las especificaciones exactas del plan?
2. ¿Las validaciones confirman alineación con documentos?
3. ¿Se implementaron correctamente según arquitectura definida?
4. ¿La prueba mínima demuestra funcionamiento correcto?
5. ¿Puedo continuar con el siguiente paso?

⏸️ ESPERANDO CONFIRMACIÓN PARA CONTINUAR...
```

**Criterios de Completitud Final:**
- **8 Fases Implementadas:** Setup → Migraciones → Entities → DTOs → Repositories → Services → Controllers → Schedulers
- **Sistema Funcional:** Aplicación ejecutable con todas las funcionalidades
- **Reglas de Negocio:** RN-001 a RN-013 implementadas y validadas
- **Criterios de Aceptación:** RF-001 a RF-008 cumplidos y verificables
- **Documentación Técnica del Código:** Archivo completo generado en `docs\\implementation\\codigo_documentacion_v1.0.md`

**Documentación Final Obligatoria:**
Al completar la implementación completa, generar:
- **Archivo:** `docs\\implementation\\codigo_documentacion_v1.0.md`
- **Contenido:** Documentación completa de todo el código implementado
- **Propósito:** Input para agente de pruebas automatizadas
- **Actualización:** Nueva versión (v1.1, v1.2, etc.) con cada modificación posterior

---

## METODOLOGÍA

Proceso iterativo obligatorio siguiendo las 7 fases restantes del plan de implementación:

**Estructura de Fases Obligatoria:**
- **FASE 1:** Migraciones y Enums (45 min) - 4 archivos SQL Flyway (V1-V4) + 6 enumeraciones Java
- **FASE 2:** Entities JPA (1 hora) - 4 entidades (Ticket, Mensaje, Advisor, AuditEvent) con relaciones
- **FASE 3:** DTOs y Validación (45 min) - 6 DTOs (Request/Response/Error) + Bean Validation
- **FASE 4:** Repositories (30 min) - 4 interfaces JPA con queries custom
- **FASE 5:** Services (3 horas) - 6 services con lógica de negocio RN-001 a RN-013
- **FASE 6:** Controllers (2 horas) - 3 controllers con 13 endpoints
- **FASE 7:** Schedulers (1.5 horas) - 2 schedulers con procesamiento asíncrono

**Proceso por Cada Paso:**

**Fase 1: IMPLEMENTAR**
- Crear código completo según especificaciones del plan
- Seguir patrones y estándares definidos en reglas automáticas
- **Documentar código:** Agregar comentarios técnicos JavaDoc y inline para cada componente
- **Mantener documentación:** Actualizar comentarios con cada modificación, corrección o mejora
- Referenciar documentos específicos del proyecto en comentarios

**Fase 2: VALIDAR**
- **Compilación:** `mvn clean compile` sin errores
- **Funcional:** Ejecutar pruebas específicas del componente
- **Validación contra Documentos:** OBLIGATORIO verificar cada implementación contra:
  - Plan de Implementación: Confirmar que sigue especificaciones exactas del paso
  - Arquitectura: Validar que respeta patrones y stack tecnológico definido
  - Requerimientos Funcionales: Verificar que cumple criterios de aceptación
- **Reglas de Negocio:** Verificar implementación de RN correspondientes según documentos
- **Trazabilidad:** Citar sección específica del documento que justifica la implementación

**Fase 3: PROBAR**
- **Pregunta Clave:** En este punto, además de las validaciones ya hechas, ¿se puede hacer alguna prueba mínima funcional para validar funcionamiento y reglas de negocio hasta el momento cubiertas?
- **Ejecutar:** Test básico apropiado para la fase actual
- **Documentar:** Resultado de la prueba mínima en el reporte
- **Repetir:** Esta acción después de cada implementación

**Fase 4: CONFIRMAR**
- Usar template de confirmación estructurado obligatorio
- Documentar evidencia de todas las validaciones realizadas
- Incluir resultado de prueba mínima funcional
- Solicitar revisión exhaustiva con preguntas específicas
- DETENERSE completamente hasta recibir aprobación

**Fase 5: CONTINUAR**
- Avanzar solo tras confirmación explícita del usuario
- Mantener contexto de fases previas completadas
- Aplicar lecciones aprendidas de revisiones anteriores
- **Actualizar documentación:** Si hay modificaciones, generar nueva versión de `codigo_documentacion_vX.Y.md`

**Validaciones Específicas por Tipo de Componente:**
- **Migraciones:** Flyway exitoso + 4 tablas creadas (V1-V4) + datos iniciales (5 asesores) + comentarios SQL explicativos + **Prueba:** Verificar tablas creadas y datos insertados
- **Entities:** Hibernate valida schema + 4 entidades + relaciones correctas + JavaDoc completo + **Prueba:** Crear instancia de cada entity y verificar mapeo
- **DTOs:** Validaciones Bean Validation + serialización JSON + 6 DTOs + comentarios de propósito + **Prueba:** Validar DTO con datos válidos e inválidos
- **Repositories:** Queries ejecutan + resultados esperados + 4 repositories + documentación de métodos custom + **Prueba:** Ejecutar query básica de cada repository
- **Services:** Reglas de negocio RN-001 a RN-013 + transacciones + logging + 6 services + JavaDoc de lógica compleja + **Prueba:** Ejecutar operación básica de cada service
- **Controllers:** Endpoints responden + códigos HTTP correctos + 3 controllers + 13 endpoints + documentación de API + **Prueba:** Llamar endpoint principal de cada controller
- **Schedulers:** Registro exitoso + ejecución programada + 2 schedulers + comentarios de frecuencia y propósito + **Prueba:** Verificar que schedulers se registran y ejecutan

---

## INICIO DE LA IMPLEMENTACIÓN

**Instrucciones de Arranque:**

1. **Leer Plan de Implementación:** Revisar `docs\\implementation\\plan_detallado_implementacion_v1.0.md` completamente
2. **Consultar Diagramas:** Revisar diagramas PlantUML (C4 Context, Sequence, ER) según fase
3. **Verificar Documentos:** Confirmar acceso a arquitectura y requerimientos funcionales
4. **Aplicar Reglas Automáticas:** Las reglas en `.amazonq\\rules\\` se aplicarán automáticamente
5. **Referenciar ADRs:** Incluir referencias a ADR-001 a ADR-005 en código según corresponda
6. **Comenzar FASE 1:** Iniciar con Migraciones y Enumeraciones siguiendo especificaciones exactas

**Primera Acción Requerida:**
Confirmar que has leído y comprendido el plan de implementación, luego comenzar con:

**FASE 1, PASO 1.1: Crear Migraciones Flyway**

Implementar exactamente según las especificaciones del plan, validar compilación y funcionalidad, ejecutar prueba mínima, luego solicitar revisión usando el template obligatorio.

**Acción Final Obligatoria:**
Al completar TODAS las 7 fases restantes, generar documentación técnica completa:
- **Crear:** `docs\\implementation\\codigo_documentacion_v1.0.md`
- **Incluir:** Documentación de todos los componentes implementados
- **Formato:** Estructura navegable para agente de pruebas automatizadas
- **Mantenimiento:** Nueva versión con cada modificación posterior

**¿Estás listo para continuar con la FASE 1: MIGRACIONES Y ENUMERACIONES?**

---

**Estado:** Refinado y Validado según Metodología Universal  
**Próximo Uso:** Listo para continuar implementación del Sistema Ticketero desde FASE 1

---

## TEMPLATE PARA DOCUMENTACIÓN TÉCNICA DEL CÓDIGO

### Estructura Obligatoria para `docs\\implementation\\codigo_documentacion_vX.Y.md`

```markdown
# Documentación Técnica del Código - Sistema Ticketero

**Versión:** X.Y  
**Fecha:** [Fecha de generación]  
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
- Java 17 + Spring Boot 3.2 + PostgreSQL 15
- Maven + Flyway + Docker + Lombok
- Telegram Bot API + Schedulers

---

## 2. ESTRUCTURA DEL CÓDIGO

### Packages y Organización
```
com.example.ticketero/
├── TicketeroApplication.java
├── controller/
├── service/
├── repository/
├── model/
│   ├── entity/
│   ├── dto/
│   └── enums/
├── scheduler/
├── config/
└── exception/
```

---

## 3. COMPONENTES IMPLEMENTADOS

### 3.1 Entities JPA
**Archivos:** [Lista de entities]
**Propósito:** [Descripción]
**Relaciones:** [Mapeo de relaciones]
**Reglas de Negocio:** [RN implementadas]

### 3.2 DTOs y Validación
**Archivos:** [Lista de DTOs]
**Validaciones:** [Bean Validation implementadas]
**Criterios:** [RF cumplidos]

### 3.3 Services
**Archivos:** [Lista de services]
**Lógica de Negocio:** [RN-001 a RN-013 implementadas]
**Transacciones:** [Configuración]

### 3.4 Controllers
**Archivos:** [Lista de controllers]
**Endpoints:** [13 endpoints implementados]
**Códigos HTTP:** [Respuestas configuradas]

### 3.5 Schedulers
**Archivos:** [Lista de schedulers]
**Frecuencias:** [Configuración de intervalos]
**Procesamiento:** [Lógica asíncrona]

---

## 4. REGLAS DE NEGOCIO IMPLEMENTADAS

### RN-001 a RN-013
[Detalle de cada regla implementada con ubicación en código]

---

## 5. CRITERIOS DE ACEPTACIÓN CUMPLIDOS

### RF-001 a RF-008
[Detalle de cada criterio cumplido con evidencia]

---

## 6. CONFIGURACIÓN Y DEPLOYMENT

### Base de Datos
- Migraciones Flyway: [V1, V2, V3, V4]
- Índices: [Configurados]
- Datos iniciales: [5 asesores]

### Docker
- docker-compose.yml: [Configurado]
- Dockerfile: [Multi-stage build]

---

## 7. PUNTOS CRÍTICOS PARA TESTING

### Componentes Clave a Probar
- [Lista de componentes críticos]

### Reglas de Negocio a Validar
- [RN específicas que requieren testing]

### Endpoints a Probar
- [13 endpoints con casos de prueba sugeridos]

### Schedulers a Validar
- [Procesamiento asíncrono y frecuencias]

---

## 8. PRUEBAS MÍNIMAS EJECUTADAS

### Por Fase
- **FASE 1:** [Prueba de migraciones y enums]
- **FASE 2:** [Prueba de entities]
- **FASE 3:** [Prueba de DTOs]
- **FASE 4:** [Prueba de repositories]
- **FASE 5:** [Prueba de services]
- **FASE 6:** [Prueba de controllers]
- **FASE 7:** [Prueba de schedulers]

---

## 9. CAMBIOS EN ESTA VERSIÓN

### Modificaciones Realizadas
- [Lista de cambios desde versión anterior]

### Impacto en Testing
- [Áreas que requieren re-testing]

---

**Generado por:** Agente Desarrollador  
**Para uso de:** Agente de Pruebas Automatizadas
```

---

## MAPEO ESPECÍFICO DE REGLAS DE NEGOCIO

### Implementación Obligatoria según Plan Detallado

**RN-001:** Validación unicidad ticket activo por cliente (TicketService.validarTicketActivoExistente)
**RN-002:** Selección por prioridad de cola (QueueManagementService.asignarSiguienteTicket)
**RN-003:** Orden FIFO dentro de cada cola (TicketRepository.findNextTicketByPriority)
**RN-004:** Balanceo de carga entre ejecutivos (AdvisorRepository.findLeastLoadedAvailable)
**RN-005:** Numeración secuencial por cola (TicketService.generarNumeroTicket)
**RN-006:** Prefijos por tipo de cola C, P, E, G (QueueType.getPrefix)
**RN-007:** Máximo 3 reintentos de envío (MensajeScheduler.manejarFalloEnvio)
**RN-008:** Backoff exponencial 30s, 60s, 120s (TelegramService @Retryable)
**RN-009:** Timeout de NO_SHOW 5 minutos (QueueProcessorScheduler.procesarTimeouts)
**RN-010:** Cálculo tiempo estimado = posición * tiempo promedio (QueueManagementService.calcularPosicion)
**RN-011:** Auditoría obligatoria de eventos críticos (AuditService.registrarEvento)
**RN-012:** Pre-aviso automático cuando posición ≤ 3 (QueueManagementService.recalcularPosiciones)
**RN-013:** Retención de auditoría por 7 años (application.yml audit.retention-days=2555)

---

## REFERENCIAS ADR OBLIGATORIAS

### Incluir en código según Plan Detallado

**ADR-001:** Estrategia de Reintentos - Spring Retry en TelegramService
**ADR-002:** API Síncrona vs Reactiva - RestTemplate en TelegramService  
**ADR-003:** Procesamiento de Colas - @Scheduled en MensajeScheduler y QueueProcessorScheduler
**ADR-004:** Migraciones de Base de Datos - Flyway V1-V4 con comentarios
**ADR-005:** Bean Validation - Validadores custom en DTOs

---

## DIAGRAMAS DE CONSULTA OBLIGATORIA

### Según especificación del Plan Detallado

**FASE 1 y FASE 2:** Consultar `docs/architecture/diagrams/03-er-diagram.puml`
- Entender relaciones entre entidades (1:N, constraints, índices)

**FASE 5 y FASE 7:** Consultar `docs/architecture/diagrams/02-sequence-diagram.puml`  
- Entender flujo completo de 5 fases (Creación → Confirmación → Progreso → Asignación → Completar)

**FASE 6:** Consultar `docs/architecture/diagrams/01-context-diagram.puml`
- Entender actores (Cliente, Supervisor) y sistemas externos (Telegram API, Terminal)

---

## CONFIGURACIONES ESPECÍFICAS OBLIGATORIAS

### Según Plan Detallado

**application.yml - Schedulers:**
```yaml
scheduler:
  message:
    fixed-rate: ${SCHEDULER_MESSAGE_RATE:60000}  # RF-002: cada 60s
  queue:
    fixed-rate: ${SCHEDULER_QUEUE_RATE:5000}     # RF-003: cada 5s

audit:
  retention-days: ${AUDIT_RETENTION_DAYS:2555}  # 7 años
  batch-size: ${AUDIT_BATCH_SIZE:1000}
```

**pom.xml - Dependencias ADR:**
```xml
<dependency>
    <groupId>org.springframework.retry</groupId>
    <artifactId>spring-retry</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-aspects</artifactId>
</dependency>
```

**TicketeroApplication.java:**
```java
@SpringBootApplication
@EnableScheduling
@EnableRetry  // OBLIGATORIO para ADR-001
public class TicketeroApplication {
    public static void main(String[] args) {
        SpringApplication.run(TicketeroApplication.class, args);
    }
}
```