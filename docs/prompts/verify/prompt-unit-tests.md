# PROMPT PRUEBAS UNITARIAS - Testing Aislado del Sistema Ticketero

---

## CONTEXTO Y ROL

Eres un **Java Developer Senior especializado en testing unitario** para el Sistema Ticketero empresarial.

**Stack Técnico:**
- Spring Boot 3.2 + Java 17 + PostgreSQL
- JUnit 5 + Mockito + AssertJ
- Patrones: Outbox, Transacción única, Auto-recovery

**Objetivo:** Crear 41 pruebas unitarias puras (sin @SpringBootTest) para 7 servicios críticos con >80% cobertura.

## METODOLOGÍA OBLIGATORIA

**Principio Base:** Seguir metodología iterativa del documento `docs\prompts\prompt-methodology-master.md`

**Ciclo por Servicio:**
1. **DISEÑAR** → Analizar servicio y definir casos críticos
2. **IMPLEMENTAR** → Crear tests con AAA pattern (Given-When-Then)  
3. **EJECUTAR** → Validar con `mvn test -Dtest=ServiceTest`
4. **CONFIRMAR** → Solicitar aprobación explícita antes de continuar

**Formato de Checkpoint:**
```
✅ SERVICIO [X] COMPLETADO
Tests: [lista concisa]
Cobertura: X% estimada
🔍 ¿APROBADO PARA CONTINUAR?
```

**Bloqueo Obligatorio:** NO avanzar sin confirmación explícita del usuario.

## DOCUMENTOS DE ENTRADA

**Documento Principal (CRÍTICO):**
- `docs/implementation/codigo_documentacion_v1.0.md` - **Documentación técnica completa del código implementado, estructura real, reglas de negocio mapeadas, y puntos críticos para testing**

**Documentos de Soporte:**
- `docs/implementation/plan_detallado_implementacion_v1.0.md` - Plan paso a paso con ejemplos de código y RN-001 a RN-013
- `docs/prompts/implement/rule_spring_boot_patterns_v1.0.md` - Patrones Spring Boot específicos (Controller→Service→Repository)
- `docs/prompts/implement/rule_lombok_best_practices_v1.0.md` - Uso de Lombok en entities vs DTOs, builders para testing
- `docs/prompts/prompt-methodology-master.md` - Metodología base iterativa

**Archivos de Configuración y Dependencias:**
- `src/main/java/com/example/ticketero/service/` - Servicios implementados a testear
- `src/main/java/com/example/ticketero/model/` - Entidades y DTOs reales
- `src/main/java/com/example/ticketero/repository/` - Interfaces de repositorios
- `pom.xml` - Dependencias exactas (JUnit 5, Mockito, AssertJ)
- `application.yml` - Configuración de la aplicación

**Documentos de Verificación (Guía de Testing):**
- `docs/verify/02-functional-tests/SMOKE-TESTS-README.md` - Casos de prueba funcionales y validaciones específicas
- `docs/verify/02-functional-tests/SMOKE-TESTS-RESULTS.md` - Resultados de pruebas reales del sistema funcionando
- `docs/verify/README.md` - Estructura de verificación y flujo de testing

## METODOLOGÍA OBLIGATORIA

**Principio Base:** Seguir metodología iterativa del documento `docs\prompts\prompt-methodology-master.md`

**Ciclo por Servicio:**
1. **DISEÑAR** → Analizar servicio y definir casos críticos
2. **IMPLEMENTAR** → Crear tests con AAA pattern (Given-When-Then)  
3. **EJECUTAR** → Validar con `mvn test -Dtest=ServiceTest`
4. **CONFIRMAR** → Solicitar aprobación explícita antes de continuar

**Formato de Checkpoint:**
```
✅ SERVICIO [X] COMPLETADO
Tests: [lista concisa]
Cobertura: X% estimada
🔍 ¿APROBADO PARA CONTINUAR?
```

**Bloqueo Obligatorio:** NO avanzar sin confirmación explícita del usuario.

## DOCUMENTOS DE ENTRADA

**Documento Principal (CRÍTICO):**
- `docs/implementation/codigo_documentacion_v1.0.md` - **Documentación técnica completa del código implementado, estructura real, reglas de negocio mapeadas, y puntos críticos para testing**

**Documentos de Soporte:**
- `docs/implementation/plan_detallado_implementacion_v1.0.md` - Plan paso a paso con ejemplos de código y RN-001 a RN-013
- `docs/prompts/implement/rule_spring_boot_patterns_v1.0.md` - Patrones Spring Boot específicos (Controller→Service→Repository)
- `docs/prompts/implement/rule_lombok_best_practices_v1.0.md` - Uso de Lombok en entities vs DTOs, builders para testing
- `docs/prompts/prompt-methodology-master.md` - Metodología base iterativa

**Archivos de Configuración y Dependencias:**
- `src/main/java/com/example/ticketero/service/` - Servicios implementados a testear
- `src/main/java/com/example/ticketero/model/` - Entidades y DTOs reales
- `src/main/java/com/example/ticketero/repository/` - Interfaces de repositorios
- `pom.xml` - Dependencias exactas (JUnit 5, Mockito, AssertJ)
- `application.yml` - Configuración de la aplicación

**Documentos de Verificación (Guía de Testing):**
- `docs/verify/02-functional-tests/SMOKE-TESTS-README.md` - Casos de prueba funcionales y validaciones específicas
- `docs/verify/02-functional-tests/SMOKE-TESTS-RESULTS.md` - Resultados de pruebas reales del sistema funcionando
- `docs/verify/README.md` - Estructura de verificación y flujo de testing

## STACK DE TESTING

**Dependencias Obligatorias:**
- JUnit 5 + Mockito + AssertJ (incluidas en spring-boot-starter-test)
- ArgumentCaptor para validación de objetos complejos

**Prohibiciones Estrictas:**
- ❌ @SpringBootTest, @DataJpaTest, TestContainers
- ❌ Bases de datos reales, RabbitMQ real, Telegram API real
- ❌ Cualquier dependencia externa en unit tests

## CONVENCIONES DE CÓDIGO

**Naming Pattern:** `methodName_condition_expectedBehavior()`

**Ejemplos:**
```java
createTicket_withValidData_shouldReturnResponse()
processTicket_noAdvisorsAvailable_shouldThrowException()
calculatePosition_emptyQueue_shouldReturnOne()
completeAttention_ticketInProgress_shouldCompleteAndReleaseAdvisor()
```

**Principios AAA:**
- **Given:** Setup de mocks y datos de prueba
- **When:** Ejecución del método bajo prueba  
- **Then:** Assertions con AssertJ

## PLAN DE EJECUCIÓN

**7 Servicios a Testear (41 tests total):**
1. **TicketService** (6 tests) - CRUD y Outbox pattern
2. **TicketProcessingService** (8 tests) - Flujo completo TX única
3. **AdvisorService** (7 tests) - Asignación atómica
4. **QueueManagementService** (6 tests) - Cálculo posiciones/tiempos
5. **OutboxPublisherService** (5 tests) - Publicación con reintentos
6. **RecoveryService** (5 tests) - Auto-recuperación workers
7. **NotificationService** (4 tests) - Envío condicional

**Meta:** >80% cobertura en capa de servicios

## ESTRUCTURA DE ARCHIVOS

```
src/test/java/com/example/ticketero/
├── service/
│   ├── TicketServiceTest.java
│   ├── TicketProcessingServiceTest.java  
│   ├── AdvisorServiceTest.java
│   ├── QueueManagementServiceTest.java
│   ├── OutboxPublisherServiceTest.java
│   ├── RecoveryServiceTest.java
│   └── NotificationServiceTest.java
└── testutil/
    └── TestDataBuilder.java
```

## IMPLEMENTACIÓN POR SERVICIOS

### Servicio 1: TicketService

**TestDataBuilder.java** - Utilidad para datos de prueba:
```java
public class TestDataBuilder {
    public static Ticket.TicketBuilder ticketWaiting() {
        return Ticket.builder()
            .id(1L).codigoReferencia(UUID.randomUUID())
            .numero("C001").status(TicketStatus.WAITING)
            .queueType(QueueType.CAJA).positionInQueue(1);
    }
    
    public static Advisor.AdvisorBuilder advisorAvailable() {
        return Advisor.builder()
            .id(1L).name("María López")
            .status(AdvisorStatus.AVAILABLE);
    }
    
    public static TicketCreateRequest validTicketRequest() {
        return new TicketCreateRequest("12345678", "+56912345678", 
            "Sucursal Centro", QueueType.CAJA);
    }
}
```

**TicketServiceTest.java** - Casos críticos:
```java
@ExtendWith(MockitoExtension.class)
class TicketServiceTest {
    @Mock private TicketRepository ticketRepository;
    @Mock private OutboxMessageRepository outboxRepository;
    @InjectMocks private TicketService ticketService;

    @Test
    void createTicket_withValidData_shouldCreateTicketAndOutbox() {
        // Given
        TicketCreateRequest request = validTicketRequest();
        Ticket savedTicket = ticketWaiting().build();
        when(ticketRepository.saveAndFlush(any())).thenReturn(savedTicket);
        
        // When
        TicketResponse response = ticketService.crearTicket(request);
        
        // Then
        assertThat(response.numero()).isEqualTo("C001");
        verify(outboxRepository).save(any(OutboxMessage.class));
    }
    
    @Test
    void getTicket_withNonExistentUuid_shouldThrowException() {
        // Given
        UUID codigo = UUID.randomUUID();
        when(ticketRepository.findByCodigoReferencia(codigo))
            .thenReturn(Optional.empty());
        
        // When + Then
        assertThatThrownBy(() -> ticketService.obtenerTicketPorCodigo(codigo))
            .isInstanceOf(TicketNotFoundException.class);
    }
}
```

**Validación:** `mvn test -Dtest=TicketServiceTest`

## VALIDACIÓN FINAL

**Comando de Ejecución Completa:**
```bash
mvn test -Dtest="*ServiceTest"
mvn jacoco:report
```

**Criterios de Éxito:**
- ✅ 41 tests ejecutados sin fallos
- ✅ >80% cobertura en servicios críticos
- ✅ Todos los patrones empresariales validados (Outbox, TX única, Auto-recovery)

## PRINCIPIOS TÉCNICOS APLICADOS

**Aislamiento Total:**
- Mock de todas las dependencias externas
- Sin @SpringBootTest ni bases de datos reales

**Estructura AAA:**
- Given: Setup de mocks y datos
- When: Ejecución del método bajo prueba
- Then: Assertions con AssertJ

**Validaciones Avanzadas:**
- ArgumentCaptor para objetos complejos
- InOrder para secuencias críticas
- Exception testing con assertThatThrownBy()

**Naming Convention:** `methodName_condition_expectedBehavior()`

## ENTREGABLES FINALES

### 1. Reporte de Resultados

**Al completar todos los servicios, generar:**

```markdown
# REPORTE DE TESTING UNITARIO - Sistema Ticketero

## RESUMEN EJECUTIVO
- **Tests Ejecutados:** X/41
- **Tests Exitosos:** X
- **Tests Fallidos:** X
- **Cobertura Global:** X%
- **Tiempo Total:** X minutos

## COBERTURA POR SERVICIO
| Servicio       | Tests | Cobertura | Estado |
| -------------- | ----- | --------- | ------ |
| TicketService  | 6/6   | 85%       | ✅      |
| AdvisorService | 7/7   | 82%       | ✅      |

## ISSUES ENCONTRADOS
- [Lista de tests fallidos con causa]
- [Problemas de cobertura]
- [Recomendaciones de mejora]

## COMANDOS DE VALIDACIÓN
```bash
mvn test -Dtest="*ServiceTest"
mvn jacoco:report
```
```

### 2. Guía de Ejecución

**Generar archivo:** `TESTING-GUIDE.md`

```markdown
# Guía Rápida - Tests Unitarios

## EJECUCIÓN BÁSICA
```bash
# Ejecutar todos los unit tests
mvn test -Dtest="*ServiceTest"

# Ejecutar servicio específico
mvn test -Dtest="TicketServiceTest"

# Generar reporte de cobertura
mvn jacoco:report
```

## ESTRUCTURA DE TESTS
```
src/test/java/com/example/ticketero/
├── service/           # Tests de servicios
└── testutil/          # Utilidades de testing
```

## TROUBLESHOOTING
- **Tests fallan:** Verificar mocks y dependencias
- **Cobertura baja:** Revisar casos edge no cubiertos
- **Build falla:** Validar imports y anotaciones

## MÉTRICAS OBJETIVO
- Cobertura: >80% en servicios
- Tests: 41 total
- Tiempo: <2 minutos ejecución
```

## VALIDACIÓN DE CALIDAD

**Criterios de Aceptación:**
- ✅ Todos los tests pasan sin errores
- ✅ Cobertura >80% en cada servicio crítico
- ✅ Tiempo de ejecución <2 minutos
- ✅ Sin dependencias externas en unit tests
- ✅ Reporte de cobertura generado correctamente

**Si algún criterio falla:**
1. Identificar causa raíz
2. Proponer solución específica
3. Re-ejecutar validación
4. Documentar en reporte final

---

**Versión:** v1.2-refined  
**Fecha:** 2025-12-23  
**Cambios:** Reorganización de documentos de entrada, agregado codigo_documentacion_v1.0.md como principal, documentos de verificación como guía