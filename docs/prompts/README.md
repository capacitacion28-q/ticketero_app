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
- **prompt_environment_setup_v1.0.md** - Configuración de ambiente de desarrollo

### Fase Tasks (v0.1.x) ✅
- **prompt_analisis_requerimientos_funcionales_v1.0.md** - Analista Senior para especificación IEEE 830

### Fase Brainstorm (v0.2.x) ✅
- **prompt_arquitectura_alto_nivel_v1.0.md** - Arquitecto de Software Senior para diseño de arquitectura
- **prompt_revision_arquitectura_v1.0.md** - Revisión y validación de arquitectura
- **rule_architecture_3min_diagram_test_v1.0.md** - Rule #1: Simplicidad Verificable

### Fase Plan (v0.3.x) ✅
- **prompt_generacion_reglas_desarrollador_v1.0.md** - Generación de reglas de desarrollo
- **prompt_plan_detallado_implementacion_v1.0.md** - Plan maestro de implementación (v1.0)
- **prompt_plan_detallado_implementacion_v1.1.md** - Plan maestro de implementación (v1.1)
- **prompt_verificacion_cumplimiento_v1.0.md** - Verificación de cumplimiento

### Fase Implement (v0.4.x) ✅
- **prompt_implementacion_completa_v1.0.md** - Implementación completa del sistema (v1.0)
- **prompt_implementacion_completa_v1.1.md** - Implementación completa del sistema (v1.1)
- **prompt_implementacion_completa_v1.2.md** - Implementación completa del sistema (v1.2)
- **prompt_revisor_par_v1.0.md** - Revisor de código par
- **prompt_documentacion_interna_codigo_v2.0.md** - Documentación interna de código
- **rule_spring_boot_patterns_v1.0.md** - Patrones Spring Boot para Sistema Ticketero
- **rule_jpa_entities_database_v1.0.md** - JPA Entities & Database para PostgreSQL
- **rule_dtos_validation_v1.0.md** - DTOs & Validation con Records Java 17
- **rule_java21_features_v1.0.md** - Features modernas Java 17 (preparado para Java 21)
- **rule_lombok_best_practices_v1.0.md** - Lombok Best Practices específicas
- **inconsistencias_resueltas_v1.0.md** - Resolución de inconsistencias detectadas

### Fase Verify (v0.5.x) ✅
- **prompt-unit-tests_v1.0.md** - Prompts para testing unitario
- **prompt_testing_e2e_funcional_v1.0.md** - Testing end-to-end funcional
- **prompt_testing_no_funcional_v1.0.md** - Testing no funcional (v1.0)
- **prompt_testing_no_funcional_v2.0_refinado.md** - Testing no funcional refinado (v2.0)
- **prompt_functional_tests_docker_migration_v1.0.md** - Migración de tests funcionales a Docker

### Fase Deploy (v0.6.x) ✅
- **prompt_docker_setup_analysis_v1.0.md** - Análisis de configuración Docker
- **prompt_environment_variables_v1.0.md** - Variables de entorno (v1.0)
- **prompt_environment_variables_v2.0.md** - Variables de entorno (v2.0)
- **prompt_deployment_validation_v1.0.md** - Validación de despliegue

### Fase Document (v0.7.x) ✅
- **prompt_documentador_codigo_v1.0.md** - Documentador de código
- **prompt_actualizador_documentacion_v1.0.md** - Actualizador de documentación
- **prompt_auditor_consistencia_v1.0.md** - Auditor de consistencia
- **prompt_documentation_implementation_v1.0.md** - Implementación de documentación
- **prompt_documentation_audit_v1.0.md** - Auditoría de documentación

## Estado del Proyecto

**Fecha:** 2025-12-24  
**Etapa Actual:** COMPLETADO (v1.0)  
**Última Versión:** v1.0 (todas las etapas completadas)  
**Progreso:** 7/7 etapas completadas ✅

### Artefactos Generados
- ✅ **Tasks:** Requerimientos de negocio y análisis funcional IEEE 830
- ✅ **Brainstorm:** Diseño de arquitectura de software completo con 3 diagramas core
- ✅ **Plan:** Plan detallado de implementación y reglas de desarrollo
- ✅ **Implement:** Sistema completo con Spring Boot, PostgreSQL, Telegram API
- ✅ **Verify:** Framework de testing completo (unitario, funcional, e2e, no funcional)
- ✅ **Deploy:** Configuración Docker, variables de entorno, validación de despliegue
- ✅ **Document:** Documentación completa (API, base de datos, manual de usuario)

### Estadísticas del Proyecto
- **Total de prompts:** 35+ prompts especializados
- **Versiones iterativas:** Múltiples versiones refinadas por fase
- **Cobertura completa:** Todas las fases del workflow implementadas
- **Calidad verificada:** Cumple reglas de simplicidad y verificabilidad

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