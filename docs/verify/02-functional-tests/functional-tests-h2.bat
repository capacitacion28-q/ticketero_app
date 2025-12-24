@echo off
chcp 65001 >nul 2>&1
echo ========================================
echo FUNCTIONAL TESTS H2 - Sistema Ticketero
echo Fecha: 2025-12-24
echo ========================================

echo [INFO] Validando precondiciones H2...

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Java no encontrado
    exit /b 1
)
echo [OK] Java disponible

mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Maven no encontrado
    exit /b 1
)
echo [OK] Maven disponible

netstat -an | findstr ":8080" >nul
if %errorlevel% equ 0 (
    echo [ERROR] Puerto 8080 ocupado
    exit /b 1
)
echo [OK] Puerto 8080 libre

echo.
echo [INFO] Ejecutando tests funcionales H2...
mvn test -Dtest=DashboardH2IT,TicketCreationH2IT > docs\verify\reports\functional-tests-h2.txt 2>&1

if %errorlevel% equ 0 (
    echo [RESULTADO] TESTS H2 EXITOSOS
) else (
    echo [RESULTADO] TESTS H2 FALLARON
    exit /b 1
)