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

### Fase Tasks (v0.1.x) ✅
- **prompt_analisis_requerimientos_funcionales_v1.0.md** - Analista Senior para especificación IEEE 830

### Fase Brainstorm (v0.2.x) ✅
- **prompt_arquitectura_alto_nivel_v1.0.md** - Arquitecto de Software Senior para diseño de arquitectura
- **prompt_revision_arquitectura_v1.0.md** - Revisión y validación de arquitectura
- **rule_architecture_3min_diagram_test_v1.0.md** - Rule #1: Simplicidad Verificable

### Fase Plan (v0.3.x) ✅
- **prompt_generacion_reglas_desarrollador_v1.0.md** - Generación de reglas de desarrollo
- **prompt_plan_detallado_implementacion_v1.0.md** - Plan maestro de implementación
- **prompt_verificacion_cumplimiento_v1.0.md** - Verificación de cumplimiento

### Fase Implement (v0.4.x) 🔄
- **prompt_implementacion_completa_v1.0.md** - Implementación completa del sistema
- **rule_spring_boot_patterns_v1.0.md** - Patrones Spring Boot para Sistema Ticketero
- **rule_jpa_entities_database_v1.0.md** - JPA Entities & Database para PostgreSQL
- **rule_dtos_validation_v1.0.md** - DTOs & Validation con Records Java 17
- **rule_java21_features_v1.0.md** - Features modernas Java 17 (preparado para Java 21)
- **rule_lombok_best_practices_v1.0.md** - Lombok Best Practices específicas
- **inconsistencias_resueltas_v1.0.md** - Resolución de inconsistencias detectadas

### Fases Pendientes
- **verify/** - Prompts para testing y validación de calidad
- **deploy/** - Prompts para configuración de despliegue
- **document/** - Prompts para documentación de usuario final

## Estado del Proyecto

**Etapa Actual:** Implement (v0.4.x)  
**Última Versión:** v0.3.0-plan (completada)  
**Progreso:** 3/7 etapas completadas

### Artefactos Generados
- ✅ Requerimientos de negocio y análisis funcional IEEE 830
- ✅ Diseño de arquitectura de software completo
- ✅ 3 diagramas core (Context, Sequence, ER)
- ✅ Plan detallado de implementación
- ✅ Reglas de desarrollo (Spring Boot, JPA, DTOs, Java 21, Lombok)
- ✅ Configuración Amazon Q para desarrollo
- ✅ Reportes de auditoría y verificación

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