# Sistema Ticketero - Development Guidelines

## Code Quality Standards

### Class-Level Documentation
- **Comprehensive JavaDoc**: Every service class includes detailed purpose, functionality, and architectural references
- **Business Context**: Classes reference specific requirements (RF-004, ADR-001) and business rules (RN-007, RN-008)
- **Architecture Links**: Documentation references design documents and implementation plans
- **Version Information**: Classes include author and version metadata

### Method Documentation Patterns
- **Business Logic Methods**: Include purpose, parameters, and business rule references
- **Public API Methods**: Comprehensive documentation with examples and constraints
- **Private Helper Methods**: Concise but clear purpose statements
- **Audit Methods**: Detailed logging context and compliance references

### Naming Conventions
- **Service Classes**: Descriptive names ending in "Service" (DashboardService, AuditService)
- **Method Names**: Spanish business terms for domain methods (registrarEvento, determinarActorType)
- **Variable Names**: Clear, descriptive names matching business context
- **Constants**: Uppercase with underscores for configuration values

## Structural Conventions

### Service Layer Architecture
- **@Service Annotation**: All business logic classes marked as Spring services
- **@RequiredArgsConstructor**: Lombok for constructor-based dependency injection
- **@Slf4j**: Standardized logging using SLF4J with Lombok
- **@Transactional**: Explicit transaction boundaries with readOnly for queries

### Dependency Injection Patterns
```java
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ServiceClass {
    private final Repository repository;
    private final OtherService otherService;
}
```

### Transaction Management
- **Read-Only Transactions**: Default @Transactional(readOnly = true) on class level
- **Write Operations**: Explicit @Transactional on methods that modify data
- **Audit Operations**: Always transactional for data consistency

### Error Handling & Logging
- **Debug Logging**: Extensive use of log.debug() for operation tracking
- **Business Context**: Log messages include business identifiers (ticket numbers, actor names)
- **Audit Trail**: Comprehensive logging for compliance and debugging

## Semantic Patterns

### Builder Pattern Usage
- **Entity Creation**: Consistent use of builder pattern for complex objects
- **AuditEvent Creation**: Standardized builder usage with fluent API
- **Configuration Objects**: Builder pattern for complex configuration setup

### Stream API Patterns
- **Data Transformation**: Extensive use of streams for collections processing
- **Filtering Operations**: Stream filters for business rule application
- **Aggregation**: Stream collectors for statistical calculations

### Enum Integration
- **Business States**: Enums for all business states (TicketStatus, AdvisorStatus, QueueType)
- **Type Safety**: Enum parameters in method signatures for type safety
- **Business Logic**: Enum methods for business calculations (getAvgTimeMinutes())

### Factory Method Pattern
- **DTO Creation**: Static factory methods (AuditEventResponse.from())
- **Response Objects**: Consistent factory pattern for API responses
- **Entity Conversion**: Standardized conversion between entities and DTOs

## Internal API Usage & Patterns

### Repository Integration
```java
// Standard repository usage pattern
private final TicketRepository ticketRepository;
private final AdvisorRepository advisorRepository;

// Query method usage
int count = (int) ticketRepository.countByStatusAndQueueType(status, queueType);
List<Advisor> advisors = advisorRepository.findByStatusOrderByAssignedTicketsCountAsc(status);
```

### Service Orchestration
```java
// Service composition pattern
private final QueueService queueService;
private final AuditService auditService;

// Method delegation with audit logging
public void performBusinessOperation() {
    // Business logic
    auditService.logSystemAction(eventType, actor, actorType, description, clientIp);
}
```

### Configuration Access
```java
// Environment-based configuration
@Value("${scheduler.queue.fixed-rate:5000}")
private long queueProcessingRate;

// Business rule configuration
private static final int NO_SHOW_TIMEOUT_MINUTES = 5;
```

## Frequently Used Code Idioms

### Null Safety Patterns
```java
// Optional usage for safe operations
return advisorRepository.findFirstByOrderByAssignedTicketsCountDesc()
    .map(advisor -> new DashboardResponse.EjecutivoProductivo(
        advisor.getName(),
        advisor.getAssignedTicketsCount()
    ))
    .orElse(new DashboardResponse.EjecutivoProductivo("N/A", 0));
```

### Collection Processing
```java
// Stream aggregation patterns
double average = Arrays.stream(QueueType.values())
    .mapToInt(QueueType::getAvgTimeMinutes)
    .average()
    .orElse(10.0);

// Filtering and counting
long criticalAlerts = alertas.stream()
    .filter(a -> "CRITICAL".equals(a.prioridad()))
    .count();
```

### Time Handling
```java
// LocalDateTime usage patterns
LocalDateTime now = LocalDateTime.now();
LocalDateTime startOfDay = now.withHour(0).withMinute(0).withSecond(0);

// Duration calculations
long horasTranscurridas = Math.max(1, Duration.between(startOfDay, now).toHours());
```

## Popular Annotations

### Spring Framework Annotations
- **@Service**: Business logic components (100% usage in service classes)
- **@Transactional**: Transaction management (readOnly = true default)
- **@Autowired**: Dependency injection in test classes
- **@Component**: Utility and test components

### Lombok Annotations
- **@RequiredArgsConstructor**: Constructor generation (100% usage in services)
- **@Slf4j**: Logging integration (100% usage in services)
- **@Builder**: Complex object creation (entities and DTOs)

### Validation Annotations
- **@NotNull**: Required field validation
- **@NotBlank**: String validation with content requirements
- **@Size**: Length constraints for strings and collections
- **@Pattern**: Regex validation for formatted fields (RUT, phone numbers)

### JPA Annotations
- **@Entity**: Database entity mapping
- **@Table**: Table name specification
- **@Column**: Column mapping and constraints
- **@Enumerated**: Enum persistence strategy

## Testing Patterns

### Test Structure
- **Main Method Testing**: Simple validation tests using main() methods
- **Component Testing**: Spring Boot test components with @Component
- **Validation Testing**: Bean Validation framework integration
- **Repository Testing**: CommandLineRunner implementation for database testing

### Assertion Patterns
```java
// Console-based validation
System.out.println("✅ Validation successful");
System.out.println("❌ ERROR: Violations found");

// Conditional validation
if (violations.isEmpty()) {
    System.out.println("✅ No violations found");
} else {
    violations.forEach(v -> System.out.println("❌ " + v.getMessage()));
}
```

### Test Data Creation
- **Valid Test Cases**: Complete objects with all required fields
- **Invalid Test Cases**: Systematic violation of validation rules
- **Factory Method Testing**: Verification of DTO creation patterns
- **Nested Record Testing**: Complex object structure validation