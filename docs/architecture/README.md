# Arquitectura - Sistema Ticketero

Documentación del diseño arquitectónico del sistema de gestión de tickets.

## 📁 Contenido

### `software_architecture_design_v1.0.md`
Diseño completo de arquitectura de software incluyendo:
- Arquitectura en capas (Controller → Service → Repository)
- Stack tecnológico (Spring Boot, PostgreSQL, Telegram Bot)
- Patrones de diseño aplicados
- Decisiones arquitectónicas justificadas

### `/diagrams/`
Diagramas PlantUML del sistema:

#### `01-context-diagram.puml`
- Diagrama de contexto del sistema
- Actores externos (Cliente, Agente, Admin)
- Sistemas externos (Telegram Bot, Base de Datos)

#### `02-sequence-diagram.puml`
- Flujo de creación y gestión de tickets
- Interacciones entre componentes
- Casos de uso principales

#### `03-er-diagram.puml`
- Modelo de datos relacional
- Entidades: Ticket, Mensaje, Usuario, Estado
- Relaciones y cardinalidades

## 🎯 Cómo Usar

1. **Leer primero:** `software_architecture_design_v1.0.md`
2. **Visualizar diagramas:** Usar PlantUML para renderizar `.puml`
3. **Validar diseño:** Aplicar Test de 3 Minutos para explicación

## ✅ Criterios de Calidad

- Arquitectura explicable en 3 minutos
- Diagramas auto-documentados
- Decisiones justificadas con contexto de negocio
- Patrones estándar de Spring Boot aplicados