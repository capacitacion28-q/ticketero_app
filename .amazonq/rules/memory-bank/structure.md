# Sistema Ticketero Digital - Project Structure

## Directory Organization

### `/docs/` - Documentation Hub
Comprehensive project documentation organized by development phases:
- **`architecture/`**: Software design, PlantUML diagrams, architectural decisions (ADRs)
- **`implementation/`**: Development plans, audit reports, code documentation
- **`prompts/`**: 7-stage methodology prompts organized by development phase
- **`requirements/`**: Business requirements, functional analysis (IEEE 830), use cases
- **`verify/`**: Testing strategies, health checks, integration tests, reports

### `/src/main/java/com/example/ticketero/` - Core Application
Spring Boot application following layered architecture pattern:

#### **`config/`** - Configuration Layer
- `SchedulerConfig.java`: Async processing and scheduler configuration
- `TelegramConfig.java`: Telegram Bot API integration setup

#### **`controller/`** - REST API Layer
- `TicketController.java`: Core ticket operations (CRUD, status updates)
- `AdminController.java`: Administrative operations and system management
- `AuditController.java`: Audit trail access and reporting
- `TelegramTestController.java`: Telegram integration testing endpoints
- `TestAssignmentController.java`: Development testing utilities

#### **`service/`** - Business Logic Layer
- `TicketService.java`: Core ticket management business logic
- `QueueService.java` & `QueueManagementService.java`: Queue processing and management
- `AdvisorService.java`: Advisor availability and assignment logic
- `TelegramService.java` & `NotificationService.java`: Communication services
- `DashboardService.java`: Analytics and reporting logic
- `AuditService.java`: Audit trail and compliance tracking

#### **`repository/`** - Data Access Layer
JPA repositories for entity persistence:
- `TicketRepository.java`: Ticket data operations
- `AdvisorRepository.java`: Advisor management data
- `MensajeRepository.java`: Message queue data
- `AuditEventRepository.java`: Audit logging data

#### **`model/`** - Data Model Layer
##### **`entity/`** - JPA Entities
- `Ticket.java`: Core ticket entity with status tracking
- `Advisor.java`: Advisor information and availability
- `Mensaje.java`: Message queue for Telegram notifications
- `AuditEvent.java`: Audit trail events

##### **`dto/`** - Data Transfer Objects
- `TicketCreateRequest.java` & `TicketResponse.java`: Ticket API contracts
- `DashboardResponse.java` & `QueueStatusResponse.java`: Dashboard data
- `AuditEventResponse.java`: Audit data transfer
- `ErrorResponse.java`: Standardized error responses

##### **`enums/`** - Type Definitions
- `TicketStatus.java`: Ticket lifecycle states
- `QueueType.java`: Queue classification (GENERAL, PRIORITY, VIP)
- `AdvisorStatus.java`: Advisor availability states
- `MessageTemplate.java`: Telegram message templates
- `ActorType.java` & `EstadoEnvio.java`: Actor types and message states

#### **`scheduler/`** - Background Processing
- `QueueProcessorScheduler.java`: Automated ticket assignment and queue processing
- `MensajeScheduler.java`: Telegram message delivery scheduling

#### **`exception/`** - Error Handling
- `GlobalExceptionHandler.java`: Centralized exception handling
- `TicketNotFoundException.java` & `TicketActivoExistenteException.java`: Domain-specific exceptions

#### **`test/`** - Internal Testing
- `DtoValidationTest.java`: DTO validation testing
- `RepositoryValidationTest.java`: Repository layer testing

### `/src/main/resources/` - Application Resources
- `application.yml`: Main application configuration
- `db/migration/`: Flyway database migration scripts

### `/src/test/` - Test Suite
- Test classes mirror main package structure
- `application-test.yml`: Test-specific configuration

### `/docker/` - Containerization
- Docker configuration and initialization scripts
- `docker-compose.yml`: Multi-container orchestration

## Architectural Patterns

### Layered Architecture
- **Controller Layer**: REST API endpoints and request handling
- **Service Layer**: Business logic and transaction management
- **Repository Layer**: Data access abstraction
- **Model Layer**: Domain entities and data contracts

### Dependency Injection
- Spring Boot IoC container manages component lifecycle
- Constructor injection for required dependencies
- Configuration classes for external integrations

### Event-Driven Processing
- Scheduler-based background processing
- Asynchronous message delivery
- Queue-based ticket assignment

### Repository Pattern
- JPA repositories abstract data access
- Custom query methods for business operations
- Transaction management at service layer

## Core Component Relationships

### Ticket Lifecycle Flow
`TicketController` → `TicketService` → `QueueService` → `AdvisorService` → `NotificationService`

### Queue Processing Flow
`QueueProcessorScheduler` → `QueueManagementService` → `TicketService` → `TelegramService`

### Audit Trail Flow
All services → `AuditService` → `AuditEventRepository`

### Dashboard Data Flow
`AdminController` → `DashboardService` → Multiple repositories → `DashboardResponse`

## Development Phase Structure
Project follows 7-stage methodology:
1. **Tasks**: Epic and task definition
2. **Brainstorm**: Analysis and initial design
3. **Plan**: Implementation roadmap
4. **Implement**: Component development (current phase)
5. **Verify**: Testing and validation
6. **Deploy**: Configuration and deployment
7. **Document**: Final documentation