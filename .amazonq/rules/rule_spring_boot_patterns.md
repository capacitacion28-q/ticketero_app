# Regla 1: Spring Boot Patterns - Sistema Ticketero

### Objetivo
Implementar arquitectura en capas estricta para el Sistema Ticketero con patrones Spring Boot específicos para las entidades Ticket, Mensaje, Usuario y Estado.

### Implementación

**Arquitectura Obligatoria:**
```
TicketController → TicketService → TicketRepository → PostgreSQL
      ↓               ↓              ↓
   HTTP/JSON    Lógica Negocio   Data Access
```

### Ejemplos Específicos

**Controller Pattern - TicketController:**
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
        log.info("Creating ticket: {}", request.titulo());
        TicketResponse response = ticketService.create(request);
        return ResponseEntity.status(201).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id) {
        return ticketService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<TicketResponse> updateEstado(
        @PathVariable Long id,
        @Valid @RequestBody EstadoUpdateRequest request
    ) {
        TicketResponse response = ticketService.updateEstado(id, request.estado());
        return ResponseEntity.ok(response);
    }
}
```

**Service Pattern - TicketService:**
```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final MensajeService mensajeService;
    private final NotificationService notificationService;
    
    @Transactional
    public TicketResponse create(TicketRequest request) {
        // 1. Validar usuario
        validateUsuario(request.usuarioId());
        
        // 2. Crear ticket
        Ticket ticket = Ticket.builder()
            .titulo(request.titulo())
            .descripcion(request.descripcion())
            .usuarioId(request.usuarioId())
            .estado(EstadoTicket.ABIERTO)
            .fechaCreacion(LocalDateTime.now())
            .build();
        
        Ticket saved = ticketRepository.save(ticket);
        
        // 3. Crear mensaje inicial
        mensajeService.createMensajeInicial(saved.getId(), request.descripcion());
        
        // 4. Notificar
        notificationService.sendTicketCreated(saved);
        
        return toResponse(saved);
    }
    
    @Transactional
    public TicketResponse updateEstado(Long id, EstadoTicket nuevoEstado) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new TicketNotFoundException(id));
        
        ticket.setEstado(nuevoEstado);
        ticket.setFechaActualizacion(LocalDateTime.now());
        
        log.info("Ticket {} estado actualizado a {}", id, nuevoEstado);
        return toResponse(ticket);
    }
    
    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
            ticket.getId(),
            ticket.getTitulo(),
            ticket.getDescripcion(),
            ticket.getEstado().name(),
            ticket.getFechaCreacion()
        );
    }
}
```

**Repository Pattern - TicketRepository:**
```java
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // Query derivadas (Spring genera SQL automáticamente)
    Optional<Ticket> findByTitulo(String titulo);
    
    List<Ticket> findByEstado(EstadoTicket estado);
    
    List<Ticket> findByUsuarioId(Long usuarioId);
    
    List<Ticket> findByFechaCreacionAfter(LocalDateTime fecha);
    
    boolean existsByUsuarioIdAndEstado(Long usuarioId, EstadoTicket estado);
    
    long countByEstado(EstadoTicket estado);
    
    // Query custom con @Query (solo cuando sea necesario)
    @Query("""
        SELECT t FROM Ticket t
        WHERE t.estado = :estado
        AND t.fechaCreacion BETWEEN :fechaInicio AND :fechaFin
        ORDER BY t.fechaCreacion DESC
        """)
    List<Ticket> findTicketsByEstadoAndFechas(
        @Param("estado") EstadoTicket estado,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    // Query nativa (último recurso)
    @Query(value = """
        SELECT * FROM tickets t
        WHERE t.estado = ?1
        LIMIT ?2
        """, nativeQuery = true)
    List<Ticket> findTopNByEstado(String estado, int limit);
}
```

**MensajeRepository:**
```java
@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    List<Mensaje> findByTicketId(Long ticketId);
    
    List<Mensaje> findByTicketIdOrderByFechaCreacionAsc(Long ticketId);
    
    boolean existsByTicketIdAndContenido(Long ticketId, String contenido);
    
    long countByTicketId(Long ticketId);
}
```

**Configuration Pattern - TicketeroConfig:**
```java
@Configuration
public class TicketeroConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
    
    @Value("${ticketero.notification.url}")
    private String notificationUrl;
    
    @Value("${ticketero.max.tickets.per.user:10}")
    private int maxTicketsPerUser;
    
    @Value("${ticketero.auto.close.days:30}")
    private int autoCloseDays;
}
```

**Exception Handling - GlobalExceptionHandler:**
```java
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
        MethodArgumentNotValidException ex
    ) {
        String errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        log.error("Validation error: {}", errors);
        
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(errors, 400, LocalDateTime.now()));
    }
    
    @ExceptionHandler(TicketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTicketNotFound(
        TicketNotFoundException ex
    ) {
        log.error("Ticket not found: {}", ex.getMessage());
        return ResponseEntity
            .status(404)
            .body(new ErrorResponse(ex.getMessage(), 404, LocalDateTime.now()));
    }
    
    @ExceptionHandler(MensajeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMensajeNotFound(
        MensajeNotFoundException ex
    ) {
        log.error("Mensaje not found: {}", ex.getMessage());
        return ResponseEntity
            .status(404)
            .body(new ErrorResponse(ex.getMessage(), 404, LocalDateTime.now()));
    }
    
    @ExceptionHandler(MaxTicketsExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxTicketsExceeded(
        MaxTicketsExceededException ex
    ) {
        log.error("Max tickets exceeded: {}", ex.getMessage());
        return ResponseEntity
            .status(400)
            .body(new ErrorResponse(ex.getMessage(), 400, LocalDateTime.now()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity
            .status(500)
            .body(new ErrorResponse("Error interno del servidor", 500, LocalDateTime.now()));
    }
}
```

**Package Structure - Sistema Ticketero:**
```
com.ticketero.app/
├── controller/           # @RestController
│   ├── TicketController.java
│   ├── MensajeController.java
│   └── UsuarioController.java
├── service/              # @Service
│   ├── TicketService.java
│   ├── MensajeService.java
│   ├── UsuarioService.java
│   └── NotificationService.java
├── repository/           # @Repository (interfaces JPA)
│   ├── TicketRepository.java
│   ├── MensajeRepository.java
│   └── UsuarioRepository.java
├── model/
│   ├── entity/           # @Entity (JPA entities)
│   │   ├── Ticket.java
│   │   ├── Mensaje.java
│   │   ├── Usuario.java
│   │   └── EstadoTicket.java
│   └── dto/              # Records (Request/Response)
│       ├── TicketRequest.java
│       ├── TicketResponse.java
│       ├── MensajeRequest.java
│       ├── MensajeResponse.java
│       └── ErrorResponse.java
├── config/               # @Configuration
│   └── TicketeroConfig.java
└── exception/            # Custom exceptions + @ControllerAdvice
    ├── TicketNotFoundException.java
    ├── MensajeNotFoundException.java
    └── GlobalExceptionHandler.java
```

**Checklist Spring Boot - Sistema Ticketero:**

Antes de crear clase, verifica:
- [ ] ¿Capa correcta? (Controller/Service/Repository)
- [ ] ¿Constructor injection con `@RequiredArgsConstructor`?
- [ ] ¿Anotación correcta? (`@Service`/`@RestController`/`@Repository`)
- [ ] ¿Usa Lombok? (`@Slf4j` si necesita logging)
- [ ] ¿Métodos públicos <20 líneas?
- [ ] ¿Logging en operaciones críticas de tickets?
- [ ] ¿Service retorna DTOs, NO entities?
- [ ] ¿Controller usa `@Valid` para validación?
- [ ] ¿Manejo específico de excepciones del dominio?
- [ ] ¿Package correcto según estructura del Sistema Ticketero?

### Referencias del Proyecto
- **Entidades relacionadas:** `Ticket.java`, `Mensaje.java`, `Usuario.java`, `EstadoTicket.java`
- **Servicios aplicables:** `TicketService.java`, `MensajeService.java`, `NotificationService.java`
- **Documentos de referencia:** 
  - `docs/architecture/software_architecture_design_v1.0.md`
  - `docs/implementation/plan_detallado_implementacion_v1.0.md`

### Criterios de Validación
□ Todos los Controllers usan `@RestController` y `ResponseEntity<T>`
□ Todos los Services usan `@Transactional(readOnly = true)` por defecto
□ Inyección de dependencias solo por constructor con `@RequiredArgsConstructor`
□ Repositories extienden `JpaRepository<Entity, Long>`
□ Services retornan DTOs, nunca entidades JPA
□ Manejo de excepciones con `@ControllerAdvice` global
□ Package structure completa del Sistema Ticketero implementada
□ Configuraciones específicas para PostgreSQL y propiedades del proyecto
□ Excepciones custom del dominio (TicketNotFoundException, etc.)
□ Query derivadas preferidas sobre @Query custom
□ Text blocks `"""` para queries multilinea
□ Logging específico para operaciones de tickets y mensajes
□ Validaciones `@Valid` en todos los endpoints
□ HTTP status codes apropiados (201, 204, 404, 400, 500)

### Comandos de Verificación
```bash
# Verificar estructura completa de packages del Sistema Ticketero
find src/main/java -type d | grep -E "(controller|service|repository|entity|dto|config|exception)"

# Verificar todas las anotaciones Spring Boot
grep -r "@RestController\|@Service\|@Repository\|@Configuration\|@ControllerAdvice" src/main/java/

# Verificar inyección por constructor (Lombok)
grep -r "@RequiredArgsConstructor" src/main/java/

# Verificar transacciones y readonly
grep -r "@Transactional" src/main/java/

# Verificar manejo de excepciones específicas del dominio
grep -r "TicketNotFoundException\|MensajeNotFoundException" src/main/java/

# Verificar uso de ResponseEntity
grep -r "ResponseEntity<" src/main/java/

# Verificar validaciones
grep -r "@Valid" src/main/java/

# Verificar queries derivadas vs custom
grep -r "findBy\|countBy\|existsBy" src/main/java/

# Verificar configuraciones específicas del proyecto
grep -r "ticketero\." src/main/resources/application.yml
```
