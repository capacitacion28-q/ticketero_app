# Regla 3: DTOs & Validation - Sistema Ticketero

**Versión:** 1.0  
**Fecha:** 2025-12-22  
**Aplicable a:** Agente Desarrollador - Sistema Ticketero  
**Stack:** Java 17 + Spring Boot 3.2 + PostgreSQL + Docker

---

## REGLA ORIGINAL
NUNCA exponer entities en API. Usar DTOs (Records) con validaciones Jakarta para todos los endpoints. Controller retorna DTO, nunca Entity. Validaciones en DTOs con @Valid, exception handling global y mappers para conversión Entity ↔ DTO.

## ADAPTACIÓN AL PROYECTO TICKETERO

### Objetivo
Implementar DTOs inmutables con Records para el Sistema Ticketero, validaciones específicas del dominio (tickets, mensajes, usuarios) y mappers para conversión segura entre entities y DTOs.

### Implementación

**Regla unificada del Sistema Ticketero:**
```
❌ DTOs con Lombok (@Value, @Builder, @Data)
✅ DTOs con Records puros (sin Lombok)
```

**IMPORTANTE:** Los DTOs en el Sistema Ticketero usan Records puros de Java 17, NO Lombok. Lombok se usa solo en Entities, Services y Controllers.

**Request DTOs - Sistema Ticketero:**

**TicketRequest:**
```java
public record TicketRequest(
    @NotBlank(message = "Título es requerido")
    @Size(min = 5, max = 200, message = "Título debe tener entre 5-200 caracteres")
    String titulo,
    
    @NotBlank(message = "Descripción es requerida")
    @Size(min = 10, max = 5000, message = "Descripción debe tener entre 10-5000 caracteres")
    String descripcion,
    
    @NotNull(message = "Usuario ID es requerido")
    @Positive(message = "Usuario ID debe ser positivo")
    Long usuarioId,
    
    @Pattern(regexp = "^(BAJA|MEDIA|ALTA|CRITICA)$", message = "Prioridad debe ser: BAJA, MEDIA, ALTA o CRITICA")
    String prioridad,
    
    @Future(message = "Fecha límite debe ser futura")
    LocalDateTime fechaLimite,
    
    @Valid
    List<String> tags
) {
    // Constructor con valores por defecto
    public TicketRequest {
        if (prioridad == null) {
            prioridad = "MEDIA";
        }
    }
}
```

**MensajeRequest:**
```java
public record MensajeRequest(
    @NotBlank(message = "Contenido del mensaje es requerido")
    @Size(min = 1, max = 10000, message = "Contenido debe tener entre 1-10000 caracteres")
    String contenido,
    
    @NotNull(message = "Ticket ID es requerido")
    @Positive(message = "Ticket ID debe ser positivo")
    Long ticketId,
    
    @NotNull(message = "Usuario ID es requerido")
    @Positive(message = "Usuario ID debe ser positivo")
    Long usuarioId,
    
    Boolean esInterno
) {
    // Constructor con valor por defecto
    public MensajeRequest {
        if (esInterno == null) {
            esInterno = false;
        }
    }
}
```

**UsuarioRequest:**
```java
public record UsuarioRequest(
    @NotBlank(message = "Email es requerido")
    @Email(message = "Email debe tener formato válido")
    @Size(max = 100, message = "Email no puede exceder 100 caracteres")
    String email,
    
    @NotBlank(message = "Nombre es requerido")
    @Size(min = 2, max = 50, message = "Nombre debe tener entre 2-50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Nombre solo puede contener letras")
    String nombre,
    
    @NotBlank(message = "Apellido es requerido")
    @Size(min = 2, max = 50, message = "Apellido debe tener entre 2-50 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "Apellido solo puede contener letras")
    String apellido,
    
    @NotNull(message = "Tipo de usuario es requerido")
    TipoUsuario tipo,
    
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$",
             message = "Password debe tener 8+ caracteres con letras y números")
    String password
) {}
```

**Response DTOs - Sistema Ticketero:**

**TicketResponse:**
```java
public record TicketResponse(
    Long id,
    String titulo,
    String descripcion,
    Long usuarioId,
    String usuarioNombre,
    String estado,
    String prioridad,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion,
    LocalDateTime fechaLimite,
    int totalMensajes,
    List<String> tags
) {
    // Constructor desde Entity
    public TicketResponse(Ticket ticket) {
        this(
            ticket.getId(),
            ticket.getTitulo(),
            ticket.getDescripcion(),
            ticket.getUsuarioId(),
            null, // Se llena en el service
            ticket.getEstado().name(),
            ticket.getPrioridad(),
            ticket.getFechaCreacion(),
            ticket.getFechaActualizacion(),
            ticket.getFechaLimite(),
            ticket.getMensajes().size(),
            ticket.getTags().stream().map(Tag::getNombre).toList()
        );
    }
}
```

**MensajeResponse:**
```java
public record MensajeResponse(
    Long id,
    String contenido,
    Long ticketId,
    Long usuarioId,
    String usuarioNombre,
    Boolean esInterno,
    LocalDateTime fechaCreacion
) {
    // Constructor desde Entity
    public MensajeResponse(Mensaje mensaje) {
        this(
            mensaje.getId(),
            mensaje.getContenido(),
            mensaje.getTicket().getId(),
            mensaje.getUsuarioId(),
            null, // Se llena en el service
            mensaje.getEsInterno(),
            mensaje.getFechaCreacion()
        );
    }
}
```

**UsuarioResponse:**
```java
public record UsuarioResponse(
    Long id,
    String email,
    String nombre,
    String apellido,
    String nombreCompleto,
    String tipo,
    String estado,
    LocalDateTime fechaCreacion,
    int totalTickets
) {
    // Constructor desde Entity
    public UsuarioResponse(Usuario usuario) {
        this(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getNombre() + " " + usuario.getApellido(),
            usuario.getTipo().name(),
            usuario.getEstado().name(),
            usuario.getFechaCreacion(),
            0 // Se llena en el service
        );
    }
}
```

**DTOs de Lista y Paginación:**

**TicketListResponse:**
```java
public record TicketListResponse(
    List<TicketResponse> tickets,
    int totalCount,
    int pageNumber,
    int pageSize,
    String filtroEstado,
    Long filtroUsuarioId
) {}
```

**MensajeListResponse:**
```java
public record MensajeListResponse(
    List<MensajeResponse> mensajes,
    Long ticketId,
    int totalCount,
    boolean incluyeInternos
) {}
```

**Validaciones Específicas del Sistema Ticketero:**

**EstadoUpdateRequest:**
```java
public record EstadoUpdateRequest(
    @NotNull(message = "Estado es requerido")
    EstadoTicket estado,
    
    @Size(max = 500, message = "Comentario no puede exceder 500 caracteres")
    String comentario
) {}
```

**TicketSearchRequest:**
```java
public record TicketSearchRequest(
    EstadoTicket estado,
    Long usuarioId,
    
    @Past(message = "Fecha desde debe ser en el pasado")
    LocalDateTime fechaDesde,
    
    @Future(message = "Fecha hasta debe ser en el futuro")
    LocalDateTime fechaHasta,
    
    @Size(min = 3, max = 100, message = "Término de búsqueda debe tener entre 3-100 caracteres")
    String termino,
    
    @Pattern(regexp = "^(BAJA|MEDIA|ALTA|CRITICA)$", message = "Prioridad debe ser: BAJA, MEDIA, ALTA o CRITICA")
    String prioridad,
    
    List<String> tags,
    
    @Min(value = 0, message = "Página debe ser mayor o igual a 0")
    Integer page,
    
    @Min(value = 1, message = "Tamaño de página debe ser mayor a 0")
    @Max(value = 100, message = "Tamaño de página no puede exceder 100")
    Integer size
) {
    // Constructor con valores por defecto
    public TicketSearchRequest {
        if (page == null) page = 0;
        if (size == null) size = 20;
    }
}
```

**Validación de DTOs Anidados - Sistema Ticketero:**

**TicketConMensajesRequest:**
```java
public record TicketConMensajesRequest(
    @Valid TicketRequest ticket,
    
    @NotEmpty(message = "Debe incluir al menos un mensaje inicial")
    @Valid
    List<MensajeRequest> mensajesIniciales
) {}
```

**TicketBulkRequest:**
```java
public record TicketBulkRequest(
    @NotEmpty(message = "Lista de tickets no puede estar vacía")
    @Size(max = 50, message = "No se pueden procesar más de 50 tickets a la vez")
    @Valid
    List<TicketRequest> tickets,
    
    @NotNull(message = "Acción es requerida")
    @Pattern(regexp = "^(CREAR|ACTUALIZAR|CERRAR)$", message = "Acción debe ser: CREAR, ACTUALIZAR o CERRAR")
    String accion
) {}
```

**Validación Condicional - Sistema Ticketero:**

```java
public interface CreateTicketValidation {}
public interface UpdateTicketValidation {}

public record TicketRequest(
    @Null(groups = CreateTicketValidation.class, message = "ID debe ser null al crear")
    @NotNull(groups = UpdateTicketValidation.class, message = "ID es requerido al actualizar")
    Long id,
    
    @NotBlank(groups = {CreateTicketValidation.class, UpdateTicketValidation.class})
    String titulo,
    
    @NotBlank(groups = CreateTicketValidation.class, message = "Descripción requerida al crear")
    String descripcion,
    
    @NotNull(groups = CreateTicketValidation.class, message = "Usuario requerido al crear")
    Long usuarioId
) {}
```

**Controller con Validación - Sistema Ticketero:**

```java
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {
    
    private final TicketService ticketService;
    
    @PostMapping
    public ResponseEntity<TicketResponse> create(
        @Validated(CreateTicketValidation.class) @RequestBody TicketRequest request
    ) {
        log.info("Creating ticket: {}", request.titulo());
        TicketResponse response = ticketService.create(request);
        return ResponseEntity.status(201).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponse> update(
        @PathVariable Long id,
        @Validated(UpdateTicketValidation.class) @RequestBody TicketRequest request
    ) {
        TicketResponse response = ticketService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<TicketResponse> updateEstado(
        @PathVariable Long id,
        @Valid @RequestBody EstadoUpdateRequest request
    ) {
        TicketResponse response = ticketService.updateEstado(id, request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/mensajes")
    public ResponseEntity<MensajeResponse> addMensaje(
        @PathVariable Long id,
        @Valid @RequestBody MensajeRequest request
    ) {
        MensajeResponse response = ticketService.addMensaje(id, request);
        return ResponseEntity.status(201).body(response);
    }
    
    @GetMapping
    public ResponseEntity<TicketListResponse> search(
        @Valid @ModelAttribute TicketSearchRequest request
    ) {
        TicketListResponse response = ticketService.search(request);
        return ResponseEntity.ok(response);
    }
}
```

**ErrorResponse - Sistema Ticketero:**

```java
public record ErrorResponse(
    String message,
    int status,
    LocalDateTime timestamp,
    List<String> errors,
    String path
) {
    // Constructor simple
    public ErrorResponse(String message, int status, String path) {
        this(message, status, LocalDateTime.now(), List.of(), path);
    }
    
    // Constructor con errores múltiples
    public ErrorResponse(String message, int status, List<String> errors, String path) {
        this(message, status, LocalDateTime.now(), errors, path);
    }
}
```

**Exception Handler Global - Sistema Ticketero:**

```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException ex,
        HttpServletRequest request
    ) {
        List<String> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .toList();
        
        log.error("Validation errors in Sistema Ticketero: {}", errors);
        
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(
                "Error de validación en datos del ticket/mensaje/usuario", 
                400, 
                errors, 
                request.getRequestURI()
            ));
    }
    
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTicketNotFound(
        TicketNotFoundException ex,
        HttpServletRequest request
    ) {
        log.error("Ticket not found: {}", ex.getMessage());
        return ResponseEntity
            .status(404)
            .body(new ErrorResponse(ex.getMessage(), 404, request.getRequestURI()));
    }
    
    @ExceptionHandler(MensajeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMensajeNotFound(
        MensajeNotFoundException ex,
        HttpServletRequest request
    ) {
        log.error("Mensaje not found: {}", ex.getMessage());
        return ResponseEntity
            .status(404)
            .body(new ErrorResponse(ex.getMessage(), 404, request.getRequestURI()));
    }
    
    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNotFound(
        UsuarioNotFoundException ex,
        HttpServletRequest request
    ) {
        log.error("Usuario not found: {}", ex.getMessage());
        return ResponseEntity
            .status(404)
            .body(new ErrorResponse(ex.getMessage(), 404, request.getRequestURI()));
    }
    
    @ExceptionHandler(InvalidTicketStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTicketState(
        InvalidTicketStateException ex,
        HttpServletRequest request
    ) {
        log.error("Invalid ticket state transition: {}", ex.getMessage());
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(ex.getMessage(), 400, request.getRequestURI()));
    }
}
```

**Mappers Entity ↔ DTO - Sistema Ticketero:**

### Opción 1: Mapper Manual (RECOMENDADO para Sistema Ticketero)

```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final UsuarioRepository usuarioRepository;
    
    @Transactional
    public TicketResponse create(TicketRequest request) {
        // Request DTO → Entity (mapeo manual)
        Ticket ticket = Ticket.builder()
            .titulo(request.titulo())
            .descripcion(request.descripcion())
            .usuarioId(request.usuarioId())
            .prioridad(request.prioridad())
            .fechaLimite(request.fechaLimite())
            .estado(EstadoTicket.ABIERTO)
            .build();
        
        Ticket saved = ticketRepository.save(ticket);
        
        // Entity → Response DTO (mapeo manual)
        return toTicketResponse(saved);
    }
    
    // Método privado de mapeo manual
    private TicketResponse toTicketResponse(Ticket ticket) {
        Usuario usuario = usuarioRepository.findById(ticket.getUsuarioId())
            .orElse(null);
        
        return new TicketResponse(
            ticket.getId(),
            ticket.getTitulo(),
            ticket.getDescripcion(),
            ticket.getUsuarioId(),
            usuario != null ? usuario.getNombre() + " " + usuario.getApellido() : null,
            ticket.getEstado().name(),
            ticket.getPrioridad(),
            ticket.getFechaCreacion(),
            ticket.getFechaActualizacion(),
            ticket.getFechaLimite(),
            ticket.getMensajes().size(),
            ticket.getTags().stream().map(Tag::getNombre).toList()
        );
    }
}
```

### Opción 2: MapStruct (Solo si hay >20 DTOs en el proyecto)

**TicketMapper:**
```java
@Mapper(componentModel = "spring")
public interface TicketMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "estado", constant = "ABIERTO")
    @Mapping(target = "mensajes", ignore = true)
    @Mapping(target = "tags", ignore = true)
    Ticket toEntity(TicketRequest request);
    
    @Mapping(target = "estado", expression = "java(ticket.getEstado().name())")
    @Mapping(target = "usuarioNombre", ignore = true) // Se llena en service
    @Mapping(target = "totalMensajes", expression = "java(ticket.getMensajes().size())")
    @Mapping(target = "tags", expression = "java(ticket.getTags().stream().map(Tag::getNombre).toList())")
    TicketResponse toResponse(Ticket ticket);
    
    List<TicketResponse> toResponseList(List<Ticket> tickets);
}
```

**MensajeMapper:**
```java
@Mapper(componentModel = "spring")
public interface MensajeMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "ticket", ignore = true) // Se asigna en service
    Mensaje toEntity(MensajeRequest request);
    
    @Mapping(target = "ticketId", source = "ticket.id")
    @Mapping(target = "usuarioNombre", ignore = true) // Se llena en service
    MensajeResponse toResponse(Mensaje mensaje);
    
    List<MensajeResponse> toResponseList(List<Mensaje> mensajes);
}
```

**UsuarioMapper:**
```java
@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "fechaActualizacion", ignore = true)
    @Mapping(target = "estado", constant = "ACTIVO")
    @Mapping(target = "password", ignore = true) // Se procesa en service
    Usuario toEntity(UsuarioRequest request);
    
    @Mapping(target = "tipo", expression = "java(usuario.getTipo().name())")
    @Mapping(target = "estado", expression = "java(usuario.getEstado().name())")
    @Mapping(target = "nombreCompleto", expression = "java(usuario.getNombre() + ' ' + usuario.getApellido())")
    @Mapping(target = "totalTickets", ignore = true) // Se llena en service
    UsuarioResponse toResponse(Usuario usuario);
    
    List<UsuarioResponse> toResponseList(List<Usuario> usuarios);
}
```

**Uso de MapStruct en Service:**
```java
@Service
@RequiredArgsConstructor
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    
    @Transactional
    public TicketResponse create(TicketRequest request) {
        Ticket ticket = ticketMapper.toEntity(request);
        Ticket saved = ticketRepository.save(ticket);
        return ticketMapper.toResponse(saved);
    }
    
    public List<TicketResponse> findAll() {
        return ticketMapper.toResponseList(ticketRepository.findAll());
    }
}
```

**Cuándo usar cada opción en Sistema Ticketero:**
- ✅ Manual: <10 DTOs, mapeos simples, control total sobre lógica de negocio
- ✅ MapStruct: >15 DTOs, mapeos complejos, múltiples proyecciones

**💫 ANTI-PATTERNS - Sistema Ticketero**

### ❌ Exponer Entity en API
```java
@RestController
public class TicketController {
    
    @GetMapping("/tickets/{id}")
    public Ticket getTicket(@PathVariable Long id) {  // ❌ NO!
        return ticketRepository.findById(id).orElseThrow();
    }
    
    @GetMapping("/tickets/{id}/mensajes")
    public List<Mensaje> getMensajes(@PathVariable Long id) {  // ❌ NO!
        return mensajeRepository.findByTicketId(id);
    }
}
```

**Problemas específicos del Sistema Ticketero:**
- Lazy loading de mensajes causa N+1 queries
- Expone estructura interna de BD (ticket_id, usuario_id)
- Circular references Ticket ↔ Mensaje en JSON
- Cambios en entities rompen contratos de API
- Información sensible expuesta (passwords, datos internos)

### ✅ Usar DTO Correctamente
```java
@RestController
public class TicketController {
    
    @GetMapping("/tickets/{id}")
    public ResponseEntity<TicketResponse> getTicket(@PathVariable Long id) {
        return ticketService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/tickets/{id}/mensajes")
    public ResponseEntity<MensajeListResponse> getMensajes(@PathVariable Long id) {
        MensajeListResponse response = mensajeService.findByTicketId(id);
        return ResponseEntity.ok(response);
    }
}
```

### ❌ Validaciones Faltantes
```java
@PostMapping("/tickets")
public ResponseEntity<TicketResponse> create(
    @RequestBody TicketRequest request  // ❌ Falta @Valid!
) {
    // Datos inválidos pasan sin validar
    return ResponseEntity.ok(ticketService.create(request));
}
```

### ✅ Validaciones Correctas
```java
@PostMapping("/tickets")
public ResponseEntity<TicketResponse> create(
    @Valid @RequestBody TicketRequest request  // ✅ Con @Valid
) {
    // Spring valida automáticamente
    return ResponseEntity.status(201).body(ticketService.create(request));
}
```

**📦 REGLAS FINALES - Sistema Ticketero**

1. **NUNCA retornar Entity (Ticket/Mensaje/Usuario) desde controller**
2. **SIEMPRE usar Records para DTOs** del Sistema Ticketero (Java 17+)
3. **SIEMPRE `@Valid` en request DTOs** (TicketRequest, MensajeRequest, etc.)
4. **Validaciones en DTO, NO en Entity** para el dominio ticketero
5. **ErrorResponse consistente** para todos los errores del sistema
6. **Mappers manuales primero**, MapStruct solo si hay >15 DTOs
7. **Un DTO Request + un DTO Response** por operación del Sistema Ticketero
8. **DTOs simples** (no lógica de negocio del dominio)
9. **Validaciones en español** para el contexto del proyecto
10. **DTOs de lista con metadatos** (paginación, filtros aplicados)
11. **Exception handlers específicos** del dominio ticketero
12. **Validaciones condicionales** para operaciones CRUD diferentes

**Package Structure - Sistema Ticketero:**

```
com.ticketero.app/
├── model/
│   ├── dto/
│   │   ├── request/
│   │   │   ├── TicketRequest.java
│   │   │   ├── MensajeRequest.java
│   │   │   ├── UsuarioRequest.java
│   │   │   ├── EstadoUpdateRequest.java
│   │   │   └── TicketSearchRequest.java
│   │   └── response/
│   │       ├── TicketResponse.java
│   │       ├── MensajeResponse.java
│   │       ├── UsuarioResponse.java
│   │       ├── TicketListResponse.java
│   │       ├── MensajeListResponse.java
│   │       └── ErrorResponse.java
│   └── entity/
│       ├── Ticket.java
│       ├── Mensaje.java
│       ├── Usuario.java
│       └── Tag.java
├── validation/
│   ├── CreateTicketValidation.java
│   └── UpdateTicketValidation.java
└── exception/
    ├── TicketNotFoundException.java
    ├── MensajeNotFoundException.java
    ├── UsuarioNotFoundException.java
    ├── InvalidTicketStateException.java
    └── GlobalExceptionHandler.java
```

**Checklist DTOs - Sistema Ticketero:**

Antes de crear DTO:
- [ ] ¿Es un Record (Java 17+)?
- [ ] ¿Tiene validaciones específicas del dominio ticketero?
- [ ] ¿Campos son inmutables?
- [ ] ¿Naming claro? (TicketRequest/TicketResponse/MensajeRequest/etc.)
- [ ] ¿Se usa `@Valid` o `@Validated` en controller?
- [ ] ¿Exception handler maneja errores específicos del Sistema Ticketero?
- [ ] ¿NO expone Entity (Ticket/Mensaje/Usuario) directamente?
- [ ] ¿Service retorna DTO, no Entity?
- [ ] ¿Validaciones incluyen reglas específicas del dominio ticketero?
- [ ] ¿DTOs de lista incluyen metadatos de paginación?
- [ ] ¿ErrorResponse incluye contexto del Sistema Ticketero?
- [ ] ¿Mappers (manual o MapStruct) implementados correctamente?
- [ ] ¿Validaciones condicionales para CRUD diferentes?
- [ ] ¿Anti-patterns evitados (no exponer entities)?
- [ ] ¿DTOs anidados con @Valid cuando sea necesario?
- [ ] ¿Validaciones avanzadas (@Pattern, @Past, @Future) implementadas?

### Referencias del Proyecto
- **DTOs Request:** `TicketRequest.java`, `MensajeRequest.java`, `UsuarioRequest.java`, `EstadoUpdateRequest.java`, `TicketSearchRequest.java`
- **DTOs Response:** `TicketResponse.java`, `MensajeResponse.java`, `UsuarioResponse.java`, `TicketListResponse.java`, `ErrorResponse.java`
- **Validaciones:** `CreateTicketValidation.java`, `UpdateTicketValidation.java`
- **Excepciones:** `TicketNotFoundException.java`, `MensajeNotFoundException.java`, `InvalidTicketStateException.java`
- **Documentos de referencia:** 
  - `docs/architecture/software_architecture_design_v1.0.md`
  - `docs/implementation/plan_detallado_implementacion_v1.0.md`

### Criterios de Validación
□ Todos los Controllers usan DTOs (Records), nunca entities
□ Todos los Request DTOs tienen validaciones Jakarta apropiadas
□ `@Valid` o `@Validated` usado en todos los endpoints
□ Exception handler global maneja errores específicos del Sistema Ticketero
□ Services retornan DTOs, nunca entities JPA
□ DTOs incluyen constructores desde entities cuando es útil
□ Validaciones condicionales implementadas con grupos
□ DTOs anidados con @Valid para operaciones complejas
□ Anti-patterns evitados (no exposición de entities)
□ Validaciones avanzadas (@Pattern, @Past, @Future) implementadas
□ ErrorResponse consistente con contexto del Sistema Ticketero
□ Package structure separa request/response DTOs
□ DTOs de lista incluyen metadatos de paginación
□ Validaciones incluyen reglas específicas del dominio ticketero
□ Mappers manuales implementados en services
□ DTOs son inmutables (Records)
□ Naming conventions claras (TicketRequest/TicketResponse)

### Comandos de Verificación
```bash
# Verificar que no se exponen entities en controllers
grep -r "ResponseEntity<.*Entity>" src/main/java/controller/

# Verificar uso de Records para DTOs
grep -r "public record.*Request\|public record.*Response" src/main/java/

# Verificar validaciones Jakarta en DTOs
grep -r "@NotBlank\|@NotNull\|@Valid\|@Email\|@Size" src/main/java/dto/

# Verificar uso de @Valid en controllers
grep -r "@Valid\|@Validated" src/main/java/controller/

# Verificar exception handlers específicos
grep -r "TicketNotFoundException\|MensajeNotFoundException" src/main/java/

# Verificar estructura de packages DTOs
find src/main/java -type d | grep -E "(dto|request|response)"

# Verificar que services retornan DTOs
grep -r "public.*Response\|Optional<.*Response>" src/main/java/service/

# Verificar ErrorResponse consistente
grep -r "ErrorResponse" src/main/java/

# Verificar validaciones condicionales
grep -r "@Validated.*\.class" src/main/java/

# Verificar mappers manuales en services
grep -r "toResponse\|toRequest" src/main/java/service/

# Verificar DTOs de lista con metadatos
grep -r "ListResponse\|totalCount\|pageNumber" src/main/java/dto/

# Verificar DTOs anidados con @Valid
grep -r "@Valid.*List<\|@Valid.*Request" src/main/java/dto/

# Verificar anti-patterns (no entities en controllers)
grep -r "ResponseEntity<.*Entity>\|return.*Entity" src/main/java/controller/

# Verificar MapStruct si se usa
grep -r "@Mapper\|componentModel" src/main/java/

# Verificar validaciones avanzadas
grep -r "@Pattern\|@Past\|@Future\|@DecimalMin\|@DecimalMax" src/main/java/dto/

# Verificar validaciones específicas del dominio
grep -r "Ticket.*debe\|Mensaje.*requerido\|Usuario.*válido" src/main/java/dto/
```

---
**Estado:** Listo para migración a `.amazonq\rules\`