# Sistema Ticketero Digital

Sistema de gestión de tickets para institución financiera desarrollado con Spring Boot y PostgreSQL.

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

## 🚀 Inicio Rápido

### 1. Clonar el Repositorio
```bash
git clone <repository-url>
cd ticketero_app
```

### 2. Configurar Variables de Entorno
```bash
# Crear archivo .env
cp .env.example .env

# Editar .env con tus configuraciones
TELEGRAM_BOT_TOKEN=tu_token_aqui
DATABASE_PASSWORD=tu_password_aqui
```

### 3. Levantar Base de Datos
```bash
# Solo PostgreSQL (recomendado para desarrollo)
docker-compose up -d postgres
```

### 4. Ejecutar Aplicación
```bash
# Opción 1: Maven (desarrollo)
mvn spring-boot:run

# Opción 2: Docker completo
docker-compose --profile full up -d
```

### 5. Verificar Funcionamiento
```bash
# Health check
curl http://localhost:8080/actuator/health

# Dashboard
curl http://localhost:8080/api/dashboard/summary
```

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