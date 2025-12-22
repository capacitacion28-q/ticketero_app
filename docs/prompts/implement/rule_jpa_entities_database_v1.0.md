# Regla 2: JPA Entities & Database - Sistema Ticketero

**Versión:** 1.0  
**Fecha:** 2025-12-22  
**Aplicable a:** Agente Desarrollador - Sistema Ticketero  
**Stack:** Java 17 + Spring Boot 3.2 + PostgreSQL + Docker

---

## REGLA ORIGINAL
Buenas prácticas JPA/Hibernate para crear entities correctas con relaciones, validaciones, migrations Flyway, queries optimizadas y manejo de performance en cualquier proyecto Spring Boot.

## ADAPTACIÓN AL PROYECTO TICKETERO

### Objetivo
Implementar entities JPA optimizadas para el Sistema Ticketero con relaciones correctas entre Ticket, Mensaje, Usuario y EstadoTicket, incluyendo migrations PostgreSQL y queries eficientes.

### Implementación

**Entities del Sistema Ticketero:**

### Ejemplos Específicos

**Entity Pattern - Ticket:**
```java
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
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    // Relación 1:N con Mensajes
    @OneToMany(
        mappedBy = "ticket",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @ToString.Exclude
    private List<Mensaje> mensajes = new ArrayList<>();
    
    // Relación M:N con Tags (categorización)
    @ManyToMany
    @JoinTable(
        name = "ticket_tags",
        joinColumns = @JoinColumn(name = "ticket_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @ToString.Exclude
    private Set<Tag> tags = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoTicket.ABIERTO;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
```

**Entity Pattern - Mensaje:**
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
    
    // Relación N:1 con Ticket
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    @ToString.Exclude
    private Ticket ticket;
    
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
```

**Entity Pattern - Usuario:**
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
    private Long id;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
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
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
    
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoUsuario.ACTIVO;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
```

**Entity Pattern - Tag (para relación ManyToMany):**
```java
@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, length = 30)
    private String nombre;
    
    @Column(length = 100)
    private String descripcion;
    
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    // Relación M:N con Tickets
    @ManyToMany(mappedBy = "tags")
    @ToString.Exclude
    private Set<Ticket> tickets = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
```

**Enums del Sistema Ticketero:**
```java
public enum EstadoTicket {
    ABIERTO,
    EN_PROGRESO,
    RESUELTO,
    CERRADO,
    CANCELADO
}

public enum TipoUsuario {
    CLIENTE,
    AGENTE,
    ADMINISTRADOR
}

public enum EstadoUsuario {
    ACTIVO,
    INACTIVO,
    SUSPENDIDO
}
```

**Flyway Migrations - Sistema Ticketero:**

**V1__create_usuarios_table.sql:**
```sql
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) UNIQUE NOT NULL,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP
);

-- Índices para performance
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_tipo ON usuarios(tipo);
CREATE INDEX idx_usuarios_estado ON usuarios(estado);
CREATE INDEX idx_usuarios_fecha_creacion ON usuarios(fecha_creacion DESC);
```

**V2__create_tickets_table.sql:**
```sql
CREATE TABLE tickets (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,
    usuario_id BIGINT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP,
    CONSTRAINT fk_ticket_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX idx_tickets_usuario_id ON tickets(usuario_id);
CREATE INDEX idx_tickets_estado ON tickets(estado);
CREATE INDEX idx_tickets_fecha_creacion ON tickets(fecha_creacion DESC);
CREATE INDEX idx_tickets_estado_fecha ON tickets(estado, fecha_creacion);
```

**V3__create_mensajes_table.sql:**
```sql
CREATE TABLE mensajes (
    id BIGSERIAL PRIMARY KEY,
    contenido TEXT NOT NULL,
    usuario_id BIGINT NOT NULL,
    ticket_id BIGINT NOT NULL,
    es_interno BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_mensaje_usuario FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id) ON DELETE CASCADE,
    CONSTRAINT fk_mensaje_ticket FOREIGN KEY (ticket_id)
        REFERENCES tickets(id) ON DELETE CASCADE
);

-- Índices para performance
CREATE INDEX idx_mensajes_ticket_id ON mensajes(ticket_id);
CREATE INDEX idx_mensajes_usuario_id ON mensajes(usuario_id);
CREATE INDEX idx_mensajes_fecha_creacion ON mensajes(fecha_creacion DESC);
CREATE INDEX idx_mensajes_es_interno ON mensajes(es_interno);
```

**V4__create_tags_table.sql:**
```sql
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(30) UNIQUE NOT NULL,
    descripcion VARCHAR(100),
    fecha_creacion TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Índices para performance
CREATE INDEX idx_tags_nombre ON tags(nombre);
```

**V5__create_ticket_tags_table.sql:**
```sql
CREATE TABLE ticket_tags (
    ticket_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (ticket_id, tag_id),
    CONSTRAINT fk_ticket_tags_ticket FOREIGN KEY (ticket_id)
        REFERENCES tickets(id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_tags_tag FOREIGN KEY (tag_id)
        REFERENCES tags(id) ON DELETE CASCADE
);

-- Índices para performance en tabla intermedia
CREATE INDEX idx_ticket_tags_ticket_id ON ticket_tags(ticket_id);
CREATE INDEX idx_ticket_tags_tag_id ON ticket_tags(tag_id);
```

**V6__add_composite_indexes.sql:**
```sql
-- Índices compuestos para búsquedas frecuentes del Sistema Ticketero
CREATE INDEX idx_tickets_usuario_estado_fecha ON tickets(usuario_id, estado, fecha_creacion DESC);
CREATE INDEX idx_tickets_estado_fecha_creacion ON tickets(estado, fecha_creacion DESC);
CREATE INDEX idx_mensajes_ticket_fecha ON mensajes(ticket_id, fecha_creacion ASC);
CREATE INDEX idx_usuarios_tipo_estado ON usuarios(tipo, estado);

-- Índice para búsquedas de texto en títulos (PostgreSQL específico)
CREATE INDEX idx_tickets_titulo_gin ON tickets USING gin(to_tsvector('spanish', titulo));
```

**Repository Queries - Sistema Ticketero:**

**TicketRepository:**
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
    
    List<Ticket> findByUsuarioIdAndEstadoOrderByFechaCreacionDesc(Long usuarioId, EstadoTicket estado);
    
    // Paginación para listados grandes
    Page<Ticket> findByEstado(EstadoTicket estado, Pageable pageable);
    
    Page<Ticket> findByUsuarioId(Long usuarioId, Pageable pageable);
    
    Page<Ticket> findByEstadoAndFechaCreacionAfter(EstadoTicket estado, LocalDateTime fecha, Pageable pageable);
    
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
    
    // Query con JOIN FETCH para evitar N+1
    @Query("""
        SELECT t FROM Ticket t
        JOIN FETCH t.mensajes
        WHERE t.usuarioId = :usuarioId
        AND t.estado = :estado
        """)
    List<Ticket> findTicketsWithMensajesByUsuarioAndEstado(
        @Param("usuarioId") Long usuarioId,
        @Param("estado") EstadoTicket estado
    );
    
    // Query nativa (último recurso)
    @Query(value = """
        SELECT * FROM tickets t
        WHERE t.estado = ?1
        AND t.fecha_creacion > NOW() - INTERVAL '30 days'
        ORDER BY t.fecha_creacion DESC
        LIMIT ?2
        """, nativeQuery = true)
    List<Ticket> findRecentTicketsByEstado(String estado, int limit);
    
    // Queries de agregación para estadísticas
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.estado = :estado AND t.fechaCreacion > :fecha")
    long countTicketsRecentesByEstado(@Param("estado") EstadoTicket estado, @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT t.estado, COUNT(t) FROM Ticket t GROUP BY t.estado")
    List<Object[]> countTicketsByEstado();
    
    @Query("SELECT AVG(EXTRACT(EPOCH FROM (t.fechaActualizacion - t.fechaCreacion))/3600) FROM Ticket t WHERE t.estado = 'RESUELTO'")
    Double getAverageResolutionTimeInHours();
    
    // Query con paginación
    @Query("""
        SELECT t FROM Ticket t
        WHERE t.fechaCreacion > :fecha
        ORDER BY t.fechaCreacion DESC
        """)
    Page<Ticket> findRecentTickets(
        @Param("fecha") LocalDateTime fecha,
        Pageable pageable
    );
}
```

**MensajeRepository:**
```java
@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    List<Mensaje> findByTicketId(Long ticketId);
    
    List<Mensaje> findByTicketIdOrderByFechaCreacionAsc(Long ticketId);
    
    List<Mensaje> findByUsuarioId(Long usuarioId);
    
    List<Mensaje> findByTicketIdAndEsInterno(Long ticketId, Boolean esInterno);
    
    boolean existsByTicketIdAndContenido(Long ticketId, String contenido);
    
    long countByTicketId(Long ticketId);
    
    // Query para mensajes públicos de un ticket
    @Query("""
        SELECT m FROM Mensaje m
        WHERE m.ticket.id = :ticketId
        AND m.esInterno = false
        ORDER BY m.fechaCreacion ASC
        """)
    List<Mensaje> findMensajesPublicosByTicketId(@Param("ticketId") Long ticketId);
    
    // Paginación para mensajes
    Page<Mensaje> findByTicketIdOrderByFechaCreacionAsc(Long ticketId, Pageable pageable);
    
    // Queries de agregación
    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.ticket.id = :ticketId AND m.esInterno = :esInterno")
    long countMensajesByTicketAndTipo(@Param("ticketId") Long ticketId, @Param("esInterno") Boolean esInterno);
}
```

**UsuarioRepository:**
```java
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByTipo(TipoUsuario tipo);
    
    List<Usuario> findByEstado(EstadoUsuario estado);
    
    List<Usuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
    
    boolean existsByEmail(String email);
    
    long countByTipoAndEstado(TipoUsuario tipo, EstadoUsuario estado);
    
    List<Usuario> findByTipoAndEstadoOrderByFechaCreacionDesc(TipoUsuario tipo, EstadoUsuario estado);
    
    // Paginación para usuarios
    Page<Usuario> findByTipo(TipoUsuario tipo, Pageable pageable);
    
    Page<Usuario> findByEstado(EstadoUsuario estado, Pageable pageable);
}

**TagRepository:**
```java
@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByNombre(String nombre);
    
    List<Tag> findByNombreContainingIgnoreCase(String nombre);
    
    boolean existsByNombre(String nombre);
    
    // Query para tags más usados
    @Query("""
        SELECT t FROM Tag t
        JOIN t.tickets tickets
        GROUP BY t
        ORDER BY COUNT(tickets) DESC
        """)
    List<Tag> findMostUsedTags(Pageable pageable);
    
    // Contar tickets por tag
    @Query("SELECT COUNT(tickets) FROM Tag t JOIN t.tickets tickets WHERE t.id = :tagId")
    long countTicketsByTag(@Param("tagId") Long tagId);
}
```

**Package Structure - Sistema Ticketero:**
```
com.ticketero.app/
├── model/
│   ├── entity/               # @Entity (JPA entities)
│   │   ├── Ticket.java
│   │   ├── Mensaje.java
│   │   ├── Usuario.java
│   │   ├── Tag.java
│   │   ├── EstadoTicket.java
│   │   ├── TipoUsuario.java
│   │   └── EstadoUsuario.java
│   └── dto/                  # Records (Request/Response)
│       ├── TicketRequest.java
│       ├── TicketResponse.java
│       ├── MensajeRequest.java
│       ├── MensajeResponse.java
│       └── UsuarioResponse.java
├── repository/               # @Repository (interfaces JPA)
│   ├── TicketRepository.java
│   ├── MensajeRepository.java
│   ├── UsuarioRepository.java
│   └── TagRepository.java
└── resources/
    └── db/migration/         # Flyway migrations
        ├── V1__create_usuarios_table.sql
        ├── V2__create_tickets_table.sql
        ├── V3__create_mensajes_table.sql
        ├── V4__create_tags_table.sql
        ├── V5__create_ticket_tags_table.sql
        └── V6__add_composite_indexes.sql
```

**Checklist JPA - Sistema Ticketero:**

Antes de crear entity:
- [ ] `@Entity` + `@Table(name = "snake_case")`
- [ ] `@Id` + `@GeneratedValue(strategy = IDENTITY)`
- [ ] `@Column` con constraints específicos del Sistema Ticketero
- [ ] `@PrePersist` / `@PreUpdate` para timestamps
- [ ] Enums con `EnumType.STRING` (EstadoTicket, TipoUsuario, EstadoUsuario)
- [ ] Relaciones con `@ToString.Exclude`
- [ ] `FetchType.LAZY` en relaciones Ticket-Mensaje
- [ ] Relación M:N Ticket-Tag configurada correctamente
- [ ] Usar `Set<>` para relaciones ManyToMany (no `List<>`)
- [ ] Inicializar colecciones (`= new ArrayList<>()`, `= new HashSet<>()`)
- [ ] Migration Flyway creada para PostgreSQL
- [ ] Índices en columnas de búsqueda del dominio ticketero
- [ ] Índices compuestos para búsquedas frecuentes
- [ ] Foreign keys explícitas con CASCADE apropiado
- [ ] Validaciones Jakarta (`@NotBlank`, `@Size`, `@Email`) en entities
- [ ] Paginación implementada con `Page<>` y `Pageable`
- [ ] Queries de agregación para estadísticas
- [ ] Queries derivadas preferidas sobre @Query custom

### Referencias del Proyecto
- **Entidades relacionadas:** `Ticket.java`, `Mensaje.java`, `Usuario.java`, `Tag.java`, `EstadoTicket.java`, `TipoUsuario.java`, `EstadoUsuario.java`
- **Repositories aplicables:** `TicketRepository.java`, `MensajeRepository.java`, `UsuarioRepository.java`, `TagRepository.java`
- **Migrations:** `V1__create_usuarios_table.sql`, `V2__create_tickets_table.sql`, `V3__create_mensajes_table.sql`, `V4__create_tags_table.sql`, `V5__create_ticket_tags_table.sql`, `V6__add_composite_indexes.sql`
- **Documentos de referencia:** 
  - `docs/architecture/software_architecture_design_v1.0.md`
  - `docs/implementation/plan_detallado_implementacion_v1.0.md`

### Criterios de Validación
□ Todas las entities usan `@Table(name = "snake_case")` explícito
□ Todas las entities tienen `@PrePersist` y `@PreUpdate` para timestamps
□ Todos los enums usan `EnumType.STRING` (EstadoTicket, TipoUsuario, EstadoUsuario)
□ Relaciones Ticket-Mensaje configuradas correctamente (1:N)
□ Relación Ticket-Tag configurada correctamente (M:N) con `@JoinTable`
□ `@ToString.Exclude` en ambos lados de relaciones
□ `FetchType.LAZY` por defecto en todas las relaciones
□ `Set<>` usado para relaciones ManyToMany (no `List<>`)
□ Migrations Flyway creadas para PostgreSQL con nomenclatura correcta
□ Índices creados en columnas de búsqueda frecuente del dominio
□ Índices compuestos implementados para búsquedas combinadas
□ Foreign keys con CASCADE apropiado para el Sistema Ticketero
□ Validaciones Jakarta implementadas en entities (`@NotBlank`, `@Size`, `@Email`)
□ Paginación implementada con `Page<>` y `Pageable` en repositories
□ Queries de agregación para estadísticas del Sistema Ticketero
□ Repositories usan query derivadas preferentemente
□ `JOIN FETCH` implementado para evitar N+1 en consultas críticas
□ Queries nativas solo para casos específicos de PostgreSQL
□ Colecciones inicializadas en entities (`= new ArrayList<>()`, `= new HashSet<>()`)

### Comandos de Verificación
```bash
# Verificar estructura de entities del Sistema Ticketero
find src/main/java -name "*.java" | grep -E "(Ticket|Mensaje|Usuario|Tag)\.java"

# Verificar anotaciones JPA
grep -r "@Entity\|@Table\|@Column\|@OneToMany\|@ManyToOne" src/main/java/

# Verificar enums con STRING
grep -r "EnumType.STRING" src/main/java/

# Verificar timestamps automáticos
grep -r "@PrePersist\|@PreUpdate" src/main/java/

# Verificar migrations Flyway
ls -la src/main/resources/db/migration/

# Verificar índices en migrations
grep -r "CREATE INDEX" src/main/resources/db/migration/

# Verificar foreign keys
grep -r "FOREIGN KEY\|REFERENCES" src/main/resources/db/migration/

# Verificar queries derivadas vs custom
grep -r "findBy\|countBy\|existsBy" src/main/java/

# Verificar JOIN FETCH para performance
grep -r "JOIN FETCH" src/main/java/

# Verificar FetchType.LAZY
grep -r "FetchType.LAZY" src/main/java/

# Verificar @ToString.Exclude en relaciones
grep -r "@ToString.Exclude" src/main/java/

# Verificar inicialización de colecciones
grep -r "= new ArrayList<>()\|= new HashSet<>()" src/main/java/

# Verificar validaciones Jakarta en entities
grep -r "@NotBlank\|@Size\|@Email\|@NotNull" src/main/java/

# Verificar paginación en repositories
grep -r "Page<\|Pageable" src/main/java/

# Verificar relaciones ManyToMany
grep -r "@ManyToMany\|@JoinTable" src/main/java/

# Verificar uso de Set para M:N
grep -r "Set<.*>" src/main/java/

# Verificar queries de agregación
grep -r "COUNT\|AVG\|SUM\|GROUP BY" src/main/java/

# Verificar índices compuestos en migrations
grep -r "CREATE INDEX.*," src/main/resources/db/migration/

# Verificar migrations de tabla intermedia M:N
ls -la src/main/resources/db/migration/ | grep "ticket_tags"
```

---
**Estado:** Listo para migración a `.amazonq\rules\`