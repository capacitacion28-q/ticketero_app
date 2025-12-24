@echo off
chcp 65001 >nul 2>&1
echo ========================================
echo TODOS LOS TESTS FUNCIONALES
echo Sistema Ticketero - Suite Completa
echo Fecha: 2025-12-24
echo ========================================

echo [INFO] Ejecutando 13 tests funcionales...
echo.

mvn test -Dtest="DashboardDockerComposeIT,DashboardH2IT,TicketCreationH2IT,H2ConfigurationValidationIT,AdminDashboardE2ETest"

set TEST_RESULT=%errorlevel%

echo.
if %TEST_RESULT% equ 0 (
    echo [RESULTADO] TODOS LOS TESTS FUNCIONALES EXITOSOS
    echo [INFO] 13 tests ejecutados correctamente
    echo [INFO] Cobertura: Dashboard, Tickets, H2, Docker Compose, RestAssured
) else (
    echo [RESULTADO] ALGUNOS TESTS FALLARON
    exit /b 1
)

echo.
echo ========================================
echo Suite completa ejecutada: 2025-12-24
echo ========================================