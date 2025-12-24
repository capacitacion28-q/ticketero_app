# PROMPT: Validación y Documentación de Despliegue

**Fecha:** 2025-12-24  
**Versión:** v1.0  
**Especialización:** DevOps Engineer - Production Deployment

---

## CONTEXTO

Eres un DevOps Engineer Senior especializado en validación de despliegues y documentación de procesos productivos.

**OBJETIVO:** Validar el correcto despliegue y funcionamiento del Sistema Ticketero completo con docker-compose, y documentar el proceso.

**DOCUMENTACIÓN DE REFERENCIA OBLIGATORIA:**
- `docs/deployment/` - Guías de despliegue existentes
- `docker-compose.yml` - Configuración de contenedores
- `src/main/resources/application.yml` - Configuración de la app
- `README.md` - Documentación actual del proyecto
- `docs/architecture/software_architecture_design_v1.0.md` - Arquitectura
- `docs/requirements/requerimientos_negocio.md` - Funcionalidades esperadas

**PRINCIPIO:** Validar → Probar → Documentar → Actualizar

---

## METODOLOGÍA OBLIGATORIA

**Proceso:**
1. **Validar:** Analizar configuración actual de Docker Compose
2. **Probar:** Ejecutar despliegue completo y pruebas básicas
3. **Documentar:** Crear/actualizar guías de despliegue
4. **Actualizar:** Mejorar README.md con proceso simplificado

**REGLA CRÍTICA:** Validar funcionamiento real del sistema completo, NO simulaciones.

---

## TAREA ESPECÍFICA

### **PASO 1: Análisis de Configuración Actual**
```bash
# Analizar documentación de deployment
fsRead docs/deployment/

# Analizar configuración Docker
fsRead docker-compose.yml
fsRead src/main/resources/application.yml

# Analizar documentación actual
fsRead README.md

# Analizar arquitectura y requerimientos
fsRead docs/architecture/software_architecture_design_v1.0.md
fsRead docs/requirements/requerimientos_negocio.md
```

### **PASO 2: Validación de Despliegue**
```bash
# Limpiar ambiente
docker-compose down -v
docker system prune -f

# Levantar servicios completos
docker-compose up -d

# Esperar inicialización
sleep 60

# Validar servicios
docker-compose ps
docker-compose logs
```

### **PASO 3: Pruebas de Funcionamiento**
```bash
# Health checks
curl -f http://localhost:8080/actuator/health
curl -f http://localhost:8080/actuator/health/db

# Prueba básica de API
curl -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -d '{"queueType":"GENERAL","priority":"NORMAL","customerName":"Test","customerPhone":"+56912345678"}'

# Validar dashboard
curl -f http://localhost:8080/api/dashboard/summary

# Cleanup
docker-compose down
```

### **PASO 4: Documentación y Actualización**
```bash
# Actualizar README.md con proceso validado
fsReplace README.md

# Crear/actualizar guía de deployment si es necesario
fsWrite docs/deployment/production-deployment-guide.md
```

---

## CRITERIOS DE VALIDACIÓN

### **Configuración Docker Compose:**
- ✅ Servicios necesarios definidos (app, database, etc.)
- ✅ Variables de entorno configuradas
- ✅ Volúmenes para persistencia
- ✅ Health checks apropiados
- ✅ Puertos expuestos correctamente

### **Funcionamiento del Sistema:**
- ✅ Aplicación inicia sin errores
- ✅ Base de datos conecta correctamente
- ✅ API responde a requests básicos
- ✅ Dashboard administrativo accesible
- ✅ Logs muestran funcionamiento normal

### **Usabilidad:**
- ✅ Comandos simples para levantar/bajar
- ✅ Tiempo de inicialización razonable (<2 minutos)
- ✅ Mensajes de error claros si algo falla
- ✅ Documentación fácil de seguir

---

## ENTREGABLES

### **1. Validación Completa**
- Reporte de funcionamiento de todos los servicios
- Identificación de problemas y soluciones
- Tiempos de inicialización medidos

### **2. README.md Actualizado**
```markdown
## 🚀 Despliegue con Docker Compose

### Prerrequisitos
- Docker Desktop instalado y ejecutándose
- 4GB RAM disponible
- Puertos 8080 y 5432 libres

### Comandos de Despliegue
```bash
# Levantar servicios
docker-compose up -d

# Verificar estado
docker-compose ps

# Ver logs
docker-compose logs -f

# Bajar servicios
docker-compose down
```

### Validación del Despliegue
- API: http://localhost:8080/actuator/health
- Dashboard: http://localhost:8080/api/dashboard/summary
- Base de datos: Conectada automáticamente

### Troubleshooting
[Problemas comunes y soluciones]
```

### **3. Guía de Producción (si es necesaria)**
- `docs/deployment/production-deployment-guide.md`
- Configuraciones específicas para producción
- Monitoreo y mantenimiento

---

## FORMATO DE CONFIRMACIÓN

```
✅ DESPLIEGUE VALIDADO
Servicios funcionando: [cantidad]
Tiempo de inicialización: [X] minutos
API respondiendo: SÍ/NO
Base de datos conectada: SÍ/NO
README.md actualizado: SÍ/NO

¿El despliegue es exitoso y está bien documentado?
```

---

**Prompt creado por:** Ingeniero de Prompts Senior  
**Especialización:** DevOps Engineer - Production Deployment  
**Fecha de creación:** 2025-12-24  
**Versión:** v1.0