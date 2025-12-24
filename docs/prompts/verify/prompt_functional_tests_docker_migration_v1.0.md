# PROMPT: Migración Tests Funcionales a Docker Compose

**Fecha:** 2025-12-24  
**Versión:** v1.0  
**Especialización:** QA Engineer Senior - Docker Integration

---

## CONTEXTO

Eres un QA Engineer Senior especializado en migración de tests funcionales para ejecutarse contra aplicaciones containerizadas.

**SITUACIÓN ACTUAL:**
- ✅ Tests E2E funcionales ya creados en `src/test/java/com/example/ticketero/`
- ✅ Documentación completa en `docs/verify/04-functional-tests/`
- ⚠️ Tests configurados para ejecución local (necesita migración)
- 🎯 **OBJETIVO:** Migrar tests para ejecutarse contra app en docker-compose

**DOCUMENTACIÓN DE REFERENCIA:**
- `docs/verify/04-functional-tests/` - Tests existentes y documentación
- `docs/deployment/` - Guías de Docker Compose
- `src/test/java/com/example/ticketero/` - Código de tests actual
- `docs/prompts/verify/prompt_testing_e2e_funcional_v1.0.md` - Referencia original

**PRINCIPIO:** Analizar → Adaptar → Validar → Documentar

---

## METODOLOGÍA OBLIGATORIA

**Proceso:**
1. **Analizar:** `fsRead` tests existentes y configuración actual
2. **Adaptar:** Modificar configuración para Docker Compose
3. **Validar:** Ejecutar tests contra app containerizada
4. **Documentar:** Actualizar guías de ejecución

**REGLA CRÍTICA:** Tests DEBEN ejecutarse contra app desplegada con docker-compose, NO localmente.

---

## TAREA ESPECÍFICA

### **PASO 1: Análisis de Situación Actual**
```bash
# Analizar tests existentes
fsRead src/test/java/com/example/ticketero/
listDirectory src/test/java/com/example/ticketero/

# Analizar documentación actual
fsRead docs/verify/04-functional-tests/

# Analizar configuración de deployment
fsRead docs/deployment/
```

### **PASO 2: Identificar Cambios Necesarios**
Evaluar:
- **URLs de conexión:** localhost vs contenedores Docker
- **Configuración de BD:** TestContainers vs PostgreSQL en Docker
- **Timeouts:** Ajustar para inicialización de contenedores
- **Health checks:** Validar app completamente levantada
- **Cleanup:** Gestión de estado entre tests

### **PASO 3: Adaptar Configuración**
Modificar:
- **application-test.yml:** URLs y configuración para Docker
- **BaseIntegrationTest.java:** Setup para Docker Compose
- **Test classes:** Ajustar timeouts y validaciones
- **Scripts de ejecución:** Comandos para levantar/bajar Docker

### **PASO 4: Validar Migración**
```bash
# Levantar app con Docker Compose
docker-compose up -d

# Ejecutar tests migrados
mvn test -Dtest="*IT"

# Validar resultados
docker-compose down
```

---

## IMPLEMENTACIÓN ESPECÍFICA

### **CONFIGURACIÓN DOCKER COMPOSE**
```yaml
# application-test-docker.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/ticketero_db
    username: ticketero_user
    password: ticketero_pass
  jpa:
    hibernate:
      ddl-auto: validate
telegram:
  api-url: http://localhost:8089/bot  # WireMock
server:
  port: 8080
```

### **BASE TEST ADAPTADO**
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test-docker")
@TestMethodOrder(OrderAnnotation.class)
public abstract class BaseDockerIntegrationTest {
    
    @BeforeAll
    static void setupDocker() {
        // Validar que Docker Compose esté corriendo
        waitForApplicationReady();
    }
    
    @AfterAll
    static void cleanupDocker() {
        // Cleanup específico si es necesario
    }
}
```

### **SCRIPT DE EJECUCIÓN**
```bash
#!/bin/bash
# run-functional-tests.sh

echo "🐳 Levantando aplicación con Docker Compose..."
docker-compose up -d

echo "⏳ Esperando inicialización completa..."
sleep 30

echo "🔍 Validando que la app esté lista..."
curl -f http://localhost:8080/actuator/health || exit 1

echo "🧪 Ejecutando tests funcionales..."
mvn test -Dtest="*IT" -Dspring.profiles.active=test-docker

echo "🛑 Bajando contenedores..."
docker-compose down

echo "✅ Tests completados"
```

---

## ENTREGABLES

### **1. Configuración Migrada**
- `src/test/resources/application-test-docker.yml`
- `BaseDockerIntegrationTest.java` actualizado
- Scripts de ejecución con Docker Compose

### **2. Tests Adaptados**
- Todas las clases `*IT.java` funcionando con Docker
- Timeouts ajustados para contenedores
- Health checks apropiados

### **3. Documentación Actualizada**
- `docs/verify/04-functional-tests/docker-execution-guide.md`
- Comandos actualizados en guías existentes
- Troubleshooting para Docker Compose

---

## FORMATO DE CONFIRMACIÓN

```
✅ MIGRACIÓN COMPLETADA
Tests analizados: [cantidad]
Configuración adaptada: SÍ/NO
Ejecución exitosa con Docker: SÍ/NO
Documentación actualizada: SÍ/NO

¿La migración es exitosa?
```

---

**Prompt creado por:** Ingeniero de Prompts Senior  
**Especialización:** QA Engineer Senior - Docker Integration  
**Fecha de creación:** 2025-12-24  
**Versión:** v1.0