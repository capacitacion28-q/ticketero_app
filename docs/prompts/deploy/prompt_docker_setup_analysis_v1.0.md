# PROMPT: Análisis de Documentación - Docker Compose Setup

**Fecha:** 2025-12-24  
**Versión:** v1.0  
**Especialización:** DevOps Engineer - Docker Infrastructure

---

## CONTEXTO

Eres un DevOps Engineer Senior especializado en análisis de documentación técnica y configuración de Docker Compose.

**OBJETIVO:** Analizar toda la documentación del proyecto Sistema Ticketero para determinar cómo levantar la APP completa con docker-compose.

**DOCUMENTACIÓN A ANALIZAR:**
- `docs/requirements/requerimientos_negocio.md`
- `docs/implementation/codigo_documentacion_v1.0.md`
- `docs/architecture/software_architecture_design_v1.0.md`
- `docker-compose.yml`
- `src/main/resources/application.yml`
- `README.md`

**PRINCIPIO:** Analizar → Extraer → Validar → Documentar

---

## METODOLOGÍA OBLIGATORIA

**Proceso:**
1. **Analizar:** `fsRead` toda la documentación relevante
2. **Extraer:** Identificar componentes, dependencias y configuraciones
3. **Validar:** Verificar que docker-compose.yml esté completo
4. **Documentar:** Crear guía paso a paso para levantar la APP

**REGLA:** Basar conclusiones ÚNICAMENTE en la documentación encontrada.

---

## TAREA ESPECÍFICA

### **PASO 1: Análisis de Documentación**
```bash
# Analizar documentación técnica
fsRead docs/requirements/requerimientos_negocio.md
fsRead docs/implementation/codigo_documentacion_v1.0.md
fsRead docs/architecture/software_architecture_design_v1.0.md

# Analizar configuración
fsRead docker-compose.yml
fsRead src/main/resources/application.yml
fsRead README.md
```

### **PASO 2: Extracción de Información**
Identificar:
- **Servicios requeridos:** (PostgreSQL, App, etc.)
- **Puertos necesarios:** (8080, 5432, etc.)
- **Variables de entorno:** (DB credentials, Telegram tokens)
- **Dependencias:** (orden de inicio, health checks)
- **Volúmenes:** (datos persistentes)

### **PASO 3: Validación**
Verificar que docker-compose.yml incluya:
- ✅ Todos los servicios identificados
- ✅ Configuración de red correcta
- ✅ Variables de entorno necesarias
- ✅ Health checks apropiados

### **PASO 4: Documentación**
Crear archivo: `docs/deployment/docker-setup-guide.md`

---

## ENTREGABLE

**Crear:** `docs/deployment/docker-setup-guide.md`

```markdown
# Docker Compose Setup - Sistema Ticketero

## Servicios Identificados
- [Lista de servicios encontrados]

## Comandos de Ejecución
```bash
# Levantar servicios
docker-compose up -d

# Verificar status
docker-compose ps

# Ver logs
docker-compose logs -f
```

## Validación
- [ ] PostgreSQL: http://localhost:5432
- [ ] API: http://localhost:8080/actuator/health
- [ ] BD Connected: http://localhost:8080/actuator/health/db

## Troubleshooting
[Problemas comunes y soluciones]
```

**FORMATO DE CONFIRMACIÓN:**
```
✅ DOCUMENTACIÓN ANALIZADA
Servicios encontrados: [cantidad]
Configuración completa: SÍ/NO
Guía creada: docs/deployment/docker-setup-guide.md

¿La guía es correcta?
```

---

**Prompt creado por:** Ingeniero de Prompts Senior  
**Especialización:** DevOps Engineer - Docker Infrastructure  
**Fecha de creación:** 2025-12-24  
**Versión:** v1.0