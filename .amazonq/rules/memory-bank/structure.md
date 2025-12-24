# Sistema Ticketero - Project Structure

## Root Directory Organization

### `/docs/` - Comprehensive Documentation
- **`/architecture/`**: Software architecture design, PlantUML diagrams, architectural decision records (ADRs)
- **`/implementation/`**: Detailed implementation plans, audit reports, code documentation standards
- **`/prompts/`**: Development methodology prompts organized by 7-stage workflow (tasks, brainstorm, plan, implement, verify, deploy, document)
- **`/requirements/`**: Business requirements, functional analysis (IEEE 830), use cases
- **`/verify/`**: Testing framework with health checks, smoke tests, unit tests, functional tests

### `/src/main/java/com/example/ticketero/` - Core Application
```
├── config/          # Spring configuration classes
├── controller/      # REST API endpoints and web controllers
├── exception/       # Custom exception handling
├── model/          # JPA entities and data models
├── repository/     # Data access layer (Spring Data JPA)
├── scheduler/      # Background job scheduling
├── service/        # Business logic layer
├── test/           # Integration and validation tests
└── TicketeroApplication.java  # Spring Boot main class
```

### `/src/main/resources/` - Configuration & Assets
- **`application.yml`**: Main application configuration
- **`db/migration/`**: Flyway database migration scripts
- **Static resources and templates**

### `/docker/` - Containerization
- Docker configuration and initialization scripts
- Database setup and container orchestration

## Core Components & Relationships

### Service Layer Architecture
- **TicketService**: Core ticket lifecycle management
- **QueueService**: Queue operations and priority handling
- **QueueManagementService**: Advanced queue orchestration
- **AdvisorService**: Advisor assignment and workload management
- **NotificationService**: Multi-channel communication coordination
- **TelegramService**: Telegram Bot API integration
- **DashboardService**: Real-time metrics and monitoring
- **AuditService**: Compliance logging and audit trail management

### Data Layer Structure
- **Repository Pattern**: Spring Data JPA repositories for data access
- **Entity Models**: JPA entities representing business domain
- **Migration Management**: Flyway-based database versioning

### Configuration Management
- **Environment-based Configuration**: Development, testing, production profiles
- **External Configuration**: Database connections, API tokens, scheduler intervals
- **Retry Policies**: Configurable retry mechanisms with exponential backoff

## Architectural Patterns

### Layered Architecture
1. **Controller Layer**: REST API endpoints and request handling
2. **Service Layer**: Business logic and orchestration
3. **Repository Layer**: Data access abstraction
4. **Model Layer**: Domain entities and data structures

### Integration Patterns
- **External API Integration**: Telegram Bot API with retry mechanisms
- **Scheduled Processing**: Background jobs for queue management and notifications
- **Event-Driven Operations**: Automated triggers for status changes

### Data Management Patterns
- **Database Migration**: Flyway-managed schema evolution
- **Audit Trail**: Comprehensive logging for compliance
- **Connection Pooling**: Optimized database connections

## Development Workflow Structure

### 7-Stage Methodology
1. **Tasks**: Epic and task definition
2. **Brainstorm**: Analysis and initial design
3. **Plan**: Detailed implementation planning
4. **Implement**: Component development (current phase)
5. **Verify**: Testing and validation
6. **Deploy**: Configuration and deployment
7. **Document**: Final documentation

### Testing Strategy
- **Unit Tests**: Service layer testing with mocks
- **Integration Tests**: Repository and API testing
- **Validation Tests**: DTO and entity validation
- **Smoke Tests**: Basic functionality verification
- **Health Checks**: System monitoring and diagnostics

## Package Organization Considerations

### Current Structure (Prototype)
- Base package: `com.example.ticketero`
- Suitable for demonstration and development

### Production Refactoring Requirements
- Update to enterprise package: `com.{empresa}.ticketero`
- Modify `groupId` in `pom.xml`
- Update all Java imports across the codebase
- Adjust configuration references