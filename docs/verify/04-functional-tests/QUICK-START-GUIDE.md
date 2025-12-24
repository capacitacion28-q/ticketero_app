# Guía de Inicio Rápido - Tests E2E

## 🚀 Ejecución en 3 Pasos

### PASO 1: Verificar Docker (2 minutos)
```bash
# Verificar instalación
docker --version
docker ps

# Si no está instalado:
# 1. Descargar Docker Desktop desde docker.com
# 2. Instalar y reiniciar
# 3. Configurar 4GB+ memoria en Settings
```

### PASO 2: Ejecutar Tests (5 minutos)
```bash
# Navegar al proyecto
cd c:\Users\Usuario\Desktop\ticketero_app

# Ejecutar suite completa E2E
mvn test -Dtest="*IT" -DfailIfNoTests=false

# O ejecutar por feature específica
mvn test -Dtest="TicketCreationIT"
```

### PASO 3: Ver Resultados (1 minuto)
```bash
# Generar reporte HTML
mvn surefire-report:report

# Abrir reporte
start target/site/surefire-report.html
```

## 📊 Resultados Esperados

### ✅ Ejecución Exitosa
```
[INFO] Tests run: 25+, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

### 📈 Cobertura Validada
- **RF-001 a RF-008:** 100% cubiertos
- **RN-001 a RN-013:** 100% validadas
- **Endpoints:** 6 APIs testadas
- **Escenarios:** 25+ casos Gherkin

## 🔧 Troubleshooting

### ❌ Error: Docker no disponible
```
Error: Could not find a valid Docker environment
```
**Solución:** Instalar Docker Desktop y verificar que esté ejecutándose

### ❌ Error: Puerto ocupado
```
Error: Port 8089 is already in use
```
**Solución:** 
```bash
netstat -ano | findstr :8089
taskkill /PID [PID_NUMBER] /F
```

### ❌ Error: Memoria insuficiente
```
Error: Container failed to start
```
**Solución:** Aumentar memoria de Docker a 4GB+ en Settings

## 🎯 Tests Alternativos (Sin Docker)

### Tests Unitarios Solamente
```bash
mvn test -Dtest="*Test" -DfailIfNoTests=false
```

### Smoke Tests Básicos
```bash
mvn test -Dtest="TicketeroApplicationTests"
```

## 📚 Documentación Completa

- **[README.md](README.md)** - Visión general completa
- **[test-execution-report.md](test-execution-report.md)** - Reporte detallado
- **[user-guide.md](user-guide.md)** - Guía completa de uso
- **[gherkin-scenarios.md](gherkin-scenarios.md)** - Escenarios de negocio

---

**Tiempo total estimado:** 8 minutos  
**Prerrequisito:** Docker Desktop instalado  
**Resultado:** Suite E2E completa ejecutada y validada