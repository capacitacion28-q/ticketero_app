# Manual de Usuario - Sistema Ticketero Digital

**Proyecto:** Sistema de Gestión de Tickets para Atención en Sucursales  
**Versión:** 1.0  
**Fecha:** 2025-12-24  
**Dirigido a:** Clientes, Supervisores y Personal de Sucursal

---

## Índice
- [Introducción](#introducción)
- [Para Clientes](#para-clientes)
- [Para Supervisores](#para-supervisores)
- [Para Personal de Sucursal](#para-personal-de-sucursal)
- [Preguntas Frecuentes](#preguntas-frecuentes)
- [Soporte Técnico](#soporte-técnico)

---

## Introducción

### ¿Qué es el Sistema Ticketero Digital?

El Sistema Ticketero Digital moderniza la atención en sucursales bancarias mediante:

- **🎫 Tickets digitales** sin papeles físicos
- **📱 Notificaciones en tiempo real** vía Telegram
- **⏰ Tiempos de espera estimados** precisos
- **🚶‍♂️ Libertad de movimiento** fuera de la sucursal
- **📊 Seguimiento completo** del estado de su turno

### Beneficios del Sistema

| Beneficio | Descripción |
|-----------|-------------|
| **Sin esperas físicas** | Reciba su turno y salga de la sucursal |
| **Notificaciones automáticas** | 3 mensajes: confirmación, pre-aviso y llamado |
| **Tiempo estimado real** | Sepa exactamente cuándo regresar |
| **Múltiples colas** | Caja, Personal Banker, Empresas, Gerencia |
| **Trazabilidad completa** | Historial de todos sus tickets |

---

## Para Clientes

### 1. Obtener un Ticket

#### Paso 1: Acercarse al Terminal
- Busque el **terminal de autoservicio** en la sucursal
- Pantalla táctil con interfaz intuitiva
- Disponible en horario de atención

#### Paso 2: Ingresar sus Datos
```
┌─────────────────────────────┐
│     SISTEMA TICKETERO       │
├─────────────────────────────┤
│ RUT: [12345678-9]          │
│ Teléfono: [+56912345678]   │
│ Servicio: [Personal Banker] │
│                             │
│     [OBTENER TICKET]        │
└─────────────────────────────┘
```

**Datos requeridos:**
- **RUT:** Formato 12345678-9 (con guión)
- **Teléfono:** Formato +56912345678 (código país obligatorio)
- **Tipo de servicio:** Seleccione según su necesidad

#### Paso 3: Seleccionar Tipo de Servicio

| Servicio | Descripción | Tiempo Promedio |
|----------|-------------|-----------------|
| **🏦 Caja** | Depósitos, retiros, pagos | 5 minutos |
| **👤 Personal Banker** | Consultas de cuenta, productos | 15 minutos |
| **🏢 Empresas** | Servicios empresariales | 20 minutos |
| **⭐ Gerencia** | Atención especializada | 30 minutos |

#### Paso 4: Confirmar Ticket
El sistema mostrará:
```
✅ TICKET CREADO EXITOSAMENTE

📋 Número: P05
📍 Posición en cola: 3
⏰ Tiempo estimado: 45 minutos
📱 Recibirá notificaciones en Telegram

Puede salir de la sucursal
```

### 2. Recibir Notificaciones

#### Mensaje 1: Confirmación (Inmediato)
```
🎫 Ticket Creado

📋 Número: P05
📍 Posición: 3
⏰ Tiempo estimado: 45 min
```

#### Mensaje 2: Pre-aviso (Posición ≤ 3)
```
🔔 ¡Tu turno está próximo!

📋 Ticket: P05
📍 Posición: 2
🏃 Dirígete a la sucursal
```

#### Mensaje 3: Llamado Final
```
✅ ¡Es tu turno!

📋 Ticket: P05
🏢 Módulo: 3
👤 Te atiende: María González
```

### 3. Consultar Estado del Ticket

#### Opción A: Por Telegram
- Responda al mensaje recibido
- El bot le dará información actualizada

#### Opción B: En Terminal
- Ingrese su número de ticket
- Consulte estado actual

#### Opción C: Por Código QR (Futuro)
- Escanee el código en su comprobante
- Acceda a información en tiempo real

### 4. Presentarse para Atención

#### Cuando Reciba el Mensaje Final:
1. **Diríjase inmediatamente** al módulo indicado
2. **Presente su RUT** al ejecutivo
3. **Mencione su número de ticket** (ej: P05)
4. **Tiempo límite:** 5 minutos para presentarse

#### Si No Se Presenta:
- Su ticket será marcado como "No Show"
- Deberá obtener un nuevo ticket
- Su posición en cola se perderá

### 5. Casos Especiales

#### ¿Qué pasa si no tengo Telegram?
- El sistema funciona sin Telegram
- Consulte estado en el terminal
- Manténgase cerca de la sucursal

#### ¿Puedo cancelar mi ticket?
- Sí, acérquese al terminal
- Ingrese su número de ticket
- Seleccione "Cancelar"

#### ¿Qué pasa si llego tarde?
- Después de 5 minutos: ticket cancelado automáticamente
- Debe obtener un nuevo ticket
- Su posición se pierde

---

## Para Supervisores

### 1. Dashboard Principal

#### Acceso al Sistema
```
URL: http://sistema-ticketero/dashboard
Usuario: supervisor@sucursal.cl
Contraseña: [Proporcionada por TI]
```

#### Vista Principal del Dashboard
```
┌─────────────────────────────────────────────┐
│           DASHBOARD EJECUTIVO               │
├─────────────────────────────────────────────┤
│ 🎫 Tickets Activos: 12                     │
│ 👥 Ejecutivos Disponibles: 3/5             │
│ ⏰ Tiempo Promedio Espera: 18 min          │
│ 📊 Estado General: NORMAL                  │
├─────────────────────────────────────────────┤
│              COLAS ACTIVAS                  │
│ 🏦 Caja: 3 tickets (15 min promedio)      │
│ 👤 Personal Banker: 5 tickets (22 min)    │
│ 🏢 Empresas: 2 tickets (35 min)           │
│ ⭐ Gerencia: 1 ticket (45 min)            │
└─────────────────────────────────────────────┘
```

### 2. Monitoreo en Tiempo Real

#### Métricas Clave
- **Actualización:** Cada 5 segundos automáticamente
- **Tickets por hora:** Promedio y picos
- **Tiempo de atención:** Por ejecutivo y por cola
- **Abandonos:** Tickets no atendidos

#### Alertas Automáticas
```
🚨 ALERTA: Cola Personal Banker saturada (>10 tickets)
⚠️  ADVERTENCIA: Ejecutivo Juan Pérez inactivo >30 min
📊 INFO: Pico de demanda detectado (15 tickets/hora)
```

### 3. Gestión de Ejecutivos

#### Estado de Ejecutivos
| Ejecutivo | Estado | Módulo | Tickets Atendidos | Último Cambio |
|-----------|--------|--------|-------------------|---------------|
| María González | 🟢 DISPONIBLE | 1 | 8 | 10:30 |
| Juan Pérez | 🔴 OCUPADO | 2 | 12 | 11:15 |
| Ana Silva | 🟡 DESCANSO | 3 | 6 | 11:00 |

#### Acciones Disponibles
- **Cambiar estado** de ejecutivos
- **Reasignar tickets** manualmente
- **Ver historial** de atención
- **Generar reportes** de productividad

### 4. Reportes y Estadísticas

#### Reporte Diario
```
📊 REPORTE DIARIO - 2025-12-24

Tickets Creados: 45
Tickets Completados: 42
Tickets Cancelados: 2
Tickets No Show: 1

Tiempo Promedio por Cola:
- Caja: 4.2 min
- Personal Banker: 14.8 min
- Empresas: 18.5 min
- Gerencia: 28.3 min

Ejecutivo Más Productivo: María González (15 tickets)
Hora Pico: 10:00-11:00 (12 tickets)
```

#### Exportar Datos
- **Excel:** Datos para análisis externo
- **PDF:** Reportes ejecutivos
- **CSV:** Integración con otros sistemas

### 5. Configuración del Sistema

#### Parámetros Ajustables
- **Tiempos promedio** por tipo de cola
- **Número máximo** de tickets por ejecutivo
- **Timeout** para No Show (default: 5 minutos)
- **Intervalos** de notificaciones

#### Gestión de Usuarios
- Crear/editar ejecutivos
- Asignar módulos
- Configurar permisos
- Resetear contraseñas

---

## Para Personal de Sucursal

### 1. Terminal de Autoservicio

#### Funciones Principales
- **Crear tickets** para clientes
- **Consultar estado** de tickets existentes
- **Cancelar tickets** si es necesario
- **Ver cola actual** por tipo de servicio

#### Mantenimiento Básico
```
Limpieza diaria:
1. Limpiar pantalla con paño suave
2. Verificar conexión a internet
3. Reiniciar si presenta lentitud
4. Reportar problemas a TI
```

### 2. Atención al Cliente

#### Proceso de Atención
1. **Cliente se presenta** con número de ticket
2. **Verificar identidad** con RUT
3. **Confirmar servicio** solicitado
4. **Proceder con atención** normal
5. **Marcar ticket como completado**

#### Casos Especiales
- **Cliente sin Telegram:** Explicar funcionamiento básico
- **Ticket vencido:** Ayudar a generar nuevo ticket
- **Problemas técnicos:** Contactar supervisor

### 3. Soporte a Clientes

#### Preguntas Frecuentes de Clientes

**"¿Cómo funciona el sistema?"**
- Explique los 3 mensajes automáticos
- Muestre el tiempo estimado
- Confirme que pueden salir de la sucursal

**"No recibí el mensaje"**
- Verifique número de teléfono
- Confirme que tiene Telegram instalado
- Ofrezca consulta manual en terminal

**"¿Puedo cambiar mi tipo de servicio?"**
- No es posible cambiar
- Debe cancelar y crear nuevo ticket
- Perderá su posición actual

### 4. Procedimientos de Emergencia

#### Falla del Sistema
1. **Informar inmediatamente** al supervisor
2. **Continuar con sistema manual** temporal
3. **Anotar tickets pendientes** para migración
4. **Comunicar a clientes** la situación

#### Falla de Internet
1. **Sistema funciona localmente** (base de datos local)
2. **Notificaciones Telegram** se pausan
3. **Informar a clientes** sobre notificaciones
4. **Sincronización automática** al restaurar conexión

---

## Preguntas Frecuentes

### Para Clientes

**P: ¿Es obligatorio tener Telegram?**
R: No, pero es altamente recomendado. Sin Telegram debe consultar manualmente su estado y permanecer cerca de la sucursal.

**P: ¿Puedo obtener múltiples tickets?**
R: No, solo puede tener un ticket activo por RUT. Debe completar o cancelar el actual antes de obtener otro.

**P: ¿Qué pasa si mi teléfono se queda sin batería?**
R: Puede consultar el estado de su ticket en el terminal usando su número de ticket.

**P: ¿Puedo ceder mi turno a otra persona?**
R: No, los tickets son personales e intransferibles. Se requiere presentar el RUT del titular.

**P: ¿Hay límite de tiempo para usar mi ticket?**
R: Los tickets son válidos durante el día de emisión. Después de las 18:00 se cancelan automáticamente.

### Para Personal

**P: ¿Qué hacer si un cliente reclama por su posición?**
R: El sistema calcula posiciones automáticamente por orden de llegada y prioridad de cola. Consulte el dashboard para verificar.

**P: ¿Cómo manejar clientes que no hablan español?**
R: El terminal tiene opción de idioma inglés. Para otros idiomas, contacte al supervisor.

**P: ¿Qué hacer si el terminal no responde?**
R: Reinicie presionando el botón de encendido por 10 segundos. Si persiste, contacte TI.

---

## Soporte Técnico

### Contactos de Soporte

| Tipo de Problema | Contacto | Horario |
|------------------|----------|---------|
| **Problemas del sistema** | TI: ext. 2200 | 8:00 - 18:00 |
| **Telegram no funciona** | Soporte: ext. 2201 | 8:00 - 18:00 |
| **Consultas de uso** | Supervisor de turno | Horario sucursal |
| **Emergencias** | Gerencia: ext. 2000 | 24/7 |

### Información del Sistema

- **Versión:** 1.0
- **Última actualización:** 2025-12-24
- **Servidor:** ticketero.sucursal.local
- **Base de datos:** PostgreSQL 15
- **Respaldo:** Diario a las 02:00

### Códigos de Error Comunes

| Código | Descripción | Solución |
|--------|-------------|----------|
| **E001** | RUT ya tiene ticket activo | Cancelar ticket existente primero |
| **E002** | Formato de teléfono inválido | Usar formato +56912345678 |
| **E003** | Sin ejecutivos disponibles | Esperar o contactar supervisor |
| **E004** | Sistema en mantenimiento | Esperar finalización |
| **E005** | Error de conexión Telegram | Verificar internet |

### Logs del Sistema

Los logs se almacenan en:
- **Aplicación:** `/var/log/ticketero/application.log`
- **Base de datos:** `/var/log/postgresql/postgresql.log`
- **Telegram:** `/var/log/ticketero/telegram.log`

---

## Anexos

### A. Horarios de Atención

| Sucursal | Lunes-Viernes | Sábados | Domingos |
|----------|---------------|---------|----------|
| Centro | 9:00 - 18:00 | 9:00 - 13:00 | Cerrado |
| Mall | 10:00 - 22:00 | 10:00 - 22:00 | 11:00 - 20:00 |
| Aeropuerto | 6:00 - 23:00 | 6:00 - 23:00 | 6:00 - 23:00 |

### B. Tipos de Servicio Detallados

**🏦 Caja (Tiempo: 5 min)**
- Depósitos en efectivo
- Retiros de cuenta
- Pagos de servicios
- Cambio de moneda

**👤 Personal Banker (Tiempo: 15 min)**
- Consultas de saldo y movimientos
- Solicitud de productos (tarjetas, créditos)
- Reclamos y consultas
- Actualización de datos

**🏢 Empresas (Tiempo: 20 min)**
- Servicios de tesorería
- Créditos empresariales
- Comercio exterior
- Cuentas corrientes empresariales

**⭐ Gerencia (Tiempo: 30 min)**
- Inversiones y patrimonio
- Seguros y previsión
- Atención VIP
- Casos especiales

### C. Mensajes de Telegram

#### Plantillas Estándar

**Confirmación:**
```
🎫 Ticket Creado

📋 Número: {NUMERO}
📍 Posición: {POSICION}
⏰ Tiempo estimado: {TIEMPO} min

Puede salir de la sucursal.
Recibirá notificaciones automáticas.
```

**Pre-aviso:**
```
🔔 ¡Tu turno está próximo!

📋 Ticket: {NUMERO}
📍 Posición: {POSICION}
🏃 Dirígete a la sucursal

Tiempo estimado: 5-10 minutos
```

**Llamado:**
```
✅ ¡Es tu turno!

📋 Ticket: {NUMERO}
🏢 Módulo: {MODULO}
👤 Te atiende: {EJECUTIVO}

Preséntese inmediatamente.
Tiempo límite: 5 minutos.
```

---

**Manual generado:** 2025-12-24  
**Versión:** 1.0  
**Mantenido por:** Equipo Sistema Ticketero  
**Próxima revisión:** 2025-06-24