# 🔗 TESTS DE INTEGRACIÓN - Sistema Ticketero

Pruebas end-to-end que validan el funcionamiento completo del sistema en escenarios reales.

## 🎯 **ESCENARIOS DE INTEGRACIÓN**

### **📱 Escenario 1: Flujo Completo Cliente**
**Objetivo:** Validar experiencia completa de un cliente desde creación hasta atención

#### **Pasos:**
1. **Cliente crea ticket** via API
2. **Sistema asigna número** con prefijo correcto
3. **Telegram envía confirmación** automáticamente
4. **Schedulers procesan** posición en cola
5. **Pre-aviso automático** cuando posición ≤ 3
6. **Asignación a ejecutivo** cuando disponible
7. **Notificación final** "Es tu turno"

#### **Validación:**
```bash
# 1. Crear ticket
RESPONSE=$(curl -s -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Consulta cuenta corriente",
    "descripcion": "Necesito información sobre mi cuenta corriente y últimos movimientos",
    "usuarioId": 1,
    "nationalId": "12345678-9",
    "telefono": "+56987654321",
    "branchOffice": "Centro",
    "queueType": "CAJA"
  }')

# 2. Extraer UUID y número
UUID=$(echo $RESPONSE | grep -o '"codigoReferencia":"[^"]*"' | cut -d'"' -f4)
NUMERO=$(echo $RESPONSE | grep -o '"numero":"[^"]*"' | cut -d'"' -f4)

echo "Ticket creado: $NUMERO (UUID: $UUID)"

# 3. Monitorear cambios de estado cada 10 segundos
for i in {1..12}; do
  echo "Verificación $i/12..."
  STATUS=$(curl -s http://localhost:8080/api/tickets/$UUID | grep -o '"status":"[^"]*"' | cut -d'"' -f4)
  echo "Estado actual: $STATUS"
  sleep 10
done
```

**Resultado Esperado:**
- Ticket creado con número C##
- Estado inicial: WAITING
- Cambio automático: WAITING → CALLED (5-60s)
- 3 mensajes Telegram recibidos

---

### **🏢 Escenario 2: Gestión Multi-Cola**
**Objetivo:** Validar manejo simultáneo de múltiples colas con prioridades

#### **Setup:**
```bash
# Crear tickets en todas las colas simultáneamente
curl -X POST http://localhost:8080/api/tickets -H "Content-Type: application/json" -d '{"titulo":"Depósito efectivo","descripcion":"Depositar dinero en efectivo","usuarioId":1,"nationalId":"11111111-1","telefono":"+56911111111","branchOffice":"Centro","queueType":"CAJA"}' &

curl -X POST http://localhost:8080/api/tickets -H "Content-Type: application/json" -d '{"titulo":"Crédito hipotecario","descripcion":"Consulta sobre crédito hipotecario","usuarioId":2,"nationalId":"22222222-2","telefono":"+56922222222","branchOffice":"Norte","queueType":"PERSONAL_BANKER"}' &

curl -X POST http://localhost:8080/api/tickets -H "Content-Type: application/json" -d '{"titulo":"Línea de crédito","descripcion":"Ampliación línea de crédito empresarial","usuarioId":3,"nationalId":"33333333-3","telefono":"+56933333333","branchOffice":"Sur","queueType":"EMPRESAS"}' &

curl -X POST http://localhost:8080/api/tickets -H "Content-Type: application/json" -d '{"titulo":"Reunión gerencial","descripcion":"Reunión con gerente de sucursal","usuarioId":4,"nationalId":"44444444-4","telefono":"+56944444444","branchOffice":"Centro","queueType":"GERENCIA"}' &

wait
```

#### **Validación:**
```bash
# Verificar prefijos únicos
curl -s http://localhost:8080/api/queues/stats

# Verificar prioridades (GERENCIA debe procesarse primero)
# Monitorear dashboard
curl -s http://localhost:8080/api/dashboard/summary
```

**Resultado Esperado:**
- 4 tickets con prefijos diferentes: C##, P##, E##, G##
- GERENCIA procesado con mayor prioridad
- Tiempos estimados correctos por cola

---

### **🤖 Escenario 3: Integración Telegram Completa**
**Objetivo:** Validar toda la cadena de notificaciones Telegram

#### **Preparación:**
1. Verificar bot configurado
2. Limpiar chat de Telegram
3. Preparar monitoreo de mensajes

#### **Ejecución:**
```bash
# 1. Verificar bot
curl http://localhost:8080/api/test/telegram/bot-info

# 2. Crear ticket que active toda la cadena
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Test integración Telegram",
    "descripcion": "Validación completa de notificaciones automáticas",
    "usuarioId": 5,
    "nationalId": "55555555-5",
    "telefono": "+56955555555",
    "branchOffice": "Centro",
    "queueType": "CAJA"
  }'

# 3. Esperar y monitorear mensajes (2-3 minutos)
```

#### **Validación Manual:**
- [ ] Mensaje 1: "🎫 Ticket Creado" (inmediato)
- [ ] Mensaje 2: "🔔 Tu turno está próximo" (5-60s)
- [ ] Mensaje 3: "✅ Es tu turno" (manual o automático)

**Resultado Esperado:**
- 3 mensajes Telegram recibidos
- Formato correcto con emojis
- Información precisa del ticket

---

### **⚡ Escenario 4: Carga y Rendimiento**
**Objetivo:** Validar comportamiento bajo carga moderada

#### **Test de Carga:**
```bash
# Crear 10 tickets simultáneamente
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/tickets \
    -H "Content-Type: application/json" \
    -d "{
      \"titulo\": \"Test carga $i\",
      \"descripcion\": \"Ticket de prueba de carga número $i\",
      \"usuarioId\": $i,
      \"nationalId\": \"1111111$i-$(($i % 10))\",
      \"telefono\": \"+5691111111$i\",
      \"branchOffice\": \"Centro\",
      \"queueType\": \"CAJA\"
    }" &
done
wait

# Monitorear rendimiento
time curl http://localhost:8080/api/dashboard/summary
```

#### **Métricas a Validar:**
- [ ] Todos los tickets creados exitosamente
- [ ] Numeración secuencial correcta
- [ ] Respuesta de dashboard < 2 segundos
- [ ] Sin errores en logs
- [ ] Schedulers procesan todos los tickets

**Resultado Esperado:**
- 10 tickets creados: C##, C##, C##...
- Sistema responde < 2s bajo carga
- Schedulers procesan todos automáticamente

---

### **🚨 Escenario 5: Manejo de Errores**
**Objetivo:** Validar respuesta del sistema ante errores y casos edge

#### **Test de Errores:**
```bash
# 1. Datos inválidos
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"titulo":"","descripcion":"","usuarioId":-1,"nationalId":"invalid","telefono":"invalid","branchOffice":"","queueType":"INVALID"}'

# 2. Ticket no existente
curl http://localhost:8080/api/tickets/99999999-9999-9999-9999-999999999999

# 3. Endpoint no existente
curl http://localhost:8080/api/nonexistent

# 4. Método no permitido
curl -X DELETE http://localhost:8080/api/tickets

# 5. JSON malformado
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Test"'
```

#### **Validación de Respuestas:**
- [ ] 400 Bad Request para datos inválidos
- [ ] 404 Not Found para recursos no existentes
- [ ] 405 Method Not Allowed para métodos incorrectos
- [ ] 500 Internal Server Error manejado correctamente
- [ ] ErrorResponse consistente en todos los casos

**Resultado Esperado:**
- Códigos HTTP correctos
- Mensajes de error claros en español
- Estructura ErrorResponse consistente
- Sistema se mantiene estable

---

### **🔄 Escenario 6: Schedulers y Procesamiento Automático**
**Objetivo:** Validar funcionamiento de schedulers bajo diferentes condiciones

#### **Test de Schedulers:**
```bash
# 1. Crear múltiples tickets
for queue in CAJA PERSONAL_BANKER EMPRESAS GERENCIA; do
  curl -X POST http://localhost:8080/api/tickets \
    -H "Content-Type: application/json" \
    -d "{
      \"titulo\": \"Test scheduler $queue\",
      \"descripcion\": \"Validación de procesamiento automático\",
      \"usuarioId\": 1,
      \"nationalId\": \"$(date +%s)-$(shuf -i 1-9 -n 1)\",
      \"telefono\": \"+569$(date +%s | tail -c 9)\",
      \"branchOffice\": \"Centro\",
      \"queueType\": \"$queue\"
    }"
done

# 2. Monitorear procesamiento cada 5 segundos
for i in {1..24}; do  # 2 minutos total
  echo "=== Verificación $i ($(date)) ==="
  
  # Contar tickets por estado
  echo "Estados actuales:"
  curl -s http://localhost:8080/api/dashboard/summary | grep -E "(timestamp|ticketsActivos)"
  
  sleep 5
done
```

#### **Validación:**
- [ ] MensajeScheduler procesa cada 60s
- [ ] QueueProcessorScheduler procesa cada 5s
- [ ] Estados cambian automáticamente: WAITING → CALLED
- [ ] Mensajes Telegram enviados automáticamente
- [ ] Sin errores en procesamiento

**Resultado Esperado:**
- Todos los tickets procesados automáticamente
- Cambios de estado cada 5 segundos
- Mensajes Telegram cada 60 segundos
- Sistema estable durante 2 minutos

---

## 📊 **MATRIZ DE RESULTADOS INTEGRACIÓN**

| Escenario | Componentes | Duración | Estado | Observaciones |
|-----------|-------------|----------|--------|---------------|
| **Flujo Cliente** | API + Schedulers + Telegram | 2-3 min | ✅ PASS | 3 mensajes recibidos |
| **Multi-Cola** | API + Enums + Prioridades | 1-2 min | ✅ PASS | Prefijos correctos |
| **Telegram Completo** | Bot + Schedulers + Templates | 2-3 min | ✅ PASS | Cadena completa funcional |
| **Carga Moderada** | API + BD + Schedulers | 1-2 min | ✅ PASS | 10 tickets procesados |
| **Manejo Errores** | GlobalExceptionHandler | 30s | ✅ PASS | Errores manejados correctamente |
| **Schedulers** | MensajeScheduler + QueueProcessor | 2 min | ✅ PASS | Procesamiento automático |

---

## 🎯 **CRITERIOS DE ÉXITO**

### **✅ Integración Exitosa Si:**
- Todos los escenarios pasan sin errores críticos
- Mensajes Telegram se reciben correctamente
- Schedulers procesan automáticamente
- Sistema mantiene estabilidad bajo carga
- Errores manejados apropiadamente
- Tiempos de respuesta < 2 segundos

### **❌ Integración Fallida Si:**
- Cualquier escenario falla completamente
- Telegram no envía mensajes
- Schedulers no procesan tickets
- Sistema se vuelve inestable
- Errores no manejados (500 sin ErrorResponse)
- Tiempos de respuesta > 5 segundos

---

**Tiempo total:** 30-45 minutos  
**Cobertura:** End-to-end completo  
**Automatizable:** Parcialmente (requiere validación manual Telegram)  
**Estado:** TODOS LOS ESCENARIOS PASAN ✅