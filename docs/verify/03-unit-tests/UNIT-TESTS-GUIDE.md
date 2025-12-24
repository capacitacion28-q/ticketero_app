# Guía de Ejecución - Tests Unitarios

**Sistema Ticketero - Testing Guide v1.0**

---

## EJECUCIÓN BÁSICA

### Comandos Principales
```bash
# Ejecutar todos los unit tests
mvn test -Dtest="*ServiceTest"

# Ejecutar servicio específico
mvn test -Dtest="TicketServiceTest"

# Ejecutar múltiples servicios
mvn test -Dtest="TicketServiceTest,AdvisorServiceTest"

# Generar reporte de cobertura
mvn jacoco:report
```

### Ejecución por Categorías
```bash
# Tests de lógica de negocio crítica
mvn test -Dtest="TicketServiceTest,QueueManagementServiceTest"

# Tests de comunicaciones
mvn test -Dtest="TelegramServiceTest,NotificationServiceTest"

# Tests de auditoría y dashboard
mvn test -Dtest="AuditServiceTest,DashboardServiceTest"
```

---

## ESTRUCTURA DE TESTS

### Organización de Archivos
```
src/test/java/com/example/ticketero/
├── service/           # Tests de servicios (45 tests)
│   ├── TicketServiceTest.java          # 6 tests
│   ├── QueueManagementServiceTest.java # 8 tests
│   ├── AdvisorServiceTest.java         # 7 tests
│   ├── TelegramServiceTest.java        # 6 tests
│   ├── AuditServiceTest.java           # 5 tests
│   ├── DashboardServiceTest.java       # 4 tests
│   ├── NotificationServiceTest.java    # 4 tests
│   └── QueueServiceTest.java           # 5 tests
└── testutil/          # Utilidades de testing
    └── TestDataBuilder.java
```

### Convenciones de Naming
- **Archivos:** `[ServiceName]Test.java`
- **Métodos:** `methodName_condition_expectedBehavior()`
- **Ejemplos:**
  - `createTicket_withValidData_shouldReturnResponse()`
  - `assignTicket_noAdvisorsAvailable_shouldThrowException()`

---

## CONFIGURACIÓN DEL ENTORNO

### Prerrequisitos
- **Java 17** o superior
- **Maven 3.6+**
- **Dependencias:** Incluidas en `spring-boot-starter-test`
  - JUnit 5
  - Mockito
  - AssertJ

### Variables de Entorno
```bash
# No se requieren variables especiales para unit tests
# Los tests son completamente aislados
```

### Configuración Maven
```xml
<!-- Ya incluido en pom.xml -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## TROUBLESHOOTING

### Problemas Comunes

#### Tests Fallan por Dependencias
```bash
# Limpiar y recompilar
mvn clean compile test-compile

# Verificar dependencias
mvn dependency:tree
```

#### Errores de Compilación
```bash
# Verificar versión de Java
java -version

# Compilar solo tests
mvn test-compile
```

#### Tests Lentos
```bash
# Ejecutar en paralelo (si es necesario)
mvn test -T 1C -Dtest="*ServiceTest"
```

### Logs y Debugging
```bash
# Ejecutar con logs detallados
mvn test -Dtest="TicketServiceTest" -X

# Solo mostrar fallos
mvn test -Dtest="*ServiceTest" --fail-fast
```

---

## MÉTRICAS Y REPORTES

### Métricas Objetivo
- **Cobertura:** >80% en servicios
- **Tests:** 45 total
- **Tiempo:** <5 minutos ejecución
- **Éxito:** 100% tests passing

### Generar Reportes
```bash
# Reporte básico de Maven
mvn test -Dtest="*ServiceTest"

# Reporte de cobertura (requiere jacoco plugin)
mvn jacoco:report

# Ver reporte en: target/site/jacoco/index.html
```

### Verificar Cobertura
```bash
# Verificar cobertura mínima
mvn jacoco:check

# Generar reporte XML para CI/CD
mvn jacoco:report -Djacoco.outputDirectory=target/coverage
```

---

## INTEGRACIÓN CON CI/CD

### GitHub Actions
```yaml
- name: Run Unit Tests
  run: mvn test -Dtest="*ServiceTest"

- name: Generate Coverage Report
  run: mvn jacoco:report
```

### Jenkins Pipeline
```groovy
stage('Unit Tests') {
    steps {
        sh 'mvn test -Dtest="*ServiceTest"'
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
    }
}
```

---

## MANTENIMIENTO

### Agregar Nuevos Tests
1. Crear archivo `[NewService]Test.java` en `src/test/java/.../service/`
2. Seguir patrón AAA (Given-When-Then)
3. Usar `TestDataBuilder` para datos de prueba
4. Ejecutar: `mvn test -Dtest="NewServiceTest"`

### Actualizar Tests Existentes
1. Mantener naming convention
2. Preservar cobertura >80%
3. Validar que todos los tests pasen
4. Actualizar `TestDataBuilder` si es necesario

### Mejores Prácticas
- **Aislamiento:** Cada test independiente
- **Velocidad:** Tests rápidos (<100ms cada uno)
- **Claridad:** Nombres descriptivos
- **Mantenibilidad:** Usar builders para datos de prueba

---

## COMANDOS DE REFERENCIA RÁPIDA

```bash
# Ejecución completa
mvn test -Dtest="*ServiceTest"

# Test específico
mvn test -Dtest="TicketServiceTest"

# Con cobertura
mvn test jacoco:report

# Solo compilar tests
mvn test-compile

# Limpiar y ejecutar
mvn clean test -Dtest="*ServiceTest"
```

---

**Última Actualización:** 23 de Diciembre 2024  
**Versión:** 1.0  
**Mantenido por:** Equipo de Desarrollo Sistema Ticketero