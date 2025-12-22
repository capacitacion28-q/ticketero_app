# Regla 5: Lombok Best Practices - Sistema Ticketero

**Versión:** 1.0  
**Fecha:** 2025-12-22  
**Aplicable a:** Agente Desarrollador - Sistema Ticketero  
**Stack:** Java 17 + Spring Boot 3.2 + PostgreSQL + Docker

---

## REGLA ORIGINAL
Usar Lombok correctamente para reducir boilerplate sin causar problemas. Anotaciones recomendadas: @RequiredArgsConstructor, @Slf4j, @Builder. Evitar @Data en entities con relaciones, usar @ToString.Exclude y @EqualsAndHashCode.Exclude apropiadamente.

## ADAPTACIÓN AL PROYECTO TICKETERO

**IMPORTANTE:** En el Sistema Ticketero, Lombok se usa SOLO en Entities, Services y Controllers. Los DTOs usan Records puros de Java 17 sin Lombok.

**Regla unificada:**
- DTOs = Records puros (sin Lombok)
- Entities = @Getter @Setter @Builder (sin @Data)
- Services/Controllers = @RequiredArgsConstructor @Slf4j

### Ejemplos Específicos

**@RequiredArgsConstructor para Services - Sistema Ticketero:**

**TicketService con Lombok:**
```java
// ✅ CORRECTO: Constructor injection con Lombok en Sistema Ticketero
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {
    private final TicketRepository ticketRepository;
    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificationService notificationService;
    
    // Lombok genera automáticamente:
    // public TicketService(TicketRepository ticketRepository, 
    //                     MensajeRepository mensajeRepository,
    //                     UsuarioRepository usuarioRepository,
    //                     NotificationService notificationService) {
    //     this.ticketRepository = ticketRepository;
    //     this.mensajeRepository = mensajeRepository;
    //     this.usuarioRepository = usuarioRepository;
    //     this.notificationService = notificationService;
    // }
    
    public TicketResponse create(TicketRequest request) {
        log.info("Creating ticket for user: {}", request.usuarioId());
        // Lógica del servicio...
        log.debug("Ticket created with ID: {}", savedTicket.getId());
        return response;
    }
}

// ❌ INCORRECTO: Constructor manual en Sistema Ticketero
@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificationService notificationService;
    
    // 15+ líneas de constructor boilerplate innecesario
    public TicketService(TicketRepository ticketRepository, 
                        MensajeRepository mensajeRepository,
                        UsuarioRepository usuarioRepository,
                        NotificationService notificationService) {
        this.ticketRepository = ticketRepository;
        this.mensajeRepository = mensajeRepository;
        this.usuarioRepository = usuarioRepository;
        this.notificationService = notificationService;
    }
}
```

**@Slf4j para Logging - Sistema Ticketero:**

**MensajeService con Logging:**
```java
// ✅ CORRECTO: Logger con Lombok en Sistema Ticketero
@Service
@RequiredArgsConstructor
@Slf4j
public class MensajeService {
    
    private final MensajeRepository mensajeRepository;
    private final TicketRepository ticketRepository;
    
    public MensajeResponse create(MensajeRequest request) {
        log.info("Creating mensaje for ticket: {}", request.ticketId());
        
        Ticket ticket = ticketRepository.findById(request.ticketId())
            .orElseThrow(() -> {
                log.error("Ticket not found: {}", request.ticketId());
                return new TicketNotFoundException(request.ticketId());
            });
        
        log.debug("Mensaje created for ticket: {} by user: {}", 
                 request.ticketId(), request.usuarioId());
        return response;
    }
    
    public void markAsRead(Long mensajeId, Long usuarioId) {
        log.info("Marking mensaje {} as read by user {}", mensajeId, usuarioId);
        // Lógica...
        log.debug("Mensaje {} marked as read", mensajeId);
    }
}

// ❌ INCORRECTO: Logger manual
@Service
public class MensajeService {
    private static final Logger log = LoggerFactory.getLogger(MensajeService.class);
    // Boilerplate innecesario
}
```

**Niveles de logging específicos del Sistema Ticketero:**
- `log.error()` - Errores críticos (ticket no encontrado, usuario sin permisos)
- `log.warn()` - Advertencias (ticket ya cerrado, mensaje duplicado)
- `log.info()` - Información importante (creación de tickets, cambios de estado)
- `log.debug()` - Debugging (detalles de procesamiento, validaciones)

**@Builder para Entities - Sistema Ticketero:**

**Ticket Entity con Builder:**
```java
// ✅ CORRECTO: Builder pattern para Entity del Sistema Ticketero
@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String titulo;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoTicket estado;
    
    @Column(nullable = false, length = 20)
    private String prioridad;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Relación 1:N con Mensajes - CRÍTICO: Excluir de toString y equals
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Mensaje> mensajes = new ArrayList<>();
    
    // Relación M:N con Tags
    @ManyToMany
    @JoinTable(name = "ticket_tags",
               joinColumns = @JoinColumn(name = "ticket_id"),
               inverseJoinColumns = @JoinColumn(name = "tag_id"))
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Tag> tags = new HashSet<>();
}

// Uso del Builder en Sistema Ticketero
Ticket ticket = Ticket.builder()
    .titulo("Error en sistema de pagos")
    .descripcion("El sistema no procesa pagos con tarjeta de crédito")
    .usuarioId(123L)
    .estado(EstadoTicket.ABIERTO)
    .prioridad("ALTA")
    .fechaCreacion(LocalDateTime.now())
    .build();
```

**Mensaje Entity con Builder:**
```java
@Entity
@Table(name = "mensajes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mensaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "es_interno", nullable = false)
    private Boolean esInterno = false;
    
    // Relación N:1 con Ticket - CRÍTICO: Excluir de toString
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @ToString.Exclude
    private Ticket ticket;
}
```

**@ToString.Exclude y @EqualsAndHashCode.Exclude - Sistema Ticketero:**

**Usuario Entity con exclusiones correctas:**
```java
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude  // No incluir ID generado en equals
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;  // ← Business key para equals
    
    @Column(nullable = false, length = 50)
    private String nombre;
    
    @Column(nullable = false, length = 50)
    private String apellido;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario tipo;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoUsuario estado;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    @EqualsAndHashCode.Exclude  // Excluir campos mutables
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    @EqualsAndHashCode.Exclude  // Excluir campos mutables
    private LocalDateTime fechaActualizacion;
    
    // Si tuviéramos relación con tickets (no recomendado por performance)
    // @OneToMany(mappedBy = "usuarioId")
    // @ToString.Exclude
    // @EqualsAndHashCode.Exclude
    // private List<Ticket> tickets = new ArrayList<>();
}
```

**Controllers con Lombok - Sistema Ticketero:**

**TicketController:**
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
        log.info("POST /api/tickets - Creating ticket for user: {}", request.usuarioId());
        TicketResponse response = ticketService.create(request);
        log.debug("Ticket created successfully with ID: {}", response.id());
        return ResponseEntity.status(201).body(response);
    }
    
    @PutMapping("/{id}/estado")
    public ResponseEntity<TicketResponse> updateEstado(
        @PathVariable Long id,
        @Valid @RequestBody EstadoUpdateRequest request
    ) {
        log.info("PUT /api/tickets/{}/estado - Updating to: {}", id, request.estado());
        TicketResponse response = ticketService.updateEstado(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getById(@PathVariable Long id) {
        log.debug("GET /api/tickets/{}", id);
        return ticketService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
```

**Anti-patterns específicos del Sistema Ticketero:**

**❌ @Data en Entities con Relaciones:**
```java
// ❌ INCORRECTO: @Data causará problemas en Sistema Ticketero
@Entity
@Data  // toString() causará lazy loading de mensajes!
public class Ticket {
    @OneToMany(mappedBy = "ticket")
    private List<Mensaje> mensajes;  // Problema: N+1 queries accidentales
}

// Al hacer ticket.toString() se ejecutarán queries para cargar mensajes
// Esto puede causar LazyInitializationException o performance issues

// ✅ CORRECTO: Usar anotaciones específicas
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    @OneToMany(mappedBy = "ticket")
    @ToString.Exclude  // CRÍTICO para Sistema Ticketero
    @EqualsAndHashCode.Exclude
    private List<Mensaje> mensajes;
}
```

**❌ Recursión infinita en relaciones bidireccionales:**
```java
// ❌ INCORRECTO: Recursión infinita
@Entity
@Data  // toString() causa recursión infinita
public class Ticket {
    @OneToMany(mappedBy = "ticket")
    private List<Mensaje> mensajes;
}

@Entity
@Data
public class Mensaje {
    @ManyToOne
    private Ticket ticket;  // ticket.toString() → mensajes.toString() → ticket.toString() → ∞
}

// ✅ CORRECTO: Excluir relaciones
@Entity
@Getter
@Setter
public class Ticket {
    @OneToMany(mappedBy = "ticket")
    @ToString.Exclude
    private List<Mensaje> mensajes;
}

@Entity
@Getter
@Setter
public class Mensaje {
    @ManyToOne
    @ToString.Exclude
    private Ticket ticket;
}
```

**Testing con Lombok - Sistema Ticketero:**

**TicketTestData Builder:**
```java
@Builder
public class TicketTestData {
    @Builder.Default
    private String titulo = "Ticket de prueba";
    
    @Builder.Default
    private String descripcion = "Descripción de prueba para testing";
    
    @Builder.Default
    private Long usuarioId = 1L;
    
    @Builder.Default
    private EstadoTicket estado = EstadoTicket.ABIERTO;
    
    @Builder.Default
    private String prioridad = "MEDIA";
    
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    public Ticket build() {
        return Ticket.builder()
            .titulo(titulo)
            .descripcion(descripcion)
            .usuarioId(usuarioId)
            .estado(estado)
            .prioridad(prioridad)
            .fechaCreacion(fechaCreacion)
            .build();
    }
}

// Uso en tests del Sistema Ticketero
@Test
void testCreateTicketWithHighPriority() {
    Ticket ticket = TicketTestData.builder()
        .titulo("Error crítico en producción")
        .prioridad("CRITICA")
        .build()
        .build();
    
    assertThat(ticket.getPrioridad()).isEqualTo("CRITICA");
    assertThat(ticket.getEstado()).isEqualTo(EstadoTicket.ABIERTO);
}
```

**MensajeTestData Builder:**
```java
@Builder
public class MensajeTestData {
    @Builder.Default
    private String contenido = "Mensaje de prueba";
    
    @Builder.Default
    private Long usuarioId = 1L;
    
    @Builder.Default
    private Boolean esInterno = false;
    
    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();
    
    private Ticket ticket; // Requerido
    
    public Mensaje build() {
        return Mensaje.builder()
            .contenido(contenido)
            .usuarioId(usuarioId)
            .esInterno(esInterno)
            .fechaCreacion(fechaCreacion)
            .ticket(ticket)
            .build();
    }
}
```

**Configuración Lombok - Sistema Ticketero:**

**lombok.config (raíz del proyecto):**
```properties
# Configuración específica para Sistema Ticketero
lombok.addLombokGeneratedAnnotation = true
lombok.anyConstructor.addConstructorProperties = true

# Logging configuration
lombok.log.fieldName = log
lombok.log.fieldIsStatic = true

# Builder configuration
lombok.builder.className = Builder

# ToString configuration - Importante para JPA
lombok.toString.doNotUseGetters = true
lombok.toString.includeFieldNames = true

# EqualsAndHashCode configuration
lombok.equalsAndHashCode.doNotUseGetters = true

# Configuración específica para evitar problemas con JPA
lombok.toString.callSuper = skip
lombok.equalsAndHashCode.callSuper = skip
```

**pom.xml dependency:**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.30</version>
    <scope>provided</scope>
</dependency>
```

**Combinaciones recomendadas para Sistema Ticketero:**

**Para Entities JPA:**
```java
@Entity
@Table(name = "tabla_name")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntityName {
    // Campos con @ToString.Exclude y @EqualsAndHashCode.Exclude en relaciones
}
```

**Para Services:**
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceName {
    private final Repository repository;
    // Métodos con logging apropiado
}
```

**Para Controllers:**
```java
@RestController
@RequestMapping("/api/endpoint")
@RequiredArgsConstructor
@Slf4j
public class ControllerName {
    private final Service service;
    // Endpoints con logging de requests importantes
}
```

**Para Value Objects del dominio:**
```java
@Value
@Builder
public class TicketSummary {
    Long id;
    String titulo;
    String estado;
    String prioridad;
    LocalDateTime fechaCreacion;
    int totalMensajes;
}
```

**Checklist Lombok - Sistema Ticketero:**

Antes de usar Lombok:
- [ ] ¿Es Entity con relaciones (Ticket-Mensaje)? → NO @Data
- [ ] ¿Necesitas logging para operaciones del dominio? → @Slf4j
- [ ] ¿Dependency injection en service/controller? → @RequiredArgsConstructor
- [ ] ¿Constructor builder para testing? → @Builder
- [ ] ¿Relaciones JPA en Sistema Ticketero? → @ToString.Exclude + @EqualsAndHashCode.Exclude
- [ ] ¿Es DTO del Sistema Ticketero? → Usar Record, NO Lombok
- [ ] ¿JPA entity? → @NoArgsConstructor requerido
- [ ] ¿Business key definido? → Excluir ID de equals/hashCode
- [ ] ¿Campos mutables en equals? → @EqualsAndHashCode.Exclude
- [ ] ¿Lazy loading posible? → @ToString.Exclude en relaciones

### Referencias del Proyecto
- **Entities con Lombok:** `Ticket.java`, `Mensaje.java`, `Usuario.java`, `Tag.java`
- **Services con Lombok:** `TicketService.java`, `MensajeService.java`, `UsuarioService.java`
- **Controllers con Lombok:** `TicketController.java`, `MensajeController.java`, `UsuarioController.java`
- **Test Data Builders:** `TicketTestData.java`, `MensajeTestData.java`, `UsuarioTestData.java`
- **Configuración:** `lombok.config`, `pom.xml`
- **Documentos de referencia:** 
  - `docs/architecture/software_architecture_design_v1.0.md`
  - `docs/implementation/plan_detallado_implementacion_v1.0.md`

### Criterios de Validación
□ Todos los Services usan @RequiredArgsConstructor para dependency injection
□ Todos los Services y Controllers usan @Slf4j para logging
□ Entities JPA usan @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
□ NO se usa @Data en entities con relaciones (Ticket-Mensaje)
□ @ToString.Exclude aplicado en TODAS las relaciones JPA
□ @EqualsAndHashCode.Exclude aplicado en ID generado y relaciones
□ Business keys usados para equals/hashCode (email en Usuario)
□ @Builder usado para construcción de entities y test data
□ Records usados para DTOs en lugar de Lombok
□ lombok.config configurado apropiadamente para el proyecto
□ Test data builders implementados para entities principales
□ Logging levels apropiados para operaciones del Sistema Ticketero
□ Exclusiones correctas para evitar lazy loading exceptions
□ Configuración Maven/Gradle correcta para Lombok

### Comandos de Verificación
```bash
# Verificar uso correcto de @RequiredArgsConstructor
grep -r "@RequiredArgsConstructor" src/main/java/service/ src/main/java/controller/

# Verificar uso de @Slf4j
grep -r "@Slf4j" src/main/java/

# Verificar que entities NO usan @Data
find src/main/java -name "*.java" -path "*/entity/*" | xargs grep -L "@Data"

# Verificar @ToString.Exclude en relaciones
grep -r "@ToString.Exclude" src/main/java/entity/

# Verificar @EqualsAndHashCode.Exclude
grep -r "@EqualsAndHashCode.Exclude" src/main/java/entity/

# Verificar @Builder en entities
grep -r "@Builder" src/main/java/entity/

# Verificar @NoArgsConstructor en entities JPA
grep -r "@NoArgsConstructor" src/main/java/entity/

# Verificar que DTOs NO usan Lombok (usan Records)
find src/main/java -name "*Request.java" -o -name "*Response.java" | xargs grep -L "@Data\|@Getter\|@Setter"

# Verificar configuración lombok.config
test -f lombok.config && echo "lombok.config exists" || echo "lombok.config missing"

# Verificar dependency Lombok en pom.xml
grep -A 5 -B 5 "lombok" pom.xml

# Verificar logging levels en código
grep -r "log\\.info\\|log\\.debug\\|log\\.error\\|log\\.warn" src/main/java/

# Verificar test data builders
find src/test/java -name "*TestData.java" | xargs grep "@Builder"

# Verificar exclusiones en relaciones bidireccionales
grep -A 5 -B 5 "@OneToMany\|@ManyToOne" src/main/java/entity/ | grep -E "@ToString.Exclude|@EqualsAndHashCode.Exclude"
```

---
**Estado:** Listo para migración a `.amazonq\rules\`