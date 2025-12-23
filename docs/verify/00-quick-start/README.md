# 🚀 INICIO RÁPIDO - Validación en 5 Minutos

Guía para validar rápidamente que el Sistema Ticketero está funcionando correctamente.

## ⚡ **VALIDACIÓN INMEDIATA**

### **Prerrequisitos (30 segundos)**
```bash
# 1. Verificar que la aplicación esté corriendo
curl http://localhost:8080/actuator/health
# Esperado: {"status":"UP"}

# 2. Verificar PostgreSQL
docker ps | findstr postgres
# Esperado: Container corriendo
```

### **Test Básico (2 minutos)**
```bash
# 1. Crear ticket
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Test rápido",
    "descripcion": "Validación de funcionamiento básico",
    "usuarioId": 1,
    "nationalId": "12345678-9",
    "telefono": "+56987654321",
    "branchOffice": "Centro",
    "queueType": "CAJA"
  }'

# 2. Verificar dashboard
curl http://localhost:8080/api/dashboard/summary

# 3. Verificar colas
curl http://localhost:8080/api/queues/CAJA
```

### **Validación Telegram (1 minuto)**
```bash
# Verificar bot info
curl http://localhost:8080/api/test/telegram/bot-info
# Esperado: Bot info con username "ticketero_capacitacion_bot"
```

## ✅ **CHECKLIST RÁPIDO**

- [ ] **Aplicación UP** - Health check responde
- [ ] **Base de datos** - PostgreSQL conectado
- [ ] **API REST** - Endpoints responden
- [ ] **Telegram** - Bot configurado
- [ ] **Tickets** - Creación funciona
- [ ] **Dashboard** - Métricas disponibles

## 🎯 **RESULTADOS ESPERADOS**

### **✅ TODO FUNCIONA SI:**
- Health check retorna `{"status":"UP"}`
- Ticket se crea con número tipo `C01`, `P01`, etc.
- Dashboard retorna métricas válidas
- Bot info muestra bot activo

### **❌ HAY PROBLEMAS SI:**
- Health check falla o no responde
- Creación de ticket retorna 500 error
- Dashboard retorna error
- Bot info no responde

## 🔧 **SOLUCIÓN RÁPIDA DE PROBLEMAS**

### **Aplicación no responde:**
```bash
# Reiniciar aplicación
mvn spring-boot:run
```

### **Base de datos desconectada:**
```bash
# Reiniciar PostgreSQL
docker-compose up -d postgres
```

### **Telegram no funciona:**
```bash
# Verificar token en application.yml
# Token debe ser: 8450583015:AAFtvB0Ljq2330--0LkHbmq-PaA0S87Zr9A
```

## 📊 **PRÓXIMOS PASOS**

### **Si todo funciona:**
1. Continuar con [`01-health-checks/`](../01-health-checks/)
2. Ejecutar tests completos en [`02-functional-tests/`](../02-functional-tests/)

### **Si hay problemas:**
1. Revisar [`04-reports/error-analysis.md`](../04-reports/error-analysis.md)
2. Ejecutar diagnóstico completo en [`01-health-checks/`](../01-health-checks/)

---

**Tiempo total:** 5 minutos  
**Nivel:** Básico  
**Objetivo:** Verificación rápida de funcionamiento