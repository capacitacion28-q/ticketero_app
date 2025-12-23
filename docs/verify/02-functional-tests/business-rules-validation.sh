#!/bin/bash
# BUSINESS RULES VALIDATION - Sistema Ticketero
# Validación específica de reglas de negocio RN-001 a RN-013
# Ejecutar mientras la aplicación está corriendo

BASE_URL="http://localhost:8080"
FAILED_RULES=0
TOTAL_RULES=0

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

log_rule() {
    echo -e "${BLUE}[RN-$((++TOTAL_RULES))]${NC} $1"
}

log_success() {
    echo -e "${GREEN}✅ CUMPLE:${NC} $1"
}

log_fail() {
    echo -e "${RED}❌ NO CUMPLE:${NC} $1"
    ((FAILED_RULES++))
}

echo "🔍 VALIDACIÓN DE REGLAS DE NEGOCIO - Sistema Ticketero"
echo "=================================================="

# RN-001: Unicidad de ticket activo por cliente
log_rule "Unicidad de ticket activo por cliente"
# Crear primer ticket
response1=$(curl -s -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"nationalId":"11111111-1","telefono":"+56911111111","branchOffice":"Centro","queueType":"CAJA"}' \
    "$BASE_URL/api/tickets")
status1="${response1: -3}"

# Intentar crear segundo ticket con mismo RUT
response2=$(curl -s -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"nationalId":"11111111-1","telefono":"+56911111111","branchOffice":"Norte","queueType":"PERSONAL_BANKER"}' \
    "$BASE_URL/api/tickets")
status2="${response2: -3}"

if [ "$status1" = "201" ] && [ "$status2" = "409" ]; then
    log_success "RN-001: Solo un ticket activo por cliente"
else
    log_fail "RN-001: Permite múltiples tickets activos por cliente"
fi

# RN-005 y RN-006: Numeración secuencial con prefijos
log_rule "Numeración secuencial con prefijos por cola"
# Crear tickets en diferentes colas
caja_response=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"nationalId":"22222222-2","telefono":"+56922222222","branchOffice":"Centro","queueType":"CAJA"}' \
    "$BASE_URL/api/tickets")

pb_response=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"nationalId":"33333333-3","telefono":"+56933333333","branchOffice":"Centro","queueType":"PERSONAL_BANKER"}' \
    "$BASE_URL/api/tickets")

caja_number=$(echo "$caja_response" | grep -o '"numero":"[^"]*"' | cut -d'"' -f4)
pb_number=$(echo "$pb_response" | grep -o '"numero":"[^"]*"' | cut -d'"' -f4)

if [[ "$caja_number" =~ ^C[0-9]{2}$ ]] && [[ "$pb_number" =~ ^P[0-9]{2}$ ]]; then
    log_success "RN-005/006: Numeración con prefijos correcta (C: $caja_number, P: $pb_number)"
else
    log_fail "RN-005/006: Numeración con prefijos incorrecta"
fi

# RN-010: Cálculo de tiempo estimado
log_rule "Cálculo de tiempo estimado (posición × tiempo promedio)"
empresas_response=$(curl -s -X POST \
    -H "Content-Type: application/json" \
    -d '{"nationalId":"44444444-4","telefono":"+56944444444","branchOffice":"Centro","queueType":"EMPRESAS"}' \
    "$BASE_URL/api/tickets")

estimated_time=$(echo "$empresas_response" | grep -o '"estimatedWaitMinutes":[0-9]*' | cut -d':' -f2)
position=$(echo "$empresas_response" | grep -o '"positionInQueue":[0-9]*' | cut -d':' -f2)

# EMPRESAS tiene 20 min promedio según QueueType enum
expected_time=$((position * 20))
if [ "$estimated_time" = "$expected_time" ]; then
    log_success "RN-010: Tiempo estimado calculado correctamente ($estimated_time min)"
else
    log_fail "RN-010: Tiempo estimado incorrecto (esperado: $expected_time, actual: $estimated_time)"
fi

# Validar Bean Validation específica del dominio
log_rule "Validaciones específicas del dominio ticketero"
# RUT inválido
rut_response=$(curl -s -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"nationalId":"invalid-rut","telefono":"+56987654321","branchOffice":"Centro","queueType":"CAJA"}' \
    "$BASE_URL/api/tickets")
rut_status="${rut_response: -3}"

# Teléfono inválido
phone_response=$(curl -s -w "%{http_code}" -X POST \
    -H "Content-Type: application/json" \
    -d '{"nationalId":"55555555-5","telefono":"invalid-phone","branchOffice":"Centro","queueType":"CAJA"}' \
    "$BASE_URL/api/tickets")
phone_status="${phone_response: -3}"

if [ "$rut_status" = "400" ] && [ "$phone_status" = "400" ]; then
    log_success "Validaciones de dominio funcionan (RUT y teléfono)"
else
    log_fail "Validaciones de dominio no funcionan correctamente"
fi

# RF-006: Consulta por UUID y número
log_rule "Consulta de tickets por UUID y número"
# Obtener UUID del último ticket creado
uuid=$(echo "$empresas_response" | grep -o '"codigoReferencia":"[^"]*"' | cut -d'"' -f4)
numero=$(echo "$empresas_response" | grep -o '"numero":"[^"]*"' | cut -d'"' -f4)

uuid_query=$(curl -s -w "%{http_code}" "$BASE_URL/api/tickets/$uuid")
number_query=$(curl -s -w "%{http_code}" "$BASE_URL/api/tickets/number/$numero")

uuid_status="${uuid_query: -3}"
number_status="${number_query: -3}"

if [ "$uuid_status" = "200" ] && [ "$number_status" = "200" ]; then
    log_success "RF-006: Consulta por UUID y número funciona"
else
    log_fail "RF-006: Consulta por UUID o número no funciona"
fi

# RF-005: Gestión de múltiples colas
log_rule "Gestión de múltiples tipos de cola"
colas=("CAJA" "PERSONAL_BANKER" "EMPRESAS" "GERENCIA")
colas_ok=0

for cola in "${colas[@]}"; do
    response=$(curl -s -w "%{http_code}" "$BASE_URL/api/queues/$cola")
    status="${response: -3}"
    if [ "$status" = "200" ]; then
        ((colas_ok++))
    fi
done

if [ $colas_ok -eq 4 ]; then
    log_success "RF-005: Todas las colas (4) son consultables"
else
    log_fail "RF-005: Solo $colas_ok de 4 colas funcionan"
fi

# RF-007: Dashboard operativo
log_rule "Dashboard con métricas en tiempo real"
dashboard_endpoints=("/api/dashboard/summary" "/api/dashboard/realtime" "/api/dashboard/alerts" "/api/dashboard/metrics")
dashboard_ok=0

for endpoint in "${dashboard_endpoints[@]}"; do
    response=$(curl -s -w "%{http_code}" "$BASE_URL$endpoint")
    status="${response: -3}"
    if [ "$status" = "200" ]; then
        ((dashboard_ok++))
    fi
done

if [ $dashboard_ok -eq 4 ]; then
    log_success "RF-007: Dashboard completo (4/4 endpoints)"
else
    log_fail "RF-007: Dashboard incompleto ($dashboard_ok/4 endpoints)"
fi

# RF-008: Auditoría de eventos
log_rule "Sistema de auditoría operativo"
audit_response=$(curl -s -w "%{http_code}" "$BASE_URL/api/audit/events")
audit_status="${audit_response: -3}"

if [ "$audit_status" = "200" ]; then
    log_success "RF-008: Sistema de auditoría funcional"
else
    log_fail "RF-008: Sistema de auditoría no funcional"
fi

# Validar ErrorResponse consistente
log_rule "ErrorResponse consistente en toda la API"
error_404=$(curl -s "$BASE_URL/api/tickets/99999999-9999-9999-9999-999999999999")
error_400=$(curl -s -X POST -H "Content-Type: application/json" -d '{"invalid":"data"}' "$BASE_URL/api/tickets")

if echo "$error_404" | grep -q '"timestamp"' && echo "$error_400" | grep -q '"timestamp"'; then
    log_success "ErrorResponse estructura consistente"
else
    log_fail "ErrorResponse estructura inconsistente"
fi

# RESUMEN FINAL
echo "=================================================="
echo "📊 RESUMEN VALIDACIÓN REGLAS DE NEGOCIO"
echo "Total reglas validadas: $TOTAL_RULES"
echo "Reglas que NO cumplen: $FAILED_RULES"
echo "Reglas que SÍ cumplen: $((TOTAL_RULES - FAILED_RULES))"

if [ $FAILED_RULES -eq 0 ]; then
    echo -e "${GREEN}🎉 TODAS LAS REGLAS DE NEGOCIO CUMPLEN${NC}"
    echo "✅ Sistema Ticketero implementado correctamente"
    exit 0
else
    echo -e "${RED}⚠️  $FAILED_RULES REGLAS NO CUMPLEN${NC}"
    echo "❌ Revisar implementación de reglas de negocio"
    exit 1
fi