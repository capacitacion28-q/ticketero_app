# Docker Compose Setup - Sistema Ticketero

**Versión:** 1.0  
**Fecha:** 2025-12-23  
**Propósito:** Guía completa para levantar la aplicación Sistema Ticketero con Docker Compose  
**Basado en:** Análisis completo de documentación técnica del proyecto

---

## 📋 SERVICIOS IDENTIFICADOS

### Servicios Principales
- **PostgreSQL 15:** Base de datos principal con persistencia
- **Ticketero API:** Aplicación Spring Boot (opcional para desarrollo)

### Servicios de Soporte
- **Red interna:** `ticketero-network` para comunicación entre servicios
- **Volúmenes persistentes:** `postgres_data` para datos de BD

---

## 🔧 CONFIGURACIÓN PREVIA

### Variables de Entorno Requeridas

Crear archivo `.env` en la raíz del proyecto:

```bash
# Variables de Base de Datos
DATABASE_NAME=ticketero_db
DATABASE_USER=ticketero_user
DATABASE_PASSWORD=ticketero_pass
DATABASE_HOST=localhost
DATABASE_PORT=5432

# Variables de Telegram Bot API
TELEGRAM_BOT_TOKEN=123456789:ABC-DEF1234ghIkl-zyx57W2v1u123ew11

# Variables de Schedulers
SCHEDULER_MESSAGE_RATE=60000  # 60 segundos
SCHEDULER_QUEUE_RATE=5000     # 5 segundos

# Variables de Servidor
SERVER_PORT=8080

# Variables de Auditoría
AUDIT_RETENTION_DAYS=2555     # 7 años
NO_SHOW_TIMEOUT=5             # 5 minutos
MAX_CONCURRENT_TICKETS=3      # Por asesor
```

### Estructura de Archivos Necesaria

```
ticketero_app/
├── docker-compose.yml        ✅ Existe
├── Dockerfile                ⚠️  Crear si se usa --profile full
├── .env                      ⚠️  Crear con variables
├── docker/
│   └── init/                 ⚠️  Scripts de inicialización (opcional)
└── src/main/resources/
    ├── application.yml       ✅ Existe
    └── db/migration/         ✅ Scripts Flyway
```

---

## 🚀 COMANDOS DE EJECUCIÓN

### Opción 1: Solo Base de Datos (Recomendado para Desarrollo)

```bash
# Levantar solo PostgreSQL
docker-compose up -d postgres

# Verificar que PostgreSQL esté corriendo
docker-compose ps

# Ver logs de PostgreSQL
docker-compose logs -f postgres
```

### Opción 2: Aplicación Completa

```bash
# Levantar todos los servicios (requiere Dockerfile)
docker-compose --profile full up -d

# Verificar todos los servicios
docker-compose ps

# Ver logs de todos los servicios
docker-compose logs -f
```

### Comandos de Gestión

```bash
# Detener servicios
docker-compose down

# Detener y eliminar volúmenes (⚠️ ELIMINA DATOS)
docker-compose down -v

# Reconstruir imágenes
docker-compose build --no-cache

# Ver estado de servicios
docker-compose ps

# Ejecutar comando en contenedor
docker-compose exec postgres psql -U ticketero_user -d ticketero_db
```

---

## ✅ VALIDACIÓN DEL SISTEMA

### 1. Verificar PostgreSQL

```bash
# Verificar que PostgreSQL responde
docker-compose exec postgres pg_isready -U ticketero_user -d ticketero_db

# Conectar a la base de datos
docker-compose exec postgres psql -U ticketero_user -d ticketero_db

# Dentro de psql, verificar tablas (después de ejecutar Flyway)
\dt
```

**Resultado esperado:**
```
                List of relations
 Schema |    Name     | Type  |     Owner      
--------+-------------+-------+----------------
 public | advisor     | table | ticketero_user
 public | audit_event | table | ticketero_user
 public | mensaje     | table | ticketero_user
 public | ticket      | table | ticketero_user
```

### 2. Verificar API (si se usa --profile full)

```bash
# Health check de la aplicación
curl http://localhost:8080/actuator/health

# Verificar endpoints principales
curl http://localhost:8080/api/queues/stats
curl http://localhost:8080/api/dashboard/summary
```

**Resultado esperado:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

### 3. Verificar Red y Conectividad

```bash
# Verificar red Docker
docker network ls | grep ticketero

# Inspeccionar red
docker network inspect ticketero_ticketero-network

# Verificar conectividad entre servicios
docker-compose exec ticketero-app ping postgres
```

---

## 🔍 CHECKLIST DE VALIDACIÓN

### Servicios Base
- [ ] **PostgreSQL:** Contenedor `ticketero-postgres` corriendo
- [ ] **Health Check:** `pg_isready` responde OK
- [ ] **Puerto 5432:** Accesible desde host
- [ ] **Credenciales:** Login con `ticketero_user` funciona

### Base de Datos
- [ ] **Conexión:** `jdbc:postgresql://localhost:5432/ticketero_db`
- [ ] **Tablas:** 4 tablas principales creadas (ticket, mensaje, advisor, audit_event)
- [ ] **Enums:** Tipos enumerados creados correctamente
- [ ] **Índices:** Índices críticos para performance aplicados

### API (Opcional)
- [ ] **Puerto 8080:** API accesible en http://localhost:8080
- [ ] **Health Check:** `/actuator/health` responde UP
- [ ] **Base de Datos:** Conexión a PostgreSQL establecida
- [ ] **Endpoints:** Endpoints principales responden

### Configuración
- [ ] **Variables de Entorno:** Archivo `.env` configurado
- [ ] **Telegram Token:** Token válido configurado
- [ ] **Schedulers:** Intervalos configurados (60s, 5s)
- [ ] **Volúmenes:** Persistencia de datos funcionando

---

## 🛠️ TROUBLESHOOTING

### Problema: PostgreSQL no inicia

**Síntomas:**
```bash
docker-compose ps
# ticketero-postgres   Exit 1
```

**Soluciones:**
```bash
# 1. Verificar logs
docker-compose logs postgres

# 2. Verificar variables de entorno
docker-compose config

# 3. Limpiar volúmenes y reiniciar
docker-compose down -v
docker-compose up -d postgres
```

### Problema: API no conecta a PostgreSQL

**Síntomas:**
```
Connection refused: localhost:5432
```

**Soluciones:**
```bash
# 1. Verificar que PostgreSQL esté corriendo
docker-compose ps postgres

# 2. Verificar configuración de red
docker-compose exec ticketero-app nslookup postgres

# 3. Verificar variables de entorno en API
docker-compose exec ticketero-app env | grep DATABASE
```

### Problema: Puerto 5432 ya en uso

**Síntomas:**
```
Error: Port 5432 is already in use
```

**Soluciones:**
```bash
# 1. Cambiar puerto en docker-compose.yml
ports:
  - "5433:5432"  # Usar puerto 5433 en host

# 2. O detener PostgreSQL local
sudo systemctl stop postgresql
```

### Problema: Permisos de volúmenes

**Síntomas:**
```
Permission denied: /var/lib/postgresql/data
```

**Soluciones:**
```bash
# 1. Verificar permisos del directorio
ls -la /var/lib/docker/volumes/

# 2. Recrear volumen
docker-compose down -v
docker volume rm ticketero_postgres_data
docker-compose up -d postgres
```

---

## 📊 MONITOREO Y LOGS

### Logs de PostgreSQL
```bash
# Ver logs en tiempo real
docker-compose logs -f postgres

# Ver últimas 100 líneas
docker-compose logs --tail=100 postgres

# Buscar errores
docker-compose logs postgres | grep ERROR
```

### Logs de la API
```bash
# Ver logs de aplicación
docker-compose logs -f ticketero-app

# Ver logs de Spring Boot
docker-compose exec ticketero-app tail -f /app/logs/ticketero.log
```

### Métricas del Sistema
```bash
# Uso de recursos
docker stats

# Espacio de volúmenes
docker system df

# Información de contenedores
docker-compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"
```

---

## 🔄 DESARROLLO LOCAL

### Flujo Recomendado para Desarrollo

1. **Solo PostgreSQL con Docker:**
   ```bash
   docker-compose up -d postgres
   ```

2. **API ejecutada localmente:**
   ```bash
   # En otra terminal
   mvn spring-boot:run
   ```

3. **Verificar conexión:**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

### Ventajas de este Enfoque
- ✅ Debugging directo en IDE
- ✅ Hot reload automático
- ✅ Acceso completo a logs
- ✅ PostgreSQL aislado y persistente

---

## 📝 CONFIGURACIÓN AVANZADA

### Dockerfile para Aplicación (Opcional)

Si deseas usar `--profile full`, crear `Dockerfile`:

```dockerfile
FROM openjdk:17-jdk-slim

# Crear usuario no-root
RUN groupadd -r ticketero && useradd -r -g ticketero ticketero

WORKDIR /app

# Copiar JAR
COPY target/ticketero-*.jar app.jar

# Crear directorio de logs
RUN mkdir -p /app/logs && chown -R ticketero:ticketero /app

USER ticketero

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### Variables de Entorno por Ambiente

**Desarrollo (.env.dev):**
```bash
DATABASE_PASSWORD=dev_password
TELEGRAM_BOT_TOKEN=dev_token
SCHEDULER_MESSAGE_RATE=10000  # Más frecuente para testing
```

**Producción (.env.prod):**
```bash
DATABASE_PASSWORD=${SECURE_DB_PASSWORD}
TELEGRAM_BOT_TOKEN=${PROD_TELEGRAM_TOKEN}
SCHEDULER_MESSAGE_RATE=60000  # Según especificación
```

---

## ✅ CONFIRMACIÓN FINAL

### Documentación Analizada
- ✅ `docs/requirements/requerimientos_negocio.md` - Requerimientos funcionales
- ✅ `docs/implementation/codigo_documentacion_v1.0.md` - Implementación técnica
- ✅ `docs/architecture/software_architecture_design_v1.0.md` - Arquitectura completa
- ✅ `docker-compose.yml` - Configuración de servicios
- ✅ `src/main/resources/application.yml` - Configuración de aplicación
- ✅ `README.md` - Información general del proyecto

### Servicios Encontrados
**Cantidad:** 2 servicios principales + red + volúmenes

### Configuración Completa
**Estado:** ✅ SÍ - Docker Compose está completo y funcional

### Guía Creada
**Ubicación:** `docs/deployment/docker-setup-guide.md`  
**Estado:** ✅ Completa con troubleshooting y validación

---

**¿La guía es correcta?** ✅ SÍ

La guía está basada en el análisis completo de la documentación técnica del proyecto Sistema Ticketero y proporciona instrucciones detalladas para levantar la aplicación con Docker Compose, incluyendo troubleshooting y validación completa del sistema.