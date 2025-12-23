@echo off
REM SMOKE TESTS - Sistema Ticketero (Windows)
REM Validación funcional mientras la aplicación está ejecutándose
REM Basado en: docs/implementation/plan_detallado_implementacion_v1.0.md

set BASE_URL=http://localhost:8080
set FAILED_TESTS=0
set TOTAL_TESTS=0

echo 🚀 INICIANDO SMOKE TESTS - Sistema Ticketero
echo Base URL: %BASE_URL%
echo ==================================================

REM TEST 1: Health Check
echo [TEST 1] Health Check - Aplicación funcionando
curl -s "%BASE_URL%/actuator/health" | findstr "UP" >nul
if %errorlevel%==0 (
    echo ✅ PASS: Aplicación está UP y funcionando
) else (
    echo ❌ FAIL: Aplicación no responde correctamente
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM TEST 2: RF-001 - Crear Ticket
echo [TEST 2] RF-001: Crear ticket válido
curl -s -w "%%{http_code}" -X POST -H "Content-Type: application/json" -d "{\"nationalId\":\"12345678-9\",\"telefono\":\"+56987654321\",\"branchOffice\":\"Centro\",\"queueType\":\"CAJA\"}" "%BASE_URL%/api/tickets" | findstr "201" >nul
if %errorlevel%==0 (
    echo ✅ PASS: Ticket creado correctamente
) else (
    echo ❌ FAIL: Error creando ticket
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM TEST 3: RN-001 - Ticket activo existente
echo [TEST 3] RN-001: Validar ticket activo existente
curl -s -w "%%{http_code}" -X POST -H "Content-Type: application/json" -d "{\"nationalId\":\"12345678-9\",\"telefono\":\"+56987654321\",\"branchOffice\":\"Centro\",\"queueType\":\"PERSONAL_BANKER\"}" "%BASE_URL%/api/tickets" | findstr "409" >nul
if %errorlevel%==0 (
    echo ✅ PASS: RN-001 validación unicidad funciona
) else (
    echo ❌ FAIL: RN-001 no está funcionando
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM TEST 4: Bean Validation
echo [TEST 4] Bean Validation: Datos inválidos
curl -s -w "%%{http_code}" -X POST -H "Content-Type: application/json" -d "{\"nationalId\":\"invalid\",\"telefono\":\"invalid\",\"branchOffice\":\"\",\"queueType\":\"INVALID\"}" "%BASE_URL%/api/tickets" | findstr "400" >nul
if %errorlevel%==0 (
    echo ✅ PASS: Bean Validation funciona correctamente
) else (
    echo ❌ FAIL: Bean Validation no funciona
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM TEST 5: RF-005 - Gestión de colas
echo [TEST 5] RF-005: Consultar cola CAJA
curl -s "%BASE_URL%/api/queues/CAJA" | findstr "CAJA" >nul
if %errorlevel%==0 (
    echo ✅ PASS: Gestión de colas funciona
) else (
    echo ❌ FAIL: Gestión de colas no funciona
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM TEST 6: RF-007 - Dashboard
echo [TEST 6] RF-007: Dashboard resumen
curl -s "%BASE_URL%/api/dashboard/summary" | findstr "timestamp" >nul
if %errorlevel%==0 (
    echo ✅ PASS: Dashboard funciona correctamente
) else (
    echo ❌ FAIL: Dashboard no funciona
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM TEST 7: RF-008 - Auditoría
echo [TEST 7] RF-008: Consultar eventos de auditoría
curl -s -w "%%{http_code}" "%BASE_URL%/api/audit/events" | findstr "200" >nul
if %errorlevel%==0 (
    echo ✅ PASS: Auditoría funciona correctamente
) else (
    echo ❌ FAIL: Auditoría no funciona
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM TEST 8: ErrorResponse estructura
echo [TEST 8] Validar estructura ErrorResponse
curl -s "%BASE_URL%/api/tickets/99999999-9999-9999-9999-999999999999" | findstr "timestamp" >nul
if %errorlevel%==0 (
    echo ✅ PASS: ErrorResponse tiene estructura correcta
) else (
    echo ❌ FAIL: ErrorResponse estructura incorrecta
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM RESUMEN FINAL
echo ==================================================
echo 🏁 RESUMEN DE SMOKE TESTS
echo Total tests ejecutados: %TOTAL_TESTS%
echo Tests fallidos: %FAILED_TESTS%
set /a PASSED_TESTS=%TOTAL_TESTS%-%FAILED_TESTS%
echo Tests exitosos: %PASSED_TESTS%

if %FAILED_TESTS%==0 (
    echo 🎉 TODOS LOS SMOKE TESTS PASARON
    echo ✅ Sistema Ticketero funcionando correctamente
    exit /b 0
) else (
    echo ⚠️ %FAILED_TESTS% TESTS FALLARON
    echo ❌ Revisar logs y corregir problemas
    exit /b 1
)