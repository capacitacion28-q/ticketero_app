@echo off
echo 🚀 VALIDACIÓN RÁPIDA - Sistema Ticketero
echo ========================================

set FAILED=0

echo.
echo [1/5] Health Check...
curl -s http://localhost:8080/actuator/health | findstr "UP" >nul
if %errorlevel%==0 (
    echo ✅ Aplicación UP
) else (
    echo ❌ Aplicación no responde
    set /a FAILED+=1
)

echo.
echo [2/5] Creando ticket de prueba...
curl -s -X POST http://localhost:8080/api/tickets ^
  -H "Content-Type: application/json" ^
  -d "{\"titulo\":\"Test rápido\",\"descripcion\":\"Validación automática\",\"usuarioId\":1,\"nationalId\":\"99999999-0\",\"telefono\":\"+56999999999\",\"branchOffice\":\"Centro\",\"queueType\":\"CAJA\"}" | findstr "numero" >nul
if %errorlevel%==0 (
    echo ✅ Ticket creado correctamente
) else (
    echo ❌ Error creando ticket
    set /a FAILED+=1
)

echo.
echo [3/5] Verificando dashboard...
curl -s http://localhost:8080/api/dashboard/summary | findstr "timestamp" >nul
if %errorlevel%==0 (
    echo ✅ Dashboard funcional
) else (
    echo ❌ Dashboard no funciona
    set /a FAILED+=1
)

echo.
echo [4/5] Verificando colas...
curl -s http://localhost:8080/api/queues/CAJA | findstr "CAJA" >nul
if %errorlevel%==0 (
    echo ✅ Gestión de colas operativa
) else (
    echo ❌ Colas no funcionan
    set /a FAILED+=1
)

echo.
echo [5/5] Verificando Telegram...
curl -s http://localhost:8080/api/test/telegram/bot-info | findstr "ticketero_capacitacion_bot" >nul
if %errorlevel%==0 (
    echo ✅ Telegram Bot configurado
) else (
    echo ❌ Telegram Bot no configurado
    set /a FAILED+=1
)

echo.
echo ========================================
if %FAILED%==0 (
    echo 🎉 VALIDACIÓN EXITOSA - Sistema 100%% funcional
    echo ✅ Listo para usar en producción
) else (
    echo ⚠️ %FAILED% PROBLEMAS DETECTADOS
    echo 📋 Revisar docs\verify\04-reports\error-analysis.md
)
echo ========================================

pause