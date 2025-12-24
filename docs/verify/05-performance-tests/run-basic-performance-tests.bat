@echo off
chcp 65001 >nul 2>&1
echo ========================================
echo PERFORMANCE TESTS - BASICOS
echo Sistema Ticketero - Tests Rapidos
echo Fecha: 2025-12-24
echo ========================================

echo [INFO] Ejecutando tests basicos de performance...
echo.

echo [1/2] API Latency Test (Rapido)...
call scripts\complete-latency-test.bat
if %errorlevel% neq 0 (
    echo [ERROR] Latency Test fallo
    exit /b 1
)

echo.
echo [2/2] API Load Test (Basico)...
call scripts\complete-load-test.bat
if %errorlevel% neq 0 (
    echo [ERROR] Load Test fallo
    exit /b 1
)

echo.
echo ========================================
echo [RESULTADO] PERFORMANCE TESTS BASICOS EXITOSOS
echo [INFO] 2 tests ejecutados: Latency, Load
echo [INFO] Tiempo total: aproximadamente 5 minutos
echo ========================================