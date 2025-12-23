# 🏥 HEALTH CHECKS - Sistema Ticketero

Verificaciones sistemáticas de la salud del sistema.

## 📋 **CHECKLIST COMPLETO**

### **1. INFRAESTRUCTURA BASE**

#### **✅ Aplicación Spring Boot**
```bash
# Health endpoint
curl http://localhost:8080/actuator/health
# Esperado: {"status":"UP"}

# Info endpoint
curl http://localhost:8080/actuator/info
# Esperado: Información de la aplicación
```

#### **✅ Base de Datos PostgreSQL**
```bash
# Verificar container
docker ps | grep postgres

# Conectividad
docker exec ticketero-postgres pg_isready -U ticketero_user -d ticketero_db

# Datos iniciales
docker exec ticketero-postgres psql -U ticketero_user -d ticketero_db -c "SELECT COUNT(*) FROM advisor;"
# Esperado: 5 asesores
```

#### **✅ Migraciones Flyway**
```bash
# Verificar migraciones
docker exec ticketero-postgres psql -U ticketero_user -d ticketero_db -c "SELECT version FROM flyway_schema_history ORDER BY installed_rank;"
# Esperado: Versiones 1, 2, 3, 4, 5
```

### **2. API REST ENDPOINTS**

#### **✅ Tickets (RF-001)**
```bash
# POST /api/tickets
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Health Check","descripcion":"Test de salud del sistema","usuarioId":1,"nationalId":"12345678-9","telefono":"+56987654321","branchOffice":"Centro","queueType":"CAJA"}'
# Esperado: 201 Created con ticket

# GET /api/tickets/{uuid}
curl http://localhost:8080/api/tickets/{uuid-del-ticket}
# Esperado: 200 OK con datos del ticket
```

#### **✅ Gestión de Colas (RF-005)**
```bash
# Todas las colas
for queue in CAJA PERSONAL_BANKER EMPRESAS GERENCIA; do
  echo "Testing $queue..."
  curl -s http://localhost:8080/api/queues/$queue | grep "$queue"
done
# Esperado: Respuesta para cada cola
```

#### **✅ Dashboard (RF-007)**
```bash
# Summary
curl http://localhost:8080/api/dashboard/summary
# Esperado: Métricas del sistema

# Real-time
curl http://localhost:8080/api/dashboard/realtime
# Esperado: Estado en tiempo real
```

#### **✅ Auditoría (RF-008)**
```bash
# Eventos
curl http://localhost:8080/api/audit/events
# Esperado: Lista de eventos (puede estar vacía)
```

### **3. INTEGRACIÓN TELEGRAM**

#### **✅ Bot Configuration**
```bash
# Bot info
curl http://localhost:8080/api/test/telegram/bot-info
# Esperado: Info del bot ticketero_capacitacion_bot

# Updates
curl http://localhost:8080/api/test/telegram/updates
# Esperado: Mensajes recibidos por el bot
```

### **4. SCHEDULERS Y PROCESAMIENTO**

#### **✅ Message Scheduler (60s)**
```bash
# Verificar mensajes pendientes
docker exec ticketero-postgres psql -U ticketero_user -d ticketero_db -c "SELECT COUNT(*) FROM mensaje WHERE estado_envio = 'PENDIENTE';"

# Crear ticket y esperar procesamiento
# Los mensajes deben procesarse automáticamente
```

#### **✅ Queue Processor (5s)**
```bash
# Verificar cambios de estado automáticos
# Los tickets WAITING deben cambiar a CALLED automáticamente
```

### **5. REGLAS DE NEGOCIO**

#### **✅ RN-001: Unicidad Ticket Activo**
```bash
# Crear primer ticket
curl -X POST http://localhost:8080/api/tickets -H "Content-Type: application/json" -d '{"titulo":"Test RN-001","descripcion":"Primer ticket","usuarioId":1,"nationalId":"11111111-1","telefono":"+56911111111","branchOffice":"Centro","queueType":"CAJA"}'

# Intentar crear segundo (debe fallar)
curl -X POST http://localhost:8080/api/tickets -H "Content-Type: application/json" -d '{"titulo":"Test RN-001 Duplicado","descripcion":"Segundo ticket","usuarioId":1,"nationalId":"11111111-1","telefono":"+56911111111","branchOffice":"Norte","queueType":"PERSONAL_BANKER"}'
# Esperado: 409 Conflict
```

#### **✅ RN-005/006: Numeración con Prefijos**
```bash
# Verificar prefijos por cola
# CAJA → C01, C02, C03...
# PERSONAL_BANKER → P01, P02, P03...
# EMPRESAS → E01, E02, E03...
# GERENCIA → G01, G02, G03...
```

## 🚨 **INDICADORES DE PROBLEMAS**

### **❌ Señales de Alerta**
- Health check retorna DOWN
- Endpoints retornan 500 Internal Server Error
- Base de datos no responde
- Migraciones Flyway fallan
- Schedulers no procesan tickets
- Telegram bot no responde

### **⚠️ Señales de Advertencia**
- Respuestas lentas (>2 segundos)
- Logs con errores frecuentes
- Memoria/CPU alta
- Conexiones de BD altas

## 🔧 **ACCIONES CORRECTIVAS**

### **Aplicación No Responde**
```bash
# 1. Verificar logs
tail -f logs/application.log

# 2. Reiniciar aplicación
mvn spring-boot:run

# 3. Verificar puerto
netstat -ano | findstr :8080
```

### **Base de Datos Problemas**
```bash
# 1. Reiniciar PostgreSQL
docker-compose restart postgres

# 2. Verificar logs
docker logs ticketero-postgres

# 3. Recrear si es necesario
docker-compose down
docker-compose up -d postgres
```

### **Telegram No Funciona**
```bash
# 1. Verificar token en application.yml
# 2. Probar bot manualmente
curl "https://api.telegram.org/bot8450583015:AAFtvB0Ljq2330--0LkHbmq-PaA0S87Zr9A/getMe"
```

## 📊 **MÉTRICAS DE SALUD**

### **✅ Sistema Saludable**
- Todos los endpoints responden < 1s
- 0 errores en logs últimos 5 min
- Memoria < 80%
- CPU < 70%
- Conexiones BD < 10

### **⚠️ Sistema Degradado**
- Algunos endpoints lentos (1-3s)
- Errores esporádicos en logs
- Memoria 80-90%
- CPU 70-85%

### **❌ Sistema Crítico**
- Endpoints no responden o >3s
- Errores constantes en logs
- Memoria >90%
- CPU >85%
- BD desconectada

---

**Frecuencia recomendada:** Cada deploy y diariamente en producción  
**Tiempo estimado:** 10-15 minutos  
**Automatizable:** Sí (CI/CD pipeline)