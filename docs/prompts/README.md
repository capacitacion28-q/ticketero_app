# Prompts de Desarrollo - Sistema Ticketero

Esta carpeta contiene todos los prompts utilizados durante el desarrollo del proyecto, organizados por fase del workflow de desarrollo.

## Estructura

```
prompts/
├── README.md                    # Este archivo
├── tasks/                      # Prompts para definición de tareas
├── brainstorm/                 # Prompts para lluvia de ideas y análisis
├── plan/                       # Prompts para planificación y diseño
├── implement/                  # Prompts para implementación de código
├── verify/                     # Prompts para testing y validación
├── deploy/                     # Prompts para despliegue y configuración
└── document/                   # Prompts para documentación
```

## Convenciones de Nomenclatura

- Usar formato: `[categoria]_[descripcion]_[version].md`
- Ejemplo: `plan_diagrama_contexto_v1.md`
- Mantener versionado para tracking de cambios

## Uso

1. Cada prompt debe incluir contexto específico del proyecto
2. Incluir ejemplos de entrada y salida esperada
3. Documentar casos de uso específicos
4. Mantener prompts reutilizables y actualizados

## Reglas de Calidad

Todos los prompts deben:
- Ser específicos al dominio del sistema ticketero
- Incluir contexto de negocio relevante
- Ser claros y concisos
- Facilitar la comprensión y ejecución