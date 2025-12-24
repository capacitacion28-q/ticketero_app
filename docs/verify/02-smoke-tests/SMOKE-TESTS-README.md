# SMOKE TESTS FUNCIONALES - Sistema Ticketero

**Validación funcional completa basada en pruebas exitosas del 2025-12-24**

---

## 📋 RESUMEN

Este conjunto de smoke tests ha sido actualizado con **validación funcional real** basada en las pruebas manuales exitosas. Incluye todos los campos obligatorios y valida las funcionalidades principales del sistema.

### Estado Actual
- ✅ **8 tests funcionales validados**
- ✅ **Campos obligatorios incluidos** (titulo, descripcion, usuarioId)
- ✅ **Reglas de negocio verificadas** (RN-001, RN-005/006)
- ✅ **APIs principales funcionando**

---

## 📁 CONTENIDO ACTUALIZADO

### Archivos Principales

#### [`smoke-tests.bat`](./smoke-tests.bat) - **ACTUALIZADO**
**Script funcional para Windows**
- Tests con campos obligatorios completos
- Validación RN-001 (unicidad de tickets)
- Verificación de numeración con prefijos
- Consulta de tickets por número
- Dashboard y gestión de colas

#### [`smoke-tests.sh`](./smoke-tests.sh) - **ACTUALIZADO**
**Script funcional para Linux/macOS**
- Misma funcionalidad que la versión Windows
- Colores mejorados para mejor visualización
- Manejo de errores más robusto

#### [`business-rules-validation.sh`](./business-rules-validation.sh)
**Validación específica de reglas de negocio**
- Mantiene validación detallada de RN-001 a RN-013
- Complementa los smoke tests básicos

---

## 🚀 INICIO RÁPIDO

### Ejecutar Tests Funcionales
```bash
# Windows
smoke-tests.bat

# Linux/macOS (requiere bash)
bash smoke-tests.sh
```

### Verificar Resultados
```bash
# Debe mostrar: 8/8 tests exitosos
# Tiempo esperado: ~10-15 segundos
```

---

## 📊 TESTS INCLUIDOS

| Test | Funcionalidad | Status |
|------|---------------|--------|
| **TEST 1** | Health Check | ✅ Validado |
| **TEST 2** | RF-001: Crear ticket completo | ✅ Validado |
| **TEST 3** | RN-001: Unicidad de tickets | ✅ Validado |
| **TEST 4** | RF-006: Consulta por número | ✅ Validado |
| **TEST 5** | RF-005: Gestión de colas | ✅ Validado |
| **TEST 6** | RF-007: Dashboard | ✅ Validado |
| **TEST 7** | RN-005/006: Prefijos | ✅ Validado |
| **TEST 8** | Schedulers automáticos | ✅ Validado |

---

## 🔍 MEJORAS IMPLEMENTADAS

### ✅ Campos Obligatorios Incluidos
```json
{
  "titulo": "Test funcional",
  "descripcion": "Validacion smoke test", 
  "usuarioId": 1,
  "nationalId": "12345678-9",
  "telefono": "+56987654321",
  "branchOffice": "Centro",
  "queueType": "CAJA"
}
```

### ✅ Validación de Reglas de Negocio
- **RN-001:** Unicidad de ticket activo por cliente
- **RN-005/006:** Numeración con prefijos (C, P, E, G)
- **RF-006:** Consulta de tickets por número

### ✅ Verificación de Funcionalidades Core
- Creación de tickets completa
- Dashboard operativo
- Gestión de colas
- Schedulers funcionando

---

## 📈 RESULTADOS ESPERADOS

### ✅ Ejecución Exitosa
```
🚀 SMOKE TESTS FUNCIONALES - Sistema Ticketero
==================================================
[TEST 1] ✅ PASS: Aplicación está UP y funcionando
[TEST 2] ✅ PASS: Ticket creado correctamente con campos obligatorios
[TEST 3] ✅ PASS: RN-001 validación unicidad funciona
[TEST 4] ✅ PASS: Consulta por número funciona
[TEST 5] ✅ PASS: Gestión de colas funciona
[TEST 6] ✅ PASS: Dashboard funciona correctamente
[TEST 7] ✅ PASS: Numeración con prefijos funciona
[TEST 8] ✅ PASS: Schedulers procesando correctamente
==================================================
🎉 TODOS LOS SMOKE TESTS FUNCIONALES PASARON
✅ Sistema Ticketero funcionando correctamente
✅ Listo para uso en producción
```

---

## 🛠️ TROUBLESHOOTING

### ❌ Error: Aplicación no responde
```
❌ FAIL: Aplicación no responde correctamente
```
**Solución:** Verificar que la aplicación esté ejecutándose en puerto 8080

### ❌ Error: Campos obligatorios
```
❌ FAIL: Error creando ticket con campos obligatorios
```
**Solución:** Los tests ahora incluyen todos los campos requeridos

### ❌ Error: RN-001 no funciona
```
❌ FAIL: RN-001 no está funcionando
```
**Solución:** Verificar que la validación de unicidad esté implementada

---

## 🎯 DIFERENCIAS CON VERSIÓN ANTERIOR

### ❌ Versión Anterior (Problemática)
- Faltaban campos obligatorios (titulo, descripcion, usuarioId)
- Tests fallaban con error 400
- No validaba funcionalidades reales

### ✅ Versión Actual (Funcional)
- Incluye todos los campos obligatorios
- Tests pasan exitosamente
- Valida funcionalidades reales del sistema
- Basado en pruebas manuales exitosas

---

## 📞 SOPORTE

### Para Desarrolladores
- Usar `smoke-tests.bat` en Windows
- Usar `bash smoke-tests.sh` en Linux/macOS
- Verificar que la aplicación esté en puerto 8080

### Para QA/Testing
- Ejecutar antes de cada release
- Validar que todos los 8 tests pasen
- Tiempo de ejecución: ~10-15 segundos

---

**Última Actualización:** 24 de Diciembre 2024  
**Versión:** 2.0 (Funcional)  
**Estado:** ✅ Validado y Funcional