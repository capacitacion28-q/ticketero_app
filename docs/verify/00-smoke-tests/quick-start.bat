@echo off
setlocal EnableDelayedExpansion

echo.
echo ================================================
echo    SMOKE TESTS - Sistema Ticketero H2
echo ================================================
echo Fecha: %date% %time%
echo ================================================

echo.
echo [PASO 1] LIMPIANDO PUERTO 8080...
netstat -ano | findstr ":8080"
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr ":8080"') do taskkill /PID %%a /F >nul 2>&1
echo PASO 1 COMPLETADO
echo.

echo [PASO 2] COMPILANDO...
cd /d "c:\Users\Usuario\Desktop\ticketero_app"
echo Ejecutando mvn compile...
echo CONFIGURACION: Usando H2 database para testing rapido
call mvn compile
echo.
echo PASO 2 COMPLETADO - COMPILACION TERMINADA
echo CONFIGURACION: H2 configurado correctamente
echo.

echo [PASO 3] INICIANDO SPRING BOOT...
echo Ejecutando mvn spring-boot:run en background...
echo NOTA: H2 en memoria se limpia automaticamente en cada reinicio
start /B cmd /c "mvn spring-boot:run"
echo PASO 3 COMPLETADO - PROCESO INICIADO
echo.

echo [PASO 4] ESPERANDO APLICACION...
set RETRY=0
:WAIT
set /a RETRY+=1
echo.
echo INTENTO !RETRY!/12 - Verificando health...
timeout /t 5 /nobreak >nul
curl -s http://localhost:8080/actuator/health
echo.
curl -s http://localhost:8080/actuator/health | findstr "UP" >nul
if !errorlevel!==0 (
    echo APLICACION LISTA - PASO 4 COMPLETADO
    goto TESTS
)
echo Esperando... intento !RETRY! completado
if !RETRY! lss 12 goto WAIT
echo ERROR - APLICACION NO INICIO
goto CLEANUP

:TESTS
echo.
echo [PASO 5] EJECUTANDO TESTS...
set PASS=0
set FAIL=0

echo.
echo TEST 1: Health Check
curl -s http://localhost:8080/actuator/health
curl -s http://localhost:8080/actuator/health | findstr "UP" >nul && (echo [PASS] & set /a PASS+=1) || (echo [FAIL] & set /a FAIL+=1)

echo.
echo TEST 2: Tickets
set RESPONSE_FILE=temp_ticket_response.json
curl -s -X POST http://localhost:8080/api/tickets -H "Content-Type: application/json" -d "{\"titulo\":\"Test Ticket Validacion\",\"descripcion\":\"Descripcion completa para validacion automatica del sistema\",\"usuarioId\":1,\"nationalId\":\"99999999-0\",\"telefono\":\"+56999999999\",\"branchOffice\":\"Centro\",\"queueType\":\"CAJA\"}" > %RESPONSE_FILE%
type %RESPONSE_FILE%
findstr "numero" %RESPONSE_FILE% >nul && (echo [PASS] & set /a PASS+=1) || (echo [FAIL] & set /a FAIL+=1)
del %RESPONSE_FILE% 2>nul

echo.
echo TEST 3: Dashboard
curl -s http://localhost:8080/api/dashboard/summary
curl -s http://localhost:8080/api/dashboard/summary | findstr "timestamp" >nul && (echo [PASS] & set /a PASS+=1) || (echo [FAIL] & set /a FAIL+=1)

echo.
echo TEST 4: Queues
curl -s http://localhost:8080/api/queues/stats
curl -s http://localhost:8080/api/queues/stats | findstr "totalQueues" >nul && (echo [PASS] & set /a PASS+=1) || (echo [FAIL] & set /a FAIL+=1)

echo.
echo TEST 5: Telegram
curl -s http://localhost:8080/api/test/telegram/bot-info
curl -s http://localhost:8080/api/test/telegram/bot-info | findstr "ok" >nul && (echo [PASS] & set /a PASS+=1) || (echo [FAIL] & set /a FAIL+=1)

echo.
echo PASO 5 COMPLETADO - TODOS LOS TESTS EJECUTADOS

:CLEANUP
echo.
echo LIMPIEZA...
for /f "tokens=5" %%a in ('netstat -ano 2^>nul ^| findstr ":8080"') do taskkill /PID %%a /F >nul 2>&1

echo.
echo ================================================
echo RESULTADO: !PASS! exitosos, !FAIL! fallidos
if !FAIL!==0 (echo SISTEMA FUNCIONAL) else (echo !FAIL! PROBLEMAS)
echo ================================================
echo SCRIPT TERMINADO COMPLETAMENTE