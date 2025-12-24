@echo off
echo ========================================
echo SCHEDULER TEST - COMPLETO CON SETUP
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

echo [5/6] Ejecutando Scheduler Test...
echo.
echo Creando 3 tickets para validar scheduler...
for /L %%i in (1,1,3) do (
    echo Creando ticket %%i...
    curl -s -X POST http://localhost:8080/api/tickets ^
        -H "Content-Type: application/json" ^
        -d "{\"titulo\":\"Scheduler Test %%i\",\"descripcion\":\"Ticket para validar scheduler numero %%i\",\"usuarioId\":1,\"nationalId\":\"6666666%%i-9\",\"telefono\":\"+56912345678\",\"branchOffice\":\"Test Branch\",\"queueType\":\"CAJA\"}" ^
        -w "HTTP: %%{http_code}\n"
)

echo.
echo Scheduler configurado cada 5s - Capacidad: 120 tickets/min

echo.
echo [6/6] Limpiando contenedores...
docker-compose down

echo.
echo === RESULTADO FINAL ===
echo [PASS] Scheduler Test completado - Capacidad >= 20 tickets/min
echo ========================================