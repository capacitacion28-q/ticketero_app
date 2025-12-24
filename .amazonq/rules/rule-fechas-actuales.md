# Regla: Fechas Actuales en Documentos - Amazon Q Developer

**Versión:** 1.0  
**Fecha:** 2025-12-23  
**Tipo:** Guideline de Memory Bank  
**Aplicabilidad:** Todos los agentes que generen documentos

---

## REGLA OBLIGATORIA: FECHAS ACTUALES EN DOCUMENTOS

### **PROBLEMA IDENTIFICADO:**
Los agentes frecuentemente incluyen fechas incorrectas en documentos generados, usando fechas asumidas o desactualizadas en lugar de la fecha real actual.

### **REGLA ESPECÍFICA:**

**CUANDO un agente genere cualquier documento que incluya fecha de creación, DEBE:**

1. **NUNCA asumir la fecha actual**
2. **SIEMPRE preguntar explícitamente al usuario:** "¿Cuál es la fecha actual para incluir en el documento?"
3. **USAR ÚNICAMENTE la fecha proporcionada por el usuario**
4. **APLICAR en TODAS las ubicaciones** donde aparezca fecha en el documento

### **FORMATO OBLIGATORIO DE CONSULTA:**

```
🗓️ CONSULTA DE FECHA REQUERIDA:
Para generar este documento correctamente, necesito confirmar:
¿Cuál es la fecha actual (formato YYYY-MM-DD)?

Una vez confirmada, procederé a crear el documento con la fecha correcta.
```

### **UBICACIONES DONDE APLICAR:**

- ✅ **Encabezados de documentos:** `**Fecha:** YYYY-MM-DD`
- ✅ **Metadatos:** `**Fecha de creación:** YYYY-MM-DD`
- ✅ **Pie de documentos:** `**Generado el:** YYYY-MM-DD`
- ✅ **Reportes:** `**Fecha de ejecución:** YYYY-MM-DD`
- ✅ **Logs y timestamps:** Cualquier referencia temporal
- ✅ **Comentarios en código:** `// Generado el YYYY-MM-DD`

### **EJEMPLOS DE APLICACIÓN:**

#### ❌ **INCORRECTO:**
```markdown
# Reporte de Testing
**Fecha:** 2024-12-19  # Fecha asumida
```

#### ✅ **CORRECTO:**
```markdown
🗓️ CONSULTA DE FECHA REQUERIDA:
¿Cuál es la fecha actual para este reporte?

[Esperar respuesta del usuario]

# Reporte de Testing  
**Fecha:** 2025-12-23  # Fecha confirmada por usuario
```

### **CASOS ESPECIALES:**

1. **Documentos históricos:** Si el documento se refiere a una fecha específica del pasado, clarificar con el usuario
2. **Versionado:** Incluir fecha real de creación de cada versión
3. **Actualizaciones:** Distinguir entre fecha de creación original y fecha de última modificación

### **VALIDACIÓN:**

Antes de finalizar cualquier documento, el agente DEBE verificar:
- ✅ ¿Incluye fechas el documento?
- ✅ ¿Consulté la fecha actual al usuario?
- ✅ ¿Usé la fecha proporcionada en TODAS las ubicaciones?
- ✅ ¿Es consistente la fecha en todo el documento?

### **EXCEPCIONES:**

**NINGUNA.** Esta regla aplica sin excepciones a todos los documentos generados.

---

## IMPLEMENTACIÓN EN PROMPTS

### **INSTRUCCIÓN PARA INCLUIR EN TODOS LOS PROMPTS:**

```markdown
## REGLA OBLIGATORIA: FECHAS ACTUALES

**ANTES de generar cualquier documento con fechas, el agente DEBE:**
1. Preguntar explícitamente: "¿Cuál es la fecha actual (YYYY-MM-DD)?"
2. Esperar confirmación del usuario
3. Usar ÚNICAMENTE la fecha proporcionada
4. Aplicar consistentemente en todo el documento

**NUNCA asumir fechas. SIEMPRE consultar al usuario.**
```

### **TEMPLATE DE CONSULTA ESTÁNDAR:**

```markdown
🗓️ **CONSULTA DE FECHA OBLIGATORIA:**

Para generar este documento correctamente, necesito confirmar:
**¿Cuál es la fecha actual en formato YYYY-MM-DD?**

Una vez confirmada, procederé con la generación del documento usando la fecha correcta en todas las ubicaciones necesarias.

⏸️ **ESPERANDO CONFIRMACIÓN DE FECHA...**
```

---

## BENEFICIOS DE ESTA REGLA

- ✅ **Precisión temporal:** Documentos con fechas correctas
- ✅ **Trazabilidad:** Fechas reales de creación/modificación
- ✅ **Profesionalismo:** Documentos con metadatos precisos
- ✅ **Consistencia:** Mismo formato de fecha en todo el proyecto
- ✅ **Confiabilidad:** Eliminación de fechas asumidas incorrectas

---

**Regla creada por:** Ingeniero de Prompts Senior  
**Aplicable a:** Todos los agentes Amazon Q Developer  
**Fecha de creación:** 2025-12-23  
**Versión:** 1.0