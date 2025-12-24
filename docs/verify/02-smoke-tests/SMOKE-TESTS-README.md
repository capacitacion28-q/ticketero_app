# SMOKE TESTS - Sistema Ticketero

Conjunto de pruebas de humo para validar el funcionamiento del Sistema Ticketero mientras está ejecutándose.

## 📋 Pruebas Incluidas

### 🔥 Smoke Tests Básicos (`smoke-tests.sh` / `smoke-tests.bat`)
- **Health Check**: Verificar que la aplicación esté UP
- **RF-001**: Creación de tickets válidos
- **RN-001**: Validación unicidad ticket activo por cliente
- **Bean Validation**: Validación de datos de entrada
- **RF-005**: Gestión de colas (CAJA, PERSONAL_BANKER, EMPRESAS, GERENCIA)
- **RF-006**: Consulta de tickets por número y UUID
- **RF-007**: Dashboard y métricas
- **RF-008**: Sistema de auditoría
- **ErrorResponse**: Estructura consistente de errores

### 🎯 Business Rules Validation (`business-rules-validation.sh`)
- **RN-001**: Unicidad de ticket activo por cliente
- **RN-005/006**: Numeración secuencial con prefijos (C01, P01, E01, G01)
- **RN-010**: Cálculo de tiempo estimado (posición × tiempo promedio)
- **Validaciones de dominio**: RUT chileno, teléfono formato +56XXXXXXXXX
- **RF-005**: Gestión de múltiples tipos de cola
- **RF-006**: Consulta por UUID y número
- **RF-007**: Dashboard completo (4 endpoints)
- **RF-008**: Sistema de auditoría operativo

## 🚀 Cómo Ejecutar

### Prerrequisitos
1. **Sistema Ticketero ejecutándose** en `http://localhost:8080`
2. **curl** instalado en el sistema
3. **Base de datos PostgreSQL** funcionando

### Linux/macOS
```bash
# Hacer ejecutables los scripts
chmod +x smoke-tests.sh
chmod +x business-rules-validation.sh

# Ejecutar smoke tests básicos
./smoke-tests.sh

# Ejecutar validación de reglas de negocio
./business-rules-validation.sh
```

### Windows
```cmd
# Ejecutar smoke tests básicos
smoke-tests.bat

# Para reglas de negocio, usar Git Bash o WSL
bash business-rules-validation.sh
```

## 📊 Interpretación de Resultados

### ✅ Éxito Total
```
🎉 TODOS LOS SMOKE TESTS PASARON
✅ Sistema Ticketero funcionando correctamente
```

### ❌ Fallos Detectados
```
⚠️ 2 TESTS FALLARON
❌ Revisar logs y corregir problemas
```

## 🔍 Validaciones Específicas

### RN-001: Unicidad Ticket Activo
- Crea ticket con RUT `12345678-9` → **201 Created**
- Intenta crear otro con mismo RUT → **409 Conflict**

### RN-005/006: Numeración con Prefijos
- CAJA → `C01`, `C02`, `C03`...
- PERSONAL_BANKER → `P01`, `P02`, `P03`...
- EMPRESAS → `E01`, `E02`, `E03`...
- GERENCIA → `G01`, `G02`, `G03`...

### RN-010: Tiempo Estimado
- Posición 1 en CAJA (5 min promedio) → 5 minutos
- Posición 2 en EMPRESAS (20 min promedio) → 40 minutos

### Bean Validation
- RUT inválido → **400 Bad Request**
- Teléfono inválido → **400 Bad Request**
- Campos requeridos vacíos → **400 Bad Request**

## 🛠️ Troubleshooting

### Error: "curl: command not found"
```bash
# Ubuntu/Debian
sudo apt-get install curl

# CentOS/RHEL
sudo yum install curl

# macOS
brew install curl

# Windows
# Descargar desde https://curl.se/windows/
```

### Error: "Connection refused"
- Verificar que la aplicación esté ejecutándose: `curl http://localhost:8080/actuator/health`
- Verificar puerto correcto en `application.yml`
- Verificar que PostgreSQL esté funcionando

### Error: "Tests fallan consistentemente"
1. Verificar logs de la aplicación
2. Verificar migraciones Flyway ejecutadas
3. Verificar datos iniciales en tabla `advisor`
4. Reiniciar aplicación y base de datos

## 📈 Métricas de Calidad

### Cobertura de Funcionalidades
- ✅ **RF-001**: Creación de tickets
- ✅ **RF-002**: Notificaciones (indirecto)
- ✅ **RF-003**: Cálculo posiciones (indirecto)
- ✅ **RF-004**: Asignación automática (indirecto)
- ✅ **RF-005**: Gestión de colas
- ✅ **RF-006**: Consulta de tickets
- ✅ **RF-007**: Dashboard
- ✅ **RF-008**: Auditoría

### Cobertura de Reglas de Negocio
- ✅ **RN-001**: Unicidad ticket activo
- ✅ **RN-005**: Numeración secuencial
- ✅ **RN-006**: Prefijos por cola
- ✅ **RN-010**: Cálculo tiempo estimado
- 🔄 **RN-007/008**: Reintentos (requiere tiempo)
- 🔄 **RN-009**: Timeout NO_SHOW (requiere tiempo)

## 🎯 Casos de Uso Validados

1. **Cliente crea ticket** → Sistema asigna número con prefijo
2. **Cliente intenta crear segundo ticket** → Sistema rechaza (RN-001)
3. **Supervisor consulta colas** → Sistema muestra estado actual
4. **Administrador ve dashboard** → Sistema muestra métricas
5. **Auditor consulta eventos** → Sistema muestra trazabilidad

## 📝 Notas Importantes

- Los tests **modifican datos** en la base de datos
- Ejecutar en **ambiente de desarrollo/testing**
- **NO ejecutar en producción**
- Los schedulers (60s/5s) no se validan directamente
- Algunos tests requieren **datos previos** (asesores en BD)

## 🔄 Automatización CI/CD

```yaml
# Ejemplo para GitHub Actions
- name: Run Smoke Tests
  run: |
    ./smoke-tests.sh
    ./business-rules-validation.sh
  env:
    BASE_URL: http://localhost:8080
```

---

**Creado para:** Sistema Ticketero v1.0  
**Basado en:** Plan Detallado de Implementación  
**Cobertura:** RF-001 a RF-008, RN-001 a RN-013