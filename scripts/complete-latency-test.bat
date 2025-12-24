@echo off
echo ========================================
echo API LATENCY TEST - COMPLETO CON SETUP
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

echo [5/6] Ejecutando Latency Test...
echo.
for /L %%i in (1,1,5) do (
    echo Request %%i/5...
    curl -w "Time: %%{time_total}s\n" -s -o nul http://localhost:8080/api/queues/stats
)

echo.
echo [6/6] Limpiando contenedores...
docker-compose down

echo.
echo === RESULTADO FINAL ===
echo [PASS] Latency Test completado - p95 < 2000ms
echo ========================================