# 🧪 TESTS FUNCIONALES - Sistema Ticketero

Validación completa de todas las funcionalidades y reglas de negocio.

## 📋 **ÍNDICE DE TESTS**

### **🎯 Tests por Funcionalidad**
1. [**RF-001: Creación de Tickets**](#rf-001-creación-de-tickets)
2. [**RF-002: Notificaciones Telegram**](#rf-002-notificaciones-telegram)
3. [**RF-005: Gestión de Colas**](#rf-005-gestión-de-colas)
4. [**RF-006: Consulta de Tickets**](#rf-006-consulta-de-tickets)
5. [**RF-007: Dashboard**](#rf-007-dashboard)
6. [**RF-008: Auditoría**](#rf-008-auditoría)

### **⚖️ Tests por Regla de Negocio**
1. [**RN-001: Unicidad Ticket Activo**](#rn-001-unicidad-ticket-activo)
2. [**RN-005/006: Numeración y Prefijos**](#rn-005006-numeración-y-prefijos)
3. [**RN-010: Cálculo Tiempo Estimado**](#rn-010-cálculo-tiempo-estimado)
4. [**RN-012: Pre-aviso Automático**](#rn-012-pre-aviso-automático)

---

## 🎯 **TESTS POR FUNCIONALIDAD**

### **RF-001: Creación de Tickets**

#### **Test 1.1: Ticket Válido**
```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Consulta saldo",
    "descripcion": "Necesito consultar el saldo de mi cuenta corriente",
    "usuarioId": 1,
    "nationalId": "12345678-9",
    "telefono": "+56987654321",
    "branchOffice": "Centro",
    "queueType": "CAJA"
  }'
```
**Esperado:** 201 Created, ticket con número C##

#### **Test 1.2: Validación Bean Validation**
```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "",
    "descripcion": "Test",
    "usuarioId": -1,
    "nationalId": "invalid",
    "telefono": "invalid",
    "branchOffice": "",
    "queueType": "INVALID"
  }'
```
**Esperado:** 400 Bad Request con errores de validación

### **RF-002: Notificaciones Telegram**

#### **Test 2.1: Bot Configurado**
```bash
curl http://localhost:8080/api/test/telegram/bot-info
```
**Esperado:** Info del bot `ticketero_capacitacion_bot`

#### **Test 2.2: Mensaje Automático**
1. Crear ticket
2. Esperar 60 segundos (scheduler)
3. Verificar mensaje recibido en Telegram

**Esperado:** Mensaje "🎫 Ticket Creado" en Telegram

### **RF-005: Gestión de Colas**

#### **Test 5.1: Todas las Colas**
```bash
for queue in CAJA PERSONAL_BANKER EMPRESAS GERENCIA; do
  echo "Testing $queue..."
  curl http://localhost:8080/api/queues/$queue
done
```
**Esperado:** Respuesta con datos de cada cola

#### **Test 5.2: Estadísticas**
```bash
curl http://localhost:8080/api/queues/stats
```
**Esperado:** Estadísticas generales de colas

### **RF-006: Consulta de Tickets**

#### **Test 6.1: Por UUID**
```bash
# Usar UUID de ticket creado anteriormente
curl http://localhost:8080/api/tickets/{uuid}
```
**Esperado:** 200 OK con datos del ticket

#### **Test 6.2: Por Número**
```bash
curl http://localhost:8080/api/tickets/number/C01
```
**Esperado:** 200 OK con datos del ticket

#### **Test 6.3: Ticket No Existe**
```bash
curl http://localhost:8080/api/tickets/99999999-9999-9999-9999-999999999999
```
**Esperado:** 404 Not Found con ErrorResponse

### **RF-007: Dashboard**

#### **Test 7.1: Summary**
```bash
curl http://localhost:8080/api/dashboard/summary
```
**Esperado:** Métricas del sistema con timestamp

#### **Test 7.2: Real-time**
```bash
curl http://localhost:8080/api/dashboard/realtime
```
**Esperado:** Estado en tiempo real

#### **Test 7.3: Alertas**
```bash
curl http://localhost:8080/api/dashboard/alerts
```
**Esperado:** Lista de alertas (puede estar vacía)

### **RF-008: Auditoría**

#### **Test 8.1: Eventos**
```bash
curl http://localhost:8080/api/audit/events
```
**Esperado:** Lista de eventos de auditoría

#### **Test 8.2: Por Ticket**
```bash
curl http://localhost:8080/api/audit/ticket/C01
```
**Esperado:** Historial del ticket específico

---

## ⚖️ **TESTS POR REGLA DE NEGOCIO**

### **RN-001: Unicidad Ticket Activo**

#### **Escenario:** Un cliente no puede tener múltiples tickets activos

```bash
# 1. Crear primer ticket
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Primer ticket","descripcion":"Test RN-001","usuarioId":1,"nationalId":"11111111-1","telefono":"+56911111111","branchOffice":"Centro","queueType":"CAJA"}'

# 2. Intentar crear segundo ticket con mismo RUT
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Segundo ticket","descripcion":"Debe fallar","usuarioId":1,"nationalId":"11111111-1","telefono":"+56911111111","branchOffice":"Norte","queueType":"PERSONAL_BANKER"}'
```

**Resultado Esperado:**
- Primer ticket: 201 Created
- Segundo ticket: 409 Conflict con mensaje "Ya existe un ticket activo para el RUT: 11111111-1"

### **RN-005/006: Numeración y Prefijos**

#### **Escenario:** Cada cola tiene prefijo único y numeración secuencial

```bash
# Crear tickets en diferentes colas
curl -X POST http://localhost:8080/api/tickets -H "Content-Type: application/json" -d '{"titulo":"Test CAJA","descripcion":"Test prefijo C","usuarioId":1,"nationalId":"22222222-2","telefono":"+56922222222","branchOffice":"Centro","queueType":"CAJA"}'

curl -X POST http://localhost:8080/api/tickets -H "Content-Type: application/json" -d '{"titulo":"Test PB","descripcion":"Test prefijo P","usuarioId":2,"nationalId":"33333333-3","telefono":"+56933333333","branchOffice":"Centro","queueType":"PERSONAL_BANKER"}'

curl -X POST http://localhost:8080/api/tickets -H "Content-Type: application/json" -d '{"titulo":"Test EMPRESAS","descripcion":"Test prefijo E","usuarioId":3,"nationalId":"44444444-4","telefono":"+56944444444","branchOffice":"Centro","queueType":"EMPRESAS"}'

curl -X POST http://localhost:8080/api/tickets -H "Content-Type: application/json" -d '{"titulo":"Test GERENCIA","descripcion":"Test prefijo G","usuarioId":4,"nationalId":"55555555-5","telefono":"+56955555555","branchOffice":"Centro","queueType":"GERENCIA"}'
```

**Resultado Esperado:**
- CAJA → C## (ej: C01, C02, C03...)
- PERSONAL_BANKER → P## (ej: P01, P02, P03...)
- EMPRESAS → E## (ej: E01, E02, E03...)
- GERENCIA → G## (ej: G01, G02, G03...)

### **RN-010: Cálculo Tiempo Estimado**

#### **Escenario:** Tiempo estimado = posición × tiempo promedio de cola

```bash
# Crear ticket en EMPRESAS (20 min promedio)
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Test tiempo","descripcion":"Validar cálculo RN-010","usuarioId":5,"nationalId":"66666666-6","telefono":"+56966666666","branchOffice":"Centro","queueType":"EMPRESAS"}'
```

**Validación:**
- Si posición = 3, tiempo estimado = 3 × 20 = 60 minutos
- Si posición = 1, tiempo estimado = 1 × 20 = 20 minutos

### **RN-012: Pre-aviso Automático**

#### **Escenario:** Mensaje automático cuando posición ≤ 3

1. Crear ticket
2. Esperar que schedulers procesen (5-60 segundos)
3. Verificar cambio de estado y mensaje Telegram

**Resultado Esperado:**
- Estado cambia: WAITING → CALLED
- Mensaje Telegram: "🔔 ¡Tu turno está próximo!"

---

## 📊 **MATRIZ DE RESULTADOS**

| Test | Funcionalidad | Estado | Tiempo | Notas |
|------|---------------|--------|--------|-------|
| RF-001.1 | Crear Ticket Válido | ✅ PASS | <1s | Número C## generado |
| RF-001.2 | Bean Validation | ✅ PASS | <1s | Errores claros |
| RF-002.1 | Bot Telegram | ✅ PASS | <1s | Bot configurado |
| RF-002.2 | Mensaje Automático | ✅ PASS | 60s | Scheduler funciona |
| RF-005.1 | Gestión Colas | ✅ PASS | <1s | 4 colas operativas |
| RF-006.1 | Consulta UUID | ✅ PASS | <1s | Datos correctos |
| RF-006.2 | Consulta Número | ✅ PASS | <1s | Datos correctos |
| RF-007.1 | Dashboard Summary | ✅ PASS | <1s | Métricas válidas |
| RF-008.1 | Auditoría | ✅ PASS | <1s | Eventos registrados |
| RN-001 | Unicidad | ✅ PASS | <1s | 409 Conflict correcto |
| RN-005/006 | Prefijos | ✅ PASS | <1s | C, P, E, G correctos |
| RN-010 | Tiempo Estimado | ✅ PASS | <1s | Cálculo correcto |
| RN-012 | Pre-aviso | ✅ PASS | 5-60s | Mensaje automático |

---

## 🚀 **EJECUCIÓN AUTOMATIZADA**

### **Script Completo (Windows)**
```bash
cd docs/verify/02-functional-tests
smoke-tests.bat
```

### **Script Completo (Linux/macOS)**
```bash
cd docs/verify/02-functional-tests
./smoke-tests.sh
```

### **Validación Reglas de Negocio**
```bash
cd docs/verify/02-functional-tests
./business-rules-validation.sh
```

---

**Tiempo total:** 20-30 minutos  
**Cobertura:** RF-001, RF-002, RF-005, RF-006, RF-007, RF-008  
**Reglas:** RN-001, RN-005, RN-006, RN-010, RN-012  
**Estado:** TODOS LOS TESTS PASAN ✅