@echo off
chcp 65001 >nul 2>&1
echo ========================================
echo PERFORMANCE TESTS - SUITE COMPLETA
echo Sistema Ticketero - Todos los Tests
echo Fecha: 2025-12-24
echo ========================================

echo [INFO] Ejecutando 4 tests de performance...
echo.

echo [1/4] API Load Test...
call ..\..\..\scripts\complete-load-test.bat
if %errorlevel% neq 0 (
    echo [ERROR] Load Test fallo
    exit /b 1
)

echo.
echo [2/4] API Latency Test...
call ..\..\..\scripts\complete-latency-test.bat
if %errorlevel% neq 0 (
    echo [ERROR] Latency Test fallo
    exit /b 1
)

echo.
echo [3/4] Scheduler Test...
call ..\..\..\scripts\complete-scheduler-test.bat
if %errorlevel% neq 0 (
    echo [ERROR] Scheduler Test fallo
    exit /b 1
)

echo.
echo [4/4] Telegram Test...
call ..\..\..\scripts\complete-telegram-test.bat
if %errorlevel% neq 0 (
    echo [ERROR] Telegram Test fallo
    exit /b 1
)

echo.
echo ========================================
echo [RESULTADO] TODOS LOS PERFORMANCE TESTS EXITOSOS
echo [INFO] 4 tests ejecutados: Load, Latency, Scheduler, Telegram
echo [INFO] Sistema validado para produccion
echo ========================================