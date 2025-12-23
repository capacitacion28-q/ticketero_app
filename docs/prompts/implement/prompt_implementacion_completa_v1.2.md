# Prompt: Implementación Completa del Sistema Ticketero

**Versión:** 1.2  
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

**Estado Actual: FASES 0, 1 y 2 COMPLETADAS ✅**

### **📋 REPORTE DE PROGRESO:**
- **Proyecto:** Sistema de Gestión de Tickets con Notificaciones en Tiempo Real
- **Metodología:** Plan Detallado de Implementación por Fases
- **Estado:** 3/8 Fases Completadas
- **Tiempo Invertido:** ~2.5 horas

### **✅ FASES COMPLETADAS:**

**FASE 0: SETUP DEL PROYECTO ✅**
- ✅ `pom.xml` - Maven con Spring Boot 3.2.11, PostgreSQL, Flyway, Lombok
- ✅ `application.yml` - Configuración BD, schedulers, Telegram API
- ✅ `docker-compose.yml` - PostgreSQL 15 containerizado
- ✅ `TicketeroApplication.java` - Clase principal con @EnableScheduling/@EnableRetry
- ✅ `.env` - Variables de entorno configuradas
- **Validación:** ✅ Aplicación compila, conecta a BD, actuator health UP

**FASE 1: MIGRACIONES Y ENUMERACIONES ✅**
- ✅ `V1__create_ticket_table.sql` - Tabla ticket con índices RN-001, RN-003
- ✅ `V2__create_mensaje_table.sql` - Tabla mensaje con FK a ticket
- ✅ `V3__create_advisor_table.sql` - Tabla advisor con índices RN-004
- ✅ `V4__create_audit_table.sql` - Tabla audit_event para RF-008
- ✅ `V5__insert_initial_data.sql` - 5 asesores iniciales
- ✅ 6 Enumeraciones Java: QueueType, TicketStatus, AdvisorStatus, MessageTemplate, EstadoEnvio, ActorType
- **Validación:** ✅ Flyway ejecutó 5 migraciones exitosamente, schema v5 aplicado

**FASE 2: ENTITIES JPA ✅**
- ✅ `Ticket.java` - Entity principal con UUID, relaciones 1:N
- ✅ `Mensaje.java` - Entity con relación N:1 hacia Ticket
- ✅ `Advisor.java` - Entity independiente para balanceo RN-004
- ✅ `AuditEvent.java` - Entity auditoría con hash integridad
- **Validación:** ✅ Hibernate valida schema, entities compilan, relaciones JPA correctas

### **🔧 CORRECCIONES CRÍTICAS REALIZADAS:**
- **Migraciones:** Eliminados duplicados, secuencia correcta V1→V2→V3→V4→V5
- **Entities:** Campos alineados con plan (codigo_referencia UUID, national_id, etc.)
- **Enums:** Agregado método fromString() a QueueType, imports validados

### **📁 ESTRUCTURA ACTUAL:**
```
ticketero_app/
├── src/main/java/com/example/ticketero/
│   ├── TicketeroApplication.java ✅
│   └── model/
│       ├── entity/ ✅ (4 entities)
│       └── enums/ ✅ (6 enums)
├── src/main/resources/
│   ├── application.yml ✅
│   └── db/migration/ ✅ (5 migraciones)
├── pom.xml ✅
├── docker-compose.yml ✅
└── .env ✅
```

### **🎯 PRÓXIMO PASO: FASE 3 - DTOs Y VALIDACIÓN**
- **Objetivo:** Crear DTOs para request/response con Bean Validation
- **Tiempo Estimado:** 45 minutos
- **Referencia:** Plan Detallado Sección 6
- **Entregables:** 6 DTOs con validaciones específicas

---

## ACCIÓN

Continuar la implementación del Sistema Ticketero desde la **FASE 3: DTOs Y VALIDACIÓN** siguiendo el plan de implementación mediante desarrollo iterativo paso a paso para:

**Objetivos de Implementación:**
- Ejecutar las 5 fases restantes del plan en secuencia (DTOs → Repositories → Services → Controllers → Schedulers)
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
- **Conformidad con Documentos**: OBLIGATORIO validar contra documentos específicos
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
- Seguir exactamente las 5 fases restantes definidas en el plan (8.5 horas estimadas restantes)
- Implementar reglas de negocio RN-001 a RN-013 según mapeo específico del plan
- Cumplir criterios de aceptación de RF-001 a RF-008
- Aplicar patrones y estándares definidos en reglas automáticas
- **Consultar diagramas obligatorios:** C4 Context, Sequence End-to-End, ER Database
- **Referenciar ADRs:** ADR-001 a ADR-005 en código según especificación

---

## FASE 3: DTOs Y VALIDACIÓN - ESPECIFICACIÓN DETALLADA

### **Entregables Requeridos (6 DTOs):**

**1. TicketCreateRequest.java:**
```java
public record TicketCreateRequest(
    @NotBlank(message = "El RUT/ID es obligatorio")
    @Pattern(regexp = "^[0-9]{7,8}-[0-9Kk]$", message = "Formato de RUT inválido")
    String nationalId,
    
    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^\\+56[0-9]{9}$", message = "Teléfono debe tener formato +56XXXXXXXXX")
    String telefono,
    
    @NotBlank(message = "La sucursal es obligatoria")
    String branchOffice,
    
    @NotNull(message = "El tipo de cola es obligatorio")
    QueueType queueType
) {}
```

**2. TicketResponse.java:**
```java
public record TicketResponse(
    UUID codigoReferencia,
    String numero,
    String nationalId,
    String telefono,
    String branchOffice,
    QueueType queueType,
    TicketStatus status,
    Integer positionInQueue,
    Integer estimatedWaitMinutes,
    String assignedAdvisor,
    Integer assignedModuleNumber,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String statusDescription
) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
            ticket.getCodigoReferencia(),
            ticket.getNumero(),
            ticket.getNationalId(),
            ticket.getTelefono(),
            ticket.getBranchOffice(),
            ticket.getQueueType(),
            ticket.getStatus(),
            ticket.getPositionInQueue(),
            ticket.getEstimatedWaitMinutes(),
            ticket.getAssignedAdvisor(),
            ticket.getAssignedModuleNumber(),
            ticket.getCreatedAt(),
            ticket.getUpdatedAt(),
            ticket.getStatus().getDescription()
        );
    }
}
```

**3. QueueStatusResponse.java:**
```java
public record QueueStatusResponse(
    QueueType queueType,
    String descripcion,
    int tiempoPromedio,
    int prioridad,
    String prefijo,
    EstadoActual estadoActual,
    List<TicketEnCola> tickets
) {
    public record EstadoActual(
        int ticketsEnEspera,
        int ticketsNotificados,
        int ticketsEnAtencion,
        int tiempoEstimadoCola,
        String proximoNumero
    ) {}
    
    public record TicketEnCola(
        String numero,
        String status,
        int positionInQueue,
        int estimatedWaitMinutes
    ) {}
}
```

**4. DashboardResponse.java:**
```java
public record DashboardResponse(
    LocalDateTime timestamp,
    int updateInterval,
    String estadoGeneral,
    ResumenEjecutivo resumenEjecutivo,
    Map<String, EstadoCola> estadoColas,
    EstadoEjecutivos estadoEjecutivos,
    List<Alerta> alertas,
    Map<String, Integer> metricas
) {
    public record ResumenEjecutivo(
        int ticketsActivos,
        int ticketsCompletadosHoy,
        double tiempoPromedioGlobal,
        double tasaCompletacionPorHora
    ) {}
    
    public record EstadoCola(
        int ticketsEnEspera,
        int tiempoEstimadoMaximo,
        String estado
    ) {}
    
    public record EstadoEjecutivos(
        int disponibles,
        int ocupados,
        int offline,
        String distribucionCarga,
        EjecutivoProductivo masProductivo
    ) {}
    
    public record EjecutivoProductivo(
        String nombre,
        int ticketsAtendidos
    ) {}
    
    public record Alerta(
        String id,
        String tipo,
        String prioridad,
        String descripcion,
        String accionSugerida,
        LocalDateTime timestamp
    ) {}
}
```

**5. AuditEventResponse.java:**
```java
public record AuditEventResponse(
    Long id,
    LocalDateTime timestamp,
    String eventType,
    String actor,
    ActorType actorType,
    String ticketNumber,
    String previousState,
    String newState,
    String additionalData,
    String ipAddress
) {
    public static AuditEventResponse from(AuditEvent auditEvent) {
        return new AuditEventResponse(
            auditEvent.getId(),
            auditEvent.getTimestamp(),
            auditEvent.getEventType(),
            auditEvent.getActor(),
            auditEvent.getActorType(),
            auditEvent.getTicketNumber(),
            auditEvent.getPreviousState(),
            auditEvent.getNewState(),
            auditEvent.getAdditionalData(),
            auditEvent.getIpAddress()
        );
    }
}
```

**6. ErrorResponse.java:**
```java
public record ErrorResponse(
    boolean success,
    String code,
    String message,
    Map<String, String> details,
    LocalDateTime timestamp
) {
    public ErrorResponse(String code, String message) {
        this(false, code, message, null, LocalDateTime.now());
    }
    
    public ErrorResponse(String code, String message, Map<String, String> details) {
        this(false, code, message, details, LocalDateTime.now());
    }
}
```

### **Criterios de Validación FASE 3:**
- ✅ 6 DTOs compilan correctamente
- ✅ Validaciones Bean Validation según RF-001
- ✅ Patrón teléfono chileno (+56XXXXXXXXX)
- ✅ Records usados para responses inmutables
- ✅ Mensajes de error en español
- ✅ Factory methods funcionan correctamente

### **Comandos de Verificación:**
```bash
mvn clean compile
mvn test-compile
```

### **Prueba Mínima FASE 3:**
Crear instancia de cada DTO y validar:
- TicketCreateRequest con datos válidos e inválidos
- Factory methods de TicketResponse y AuditEventResponse
- Serialización JSON de records anidados

---

## RESULTADO

Para la FASE 3, entregar:

**Template de Confirmación Obligatorio:**
```
✅ PASO 3.0 COMPLETADO
**Fase:** DTOs y Validación
**Archivos creados/modificados:**
- src/main/java/com/example/ticketero/model/dto/TicketCreateRequest.java
- src/main/java/com/example/ticketero/model/dto/TicketResponse.java
- src/main/java/com/example/ticketero/model/dto/QueueStatusResponse.java
- src/main/java/com/example/ticketero/model/dto/DashboardResponse.java
- src/main/java/com/example/ticketero/model/dto/AuditEventResponse.java
- src/main/java/com/example/ticketero/model/dto/ErrorResponse.java

**Validaciones realizadas:**
- ✅ Compilación: mvn clean compile exitoso
- ✅ Funcional: [prueba específica de validaciones Bean Validation]
- ✅ Prueba Mínima: [test de DTOs con datos válidos/inválidos]
- ✅ Documentación: Comentarios técnicos agregados
- ✅ Plan de Implementación: Sección 6 validada
- ✅ Arquitectura: Records inmutables aplicados
- ✅ Requerimientos: RF-001 validaciones cumplidas
- ✅ Trazabilidad: [cita del plan que justifica implementación]

🔍 SOLICITO REVISIÓN EXHAUSTIVA:
1. ¿El código cumple las especificaciones exactas del plan?
2. ¿Las validaciones confirman alineación con documentos?
3. ¿Se implementaron correctamente según arquitectura definida?
4. ¿La prueba mínima demuestra funcionamiento correcto?
5. ¿Puedo continuar con el siguiente paso?

⏸️ ESPERANDO CONFIRMACIÓN PARA CONTINUAR...
```

---

## ROADMAP RESTANTE

**FASE 4:** Repositories (30 min) - 4 interfaces JPA con queries custom
**FASE 5:** Services (3 horas) - 6 services con lógica de negocio RN-001 a RN-013
**FASE 6:** Controllers (2 horas) - 3 controllers con 13 endpoints REST
**FASE 7:** Schedulers (1.5 horas) - 2 schedulers con procesamiento asíncrono

**Total Restante:** ~7 horas para completar sistema funcional

---

## INSTRUCCIONES DE INICIO

**Comandos Iniciales:**
```bash
cd c:\Users\Usuario\Desktop\ticketero_app
# Verificar que app sigue corriendo
curl http://localhost:8080/actuator/health
# Si no está corriendo:
mvn spring-boot:run
```

**Primera Acción Requerida:**
Comenzar inmediatamente con **FASE 3: DTOs Y VALIDACIÓN** creando la carpeta:
```
src/main/java/com/example/ticketero/model/dto/
```

Implementar los 6 DTOs según especificaciones exactas, validar compilación y funcionalidad, ejecutar prueba mínima, luego solicitar revisión usando el template obligatorio.

**¿Estás listo para implementar la FASE 3: DTOs Y VALIDACIÓN?**

---

**Estado:** Actualizado con progreso real hasta FASE 2  
**Próximo Uso:** Continuar implementación desde FASE 3 DTOs y Validación