# Sistema Ticketero Digital - Development Guidelines

## Code Quality Standards

### Package Structure Convention
- **Base Package**: `com.example.ticketero` (prototype - change to `com.{company}.ticketero` for production)
- **Layer Separation**: Clear separation between controller, service, repository, model layers
- **Consistent Naming**: Package names follow domain-driven design principles

### Class and Method Naming
- **Service Classes**: End with `Service` suffix (e.g., `DashboardService`, `AuditService`)
- **Repository Classes**: End with `Repository` suffix following Spring Data JPA conventions
- **Controller Classes**: End with `Controller` suffix for REST endpoints
- **DTO Classes**: Descriptive names with `Request`/`Response` suffixes
- **Method Names**: Descriptive, action-oriented names (e.g., `generateResumenEjecutivo`, `logTicketCreated`)

### Documentation Standards
- **Class-level JavaDoc**: Include purpose and functional requirement reference (e.g., `RF-004`, `RF-008`)
- **Method Documentation**: Brief descriptions for complex business logic methods
- **Inline Comments**: Spanish comments for business domain explanations
- **Code Self-Documentation**: Prefer readable code over excessive comments

## Structural Conventions

### Service Layer Patterns
- **Constructor Injection**: Use `@RequiredArgsConstructor` with `final` fields
- **Transaction Management**: `@Transactional(readOnly = true)` at class level, `@Transactional` for write operations
- **Logging**: `@Slf4j` annotation with structured logging using `log.debug()` for operations
- **Builder Pattern**: Use Lombok `@Builder` for complex entity creation

### Repository Layer Standards
- **JPA Repositories**: Extend `JpaRepository<Entity, ID>` for standard CRUD operations
- **Query Methods**: Use Spring Data JPA derived query methods (e.g., `countByStatusAndQueueType`)
- **Custom Queries**: Define complex queries using `@Query` annotation when needed
- **Method Naming**: Follow Spring Data JPA conventions for automatic query generation

### Controller Layer Conventions
- **REST Endpoints**: Clear, RESTful URL patterns
- **Response DTOs**: Always return structured DTOs, never entities directly
- **Exception Handling**: Centralized exception handling with `@GlobalExceptionHandler`
- **Validation**: Use Bean Validation annotations at DTO level

### Model Layer Patterns
- **Records for DTOs**: Use Java records for immutable data transfer objects
- **Validation Annotations**: Comprehensive Bean Validation with custom messages in Spanish
- **Enum Usage**: Strong typing with enums for status, types, and categories
- **Factory Methods**: Static factory methods for DTO creation from entities (e.g., `from(Entity)`)

## Semantic Patterns

### Error Handling Strategy
- **Global Exception Handler**: Centralized error handling with `@ControllerAdvice`
- **Custom Exceptions**: Domain-specific exceptions (e.g., `TicketNotFoundException`)
- **Structured Error Responses**: Consistent `ErrorResponse` DTO with status codes and messages
- **Validation Error Mapping**: Bean Validation violations mapped to user-friendly messages

### Audit Trail Implementation
- **Comprehensive Logging**: All business operations logged through `AuditService`
- **Event Types**: Standardized event types (e.g., `TICKET_CREATED`, `STATUS_CHANGED`)
- **Actor Tracking**: Track actor type (CLIENT, ADVISOR, SYSTEM, SUPERVISOR)
- **IP Address Logging**: Include client IP for security and compliance

### Data Validation Patterns
- **Bean Validation**: Use Jakarta Validation annotations extensively
- **Custom Validators**: Pattern-based validation for business rules (RUT format, phone numbers)
- **Multilingual Messages**: Validation messages in Spanish for user-facing errors
- **Positive ID Validation**: Ensure all ID fields are positive numbers

### Business Logic Patterns
- **Status Management**: Enum-based status tracking with clear state transitions
- **Queue Processing**: Automated background processing with Spring `@Scheduled`
- **Load Balancing**: Least-loaded advisor assignment algorithm
- **Metrics Calculation**: Real-time dashboard metrics with statistical analysis

## Internal API Usage Patterns

### Spring Boot Annotations
```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExampleService {
    
    private final ExampleRepository repository;
    
    @Transactional
    public void writeOperation() {
        // Write operations
    }
}
```

### Repository Query Patterns
```java
// Derived query methods
List<Entity> findByStatusOrderByAssignedTicketsCountAsc(Status status);
long countByStatusAndQueueType(TicketStatus status, QueueType queueType);

// Custom queries for complex operations
@Query("SELECT e FROM Entity e WHERE e.condition = :param")
List<Entity> customQuery(@Param("param") String param);
```

### DTO Validation Patterns
```java
public record RequestDTO(
    @NotBlank(message = "Campo obligatorio")
    @Size(min = 5, max = 200, message = "Debe tener entre 5-200 caracteres")
    String field,
    
    @NotNull(message = "ID es obligatorio")
    @Positive(message = "ID debe ser positivo")
    Long id,
    
    @Pattern(regexp = "^\\+56[0-9]{9}$", message = "Formato inválido")
    String phone
) {}
```

### Audit Logging Pattern
```java
@Transactional
public void logBusinessEvent(Entity entity, String actor, ActorType actorType, String clientIp) {
    AuditEvent event = AuditEvent.builder()
        .timestamp(LocalDateTime.now())
        .eventType("BUSINESS_EVENT")
        .actor(actor)
        .actorType(actorType)
        .additionalData(formatAdditionalData(entity))
        .ipAddress(clientIp)
        .build();
    
    auditEventRepository.save(event);
    log.debug("Audit logged: {} for {}", eventType, entityId);
}
```

## Frequently Used Code Idioms

### Stream Processing for Collections
```java
// Convert entities to DTOs
return entities.stream()
    .map(ResponseDTO::from)
    .collect(Collectors.toList());

// Statistical calculations
double average = values.stream()
    .mapToInt(Type::getValue)
    .average()
    .orElse(0.0);
```

### Conditional Logic for Business Rules
```java
// Status determination based on thresholds
private String determineStatus(int count) {
    if (count == 0) return "EMPTY";
    if (count <= 3) return "NORMAL";
    if (count <= 7) return "MODERATE";
    return "SATURATED";
}
```

### Builder Pattern for Complex Objects
```java
Entity entity = Entity.builder()
    .field1(value1)
    .field2(value2)
    .timestamp(LocalDateTime.now())
    .build();
```

## Popular Annotations

### Class-Level Annotations
- `@Service` - Service layer components
- `@Repository` - Data access layer (auto-applied by Spring Data JPA)
- `@Controller` / `@RestController` - Web layer components
- `@Component` - Generic Spring components
- `@RequiredArgsConstructor` - Lombok constructor injection
- `@Slf4j` - Lombok logging
- `@Transactional` - Transaction management

### Method-Level Annotations
- `@Transactional` - Write operations
- `@Query` - Custom JPA queries
- `@Scheduled` - Background processing
- `@Override` - Method overriding

### Validation Annotations
- `@NotNull` / `@NotBlank` - Null/empty validation
- `@Size(min, max)` - String/collection size validation
- `@Pattern(regexp)` - Regular expression validation
- `@Positive` - Positive number validation

### Testing Annotations
- `@Component` - Test components
- `@Autowired` - Dependency injection in tests
- `CommandLineRunner` - Test execution interface

## Development Best Practices

### Performance Considerations
- Use `@Transactional(readOnly = true)` for read-only operations
- Implement efficient JPA queries with proper indexing
- Use streaming for large collections processing
- Cache frequently accessed data where appropriate

### Security Practices
- Validate all input data with Bean Validation
- Log security-relevant events through audit trail
- Never expose internal entity structure in APIs
- Use structured error responses without sensitive information

### Maintainability Standards
- Keep methods focused on single responsibilities
- Use meaningful variable and method names
- Implement comprehensive logging for debugging
- Follow consistent code formatting and structure