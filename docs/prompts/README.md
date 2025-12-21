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

## Inventario de Prompts Actuales

### Prompts Maestros (Raíz)
- **prompt_git_control_versiones_v1.0.md** - Git Specialist & DevOps Engineer para control de versiones con Conventional Commits
- **prompt-methodology-master.md** - Guía metodológica del workflow de desarrollo en 7 etapas
- **prompt-refinement-master.md** - Técnicas de refinamiento y optimización de prompts

### Fase Tasks (v0.1.x)
- **prompt_analisis_requerimientos_funcionales_v1.0.md** - Analista Senior para especificación IEEE 830 de requerimientos funcionales

### Fase Brainstorm (v0.2.x)
- **prompt_arquitectura_alto_nivel_v1.0.md** - Arquitecto de Software Senior para diseño de arquitectura de alto nivel
- **rule_architecture_3min_diagram_test_v1.0.md** - Rule #1: Simplicidad Verificable con Test de 3 Minutos

### Fases Pendientes
- **plan/** - Prompts para planificación detallada y roadmap
- **implement/** - Prompts para desarrollo de código backend
- **verify/** - Prompts para testing y validación de calidad
- **deploy/** - Prompts para configuración de despliegue
- **document/** - Prompts para documentación de usuario final

## Estado del Proyecto

**Etapa Actual:** Brainstorm (v0.2.x)  
**Última Versión:** v0.1.0-tasks (completada)  
**Progreso:** 2/7 etapas completadas

### Artefactos Generados
- ✅ Requerimientos de negocio iniciales
- ✅ Análisis completo de requerimientos funcionales (IEEE 830)
- ✅ Diseño de arquitectura de software
- ✅ 3 diagramas core (Context, Sequence, ER)
- ✅ Reglas de arquitectura establecidas

## Uso

1. Cada prompt debe incluir contexto específico del proyecto
2. Incluir ejemplos de entrada y salida esperada
3. Documentar casos de uso específicos
4. Mantener prompts reutilizables y actualizados
5. Seguir versionado semántico para tracking de cambios
6. Aplicar reglas de simplicidad verificable (Rule #1)

## Reglas de Calidad

Todos los prompts deben:
- Ser específicos al dominio del sistema ticketero
- Incluir contexto de negocio relevante
- Ser claros y concisos
- Facilitar la comprensión y ejecución
- Cumplir con el Test de 3 Minutos para explicación
- Generar artefactos verificables y medibles