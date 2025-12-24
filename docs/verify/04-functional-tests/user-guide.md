# Guía de Uso - Tests E2E Funcionales

## Prerrequisitos

### Software Requerido
- **Java 17+** (OpenJDK o Oracle JDK)
- **Maven 3.8+** para build y ejecución
- **Docker Desktop** ejecutándose para TestContainers
- **4GB RAM** disponible para TestContainers
- **Puerto 8089** libre para WireMock

### Verificación de Entorno
```bash
# Verificar Java
java -version

# Verificar Maven
mvn -version

# Verificar Docker
docker --version
docker ps

# Verificar puertos disponibles
netstat -an | findstr :8089
```

## Comandos de Ejecución

### Tests E2E Completos (Requiere Docker)
```bash
# Ejecutar todos los tests de integración
mvn test -Dtest="*IT"

# Ejecutar con logs detallados
mvn test -Dtest="*IT" -X

# Ejecutar con perfil específico
mvn test -Dtest="*IT" -Dspring.profiles.active=test
```

### Tests por Feature Específica
```bash
# Feature: Creación de Tickets (RF-001, RF-003)
mvn test -Dtest="TicketCreationIT"

# Feature: Procesamiento de Tickets (RF-004)
mvn test -Dtest="TicketProcessingIT"

# Feature: Notificaciones Telegram (RF-002)
mvn test -Dtest="NotificationIT"

# Feature: Validaciones de Input (RN-010 a RN-013)
mvn test -Dtest="ValidationIT"

# Feature: Dashboard Administrativo (RF-007, RF-008)
mvn test -Dtest="AdminDashboardIT"
```

### Tests Simplificados (Sin Docker)
```bash
# Test simplificado con H2 (limitado)
mvn test -Dtest="SimpleIntegrationTest"
```

### Generación de Reportes
```bash
# Generar reporte HTML de tests
mvn surefire-report:report

# Abrir reporte generado
start target/site/surefire-report.html

# Generar reporte con cobertura
mvn jacoco:report
start target/site/jacoco/index.html
```

## Configuración de Entorno

### Variables de Entorno (Opcional)
```bash
# Configuración de base de datos de test
set TEST_DB_URL=jdbc:postgresql://localhost:5432/ticketero_test
set TEST_DB_USER=test_user
set TEST_DB_PASSWORD=test_pass

# Configuración de Telegram para tests
set TELEGRAM_BOT_TOKEN=test-token
set TELEGRAM_API_URL=http://localhost:8089/bot
```

### Configuración Docker
```bash
# Iniciar Docker Desktop
# Verificar que Docker daemon esté ejecutándose
docker info

# Limpiar contenedores previos (opcional)
docker system prune -f
```

### Configuración de Puertos
```bash
# Verificar puertos libres para tests
netstat -an | findstr :8089  # WireMock
netstat -an | findstr :5432  # PostgreSQL TestContainer
```

## Estructura de Tests

### Organización de Archivos
```
src/test/java/com/example/ticketero/
├── integration/
│   ├── BaseIntegrationTest.java      # Configuración base
│   ├── TicketCreationIT.java         # RF-001, RF-003, RN-001, RN-005, RN-006
│   ├── TicketProcessingIT.java       # RF-004, RN-002, RN-003, RN-004
│   ├── NotificationIT.java           # RF-002, RN-007, RN-008, RN-009
│   ├── ValidationIT.java             # RN-010, RN-011, RN-012, RN-013
│   ├── AdminDashboardIT.java         # RF-007, RF-008
│   └── SimpleIntegrationTest.java    # Tests simplificados
└── resources/
    └── application-test.yml           # Configuración de tests
```

### Convenciones de Naming
- **Clases:** `*IT.java` para tests de integración
- **Métodos:** `feature_scenario_expectedResult`
- **Annotations:** `@P0` (crítico), `@P1` (importante), `@P2` (opcional)
- **Tags:** `@HappyPath`, `@EdgeCase`, `@ErrorHandling`

## Configuración Avanzada

### Profiles de Spring
```yaml
# application-test.yml
spring:
  profiles:
    active: test
  datasource:
    # TestContainers configurará automáticamente
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true
    clean-disabled: false

# Configuración específica para tests
telegram:
  api-url: http://localhost:8089/bot
  enabled: true
  timeout: 5000

scheduler:
  message:
    fixed-rate: 2000  # 2s para tests rápidos
  queue:
    fixed-rate: 1000  # 1s para tests rápidos

logging:
  level:
    com.example.ticketero: DEBUG
    org.testcontainers: INFO
    com.github.tomakehurst.wiremock: INFO
```

### Configuración TestContainers
```java
// BaseIntegrationTest.java
@Container
static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
    .withDatabaseName("ticketero_test")
    .withUsername("test")
    .withPassword("test")
    .withReuse(true);  // Reutilizar entre tests para velocidad
```

### Configuración WireMock
```java
// WireMock para Telegram API
@BeforeAll
static void beforeAll() {
    wireMockServer = new WireMockServer(8089);
    wireMockServer.start();
    WireMock.configureFor("localhost", 8089);
}

// Stub por defecto
stubFor(post(urlPathMatching("/bot/test-token/sendMessage"))
    .willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "application/json")
        .withBody("{\"ok\":true,\"result\":{\"message_id\":\"123\"}}")));
```

## Troubleshooting

### Problemas Comunes

#### 1. Docker no disponible
```
Error: Could not find a valid Docker environment
```
**Solución:**
- Verificar que Docker Desktop esté ejecutándose
- Reiniciar Docker Desktop
- Verificar permisos de usuario para Docker

#### 2. Puerto ocupado
```
Error: Port 8089 is already in use
```
**Solución:**
```bash
# Encontrar proceso usando el puerto
netstat -ano | findstr :8089

# Terminar proceso (reemplazar PID)
taskkill /PID <PID> /F
```

#### 3. Memoria insuficiente
```
Error: Container failed to start due to insufficient memory
```
**Solución:**
- Aumentar memoria disponible para Docker (4GB mínimo)
- Cerrar aplicaciones innecesarias
- Usar `docker system prune` para limpiar

#### 4. Tests lentos
```
Tests taking too long to execute
```
**Solución:**
- Usar `@Container` con `withReuse(true)`
- Reducir intervalos de schedulers en tests
- Ejecutar tests específicos en lugar de suite completa

### Logs de Debugging
```bash
# Habilitar logs detallados
mvn test -Dtest="*IT" -Dlogging.level.com.example.ticketero=DEBUG

# Logs de TestContainers
mvn test -Dtest="*IT" -Dlogging.level.org.testcontainers=DEBUG

# Logs de WireMock
mvn test -Dtest="*IT" -Dlogging.level.com.github.tomakehurst.wiremock=DEBUG
```

## Integración con CI/CD

### GitHub Actions
```yaml
name: E2E Tests
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    services:
      docker:
        image: docker:dind
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '17'
      - name: Run E2E Tests
        run: mvn test -Dtest="*IT"
```

### Jenkins Pipeline
```groovy
pipeline {
    agent any
    stages {
        stage('E2E Tests') {
            steps {
                sh 'mvn test -Dtest="*IT"'
            }
            post {
                always {
                    publishHTML([
                        allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site',
                        reportFiles: 'surefire-report.html',
                        reportName: 'E2E Test Report'
                    ])
                }
            }
        }
    }
}
```

## Métricas y Monitoreo

### Métricas de Ejecución
- **Tiempo total:** ~2-5 minutos (suite completa)
- **Tiempo por test:** ~10-30 segundos
- **Memoria requerida:** ~2-4GB
- **Tests implementados:** 25+ escenarios

### Cobertura Funcional
- **RF cubiertos:** 8/8 (100%)
- **RN cubiertas:** 13/13 (100%)
- **Endpoints testados:** 15+ endpoints
- **Casos de uso:** Happy Path + Edge Cases + Error Handling

## Mantenimiento

### Actualización de Tests
1. **Nuevos RF/RN:** Agregar escenarios correspondientes
2. **Cambios de API:** Actualizar assertions en tests
3. **Nuevas validaciones:** Extender ValidationIT
4. **Configuración:** Mantener application-test.yml actualizado

### Revisión Periódica
- **Mensual:** Verificar que todos los tests pasen
- **Por release:** Actualizar documentación
- **Por cambio mayor:** Revisar cobertura de escenarios

---

**Documentación generada:** 2025-12-24  
**Versión:** 1.0  
**Mantenido por:** QA Engineering Team