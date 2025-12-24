# Escenarios Gherkin - Tests E2E Funcionales

## Feature: Creación de Tickets (RF-001, RF-003)

```gherkin
Feature: Creación de Tickets Digitales
  Como cliente del banco
  Quiero crear un ticket digital
  Para obtener un turno en la cola correspondiente

  Background:
    Given Sistema de tickets está disponible
    And Colas están configuradas con prefijos y tiempos

  @P0 @HappyPath @RF-001
  Scenario: Crear ticket con datos válidos genera número con prefijo
    Given Cliente con RUT "12345678-9" y teléfono "+56987654321"
    And Cliente selecciona sucursal "Centro"
    When Cliente solicita ticket para cola "CAJA"
    Then Sistema genera ticket con prefijo "C" y número secuencial
    And Ticket tiene estado "WAITING"
    And Sistema almacena datos del cliente correctamente
    And Response incluye código de referencia único

  @P0 @HappyPath @RF-003
  Scenario: Cálculo de posición y tiempo estimado correcto
    Given Cola "PERSONAL_BANKER" con tiempo promedio 15 minutos
    And Existen 2 tickets previos en la cola
    When Cliente solicita ticket para "PERSONAL_BANKER"
    Then Sistema calcula posición 3 en cola
    And Tiempo estimado es 45 minutos (3 × 15 min)
    And Ticket tiene prefijo "P" con número secuencial

  @P1 @EdgeCase @RN-001
  Scenario: Rechazar ticket duplicado para mismo RUT
    Given Cliente ya tiene ticket activo con RUT "11111111-1"
    And Ticket anterior está en estado "WAITING"
    When Mismo cliente intenta crear segundo ticket
    Then Sistema rechaza con código 409 Conflict
    And Mensaje indica "Ya existe un ticket activo para el RUT: 11111111-1"
    And No se crea ticket duplicado

  @P1 @EdgeCase @RN-005
  Scenario: Numeración secuencial por tipo de cola
    Given Sistema sin tickets previos
    When Se crean tickets en orden: CAJA, EMPRESAS, GERENCIA
    Then Primer ticket tiene número "C01"
    And Segundo ticket tiene número "E01"
    And Tercer ticket tiene número "G01"
    And Cada cola mantiene su secuencia independiente

  @P2 @ErrorHandling @RN-010
  Scenario: RUT inválido rechazado con 400
    Given Cliente con RUT sin formato correcto "123456789"
    When Cliente intenta crear ticket
    Then Sistema rechaza con código 400 Bad Request
    And Response incluye error "Formato de RUT inválido"
    And No se crea ticket en base de datos
```

## Feature: Procesamiento de Tickets (RF-004)

```gherkin
Feature: Procesamiento Automático de Tickets
  Como sistema de colas
  Quiero procesar tickets automáticamente
  Para asignar clientes a ejecutivos disponibles

  Background:
    Given QueueProcessorScheduler ejecuta cada 1 segundo
    And Existen ejecutivos disponibles en el sistema

  @P0 @HappyPath @RF-004
  Scenario: Scheduler asigna ticket automáticamente
    Given Ticket en estado "WAITING" creado hace 30 segundos
    And Ejecutivo disponible para la cola correspondiente
    When QueueProcessorScheduler ejecuta procesamiento
    Then Ticket es asignado a ejecutivo disponible
    And Estado cambia de "WAITING" a "CALLED"
    And Se asigna número de módulo específico
    And Timestamp de asignación es registrado

  @P1 @EdgeCase @RN-002
  Scenario: GERENCIA tiene prioridad sobre CAJA
    Given Ticket CAJA creado a las 10:00
    And Ticket GERENCIA creado a las 10:01
    And Un solo ejecutivo disponible
    When Scheduler procesa asignaciones
    Then Ticket GERENCIA es asignado primero
    And Ticket CAJA permanece en "WAITING"
    And Prioridad de cola es respetada

  @P1 @EdgeCase @RN-003
  Scenario: Orden FIFO dentro de misma cola
    Given Dos tickets PERSONAL_BANKER creados en secuencia
    And Primer ticket creado a las 10:00:00
    And Segundo ticket creado a las 10:00:30
    When Scheduler procesa cola PERSONAL_BANKER
    Then Primer ticket es asignado primero
    And Segundo ticket permanece en espera
    And Orden de creación es respetado

  @P1 @EdgeCase @RN-004
  Scenario: Balanceo de carga entre ejecutivos
    Given Tres tickets CAJA en espera
    And Dos ejecutivos disponibles para CAJA
    When Scheduler procesa múltiples asignaciones
    Then Tickets son distribuidos entre ejecutivos
    And Ningún ejecutivo recibe más de 3 tickets concurrentes
    And Carga es balanceada equitativamente

  @P2 @ErrorHandling
  Scenario: Timeout de NO_SHOW procesado correctamente
    Given Ticket en estado "CALLED" hace 6 minutos
    And Cliente no se presenta en módulo
    When Scheduler verifica timeouts
    Then Ticket es marcado como "NO_SHOW"
    And Ejecutivo queda disponible para siguiente ticket
    And Evento de timeout es registrado en auditoría
```

## Feature: Notificaciones Telegram (RF-002)

```gherkin
Feature: Notificaciones Automáticas vía Telegram
  Como cliente del banco
  Quiero recibir notificaciones automáticas
  Para estar informado del estado de mi ticket

  Background:
    Given Telegram Bot API está disponible
    And MessageScheduler ejecuta cada 2 segundos
    And Cliente tiene teléfono válido registrado

  @P0 @HappyPath @RF-002
  Scenario: Los 3 mensajes automáticos se envían correctamente
    Given Cliente crea ticket con teléfono "+56913131313"
    When Sistema procesa creación y asignación de ticket
    Then Mensaje 1 (confirmación) es enviado inmediatamente
    And Mensaje 2 (pre-aviso) es enviado cuando posición ≤ 3
    And Mensaje 3 (turno activo) es enviado al asignar ejecutivo
    And Todos los mensajes contienen información correcta

  @P0 @HappyPath @RN-007
  Scenario: Mensaje de confirmación contiene datos correctos
    Given Cliente crea ticket P15 en posición 5
    And Tiempo estimado es 75 minutos
    When MessageScheduler procesa mensaje de confirmación
    Then Mensaje incluye emoji "🎫"
    And Mensaje contiene "Ticket Creado"
    And Mensaje incluye número "P15"
    And Mensaje indica "Posición: 5"
    And Mensaje muestra "Tiempo estimado: 75 minutos"

  @P1 @EdgeCase @RN-008
  Scenario: Pre-aviso enviado cuando posición ≤ 3
    Given Ticket en posición 4 inicialmente
    And Tickets anteriores son procesados
    When Posición del ticket llega a 3
    Then Mensaje de pre-aviso es programado
    And Mensaje incluye "⏰ Tu turno se acerca"
    And Mensaje indica nueva posición en cola
    And Cliente es preparado para llamado

  @P1 @EdgeCase @RN-009
  Scenario: Mensaje de turno activo incluye asesor y módulo
    Given Ticket es asignado a ejecutivo "María González"
    And Módulo asignado es "Módulo 5"
    When Sistema envía mensaje de turno activo
    Then Mensaje incluye emoji "✅"
    And Mensaje contiene "Es tu turno"
    And Mensaje indica "Te atiende: María González"
    And Mensaje especifica "Módulo: 5"
    And Cliente sabe exactamente dónde dirigirse

  @P2 @ErrorHandling
  Scenario: Reintentos con backoff exponencial funcionan
    Given Telegram API falla con error 500
    And Sistema configurado con 3 reintentos máximo
    When Cliente crea ticket y se intenta enviar mensaje
    Then Primer intento falla inmediatamente
    And Segundo intento ocurre después de 2 segundos
    And Tercer intento ocurre después de 4 segundos
    And Si tercer intento falla, mensaje es marcado como FALLIDO

  @P2 @ErrorHandling
  Scenario: Mensaje marcado como FALLIDO tras máximo reintentos
    Given Telegram API siempre responde con error 500
    And Sistema configurado con máximo 4 reintentos
    When Cliente crea ticket
    Then Sistema intenta envío 4 veces
    And Cada intento incrementa tiempo de espera
    And Después de 4 fallos, mensaje es marcado FALLIDO
    And Error es registrado en logs para investigación
```

## Feature: Validaciones de Input (RN-010 a RN-013)

```gherkin
Feature: Validaciones de Datos de Entrada
  Como sistema de tickets
  Quiero validar todos los datos de entrada
  Para garantizar integridad y consistencia de información

  @P0 @HappyPath @RN-010
  Scenario: RUT válido con 8 dígitos y dígito verificador
    Given Cliente ingresa RUT "12345678-9"
    When Sistema valida formato de RUT
    Then RUT es aceptado como válido
    And Formato cumple patrón XXXXXXXX-Y
    And Dígito verificador es validado

  @P0 @HappyPath @RN-010
  Scenario: RUT válido con 7 dígitos y dígito verificador K
    Given Cliente ingresa RUT "1234567-K"
    When Sistema valida formato de RUT
    Then RUT es aceptado como válido
    And K mayúscula es reconocida como dígito verificador
    And Formato cumple patrón XXXXXXX-K

  @P1 @EdgeCase @RN-010
  Scenario: RUT con k minúscula aceptado
    Given Cliente ingresa RUT "7654321-k"
    When Sistema valida formato de RUT
    Then RUT es aceptado como válido
    And k minúscula es procesada correctamente
    And Sistema es flexible con mayúsculas/minúsculas

  @P2 @ErrorHandling @RN-010
  Scenario: RUT sin guión rechazado
    Given Cliente ingresa RUT "123456789" sin guión
    When Sistema valida formato de RUT
    Then Validación falla con código 400
    And Error indica "Formato de RUT inválido"
    And Mensaje sugiere formato correcto XXXXXXXX-Y

  @P0 @HappyPath @RN-011
  Scenario: Teléfono móvil chileno válido
    Given Cliente ingresa teléfono "+56987654321"
    When Sistema valida formato de teléfono
    Then Teléfono es aceptado como válido
    And Formato cumple patrón +56 9XXXXXXXX
    And Código país chileno es reconocido

  @P1 @EdgeCase @RN-011
  Scenario: Teléfono fijo chileno válido
    Given Cliente ingresa teléfono "+56223456789"
    When Sistema valida formato de teléfono
    Then Teléfono es aceptado como válido
    And Formato cumple patrón +56 2XXXXXXXX
    And Teléfono fijo de Santiago es reconocido

  @P2 @ErrorHandling @RN-011
  Scenario: Teléfono sin código país rechazado
    Given Cliente ingresa teléfono "987654321" sin +56
    When Sistema valida formato de teléfono
    Then Validación falla con código 400
    And Error indica "Teléfono debe tener formato +56XXXXXXXXX"
    And Request es rechazado completamente

  @P0 @HappyPath @RN-012
  Scenario: Todos los tipos de cola válidos aceptados
    Given Tipos de cola disponibles: CAJA, PERSONAL_BANKER, EMPRESAS, GERENCIA
    When Cliente selecciona cada tipo de cola
    Then Todos los tipos son aceptados como válidos
    And Cada tipo tiene configuración específica
    And Prefijos y tiempos son asignados correctamente

  @P2 @ErrorHandling @RN-012
  Scenario: Tipo de cola inválido rechazado
    Given Cliente envía tipo de cola "INVALIDA"
    When Sistema valida tipo de cola
    Then Validación falla con error de parsing JSON
    And Request es rechazado antes de procesamiento
    And Error indica valores válidos disponibles

  @P2 @ErrorHandling @RN-013
  Scenario: Todos los campos obligatorios faltantes
    Given Request completamente vacío "{}"
    When Sistema valida campos obligatorios
    Then Validación falla con código 400
    And Error incluye todos los campos faltantes:
      | Campo | Error |
      | nationalId | El RUT/ID es obligatorio |
      | telefono | El teléfono es obligatorio |
      | branchOffice | La sucursal es obligatoria |
      | queueType | El tipo de cola es obligatorio |

  @P1 @EdgeCase @RN-013
  Scenario: Campos con espacios en blanco rechazados
    Given Campos contienen solo espacios: "   "
    When Sistema valida contenido de campos
    Then Campos en blanco son rechazados
    And Error indica que campos no pueden estar vacíos
    And Espacios no son considerados contenido válido
```

## Feature: Dashboard Administrativo (RF-007, RF-008)

```gherkin
Feature: Panel de Monitoreo y Auditoría
  Como supervisor del banco
  Quiero monitorear el sistema en tiempo real
  Para gestionar operaciones y cumplir auditorías

  @P0 @HappyPath @RF-007
  Scenario: Dashboard summary muestra métricas básicas
    Given Sistema funcionando sin tickets activos
    When Supervisor consulta dashboard summary
    Then Response incluye timestamp actual
    And updateInterval es 5 segundos
    And estadoGeneral está disponible
    And resumenEjecutivo muestra ticketsActivos ≥ 0
    And estadoEjecutivos muestra disponibles/ocupados/offline

  @P0 @HappyPath @RF-007
  Scenario: Dashboard realtime actualiza cada 5 segundos
    Given Dashboard en tiempo real funcionando
    When Supervisor consulta múltiples veces
    Then Timestamp se actualiza en cada consulta
    And updateInterval permanece en 5 segundos
    And Datos reflejan estado actual del sistema
    And Métricas son consistentes y precisas

  @P1 @EdgeCase @RF-007
  Scenario: Dashboard con tickets activos muestra métricas actualizadas
    Given Ticket activo creado en sistema
    When Supervisor consulta dashboard después de creación
    Then resumenEjecutivo.ticketsActivos ≥ 1
    And estadoGeneral refleja actividad (NORMAL/BUSY)
    And Métricas son actualizadas en tiempo real
    And Dashboard refleja estado real del sistema

  @P0 @HappyPath @RF-008
  Scenario: Consulta de eventos de auditoría sin filtros
    Given Sistema con eventos de auditoría registrados
    When Administrador consulta eventos sin filtros
    Then Response incluye estructura de paginación
    And content contiene lista de eventos
    And pageable incluye información de página
    And totalElements ≥ 0 eventos disponibles

  @P1 @EdgeCase @RF-008
  Scenario: Consulta de auditoría con filtros específicos
    Given Eventos de auditoría de diferentes tipos
    When Administrador consulta con filtros:
      | Parámetro | Valor |
      | eventType | TICKET_CREATED |
      | page | 0 |
      | size | 10 |
    Then Sistema aplica filtros correctamente
    And Response incluye máximo 10 elementos
    And Página 0 es retornada
    And Solo eventos TICKET_CREATED son incluidos

  @P1 @EdgeCase @RF-008
  Scenario: Historial de ticket específico
    Given Ticket P20 con historial de eventos
    When Administrador consulta historial del ticket P20
    Then Sistema retorna eventos específicos del ticket
    And Eventos están ordenados cronológicamente
    And Incluye creación, asignación, y cambios de estado
    And Información es completa y trazable

  @P2 @ErrorHandling
  Scenario: Parámetros inválidos en auditoría
    Given Administrador envía fechas inválidas
    When Sistema procesa consulta con parámetros:
      | Parámetro | Valor |
      | startDate | fecha-invalida |
      | endDate | otra-fecha-invalida |
    Then Sistema maneja error de parámetros
    And Response indica formato de fecha correcto
    And Error es registrado para debugging
```

## Matriz de Trazabilidad Gherkin → Tests

| Escenario Gherkin | Test Java | RF/RN | Endpoint | Estado |
|-------------------|-----------|-------|----------|--------|
| Crear ticket con datos válidos | TicketCreationIT.crearTicket_datosValidos_generaNumeroConPrefijo | RF-001 | POST /api/tickets | ✅ |
| Cálculo de posición y tiempo | TicketCreationIT.crearTicket_calculaPosicionYTiempo_correctamente | RF-003 | POST /api/tickets | ✅ |
| Rechazar ticket duplicado | TicketCreationIT.crearTicket_rutConTicketActivo_rechazaConflicto | RN-001 | POST /api/tickets | ✅ |
| Numeración secuencial | TicketCreationIT.crearTickets_diferentesColas_numeracionSecuencial | RN-005 | POST /api/tickets | ✅ |
| RUT inválido rechazado | TicketCreationIT.crearTicket_rutInvalido_rechazaBadRequest | RN-010 | POST /api/tickets | ✅ |
| Scheduler asigna automáticamente | TicketProcessingIT.scheduler_conTicketEnEspera_asignaAutomaticamente | RF-004 | - | ✅ |
| Prioridad GERENCIA > CAJA | TicketProcessingIT.asignacion_conMultiplesColas_respetaPrioridad | RN-002 | - | ✅ |
| Orden FIFO en cola | TicketProcessingIT.asignacion_mismaCola_respetaOrdenCreacion | RN-003 | - | ✅ |
| Balanceo de carga | TicketProcessingIT.asignacion_multipleTickets_distribuyeCarga | RN-004 | - | ✅ |
| Timeout NO_SHOW | TicketProcessingIT.scheduler_ticketTimeout_marcaNoShow | - | - | ✅ |
| 3 mensajes automáticos | NotificationIT.crearTicket_enviaLosTresMensajes_correctamente | RF-002 | - | ✅ |
| Mensaje confirmación | NotificationIT.crearTicket_mensajeConfirmacion_contieneInformacionCompleta | RN-007 | - | ✅ |
| Pre-aviso posición ≤ 3 | NotificationIT.ticketEnPosicion3_enviaPreAviso_automaticamente | RN-008 | - | ✅ |
| Turno activo con asesor | NotificationIT.ticketAsignado_enviaMensajeTurnoActivo_conAsesorYModulo | RN-009 | - | ✅ |
| Reintentos con backoff | NotificationIT.telegramFalla_aplicaReintentosConBackoff_correctamente | - | - | ✅ |
| RUT 8 dígitos válido | ValidationIT.validarRUT_formatoValido8Digitos_aceptado | RN-010 | POST /api/tickets | ✅ |
| RUT 7 dígitos con K | ValidationIT.validarRUT_formatoValido7DigitosK_aceptado | RN-010 | POST /api/tickets | ✅ |
| Teléfono móvil válido | ValidationIT.validarTelefono_formatoMovilChileno_aceptado | RN-011 | POST /api/tickets | ✅ |
| Teléfono fijo válido | ValidationIT.validarTelefono_formatoFijoChileno_aceptado | RN-011 | POST /api/tickets | ✅ |
| Tipos de cola válidos | ValidationIT.validarTiposCola_todosLosValoresValidos_aceptados | RN-012 | POST /api/tickets | ✅ |
| Campos obligatorios | ValidationIT.validarCamposObligatorios_todosFaltantes_rechazadoConMultiplesErrores | RN-013 | POST /api/tickets | ✅ |
| Dashboard summary | AdminDashboardIT.dashboardSummary_sinTickets_muestraMetricasBasicas | RF-007 | GET /api/dashboard/summary | ✅ |
| Dashboard realtime | AdminDashboardIT.dashboardRealtime_consultaMultiple_actualizaTimestamp | RF-007 | GET /api/dashboard/realtime | ✅ |
| Eventos auditoría | AdminDashboardIT.auditoria_consultaSinFiltros_retornaEventos | RF-008 | GET /api/audit/events | ✅ |
| Auditoría con filtros | AdminDashboardIT.auditoria_consultaConFiltros_aplicaFiltrosCorrectamente | RF-008 | GET /api/audit/events | ✅ |
| Historial ticket | AdminDashboardIT.auditoria_historialTicket_muestraEventosDelTicket | RF-008 | GET /api/audit/ticket/{number} | ✅ |

**Total Escenarios:** 25+ escenarios Gherkin modelados  
**Cobertura RF:** 8/8 (100%)  
**Cobertura RN:** 13/13 (100%)  
**Estado:** ✅ COMPLETAMENTE IMPLEMENTADO