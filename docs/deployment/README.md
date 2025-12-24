# Deployment - Sistema Ticketero

Documentación para despliegue y configuración del sistema.

## 📁 Contenido

### `docker-setup-guide.md`
Guía completa para levantar el sistema con Docker Compose:
- Configuración de servicios (PostgreSQL + API)
- Variables de entorno requeridas
- Comandos de ejecución y gestión
- Troubleshooting y validación

### `production-deployment-guide.md`
Guía para despliegue en ambiente productivo:
- Configuración de producción
- Consideraciones de seguridad
- Monitoreo y mantenimiento

### Reportes de Validación
- `validation-report-2025-12-24.md` - Reporte completo del sistema

## 🚀 Inicio Rápido

```bash
# Solo PostgreSQL (recomendado para desarrollo)
docker-compose up -d postgres
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Sistema completo con Docker
docker-compose --profile full up -d
```

## 📋 Validación

```bash
# Health check
curl http://localhost:8080/actuator/health

# Dashboard
curl http://localhost:8080/api/dashboard/summary
```