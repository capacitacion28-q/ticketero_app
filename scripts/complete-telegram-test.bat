@echo off
echo ========================================
echo TELEGRAM INTEGRATION TEST - CORREGIDO
echo ========================================
echo.

echo [1/6] Bajando contenedores existentes...
docker-compose down 2>nul

echo [2/6] Levantando aplicacion completa...
docker-compose --profile full up -d
if %errorlevel% neq 0 (
    echo ERROR: No se pudo levantar Docker
    exit /b 1
)

echo [3/6] Esperando inicializacion (30s)...
ping -n 30 127.0.0.1 >nul

echo [4/6] Validando API disponible...
curl -f http://localhost:8080/actuator/health >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: API no disponible
    docker-compose down
    exit /b 1
)

echo [5/6] Ejecutando Telegram Integration Test...
echo.

set /a success=0
set /a total=0

echo Test 1/2: Bot Info (validar token)...
set /a total+=1
curl -s http://localhost:8080/api/test/telegram/bot-info | findstr "ok.*true" >nul
if %errorlevel% equ 0 (
    set /a success+=1
    echo [PASS] Bot token valido
) else (
    echo [FAIL] Bot token invalido
)

echo Test 2/2: Get Updates (validar API)...
set /a total+=1
curl -s http://localhost:8080/api/test/telegram/updates | findstr "ok.*true" >nul
if %errorlevel% equ 0 (
    set /a success+=1
    echo [PASS] API Telegram accesible
) else (
    echo [FAIL] API Telegram no accesible
)

echo.
echo [6/6] Limpiando contenedores...
docker-compose down

echo.
echo === RESULTADO FINAL ===
echo Tests exitosos: %success%/%total%
set /a rate=%success%*100/%total%
echo Success rate: %rate%%%
if %rate% geq 95 (
    echo [PASS] Telegram Integration >= 95%% success rate
) else (
    echo [FAIL] Telegram Integration < 95%% success rate
)
echo ========================================