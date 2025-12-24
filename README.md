# Sistema Ticketero Digital

Sistema de gestión de tickets para institución financiera desarrollado con Spring Boot y PostgreSQL.

## 📚 Índice
- [🚀 Inicio Rápido (5 minutos)](#-inicio-rápido-5-minutos)
- [🛠️ Requisitos del Ambiente](#️-requisitos-del-ambiente)
- [⚙️ Variables de Entorno](#️-variables-de-entorno)
- [🚀 Despliegue con Docker Compose](#-despliegue-con-docker-compose)
- [📊 Estado del Proyecto](#-estado-del-proyecto)

---

## 🛠️ Requisitos del Ambiente

### Herramientas Requeridas

| Herramienta | Versión Mínima | Propósito |
|-------------|----------------|----------|
| **Java** | 17+ | Runtime de la aplicación Spring Boot |
| **Maven** | 3.9+ | Build y gestión de dependencias |
| **Docker** | Latest | Containerización y PostgreSQL |
| **Docker Compose** | v2+ | Orquestación de servicios |
| **Git** | Any | Control de versiones |

### Instalación por Sistema Operativo

#### 🪟 Windows

**Opción 1: Instaladores Individuales**
```powershell
# Java 17+ (OpenJDK)
# Descargar desde: https://adoptium.net/

# Maven 3.9+
# Descargar desde: https://maven.apache.org/download.cgi

# Docker Desktop
# Descargar desde: https://www.docker.com/products/docker-desktop/

# Git
# Descargar desde: https://git-scm.com/download/win
```

**Opción 2: Chocolatey (Recomendado)**
```powershell
# Instalar Chocolatey primero
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

# Instalar herramientas
choco install openjdk17 maven docker-desktop git -y
```

#### 🐧 Linux (Ubuntu/Debian)

```bash
# Actualizar repositorios
sudo apt update

# Java 17
sudo apt install openjdk-17-jdk -y

# Maven
sudo apt install maven -y

# Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Docker Compose (incluido en Docker Desktop)
sudo apt install docker-compose-plugin -y

# Git
sudo apt install git -y
```

#### 🍎 macOS

**Opción 1: Homebrew (Recomendado)**
```bash
# Instalar Homebrew primero
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Instalar herramientas
brew install openjdk@17 maven docker git
brew install --cask docker
```

**Opción 2: Instaladores Individuales**
- Java 17+: https://adoptium.net/
- Maven: https://maven.apache.org/download.cgi
- Docker Desktop: https://www.docker.com/products/docker-desktop/
- Git: https://git-scm.com/download/mac

### ✅ Validación del Ambiente

```bash
# Verificar versiones instaladas
java -version          # Debe mostrar 17+
mvn -version           # Debe mostrar 3.9+
docker --version       # Cualquier versión reciente
docker-compose --version  # v2+
git --version          # Cualquier versión

# Verificar Docker daemon
docker ps              # Debe conectar sin errores

# Probar build del proyecto
mvn clean compile      # Debe compilar sin errores
```

### 🚨 Troubleshooting Común

**Java:**
- Verificar `JAVA_HOME` apunta a JDK 17+
- Agregar Java al `PATH`

**Maven:**
- Verificar `M2_HOME` y agregar `bin` al `PATH`

**Docker:**
- En Windows/Mac: Iniciar Docker Desktop
- En Linux: `sudo systemctl start docker`

## 📁 Estructura del Proyecto

### `/docs/architecture`
Diseño de arquitectura de software y diagramas del sistema.
- Arquitectura de alto nivel
- Diagramas PlantUML (Contexto, Secuencia, ER)
- Decisiones arquitectónicas

### `/docs/implementation`
Planes de implementación y reportes de auditoría.
- Plan detallado de desarrollo
- Reportes de verificación y cumplimiento
- Auditorías de calidad

### `/docs/prompts`
Prompts de desarrollo organizados por fase del workflow.
- Metodología de 7 etapas
- Prompts especializados por rol
- Reglas de desarrollo

### `/docs/requirements`
Análisis y especificación de requerimientos del sistema.
- Requerimientos de negocio
- Análisis funcional IEEE 830
- Casos de uso

## 🚀 Inicio Rápido (5 minutos)

### Prerrequisitos
- Java 17+, Maven 3.9+, Docker Desktop
- [Validar ambiente](#-validación-del-ambiente)

### 1. Configurar Variables de Entorno
```bash
# Copiar archivo de configuración
cp .env.example .env
```

### 2. Editar Variables OBLIGATORIAS
```bash
# Abrir .env y cambiar:
DATABASE_PASSWORD=tu_password_seguro
TELEGRAM_BOT_TOKEN=123456789:ABC-DEF1234ghIkl-zyx57W2v1u123ew11
```

### 3. Obtener Token de Telegram
1. Buscar `@BotFather` en Telegram
2. Enviar `/newbot` y seguir instrucciones
3. Copiar token a `TELEGRAM_BOT_TOKEN` en `.env`

### 4. Levantar Sistema
```bash
# PostgreSQL + Aplicación local (recomendado)
docker-compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 5. Validar Funcionamiento
```bash
# Health check
curl http://localhost:8080/actuator/health
# Debe retornar: {"status":"UP"}

# Dashboard
curl http://localhost:8080/api/dashboard/summary
```

**¡Listo!** El sistema está funcionando en http://localhost:8080

---

## ⚙️ Variables de Entorno

**Fecha de actualización:** 2025-12-24

### Variables Obligatorias

| Variable | Descripción | Ejemplo | Requerida |
|----------|-------------|---------|----------|
| `DATABASE_PASSWORD` | Contraseña de PostgreSQL | `mi_password_seguro` | ✅ |
| `TELEGRAM_BOT_TOKEN` | Token del Bot de Telegram | `123456:ABC-DEF...` | ✅ |

### Variables de Configuración

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `SERVER_PORT` | Puerto de la aplicación | `8080` |
| `DATABASE_HOST` | Host de PostgreSQL | `localhost` |
| `DATABASE_PORT` | Puerto de PostgreSQL | `5432` |
| `DATABASE_NAME` | Nombre de la base de datos | `ticketero_db` |
| `DATABASE_USER` | Usuario de PostgreSQL | `ticketero_user` |
| `TELEGRAM_DEFAULT_CHAT_ID` | Chat ID por defecto | `5598409030` |
| `SCHEDULER_MESSAGE_RATE` | Intervalo procesamiento mensajes (ms) | `60000` |
| `SCHEDULER_QUEUE_RATE` | Intervalo procesamiento colas (ms) | `5000` |
| `SCHEDULER_THREAD_POOL_SIZE` | Threads para schedulers | `2` |
| `TELEGRAM_TIMEOUT` | Timeout Telegram API (ms) | `30000` |
| `AUDIT_RETENTION_DAYS` | Días retención auditoría | `2555` |
| `NO_SHOW_TIMEOUT` | Timeout no-show (minutos) | `5` |
| `MAX_CONCURRENT_TICKETS` | Tickets máximos por asesor | `3` |

### Configuración Paso a Paso

#### 1. Copiar archivo de ejemplo
```bash
cp .env.example .env
```

#### 2. Editar variables obligatorias
```bash
# Editar con tu editor preferido
nano .env
# o
code .env
```

#### 3. Configurar credenciales mínimas
```bash
# En el archivo .env, cambiar:
DATABASE_PASSWORD=tu_password_seguro
TELEGRAM_BOT_TOKEN=tu_bot_token_de_botfather
```

#### 4. Levantar con variables
```bash
# Solo PostgreSQL
docker-compose --env-file .env up -d postgres

# Sistema completo
docker-compose --env-file .env --profile full up -d
```

### Perfiles de Configuración

| Perfil | Descripción | Uso |
|--------|-------------|-----|
| `dev` | PostgreSQL local | Desarrollo con Docker PostgreSQL |
| `docker` | PostgreSQL container | Despliegue completo con Docker |
| `quiet` | Schedulers lentos | Testing sin logs frecuentes |
| `test` | H2 en memoria | Pruebas unitarias |

### Obtener Token de Telegram

1. Buscar `@BotFather` en Telegram
2. Enviar `/newbot`
3. Seguir instrucciones
4. Copiar token generado a `TELEGRAM_BOT_TOKEN`

### 🚨 Troubleshooting Rápido

| Error | Causa | Solución |
|-------|-------|----------|
| `Failed to configure a DataSource` | PostgreSQL no iniciado | `docker-compose up -d postgres` |
| `Telegram API 401 Unauthorized` | Token inválido | Verificar `TELEGRAM_BOT_TOKEN` en `.env` |
| `Port 8080 already in use` | Puerto ocupado | Cambiar `SERVER_PORT=8081` en `.env` |
| `Connection refused` | Docker no iniciado | Iniciar Docker Desktop |

### Validación de Variables

```bash
# Verificar configuración
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Verificar health check
curl http://localhost:8080/actuator/health
```

## 🚀 Despliegue con Docker Compose

### Prerrequisitos
- Docker Desktop instalado y ejecutándose
- 2GB RAM disponible
- Puertos 8080 y 5432 libres

### ✅ DESPLIEGUE VALIDADO - Sistema Completo

**Estado:** Completamente funcional  
**Tiempo de inicialización:** < 2 minutos  
**Servicios:** PostgreSQL + Spring Boot API  
**Última validación:** 2025-12-24

### Opción 1: Solo PostgreSQL (Recomendado para Desarrollo)

```bash
# 1. Crear archivo de variables de entorno
cp .env.example .env
# Editar .env con tus configuraciones si es necesario

# 2. Levantar PostgreSQL
docker-compose up -d postgres

# 3. Ejecutar aplicación localmente
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 4. Verificar funcionamiento
curl http://localhost:8080/actuator/health
curl http://localhost:8080/api/dashboard/summary
```

### Opción 2: Sistema Completo con Docker (✅ VALIDADO)

```bash
# 1. Configurar variables de entorno
cp .env.example .env

# 2. Levantar todos los servicios
docker-compose --profile full up -d

# 3. Esperar inicialización (30-45 segundos)
# PostgreSQL: ~30 segundos
# Aplicación: ~45 segundos adicionales

# 4. Verificar estado
docker-compose ps

# 5. Ver logs
docker-compose logs -f
```

### Comandos de Gestión

```bash
# Ver estado de servicios
docker-compose ps

# Ver logs específicos
docker-compose logs -f postgres
docker-compose logs -f ticketero-app

# Detener servicios
docker-compose down

# Detener y eliminar datos (⚠️ ELIMINA DATOS)
docker-compose down -v

# Reiniciar servicio específico
docker-compose restart postgres
```

### ✅ Validación del Despliegue

| Endpoint | Estado | Resultado Esperado |
|----------|--------|--------------------|  
| `http://localhost:8080/actuator/health` | ✅ VALIDADO | `{"status":"UP"}` |
| `http://localhost:8080/api/dashboard/summary` | ✅ VALIDADO | JSON con métricas del sistema |
| `http://localhost:8080/api/queues/stats` | ✅ VALIDADO | `{"avgWaitTime":15,"totalQueues":4,"activeTickets":0}` |

**Servicios Funcionando:**
- ✅ PostgreSQL 15: Saludable y respondiendo
- ✅ Ticketero API: Iniciada correctamente
- ✅ Base de Datos: 5 tablas creadas por Flyway
- ✅ Health Checks: Todos los endpoints UP
- ✅ Schedulers: Funcionando según especificación (5s/60s)

### Troubleshooting

**PostgreSQL no inicia:**
```bash
# Verificar logs
docker-compose logs postgres

# Limpiar y reiniciar
docker-compose down -v
docker-compose up -d postgres
```

**Puerto 5432 ocupado:**
```bash
# Cambiar puerto en docker-compose.yml
ports:
  - "5433:5432"  # Usar puerto 5433
```

**Tablas no creadas:**
- El sistema usa Flyway para crear tablas automáticamente
- Si hay problemas, verificar logs: `docker-compose logs ticketero-app`
- Las migraciones están en: `src/main/resources/db/migration/`

### Configuraciones Disponibles

- **`application.yml`**: Configuración por defecto (H2 en memoria)
- **`application-dev.yml`**: PostgreSQL local para desarrollo
- **`application-docker.yml`**: PostgreSQL en Docker Compose
- **`application-quiet.yml`**: Desarrollo silencioso con schedulers reducidos

### Notas Importantes

- Los **schedulers** ejecutan consultas cada 5 segundos (colas) y 60 segundos (mensajes)
- Esto es el **comportamiento normal** del sistema según especificaciones
- Para testing silencioso, usar perfil `quiet` con intervalos más largos
- La aplicación crea las tablas automáticamente con Hibernate

## 🚀 Estado del Proyecto

**Fase Actual:** IMPLEMENT (v0.4.x)  
**Progreso:** 3/7 etapas completadas

- ✅ Tasks - Definición de tareas y épicas
- ✅ Brainstorm - Análisis y diseño inicial  
- ✅ Plan - Planificación y roadmap
- 🔄 Implement - Desarrollo de componentes
- ⏳ Verify - Pruebas y validación
- ⏳ Deploy - Configuración y despliegue
- ⏳ Document - Documentación final

## 📋 Consideraciones del Proyecto

### Estructura de Packages
Este proyecto utiliza `com.example.ticketero` como package base por ser un **prototipo de demostración**.

**Para uso productivo:**
1. Refactorizar packages a `com.{empresa}.ticketero`
2. Actualizar `groupId` en `pom.xml`
3. Ajustar imports en todos los archivos Java

### Configuraciones de Desarrollo
- Base de datos: PostgreSQL local con credenciales de desarrollo
- Tokens: Variables de entorno para Telegram Bot API
- Schedulers: Intervalos configurables para testing

## 📚 Cómo Usar

### Para Desarrolladores
1. **Configurar ambiente** → Seguir sección "Requisitos del Ambiente"
2. **Inicio rápido** → Seguir sección "Inicio Rápido"
3. **Revisar requerimientos** → `/docs/requirements/`
4. **Entender arquitectura** → `/docs/architecture/`
5. **Seguir plan de implementación** → `/docs/implementation/`
6. **Usar prompts de desarrollo** → `/docs/prompts/`

### Para DevOps
- **Docker setup** → `/docs/deployment/docker-setup-guide.md`
- **Variables de entorno** → Archivo `.env.example`
- **Configuración** → `src/main/resources/application.yml`

Cada carpeta contiene su propio README con instrucciones específicas.