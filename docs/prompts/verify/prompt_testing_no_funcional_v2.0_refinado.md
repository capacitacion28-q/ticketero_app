# PROMPT REFINADO: Testing No Funcional - Sistema Ticketero

**Fecha:** 2025-12-24  
**Versión:** v2.0 (Refinado)  
**Ingeniero:** Prompts Senior Amazon Q Developer

---

## CONTEXTO

Eres un Performance Engineer Senior especializado en testing no funcional del Sistema Ticketero.

**STACK REAL:**
- Spring Boot 3.2 + Java 17 + PostgreSQL 15
- Telegram Bot API directo
- @Scheduled para procesamiento asíncrono

**DOCUMENTACIÓN OBLIGATORIA:**
- `docs/requirements/requerimientos_negocio.md`
- `docs/implementation/codigo_documentacion_v1.0.md`
- `docs/architecture/software_architecture_design_v1.0.md`
- `docs/deployment/` - Guías de despliegue y configuración Docker
- `docs/prompts/prompt-methodology-master.md`

**PRINCIPIO FUNDAMENTAL:** 
Implementar → Ejecutar → Validar → Confirmar → Continuar

**REGLA CRÍTICA:** 
Cada test implementado DEBE ejecutarse exitosamente antes de avanzar al siguiente paso.

**REQUISITO OBLIGATORIO:**
Todas las pruebas deben ejecutarse sobre la app corriendo en tiempo real usando Docker Compose. ANTES de cada test:
1. Levantar contenedores con `docker-compose up -d`
2. Validar que API esté disponible
3. Validar que BD esté conectada
4. Si falla alguna validación, NO ejecutar el test

**AL FINAL de cada test:** Bajar contenedores con `docker-compose down`

**BLOQUEO OBLIGATORIO:** NO avanzar al siguiente test sin ejecución exitosa del anterior.

**RESTRICCIÓN CRÍTICA:** 
NO realizar pruebas con la app ejecutándose localmente o mediante simulaciones. 
TODOS los tests deben ejecutarse contra la app desplegada completamente con docker-compose.

---

## REQUISITOS NO FUNCIONALES

**Tests mínimos obligatorios:**

| Test | Métrica | Umbral | Validación |
|------|---------|--------|------------|
| API Load | Throughput | ≥30 tickets/min | `curl` + contador |
| API Latency | Response time p95 | <2000ms | `time curl` |
| Scheduler | Procesamiento | ≥20 tickets/min | Log analysis |
| Telegram | Success rate | ≥95% | HTTP status codes |

**CRITERIO DE ÉXITO:** Todos los tests DEBEN ejecutarse y PASAR antes de continuar.

---

## METODOLOGÍA OBLIGATORIA

**Ciclo por cada test:**
1. **Levantar App:** `docker-compose up -d` y validar API + BD
2. **Implementar:** Crear script de test
3. **Ejecutar:** Correr script y capturar output
4. **Validar:** Verificar que PASA el umbral
5. **Bajar App:** `docker-compose down`
6. **Confirmar:** Mostrar resultado y solicitar aprobación

**REGLA CRÍTICA:** Si el test FALLA, CORREGIR antes de continuar.

**Formato de confirmación:**
```
✅ DOCKER: UP
✅ API: RUNNING
✅ BD: CONNECTED
✅ TEST [nombre] COMPLETADO
Comando ejecutado: [comando exacto]
Resultado: [PASS/FAIL]
Métrica: [valor] (umbral: [límite])
✅ DOCKER: DOWN

¿Continúo con el siguiente test?
```

---

## TAREA: 4 TESTS MÍNIMOS

**PRINCIPIO:** Analizar código → Implementar test → Ejecutar → Validar → Continuar

### **TESTS OBLIGATORIOS:**

**TEST 1: API Load Testing**
- Analizar: `fsRead` controllers para identificar endpoints
- Implementar: Script bash con `curl` para carga sostenida
- Ejecutar: 30 requests/min durante 2 minutos
- Validar: Throughput ≥30 tickets/min

**TEST 2: API Latency Testing**  
- Implementar: Script con `time curl` para medir latencia
- Ejecutar: 10 requests consecutivos
- Validar: p95 <2000ms

**TEST 3: Scheduler Performance**
- Analizar: `fsRead` scheduler classes
- Implementar: Monitor de logs para contar procesamiento
- Ejecutar: Observar durante 3 minutos
- Validar: ≥20 tickets procesados/min

**TEST 4: Telegram Integration**
- Implementar: Test de envío de notificaciones
- Ejecutar: 20 notificaciones de prueba
- Validar: Success rate ≥95%

**REGLA:** Cada test DEBE ejecutarse exitosamente antes del siguiente.

---

## IMPLEMENTACIÓN ESPECÍFICA

### **VALIDACIÓN PREVIA OBLIGATORIA:**
```bash
# Consultar documentación de deployment
fsRead docs/deployment/

# Levantar contenedores según guías de deployment
docker-compose up -d

# Esperar 30 segundos para inicialización
sleep 30

# Validar API disponible
curl -f http://localhost:8080/actuator/health || { docker-compose down; exit 1; }

# Validar BD conectada
curl -f http://localhost:8080/actuator/health/db || { docker-compose down; exit 1; }

echo "✅ DOCKER: UP"
echo "✅ API: RUNNING"
echo "✅ BD: CONNECTED"
```

### **CLEANUP OBLIGATORIO:**
```bash
# Al final de cada test (según docs/deployment)
docker-compose down
echo "✅ DOCKER: DOWN"
```

### **TEST 1: API Load Testing**
```bash
# 1. Consultar guías de deployment
fsRead docs/deployment/

# 2. Levantar y validar según documentación
docker-compose up -d
sleep 30
curl -f http://localhost:8080/actuator/health || { docker-compose down; exit 1; }
curl -f http://localhost:8080/actuator/health/db || { docker-compose down; exit 1; }

# 3. Analizar endpoints
fsRead src/main/java/com/example/ticketero/controller/

# 4. Crear script
fsWrite scripts/api-load-test.sh:
#!/bin/bash
for i in {1..60}; do
  curl -X POST http://localhost:8080/api/tickets \
    -H "Content-Type: application/json" \
    -d '{"queueType":"GENERAL","priority":"NORMAL"}'
  sleep 2
done

# 5. Ejecutar y medir
executeBash ./scripts/api-load-test.sh

# 6. Cleanup según docs/deployment
docker-compose down
```

### **TEST 2: API Latency Testing**
```bash
# 1. Consultar guías de deployment
fsRead docs/deployment/

# 2. Levantar y validar según documentación
docker-compose up -d
sleep 30
curl -f http://localhost:8080/actuator/health || { docker-compose down; exit 1; }
curl -f http://localhost:8080/actuator/health/db || { docker-compose down; exit 1; }

# 3. Script de latencia
fsWrite scripts/api-latency-test.sh:
#!/bin/bash
for i in {1..10}; do
  time curl -X GET http://localhost:8080/api/tickets/status
done

# 4. Ejecutar y calcular p95

# 5. Cleanup según docs/deployment
docker-compose down
```

### **TEST 3: Scheduler Performance**
```bash
# 1. Consultar guías de deployment
fsRead docs/deployment/

# 2. Levantar y validar según documentación
docker-compose up -d
sleep 30
curl -f http://localhost:8080/actuator/health || { docker-compose down; exit 1; }
curl -f http://localhost:8080/actuator/health/db || { docker-compose down; exit 1; }

# 3. Monitor de logs
tail -f logs/application.log | grep "tickets procesados" | wc -l

# 4. Cleanup según docs/deployment
docker-compose down
```

### **TEST 4: Telegram Integration**
```bash
# 1. Consultar guías de deployment
fsRead docs/deployment/

# 2. Levantar y validar según documentación
docker-compose up -d
sleep 30
curl -f http://localhost:8080/actuator/health || { docker-compose down; exit 1; }
curl -f http://localhost:8080/actuator/health/db || { docker-compose down; exit 1; }

# 3. Test de notificaciones
curl -X POST http://localhost:8080/api/notifications/test

# 4. Cleanup según docs/deployment
docker-compose down
```

**REGLA:** Ejecutar cada script, verificar PASS, cleanup automático según docs/deployment, luego continuar.

---

## DOCUMENTACIÓN MÍNIMA

**Crear un solo archivo:** `docs/verify/performance-test-results.md`

```markdown
# Performance Tests - Sistema Ticketero
**Fecha:** 2025-12-24

## Resultados
| Test | Comando | Resultado | Métrica |
|------|---------|-----------|---------|
| API Load | `./scripts/api-load-test.sh` | PASS/FAIL | X tickets/min |
| API Latency | `./scripts/api-latency-test.sh` | PASS/FAIL | X ms p95 |
| Scheduler | Log monitoring | PASS/FAIL | X tickets/min |
| Telegram | Notification test | PASS/FAIL | X% success |

## Scripts Ejecutables
- `scripts/api-load-test.sh`
- `scripts/api-latency-test.sh`

## Conclusión
[PASS/FAIL general] - Sistema listo para producción: SÍ/NO
```

**ENTREGABLES FINALES:**
- 1 archivo de resultados
- 4 scripts ejecutables máximo

---

## RESUMEN DE MEJORAS APLICADAS

**Simplificación:**
- ✅ Reducido de 7 a 4 tests esenciales
- ✅ Eliminada complejidad innecesaria
- ✅ Scripts específicos y ejecutables

**Metodología Estricta:**
- ✅ Ciclo obligatorio: Implementar → Ejecutar → Validar → Confirmar
- ✅ Regla crítica: Corregir fallos antes de continuar
- ✅ Formato de confirmación específico

**Integración con Metodología Master:**
- ✅ Principios de simplicidad aplicados
- ✅ Enfoque en ejecución práctica
- ✅ Validación obligatoria por paso

---

**Prompt refinado por:** Ingeniero de Prompts Senior  
**Metodología aplicada:** Refinamiento Iterativo Sección por Sección  
**Fecha de refinamiento:** 2025-12-24  
**Versión original:** v1.0 → **Versión refinada:** v2.0