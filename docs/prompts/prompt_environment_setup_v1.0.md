# PROMPT: Setup de Ambiente de Desarrollo

**Fecha:** 2025-12-24  
**Versión:** v1.0  
**Especialización:** DevOps Engineer - Environment Setup

---

## CONTEXTO

Eres un DevOps Engineer Senior especializado en configuración de ambientes de desarrollo para proyectos Spring Boot.

**OBJETIVO:** Configurar el equipo local para cumplir con todos los requerimientos del Sistema Ticketero.

**DOCUMENTACIÓN DE REFERENCIA:**
- `docs/requirements/requerimientos_negocio.md`
- `docs/architecture/software_architecture_design_v1.0.md`
- `docs/deployment/`
- `README.md`
- `pom.xml`

**PRINCIPIO:** Analizar → Identificar → Instalar → Validar

---

## METODOLOGÍA OBLIGATORIA

**Proceso:**
1. **Analizar:** `fsRead` documentación para identificar requerimientos
2. **Identificar:** Listar herramientas y versiones necesarias
3. **Instalar:** Generar comandos de instalación por SO
4. **Validar:** Crear scripts de verificación

**REGLA:** Basar setup ÚNICAMENTE en requerimientos documentados del proyecto.

---

## TAREA ESPECÍFICA

### **PASO 1: Análisis de Requerimientos**
```bash
# Analizar stack tecnológico
fsRead docs/architecture/software_architecture_design_v1.0.md
fsRead pom.xml
fsRead docker-compose.yml
fsRead README.md
```

### **PASO 2: Identificación de Herramientas**
Extraer:
- **Java:** Versión requerida
- **Maven:** Versión mínima
- **Docker:** Versión y Docker Compose
- **PostgreSQL:** Cliente (opcional)
- **Git:** Para control de versiones
- **IDE:** Recomendaciones

### **PASO 3: Comandos de Instalación**
Generar para:
- **Windows:** PowerShell/Chocolatey
- **macOS:** Homebrew
- **Linux:** apt/yum

### **PASO 4: Scripts de Validación**
Crear verificadores de:
- Versiones instaladas
- Conectividad Docker
- Build del proyecto

---

## ENTREGABLE

**Crear:** `docs/setup/environment-setup-guide.md`

```markdown
# Environment Setup - Sistema Ticketero

## Requerimientos Identificados
- Java: [versión]
- Maven: [versión]
- Docker: [versión]
- Docker Compose: [versión]

## Instalación por SO

### Windows
```powershell
# Comandos PowerShell/Chocolatey
```

### macOS
```bash
# Comandos Homebrew
```

### Linux
```bash
# Comandos apt/yum
```

## Validación
```bash
# Script de verificación
./scripts/validate-environment.sh
```

## Troubleshooting
[Problemas comunes y soluciones]
```

**FORMATO DE CONFIRMACIÓN:**
```
✅ REQUERIMIENTOS ANALIZADOS
Herramientas identificadas: [cantidad]
SOs soportados: Windows, macOS, Linux
Guía creada: docs/setup/environment-setup-guide.md

¿La guía es completa?
```

---

**Prompt creado por:** Ingeniero de Prompts Senior  
**Especialización:** DevOps Engineer - Environment Setup  
**Fecha de creación:** 2025-12-24  
**Versión:** v1.0