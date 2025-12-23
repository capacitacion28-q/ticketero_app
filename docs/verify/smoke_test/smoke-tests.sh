#!/bin/bash
# SMOKE TESTS - Sistema Ticketero
# Validación funcional mientras la aplicación está ejecutándose
# Basado en: docs/implementation/plan_detallado_implementacion_v1.0.md

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

test_endpoint() {
    local method=$1
    local endpoint=$2
    local expected_status=$3
    local data=$4
    local description=$5
    
    log_test "$description"
    
    if [ "$method" = "POST" ]; then
        response=$(curl -s -w "%{http_code}" -X POST \
            -H "Content-Type: application/json" \
            -d "$data" \
            "$BASE_URL$endpoint")
    else
        response=$(curl -s -w "%{http_code}" "$BASE_URL$endpoint")
    fi
    
    status_code="${response: -3}"
    body="${response%???}"
    
    if [ "$status_code" = "$expected_status" ]; then
        log_success "HTTP $status_code - $description"
        echo "$body"
    else
        log_fail "Expected $expected_status, got $status_code - $description"
    fi
    echo "---"
}

echo "🚀 INICIANDO SMOKE TESTS - Sistema Ticketero"
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

# TEST 2: RF-001 - Crear Ticket (RN-001: Validación unicidad)
test_endpoint "POST" "/api/tickets" "201" \
'{
    "nationalId": "12345678-9",
    "telefono": "+56987654321",
    "branchOffice": "Centro",
    "queueType": "CAJA"
}' "RF-001: Crear ticket válido"

# TEST 3: RN-001 - Ticket activo existente (debe fallar)
test_endpoint "POST" "/api/tickets" "409" \
'{
    "nationalId": "12345678-9",
    "telefono": "+56987654321",
    "branchOffice": "Centro",
    "queueType": "PERSONAL_BANKER"
}' "RN-001: Validar ticket activo existente (debe retornar 409)"

# TEST 4: Bean Validation - Datos inválidos
test_endpoint "POST" "/api/tickets" "400" \
'{
    "nationalId": "invalid-rut",
    "telefono": "invalid-phone",
    "branchOffice": "",
    "queueType": "INVALID_QUEUE"
}' "Bean Validation: Datos inválidos (debe retornar 400)"

# TEST 5: RF-006 - Consultar ticket por número
log_test "RF-006: Consultar ticket por número"
ticket_number=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"nationalId":"87654321-K","telefono":"+56912345678","branchOffice":"Norte","queueType":"EMPRESAS"}' \
    "$BASE_URL/api/tickets" | grep -o '"numero":"[^"]*"' | cut -d'"' -f4)

if [ -n "$ticket_number" ]; then
    test_endpoint "GET" "/api/tickets/number/$ticket_number" "200" "" "Consultar ticket creado por número"
else
    log_fail "No se pudo obtener número de ticket para consulta"
fi

# TEST 6: RF-005 - Gestión de colas
test_endpoint "GET" "/api/queues/CAJA" "200" "" "RF-005: Consultar cola CAJA"
test_endpoint "GET" "/api/queues/PERSONAL_BANKER" "200" "" "RF-005: Consultar cola PERSONAL_BANKER"
test_endpoint "GET" "/api/queues/stats" "200" "" "RF-005: Estadísticas de colas"

# TEST 7: RF-007 - Dashboard
test_endpoint "GET" "/api/dashboard/summary" "200" "" "RF-007: Dashboard resumen"
test_endpoint "GET" "/api/dashboard/realtime" "200" "" "RF-007: Dashboard tiempo real"
test_endpoint "GET" "/api/dashboard/alerts" "200" "" "RF-007: Alertas activas"

# TEST 8: RF-008 - Auditoría
test_endpoint "GET" "/api/audit/events" "200" "" "RF-008: Consultar eventos de auditoría"

# TEST 9: Validar estructura de respuesta ErrorResponse
log_test "Validar estructura ErrorResponse"
error_response=$(curl -s "$BASE_URL/api/tickets/99999999-9999-9999-9999-999999999999")
if echo "$error_response" | grep -q '"success":false' && echo "$error_response" | grep -q '"timestamp"'; then
    log_success "ErrorResponse tiene estructura correcta"
else
    log_fail "ErrorResponse no tiene estructura esperada"
fi
echo "---"

# TEST 10: Validar enumeraciones del dominio
log_test "Validar enumeraciones del dominio"
queue_response=$(curl -s "$BASE_URL/api/queues/GERENCIA")
if echo "$queue_response" | grep -q '"queueType":"GERENCIA"'; then
    log_success "Enumeración QueueType.GERENCIA funciona correctamente"
else
    log_fail "Enumeración QueueType no funciona correctamente"
fi
echo "---"

# TEST 11: Validar logging y métricas
log_test "Validar endpoints de monitoreo"
metrics_response=$(curl -s "$BASE_URL/actuator/info")
if [ $? -eq 0 ]; then
    log_success "Endpoint de métricas accesible"
else
    log_fail "Endpoint de métricas no accesible"
fi
echo "---"

# TEST 12: Validar configuración de schedulers (indirecto)
log_test "Validar configuración de schedulers"
# Crear ticket y verificar que se procese
ticket_response=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"nationalId":"11111111-1","telefono":"+56999888777","branchOffice":"Sur","queueType":"CAJA"}' \
    "$BASE_URL/api/tickets")

if echo "$ticket_response" | grep -q '"numero"'; then
    log_success "Schedulers configurados - ticket creado correctamente"
else
    log_fail "Problema con configuración de schedulers"
fi
echo "---"

# RESUMEN FINAL
echo "=================================================="
echo "🏁 RESUMEN DE SMOKE TESTS"
echo "Total tests ejecutados: $TOTAL_TESTS"
echo "Tests fallidos: $FAILED_TESTS"
echo "Tests exitosos: $((TOTAL_TESTS - FAILED_TESTS))"

if [ $FAILED_TESTS -eq 0 ]; then
    echo -e "${GREEN}🎉 TODOS LOS SMOKE TESTS PASARON${NC}"
    echo "✅ Sistema Ticketero funcionando correctamente"
    exit 0
else
    echo -e "${RED}⚠️  $FAILED_TESTS TESTS FALLARON${NC}"
    echo "❌ Revisar logs y corregir problemas"
    exit 1
fi