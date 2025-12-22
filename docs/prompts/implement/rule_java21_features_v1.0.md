# Regla 4: Java 17 Modern Features - Sistema Ticketero

**Versión:** 1.0  
**Fecha:** 2025-12-22  
**Aplicable a:** Agente Desarrollador - Sistema Ticketero  
**Stack:** Java 17 + Spring Boot 3.2 + PostgreSQL + Docker

---

## REGLA ORIGINAL
Aprovechar características modernas de Java 17 para escribir código más limpio y conciso: Records para DTOs, Text Blocks para queries, Pattern Matching, Switch Expressions. Preparar código para migración futura a Java 21.

## ADAPTACIÓN AL PROYECTO TICKETERO

### Objetivo
Implementar features modernas de Java 17 en el Sistema Ticketero para código más limpio, legible y mantenible en entities, services y queries específicas del dominio.

### Implementación

**Stack Actual:** Java 17 - Features disponibles AHORA
**Preparación:** Código comentado para migración futura a Java 21

**Roadmap de Migración Java 17 → Java 21:**
- **Fase 1 (Actual):** Usar features Java 17 (Records, Text Blocks, Pattern Matching básico, Switch Expressions)
- **Fase 2 (Futura):** Migrar a Java 21 (Virtual Threads, Record Patterns, String Templates)
- **Fase 3 (Optimización):** Aprovechar features avanzadas (Pattern Matching con guards, Sealed Classes exhaustivas)

### Ejemplos Específicos

**Records para DTOs - Sistema Ticketero:**

**TicketRequest con Record:**
```java
// ✅ CORRECTO: Record para DTO inmutable del Sistema Ticketero
public record TicketRequest(
    @NotBlank(message = "Título es requerido")
    @Size(min = 5, max = 200)
    String titulo,
    
    @NotBlank(message = "Descripción es requerida") 
    @Size(min = 10, max = 5000)
    String descripcion,
    
    @NotNull @Positive
    Long usuarioId,
    
    @Pattern(regexp = "^(BAJA|MEDIA|ALTA|CRITICA)$")
    String prioridad,
    
    List<String> tags
) {
    // Constructor compacto con validación adicional
    public TicketRequest {
        if (titulo != null && titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Título no puede estar vacío");
        }
        if (prioridad == null) {
            prioridad = "MEDIA";
        }
    }
    
    // Método de instancia específico del dominio
    public boolean esUrgente() {
        return "ALTA".equals(prioridad) || "CRITICA".equals(prioridad);
    }
    
    // Factory method para el Sistema Ticketero
    public static TicketRequest createBasico(String titulo, String descripcion, Long usuarioId) {
        return new TicketRequest(titulo, descripcion, usuarioId, "MEDIA", List.of());
    }
}

// ❌ INCORRECTO: Clase tradicional con boilerplate
public class TicketRequest {
    private String titulo;
    private String descripcion;
    // ... 50+ líneas de getters, setters, equals, hashCode, toString
}
```

**MensajeResponse con Record:**
```java
public record MensajeResponse(
    Long id,
    String contenido,
    Long ticketId,
    String ticketTitulo,
    Long usuarioId,
    String usuarioNombre,
    Boolean esInterno,
    LocalDateTime fechaCreacion
) {
    // Método específico del dominio ticketero
    public boolean esReciente() {
        return fechaCreacion.isAfter(LocalDateTime.now().minusHours(24));
    }
    
    // Constructor desde Entity
    public MensajeResponse(Mensaje mensaje, String ticketTitulo, String usuarioNombre) {
        this(
            mensaje.getId(),
            mensaje.getContenido(),
            mensaje.getTicket().getId(),
            ticketTitulo,
            mensaje.getUsuarioId(),
            usuarioNombre,
            mensaje.getEsInterno(),
            mensaje.getFechaCreacion()
        );
    }
}
```

**Text Blocks para Queries - Sistema Ticketero:**

**TicketRepository con Text Blocks:**
```java
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    
    // ✅ CORRECTO: Text block para query compleja del Sistema Ticketero
    @Query("""
        SELECT t FROM Ticket t
        LEFT JOIN FETCH t.mensajes m
        LEFT JOIN FETCH t.tags tags
        WHERE t.estado = :estado
        AND t.usuarioId = :usuarioId
        AND t.fechaCreacion BETWEEN :fechaInicio AND :fechaFin
        ORDER BY t.fechaCreacion DESC, t.prioridad DESC
        """)
    List<Ticket> findTicketsCompletos(
        @Param("estado") EstadoTicket estado,
        @Param("usuarioId") Long usuarioId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
    
    // Query nativa PostgreSQL específica para el Sistema Ticketero
    @Query(value = """
        SELECT t.*, 
               COUNT(m.id) as total_mensajes,
               AVG(EXTRACT(EPOCH FROM (t.fecha_actualizacion - t.fecha_creacion))/3600) as horas_resolucion
        FROM tickets t
        LEFT JOIN mensajes m ON t.id = m.ticket_id
        WHERE t.estado IN ('RESUELTO', 'CERRADO')
        AND t.fecha_creacion > NOW() - INTERVAL '30 days'
        GROUP BY t.id
        ORDER BY horas_resolucion ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findTicketsResueltosMasRapido(@Param("limit") int limit);
    
    // ❌ INCORRECTO: String concatenación
    @Query("SELECT t FROM Ticket t " +
           "LEFT JOIN FETCH t.mensajes m " +
           "WHERE t.estado = :estado " +
           "AND t.usuarioId = :usuarioId")
    List<Ticket> findTicketsBad(@Param("estado") EstadoTicket estado, @Param("usuarioId") Long usuarioId);
}
```

**Templates para Notificaciones - Sistema Ticketero:**
```java
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    // Template de email para el Sistema Ticketero
    public String createTicketNotificationEmail(String usuarioNombre, String ticketTitulo, Long ticketId) {
        return """
            <html>
            <body>
                <h2>Sistema Ticketero - Nuevo Ticket Creado</h2>
                <p>Hola %s,</p>
                <p>Se ha creado un nuevo ticket:</p>
                <div style="border: 1px solid #ccc; padding: 10px; margin: 10px 0;">
                    <h3>%s</h3>
                    <p><strong>ID:</strong> #%d</p>
                    <p><strong>Estado:</strong> ABIERTO</p>
                </div>
                <p>Puedes ver los detalles en el sistema.</p>
                <p>Saludos,<br>Sistema Ticketero</p>
            </body>
            </html>
            """.formatted(usuarioNombre, ticketTitulo, ticketId);
    }
    
    // Template JSON para webhook del Sistema Ticketero
    public String createWebhookPayload(Ticket ticket, String accion) {
        return """
            {
                "evento": "ticket_%s",
                "timestamp": "%s",
                "ticket": {
                    "id": %d,
                    "titulo": "%s",
                    "estado": "%s",
                    "prioridad": "%s",
                    "usuario_id": %d
                },
                "sistema": "ticketero"
            }
            """.formatted(
                accion.toLowerCase(),
                LocalDateTime.now().toString(),
                ticket.getId(),
                ticket.getTitulo().replace("\"", "\\\""),
                ticket.getEstado().name(),
                ticket.getPrioridad(),
                ticket.getUsuarioId()
            );
    }
}
```

**Pattern Matching - Sistema Ticketero (Java 17+):**

**TicketService con Pattern Matching:**
```java
@Service
@RequiredArgsConstructor
public class TicketService {
    
    // ✅ CORRECTO: Pattern matching para entities del Sistema Ticketero
    public String formatEntity(Object entity) {
        if (entity instanceof Ticket ticket) {
            return "Ticket #%d: %s [%s]".formatted(
                ticket.getId(), 
                ticket.getTitulo(), 
                ticket.getEstado().name()
            );
        } else if (entity instanceof Mensaje mensaje) {
            return "Mensaje #%d en Ticket #%d: %s".formatted(
                mensaje.getId(),
                mensaje.getTicket().getId(),
                mensaje.getContenido().substring(0, Math.min(50, mensaje.getContenido().length()))
            );
        } else if (entity instanceof Usuario usuario) {
            return "Usuario: %s %s (%s)".formatted(
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getTipo().name()
            );
        }
    // 🔮 PREPARADO PARA JAVA 21: Record Patterns
    /*
    public String formatTicketResponse(Object response) {
        return switch (response) {
            case TicketResponse(Long id, String titulo, _, _, _, _, _, _, _, _, _) ->
                "Ticket #%d: %s".formatted(id, titulo);
            case MensajeResponse(Long id, String contenido, Long ticketId, _, _, _, _, _) ->
                "Mensaje #%d en Ticket #%d: %s".formatted(id, ticketId, 
                    contenido.substring(0, Math.min(30, contenido.length())));
            default -> "Response desconocido";
        };
    }
    */
    
    // ❌ INCORRECTO: Cast manual
    public String formatEntityBad(Object entity) {
        if (entity instanceof Ticket) {
            Ticket ticket = (Ticket) entity;  // Cast innecesario
            return "Ticket: " + ticket.getTitulo();
        }
        return "Unknown";
    }
    
    // Pattern matching para validaciones del Sistema Ticketero
    public boolean canUpdateTicket(Object user, Ticket ticket) {
        if (user instanceof Usuario usuario) {
            return switch (usuario.getTipo()) {
                case ADMINISTRADOR -> true;
                case AGENTE -> ticket.getEstado() != EstadoTicket.CERRADO;
                case CLIENTE -> ticket.getUsuarioId().equals(usuario.getId()) && 
                               ticket.getEstado() == EstadoTicket.ABIERTO;
            };
        }
    // 🔮 PREPARADO PARA JAVA 21: Pattern Matching con guards
    /*
    public boolean canProcessTicketAdvanced(Ticket ticket) {
        return switch (ticket.getEstado()) {
            case EstadoTicket estado when estado == ABIERTO && ticket.getPrioridad().equals("CRITICA") -> true;
            case EstadoTicket estado when estado == EN_PROGRESO -> true;
            default -> false;
        };
    }
    */
}
```

**Switch Expressions - Sistema Ticketero:**

**EstadoTicketService con Switch Expressions:**
```java
@Service
public class EstadoTicketService {
    
    // ✅ CORRECTO: Switch expression para lógica del Sistema Ticketero
    public String getEstadoDescripcion(EstadoTicket estado) {
        return switch (estado) {
            case ABIERTO -> "Ticket recién creado, pendiente de asignación";
            case EN_PROGRESO -> "Ticket siendo trabajado por un agente";
            case RESUELTO -> "Ticket resuelto, esperando confirmación del cliente";
            case CERRADO -> "Ticket cerrado definitivamente";
            case CANCELADO -> "Ticket cancelado por el usuario";
        };
    }
    
    public int getDiasMaximosPorPrioridad(String prioridad) {
        return switch (prioridad) {
            case "CRITICA" -> 1;
            case "ALTA" -> 3;
            case "MEDIA" -> {
                // Lógica compleja con yield
                int diasBase = 7;
                if (esHorarioLaboral()) {
                    yield diasBase - 2;
                }
                yield diasBase;
            }
            case "BAJA" -> 14;
            default -> throw new IllegalArgumentException("Prioridad inválida: " + prioridad);
        };
    }
    
    public boolean puedeTransicionar(EstadoTicket desde, EstadoTicket hacia) {
        return switch (desde) {
            case ABIERTO -> hacia == EstadoTicket.EN_PROGRESO || hacia == EstadoTicket.CANCELADO;
            case EN_PROGRESO -> hacia == EstadoTicket.RESUELTO || hacia == EstadoTicket.ABIERTO;
            case RESUELTO -> hacia == EstadoTicket.CERRADO || hacia == EstadoTicket.EN_PROGRESO;
            case CERRADO, CANCELADO -> false; // Estados finales
        };
    }
    
    // ❌ INCORRECTO: Switch statement tradicional
    public String getEstadoDescripcionBad(EstadoTicket estado) {
        String descripcion;
        switch (estado) {
            case ABIERTO:
                descripcion = "Ticket recién creado";
                break;
            case EN_PROGRESO:
                descripcion = "Ticket en progreso";
                break;
            // ... más casos con break
            default:
                descripcion = "Estado desconocido";
        }
        return descripcion;
    }
    
    private boolean esHorarioLaboral() {
        LocalTime now = LocalTime.now();
        return now.isAfter(LocalTime.of(9, 0)) && now.isBefore(LocalTime.of(18, 0));
    }
}
```

**Virtual Threads para Sistema Ticketero (Preparado para Java 21):**

**AsyncConfig para Sistema Ticketero:**
```java
@Configuration
@EnableAsync
public class TicketeroAsyncConfig implements AsyncConfigurer {
    
    // 🔄 JAVA 17: ThreadPoolTaskExecutor
    @Bean
    public ExecutorService ticketProcessingExecutor() {
        return Executors.newFixedThreadPool(10);
    }
    
    // 🔮 PREPARADO PARA JAVA 21: Virtual Threads
    /*
    @Bean
    @ConditionalOnJavaVersion(JavaVersion.TWENTY_ONE)
    public ExecutorService virtualThreadExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
    */
    
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Ticketero-Async-");
        executor.initialize();
    // 🔮 PREPARADO PARA JAVA 21: Virtual Thread Async Executor
    /*
    @Override
    @ConditionalOnJavaVersion(JavaVersion.TWENTY_ONE)
    public Executor getAsyncExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
    */
}
```

**NotificationService Asíncrono:**
```java
@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final ExecutorService ticketProcessingExecutor;
    private final EmailService emailService;
    
    // Operaciones asíncronas para el Sistema Ticketero
    @Async
    public void notifyTicketCreated(Ticket ticket, Usuario usuario) {
        // Se ejecuta en thread separado
        String emailBody = createTicketNotificationEmail(
            usuario.getNombre(), 
            ticket.getTitulo(), 
            ticket.getId()
        );
        emailService.sendEmail(usuario.getEmail(), "Nuevo Ticket Creado", emailBody);
    }
    
    @Async
    public void notifyTicketStatusChanged(Ticket ticket, EstadoTicket estadoAnterior) {
        // Notificación asíncrona de cambio de estado
        String mensaje = """
            El ticket #%d "%s" ha cambiado de estado:
            %s → %s
            """.formatted(
                ticket.getId(),
                ticket.getTitulo(),
                estadoAnterior.name(),
                ticket.getEstado().name()
            );
        
        // Enviar a webhook externo de forma asíncrona
        ticketProcessingExecutor.submit(() -> {
            sendWebhookNotification(ticket, "estado_cambiado");
    // 🔮 PREPARADO PARA JAVA 21: String Templates
    /*
    public String createNotificationWithTemplate(Ticket ticket) {
        return STR."Ticket #\{ticket.getId()}: \{ticket.getTitulo()} - Estado: \{ticket.getEstado()}";
    }
    */
    
    private void sendWebhookNotification(Ticket ticket, String accion) {
        // Simulación de llamada HTTP bloqueante
        try {
            Thread.sleep(1000); // Simula latencia de red
            String payload = createWebhookPayload(ticket, accion);
            // Enviar a webhook...
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

**Sealed Classes para Sistema Ticketero (Preparado para Java 17+):**

**NotificationType Sealed Interface:**
```java
public sealed interface NotificationType 
    permits EmailNotification, SmsNotification, WebhookNotification {
    
    void send(String destinatario, String mensaje);
    boolean isAvailable();
}

public final class EmailNotification implements NotificationType {
    private final EmailService emailService;
    
    public EmailNotification(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @Override
    public void send(String destinatario, String mensaje) {
        emailService.sendEmail(destinatario, "Sistema Ticketero", mensaje);
    }
    
    @Override
    public boolean isAvailable() {
        return emailService.isConfigured();
    }
}

public final class SmsNotification implements NotificationType {
    @Override
    public void send(String destinatario, String mensaje) {
        // Implementar envío SMS
    }
    
    @Override
    public boolean isAvailable() {
        return false; // No implementado aún
    }
}

public final class WebhookNotification implements NotificationType {
    private final String webhookUrl;
    
    public WebhookNotification(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }
    
    @Override
    public void send(String destinatario, String mensaje) {
        // Enviar a webhook
    }
    
    @Override
    public boolean isAvailable() {
        return webhookUrl != null && !webhookUrl.isEmpty();
    }
}
```

**NotificationService con Sealed Classes:**
```java
@Service
public class NotificationService {
    
    private final List<NotificationType> notificationTypes;
    
    public NotificationService(EmailService emailService, 
                              @Value("${ticketero.webhook.url:}") String webhookUrl) {
        this.notificationTypes = List.of(
            new EmailNotification(emailService),
            new SmsNotification(),
            new WebhookNotification(webhookUrl)
        );
    }
    
    // Pattern matching con sealed classes (Java 21)
    public BigDecimal calculateNotificationCost(NotificationType type, int quantity) {
        return switch (type) {
            case EmailNotification email -> BigDecimal.valueOf(0.01 * quantity);
            case SmsNotification sms -> BigDecimal.valueOf(0.05 * quantity);
            case WebhookNotification webhook -> BigDecimal.ZERO;
            // No necesita default, compilador sabe que es exhaustivo
        };
    }
    
    public void sendNotification(String destinatario, String mensaje) {
        notificationTypes.stream()
            .filter(NotificationType::isAvailable)
            .forEach(type -> type.send(destinatario, mensaje));
    }
}
```

**Package Structure - Sistema Ticketero con Java Features:**

```
com.ticketero.app/
├── model/
│   ├── dto/                    # Records para DTOs
│   │   ├── request/
│   │   │   ├── TicketRequest.java      # Record
│   │   │   ├── MensajeRequest.java     # Record
│   │   │   └── UsuarioRequest.java     # Record
│   │   └── response/
│   │       ├── TicketResponse.java     # Record
│   │       ├── MensajeResponse.java    # Record
│   │       └── UsuarioResponse.java    # Record
│   ├── entity/                 # Classes tradicionales (@Entity)
│   │   ├── Ticket.java
│   │   ├── Mensaje.java
│   │   └── Usuario.java
│   └── notification/           # Sealed classes
│       ├── NotificationType.java       # Sealed interface
│       ├── EmailNotification.java      # Final class
│       ├── SmsNotification.java        # Final class
│       └── WebhookNotification.java    # Final class
├── service/
│   ├── TicketService.java      # Pattern matching
│   ├── EstadoTicketService.java # Switch expressions
│   └── NotificationService.java # Async + Virtual threads
├── repository/
│   ├── TicketRepository.java   # Text blocks para queries
│   ├── MensajeRepository.java  # Text blocks
│   └── UsuarioRepository.java  # Text blocks
└── config/
    └── TicketeroAsyncConfig.java # Virtual threads config
```

**📋 REGLAS DE MIGRACIÓN JAVA 17 → JAVA 21 - Sistema Ticketero**

### Fase 1: Preparación (Java 17)
1. **Usar Records** para todos los DTOs del Sistema Ticketero
2. **Implementar Text Blocks** en queries y templates
3. **Aplicar Pattern Matching básico** con instanceof
4. **Convertir a Switch Expressions** toda lógica de estados
5. **Configurar Async** con ThreadPoolTaskExecutor
6. **Definir Sealed Classes** para jerarquías cerradas

### Fase 2: Migración (Java 21)
1. **Activar Virtual Threads** reemplazando ThreadPoolTaskExecutor
2. **Implementar Record Patterns** en switch expressions
3. **Usar String Templates** para notificaciones
4. **Aplicar Pattern Matching con guards** para validaciones complejas
5. **Eliminar default cases** en switches exhaustivos
6. **Optimizar performance** con Virtual Threads en I/O

### Fase 3: Optimización (Java 21+)
1. **Refactorizar queries** con String Templates
2. **Simplificar validaciones** con Pattern Matching avanzado
3. **Optimizar concurrencia** con Virtual Threads
4. **Mejorar type safety** con Record Patterns

**Checklist Java Features - Sistema Ticketero:**

Antes de escribir código:
- [ ] ¿Puedo usar Record en lugar de clase para DTOs del Sistema Ticketero?
- [ ] ¿Tengo query SQL multilinea para tickets/mensajes? → Text block
- [ ] ¿Hago instanceof + cast con entities del dominio? → Pattern matching
- [ ] ¿Uso switch tradicional para EstadoTicket/TipoUsuario? → Switch expression
- [ ] ¿Operaciones I/O bloqueantes (emails, webhooks)? → Async/Virtual threads
- [ ] ¿Jerarquía cerrada conocida (NotificationType)? → Sealed class
- [ ] ¿Compatibilidad con Java 17 mantenida?
- [ ] ¿Features preparadas para migración a Java 21?
- [ ] ¿Código comentado preparado para Java 21?
- [ ] ¿Configuración condicional por versión Java implementada?

### Referencias del Proyecto
- **Records:** `TicketRequest.java`, `MensajeResponse.java`, `UsuarioResponse.java`
- **Text Blocks:** Queries en `TicketRepository.java`, templates en `NotificationService.java`
- **Pattern Matching:** `TicketService.formatEntity()`, validaciones de permisos
- **Switch Expressions:** `EstadoTicketService.java`, transiciones de estado
- **Async/Virtual Threads:** `NotificationService.java`, `TicketeroAsyncConfig.java`
- **Sealed Classes:** `NotificationType.java` y implementaciones
- **Documentos de referencia:** 
  - `docs/architecture/software_architecture_design_v1.0.md`
  - `docs/implementation/plan_detallado_implementacion_v1.0.md`

### Criterios de Validación
□ Todos los DTOs usan Records en lugar de clases tradicionales
□ Queries multilinea usan Text Blocks (""") en repositories
□ Pattern matching usado en lugar de instanceof + cast
□ Switch expressions usadas para lógica de estados y tipos
□ Operaciones asíncronas configuradas para notificaciones
□ Sealed classes implementadas para jerarquías cerradas del dominio
□ Compatibilidad con Java 17 mantenida
□ Código preparado para migración futura a Java 21
□ Features modernas aplicadas específicamente al dominio ticketero
□ Templates de email/JSON usan Text Blocks
□ Validaciones en Records con constructor compacto
□ Factory methods en Records para casos comunes del dominio
□ Pattern matching exhaustivo con sealed classes (preparado)
□ Async configuration optimizada para operaciones I/O del sistema
□ Código comentado preparado para Java 21 incluido
□ Configuración condicional por versión Java implementada
□ Roadmap de migración Java 17→21 definido

### Comandos de Verificación
```bash
# Verificar uso de Records para DTOs
grep -r "public record.*Request\|public record.*Response" src/main/java/

# Verificar Text Blocks en queries
grep -r '"""' src/main/java/repository/

# Verificar Pattern Matching (instanceof con variable)
grep -r "instanceof.*[a-zA-Z]" src/main/java/

# Verificar Switch Expressions (con ->)
grep -r "switch.*{.*->" src/main/java/

# Verificar configuración Async
grep -r "@Async\|@EnableAsync\|ExecutorService" src/main/java/

# Verificar Sealed Classes
grep -r "sealed.*interface\|sealed.*class" src/main/java/

# Verificar que no hay clases tradicionales para DTOs
find src/main/java -name "*Request.java" -o -name "*Response.java" | xargs grep -L "public record"

# Verificar Text Blocks en templates
grep -r '"""\s*<html>\|"""\s*{' src/main/java/

# Verificar factory methods en Records
grep -r "public static.*Record" src/main/java/dto/

# Verificar métodos de instancia en Records
grep -r "public.*boolean\|public.*String" src/main/java/dto/

# Verificar switch exhaustivo sin default
grep -r "switch.*{.*case.*case.*}" src/main/java/ | grep -v default

# Verificar código preparado para Java 21
grep -r "🔮 PREPARADO PARA JAVA 21\|@ConditionalOnJavaVersion" src/main/java/

# Verificar roadmap de migración implementado
grep -r "JAVA 17:\|JAVA 21:" src/main/java/

# Verificar configuración condicional
grep -r "ConditionalOnJavaVersion\|JavaVersion.TWENTY_ONE" src/main/java/

# Verificar comentarios de migración
grep -r "/\*.*Java 21.*\*/" src/main/java/

# Verificar async operations específicas del dominio
grep -r "notifyTicket\|sendNotification" src/main/java/service/
```

---
**Estado:** Listo para migración a `.amazonq\rules\`