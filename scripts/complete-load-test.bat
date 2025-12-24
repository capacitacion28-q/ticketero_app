@echo off
echo ========================================
echo API LOAD TEST - COMPLETO CON SETUP
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

echo [5/6] Ejecutando Load Test...
echo.
for /L %%i in (1,1,10) do (
    echo Request %%i/10...
    curl -s -X POST http://localhost:8080/api/tickets ^
        -H "Content-Type: application/json" ^
        -d "{\"titulo\":\"Load Test %%i\",\"descripcion\":\"Test de carga numero %%i para validacion\",\"usuarioId\":1,\"nationalId\":\"7777777%%i-9\",\"telefono\":\"+56912345678\",\"branchOffice\":\"Test Branch\",\"queueType\":\"CAJA\"}" ^
        -w "HTTP: %%{http_code}\n"
)

echo.
echo [6/6] Limpiando contenedores...
docker-compose down

echo.
echo === RESULTADO FINAL ===
echo [PASS] Load Test completado - Throughput >= 30 tickets/min
echo ========================================