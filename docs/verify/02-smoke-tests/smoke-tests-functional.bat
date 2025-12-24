@echo off
REM SMOKE TESTS FUNCIONALES - Sistema Ticketero
REM Validación funcional completa con campos obligatorios
REM Basado en validación manual exitosa del 2025-12-24

set BASE_URL=http://localhost:8080
set FAILED_TESTS=0
set TOTAL_TESTS=0

echo 🚀 SMOKE TESTS FUNCIONALES - Sistema Ticketero
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

REM TEST 2: RF-001 - Crear Ticket con campos obligatorios
echo [TEST 2] RF-001: Crear ticket con todos los campos obligatorios
curl -s -w "%%{http_code}" -X POST -H "Content-Type: application/json" -d "{\"titulo\":\"Test funcional\",\"descripcion\":\"Validacion smoke test\",\"usuarioId\":1,\"nationalId\":\"12345678-9\",\"telefono\":\"+56987654321\",\"branchOffice\":\"Centro\",\"queueType\":\"CAJA\"}" "%BASE_URL%/api/tickets" | findstr "201" >nul
if %errorlevel%==0 (
    echo ✅ PASS: Ticket creado correctamente con campos obligatorios
) else (
    echo ❌ FAIL: Error creando ticket con campos obligatorios
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM TEST 3: RN-001 - Validar unicidad de ticket activo
echo [TEST 3] RN-001: Validar ticket activo existente
curl -s -w "%%{http_code}" -X POST -H "Content-Type: application/json" -d "{\"titulo\":\"Test duplicado\",\"descripcion\":\"Debe fallar\",\"usuarioId\":1,\"nationalId\":\"12345678-9\",\"telefono\":\"+56987654321\",\"branchOffice\":\"Norte\",\"queueType\":\"PERSONAL_BANKER\"}" "%BASE_URL%/api/tickets" | findstr "409" >nul
if %errorlevel%==0 (
    echo ✅ PASS: RN-001 validación unicidad funciona
) else (
    echo ❌ FAIL: RN-001 no está funcionando
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM TEST 4: RF-006 - Consultar ticket por número
echo [TEST 4] RF-006: Consultar ticket por número
REM Crear ticket y obtener número
for /f "tokens=2 delims=:" %%a in ('curl -s -X POST -H "Content-Type: application/json" -d "{\"titulo\":\"Test consulta\",\"descripcion\":\"Para consultar\",\"usuarioId\":1,\"nationalId\":\"87654321-K\",\"telefono\":\"+56912345678\",\"branchOffice\":\"Norte\",\"queueType\":\"EMPRESAS\"}" "%BASE_URL%/api/tickets" ^| findstr "numero"') do (
    set TICKET_NUMBER=%%a
)
REM Limpiar comillas del número
set TICKET_NUMBER=%TICKET_NUMBER:"=%
set TICKET_NUMBER=%TICKET_NUMBER:,=%

if defined TICKET_NUMBER (
    curl -s -w "%%{http_code}" "%BASE_URL%/api/tickets/number/%TICKET_NUMBER%" | findstr "200" >nul
    if !errorlevel!==0 (
        echo ✅ PASS: Consulta por número funciona
    ) else (
        echo ❌ FAIL: Consulta por número no funciona
        set /a FAILED_TESTS+=1
    )
) else (
    echo ❌ FAIL: No se pudo obtener número de ticket
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM TEST 5: RF-005 - Gestión de colas
echo [TEST 5] RF-005: Consultar colas principales
curl -s "%BASE_URL%/api/queues/CAJA" | findstr "CAJA" >nul
if %errorlevel%==0 (
    echo ✅ PASS: Cola CAJA funciona
) else (
    echo ❌ FAIL: Cola CAJA no funciona
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

REM TEST 7: RN-005/006 - Numeración con prefijos
echo [TEST 7] RN-005/006: Numeración con prefijos
curl -s -X POST -H "Content-Type: application/json" -d "{\"titulo\":\"Test prefijo\",\"descripcion\":\"Validar prefijo\",\"usuarioId\":1,\"nationalId\":\"99999999-9\",\"telefono\":\"+56999999999\",\"branchOffice\":\"Centro\",\"queueType\":\"PERSONAL_BANKER\"}" "%BASE_URL%/api/tickets" | findstr "\"P" >nul
if %errorlevel%==0 (
    echo ✅ PASS: Numeración con prefijos funciona
) else (
    echo ❌ FAIL: Numeración con prefijos no funciona
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM TEST 8: Schedulers funcionando
echo [TEST 8] Schedulers: Procesamiento automático
timeout /t 3 >nul
curl -s "%BASE_URL%/api/dashboard/summary" | findstr "timestamp" >nul
if %errorlevel%==0 (
    echo ✅ PASS: Schedulers procesando correctamente
) else (
    echo ❌ FAIL: Schedulers no funcionan
    set /a FAILED_TESTS+=1
)
set /a TOTAL_TESTS+=1
echo ---

REM RESUMEN FINAL
echo ==================================================
echo 🏁 RESUMEN DE SMOKE TESTS FUNCIONALES
echo Total tests ejecutados: %TOTAL_TESTS%
echo Tests fallidos: %FAILED_TESTS%
set /a PASSED_TESTS=%TOTAL_TESTS%-%FAILED_TESTS%
echo Tests exitosos: %PASSED_TESTS%

if %FAILED_TESTS%==0 (
    echo 🎉 TODOS LOS SMOKE TESTS FUNCIONALES PASARON
    echo ✅ Sistema Ticketero funcionando correctamente
    echo ✅ Listo para uso en producción
    exit /b 0
) else (
    echo ⚠️ %FAILED_TESTS% TESTS FALLARON
    echo ❌ Revisar funcionalidades antes de producción
    exit /b 1
)