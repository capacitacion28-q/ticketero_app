# Smoke Tests - Sistema Ticketero

## Propósito
Smoke tests completos del sistema usando H2 en memoria con máxima transparencia.

## Ejecución
```bash
docs\verify\00-smoke-tests\quick-start.bat
```

## Proceso Automático
1. **Setup:** Limpia puerto 8080, compila, inicia Spring Boot con H2
2. **Smoke Tests:** 5 tests esenciales con logs completos
3. **Cleanup:** Detiene aplicación automáticamente
4. **Reporte:** Resultado final con estadísticas

## Tests Incluidos
- **Health Check** - Verificar que la aplicación responde
- **Ticket Creation** - Crear ticket de prueba
- **Dashboard** - Verificar métricas del sistema
- **Queue Management** - Verificar gestión de colas
- **Telegram Integration** - Verificar bot configurado

## Características
- ✅ **Transparencia total** - Muestra todos los logs de Maven y APIs
- ✅ **BD limpia** - H2 en memoria se reinicia en cada ejecución
- ✅ **Tests reproducibles** - Mismo RUT funciona siempre
- ✅ **Sin dependencias** - No requiere Docker ni PostgreSQL
- ✅ **Setup automático** - Inicia y para la aplicación
- ✅ **Progreso visible** - Indicadores paso a paso

## Requisitos
- Maven y Java 17+
- Puerto 8080 disponible

## Interpretación de Resultados
- **5/5 exitosos:** Sistema 100% funcional
- **4/5 exitosos:** Sistema funcional con advertencias
- **<4/5 exitosos:** Problemas que requieren atención

## Base de Datos
- Usa H2 en memoria (`jdbc:h2:mem:testdb`)
- Se limpia automáticamente en cada ejecución
- Permite reutilizar datos de prueba

## Resultado Esperado
```
RESULTADO: 5 exitosos, 0 fallidos
SISTEMA FUNCIONAL
```

## Ventajas sobre Smoke Tests Tradicionales
- **Setup automático** - No requiere aplicación corriendo
- **BD limpia** - Sin conflictos de datos entre ejecuciones
- **Más transparente** - Logs completos de Maven y APIs
- **Más robusto** - Maneja errores y cleanup automático