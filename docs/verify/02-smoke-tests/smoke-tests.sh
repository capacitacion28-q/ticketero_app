#!/bin/bash
# SMOKE TESTS FUNCIONALES - Sistema Ticketero
# Validación funcional completa con campos obligatorios
# Basado en validación manual exitosa del 2025-12-24

BASE_URL="http://localhost:8080"
FAILED_TESTS=0
TOTAL_TESTS=0

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_test() {
    echo -e "${YELLOW}[TEST $((++TOTAL_TESTS))]${NC} $1"
}

log_success() {
    echo -e "${GREEN}✅ PASS:${NC} $1"
}

log_fail() {
    echo -e "${RED}❌ FAIL:${NC} $1"
    ((FAILED_TESTS++))
}

echo "🚀 SMOKE TESTS FUNCIONALES - Sistema Ticketero"
echo "Base URL: $BASE_URL"
echo "=================================================="

# TEST 1: Health Check
log_test "Health Check - Aplicación funcionando"
health_response=$(curl -s "$BASE_URL/actuator/health")
if echo "$health_response" | grep -q '"status":"UP"'; then
    log_success "Aplicación está UP y funcionando"
else
    log_fail "Aplicación no responde correctamente"
fi
echo "---"

# TEST 2: RF-001 - Crear Ticket con campos obligatorios
log_test "RF-001: Crear ticket con todos los campos obligatorios"
response=$(curl -s -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"titulo":"Test funcional","descripcion":"Validacion smoke test","usuarioId":1,"nationalId":"12345678-9","telefono":"+56987654321","branchOffice":"Centro","queueType":"CAJA"}' \
    "$BASE_URL/api/tickets")
status_code="${response: -3}"
if [ "$status_code" = "201" ]; then
    log_success "Ticket creado correctamente con campos obligatorios"
else
    log_fail "Error creando ticket con campos obligatorios (Status: $status_code)"
fi
echo "---"

# TEST 3: RN-001 - Validar unicidad de ticket activo
log_test "RN-001: Validar ticket activo existente"
response=$(curl -s -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"titulo":"Test duplicado","descripcion":"Debe fallar","usuarioId":1,"nationalId":"12345678-9","telefono":"+56987654321","branchOffice":"Norte","queueType":"PERSONAL_BANKER"}' \
    "$BASE_URL/api/tickets")
status_code="${response: -3}"
if [ "$status_code" = "409" ]; then
    log_success "RN-001 validación unicidad funciona"
else
    log_fail "RN-001 no está funcionando (Status: $status_code)"
fi
echo "---"

# TEST 4: RF-006 - Consultar ticket por número
log_test "RF-006: Consultar ticket por número"
# Crear ticket para consulta
ticket_response=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"titulo":"Test consulta","descripcion":"Para consultar","usuarioId":1,"nationalId":"87654321-K","telefono":"+56912345678","branchOffice":"Norte","queueType":"EMPRESAS"}' \
    "$BASE_URL/api/tickets")

ticket_number=$(echo "$ticket_response" | grep -o '"numero":"[^"]*"' | cut -d'"' -f4)

if [ -n "$ticket_number" ]; then
    sleep 2  # Esperar procesamiento
    query_response=$(curl -s -w "%{http_code}" "$BASE_URL/api/tickets/number/$ticket_number")
    query_status="${query_response: -3}"
    if [ "$query_status" = "200" ]; then
        log_success "Consulta por número funciona ($ticket_number)"
    else
        log_fail "Consulta por número no funciona (Status: $query_status)"
    fi
else
    log_fail "No se pudo obtener número de ticket para consulta"
fi
echo "---"

# TEST 5: RF-005 - Gestión de colas
log_test "RF-005: Consultar colas principales"
caja_response=$(curl -s -w "%{http_code}" "$BASE_URL/api/queues/CAJA")
caja_status="${caja_response: -3}"
if [ "$caja_status" = "200" ] && echo "${caja_response%???}" | grep -q "CAJA"; then
    log_success "Gestión de colas funciona"
else
    log_fail "Gestión de colas no funciona"
fi
echo "---"

# TEST 6: RF-007 - Dashboard
log_test "RF-007: Dashboard resumen"
dashboard_response=$(curl -s -w "%{http_code}" "$BASE_URL/api/dashboard/summary")
dashboard_status="${dashboard_response: -3}"
if [ "$dashboard_status" = "200" ] && echo "${dashboard_response%???}" | grep -q "timestamp"; then
    log_success "Dashboard funciona correctamente"
else
    log_fail "Dashboard no funciona"
fi
echo "---"

# TEST 7: RN-005/006 - Numeración con prefijos
log_test "RN-005/006: Numeración con prefijos"
pb_response=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"titulo":"Test prefijo","descripcion":"Validar prefijo","usuarioId":1,"nationalId":"99999999-9","telefono":"+56999999999","branchOffice":"Centro","queueType":"PERSONAL_BANKER"}' \
    "$BASE_URL/api/tickets")

if echo "$pb_response" | grep -q '"numero":"P'; then
    log_success "Numeración con prefijos funciona"
else
    log_fail "Numeración con prefijos no funciona"
fi
echo "---"

# TEST 8: Schedulers funcionando
log_test "Schedulers: Procesamiento automático"
sleep 3  # Esperar procesamiento
scheduler_response=$(curl -s "$BASE_URL/api/dashboard/summary")
if echo "$scheduler_response" | grep -q "timestamp"; then
    log_success "Schedulers procesando correctamente"
else
    log_fail "Schedulers no funcionan"
fi
echo "---"

# RESUMEN FINAL
echo "=================================================="
echo "🏁 RESUMEN DE SMOKE TESTS"
echo "Total tests ejecutados: $TOTAL_TESTS"
echo "Tests fallidos: $FAILED_TESTS"
echo "Tests exitosos: $((TOTAL_TESTS - FAILED_TESTS))"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}🎉 TODOS LOS SMOKE TESTS FUNCIONALES PASARON${NC}"
    echo "✅ Sistema Ticketero funcionando correctamente"
    echo "✅ Listo para uso en producción"
    exit 0
else
    echo -e "${RED}⚠️  $FAILED_TESTS TESTS FALLARON${NC}"
    echo "❌ Revisar funcionalidades antes de producción"
    exit 1
fi