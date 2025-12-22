# Inconsistencias Resueltas - Reglas Sistema Ticketero

**Versión:** 1.0  
**Fecha:** 2025-12-22
**Aplicable a:** Agente Desarrollador - Sistema Ticketero  
**Stack:** Java 17 + Spring Boot 3.2 + PostgreSQL + Docker

---

## INCONSISTENCIAS IDENTIFICADAS Y RESOLUCIONES

### 1. ❌ INCONSISTENCIA: Versión de Java

**Problema encontrado:**
- `rule_java21_features_v1.0.md` menciona Java 21 pero el stack es Java 17
- Confusión sobre qué features están disponibles

**✅ RESOLUCIÓN:**
- **Stack oficial:** Java 17 + Spring Boot 3.2
- **Renombrar:** `rule_java21_features_v1.0.md` → `rule_java17_modern_features_v1.0.md`
- **Features disponibles:** Records, Text Blocks, Pattern Matching básico, Switch Expressions
- **Features preparadas para migración futura:** Virtual Threads, Record Patterns (comentadas)

### 2. ❌ INCONSISTENCIA: DTOs - Records vs Lombok

**Problema encontrado:**
- `rule_dtos_validation_v1.0.md`: "SIEMPRE usar Records"
- `rule_lombok_best_practices_v1.0.md`: Muestra @Value y @Builder para DTOs

**✅ RESOLUCIÓN:**
```java
// ✅ ESTÁNDAR ÚNICO: Records puros para DTOs
public record TicketRequest(
    @NotBlank String titulo,
    @NotBlank String descripcion,
    @NotNull Long usuarioId
) {}

// ❌ NO usar Lombok en DTOs
// @Value @Builder public class TicketRequest { ... }
```

**Regla unificada:** DTOs = Records puros, sin Lombok

### 3. ❌ INCONSISTENCIA: Entities - Uso de @Data

**Problema encontrado:**
- `rule_jpa_entities_database_v1.0.md`: Usa @Data en entities
- `rule_lombok_best_practices_v1.0.md`: Prohíbe @Data en entities con relaciones

**✅ RESOLUCIÓN:**
```java
// ✅ ESTÁNDAR ÚNICO: NO @Data en entities
@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @OneToMany(mappedBy = "ticket")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Mensaje> mensajes;
}

// ❌ NO usar @Data en entities
// @Entity @Data public class Ticket { ... }
```

**Regla unificada:** Entities = @Getter @Setter @Builder, NUNCA @Data

### 4. ❌ INCONSISTENCIA: Ubicación de Validaciones

**Problema encontrado:**
- `rule_dtos_validation_v1.0.md`: "Validaciones en DTO, NO en Entity"
- `rule_jpa_entities_database_v1.0.md`: Muestra validaciones en Entity

**✅ RESOLUCIÓN:**
```java
// ✅ VALIDACIONES SOLO EN DTOs
public record TicketRequest(
    @NotBlank @Size(min = 5, max = 200) String titulo,
    @NotBlank @Size(min = 10, max = 5000) String descripcion,
    @NotNull @Positive Long usuarioId
) {}

// ✅ ENTITIES SIN VALIDACIONES JAKARTA
@Entity
public class Ticket {
    @Column(nullable = false, length = 200)
    private String titulo;  // Sin @NotBlank
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;  // Sin @Size
}
```

**Regla unificada:** Validaciones = Solo en DTOs, Entities = Solo constraints DB

### 5. ❌ INCONSISTENCIA: Package Structure

**Problema encontrado:**
- Diferentes estructuras propuestas en cada documento

**✅ RESOLUCIÓN - ESTRUCTURA ÚNICA:**
```
com.ticketero.app/
├── controller/              # @RestController
│   ├── TicketController.java
│   └── MensajeController.java
├── service/                 # @Service
│   ├── TicketService.java
│   └── MensajeService.java
├── repository/              # @Repository
│   ├── TicketRepository.java
│   └── MensajeRepository.java
├── model/
│   ├── entity/              # @Entity (JPA)
│   │   ├── Ticket.java
│   │   └── Mensaje.java
│   └── dto/                 # Records
│       ├── TicketRequest.java
│       ├── TicketResponse.java
│       ├── MensajeRequest.java
│       └── MensajeResponse.java
├── config/                  # @Configuration
│   └── TicketeroConfig.java
└── exception/               # @ControllerAdvice
    ├── TicketNotFoundException.java
    └── GlobalExceptionHandler.java
```

### 6. ❌ INCONSISTENCIA: Mappers Entity ↔ DTO

**Problema encontrado:**
- Diferentes enfoques: manual vs MapStruct vs constructores

**✅ RESOLUCIÓN - ENFOQUE ÚNICO:**
```java
// ✅ ESTÁNDAR: Mappers manuales en Service
@Service
@RequiredArgsConstructor
public class TicketService {
    
    public TicketResponse create(TicketRequest request) {
        // Request → Entity
        Ticket ticket = Ticket.builder()
            .titulo(request.titulo())
            .descripcion(request.descripcion())
            .usuarioId(request.usuarioId())
            .build();
        
        Ticket saved = ticketRepository.save(ticket);
        
        // Entity → Response
        return new TicketResponse(
            saved.getId(),
            saved.getTitulo(),
            saved.getDescripcion(),
            saved.getEstado().name(),
            saved.getFechaCreacion()
        );
    }
}

// ❌ NO usar MapStruct inicialmente
// ❌ NO usar constructores desde Entity en Records
```

**Regla unificada:** Mappers = Manuales en Service, MapStruct solo si >20 DTOs

---

## REGLAS CONSOLIDADAS FINALES

### 🎯 STACK TECNOLÓGICO DEFINITIVO
- **Java:** 17 (features modernas disponibles)
- **Spring Boot:** 3.2
- **Base de datos:** PostgreSQL
- **Contenedores:** Docker
- **Build:** Maven

### 🏗️ ARQUITECTURA OBLIGATORIA
```
Controller → Service → Repository → Database
    ↓         ↓          ↓
  DTOs    Lógica     Entities
```

### 📦 PATRONES DE CÓDIGO UNIFICADOS

**DTOs (Request/Response):**
```java
public record TicketRequest(
    @NotBlank @Size(min = 5, max = 200) String titulo,
    @NotBlank String descripcion,
    @NotNull Long usuarioId
) {}
```

**Entities (JPA):**
```java
@Entity
@Table(name = "tickets")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Ticket {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToMany(mappedBy = "ticket")
    @ToString.Exclude @EqualsAndHashCode.Exclude
    private List<Mensaje> mensajes;
}
```

**Services:**
```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TicketService {
    private final TicketRepository ticketRepository;
    
    @Transactional
    public TicketResponse create(TicketRequest request) {
        // Mapeo manual Request → Entity → Response
    }
}
```

**Controllers:**
```java
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {
    private final TicketService ticketService;
    
    @PostMapping
    public ResponseEntity<TicketResponse> create(
        @Valid @RequestBody TicketRequest request
    ) {
        return ResponseEntity.status(201).body(ticketService.create(request));
    }
}
```

### ✅ CHECKLIST UNIFICADO

**Antes de crear cualquier clase:**
- [ ] ¿Package correcto según estructura única?
- [ ] ¿DTO? → Record puro (sin Lombok)
- [ ] ¿Entity? → @Getter @Setter @Builder (sin @Data)
- [ ] ¿Service? → @RequiredArgsConstructor @Slf4j @Transactional
- [ ] ¿Controller? → @RestController @RequiredArgsConstructor @Slf4j
- [ ] ¿Validaciones? → Solo en DTOs (nunca en Entities)
- [ ] ¿Relaciones JPA? → @ToString.Exclude @EqualsAndHashCode.Exclude
- [ ] ¿Mapeo? → Manual en Service (no MapStruct inicialmente)

### 🚫 ANTI-PATTERNS PROHIBIDOS

1. **❌ @Data en entities con relaciones**
2. **❌ Lombok en DTOs (usar Records)**
3. **❌ Validaciones Jakarta en Entities**
4. **❌ Exponer Entities en Controllers**
5. **❌ MapStruct sin justificación (>20 DTOs)**
6. **❌ Constructores desde Entity en Records**
7. **❌ Package structure diferente a la definida**

---

## MIGRACIÓN DE DOCUMENTOS EXISTENTES

### Cambios requeridos:

1. **rule_java21_features_v1.0.md** → **rule_java17_modern_features_v1.0.md**
   - Remover features Java 21 del contenido principal
   - Mantener solo como comentarios preparatorios

2. **rule_jpa_entities_database_v1.0.md**
   - Remover @Data de ejemplos
   - Remover validaciones Jakarta de entities
   - Usar @Getter @Setter @Builder

3. **rule_lombok_best_practices_v1.0.md**
   - Clarificar: NO Lombok en DTOs
   - Enfatizar: Records puros para DTOs

4. **rule_dtos_validation_v1.0.md**
   - Remover ejemplos de constructores desde Entity
   - Enfatizar mapeo manual en Service

5. **rule_spring_boot_patterns_v1.0.md**
   - Actualizar package structure a la unificada
   - Clarificar mapeo manual como estándar

### Estado: ✅ INCONSISTENCIAS RESUELTAS
**Próximo paso:** Aplicar cambios a documentos individuales según resoluciones definidas.