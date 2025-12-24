# Sistema Ticketero Digital - Technology Stack

## Programming Languages & Versions
- **Java 17**: Primary development language with modern LTS features
- **SQL**: PostgreSQL-specific queries and Flyway migrations
- **YAML**: Configuration files (application.yml, docker-compose.yml)
- **XML**: Maven POM configuration

## Core Framework & Dependencies

### Spring Boot 3.2.11
- **spring-boot-starter-web**: REST API development with embedded Tomcat
- **spring-boot-starter-data-jpa**: JPA/Hibernate ORM with repository pattern
- **spring-boot-starter-validation**: Bean validation with JSR-303 annotations
- **spring-boot-starter-actuator**: Production monitoring and health checks
- **spring-boot-starter-test**: Comprehensive testing framework

### Database & Persistence
- **PostgreSQL**: Primary production database
- **Flyway 9.22.3**: Database migration management
- **H2**: In-memory database for testing
- **JPA/Hibernate**: ORM with entity relationship mapping

### Additional Libraries
- **Lombok**: Boilerplate code reduction (getters, setters, builders)
- **Spring Retry**: Resilience patterns for external API calls
- **Spring Aspects**: AOP support for cross-cutting concerns

## Build System & Tools

### Maven Configuration
```xml
<groupId>com.example</groupId>
<artifactId>ticketero</artifactId>
<version>1.0.0</version>
<java.version>17</java.version>
```

### Key Maven Plugins
- **spring-boot-maven-plugin**: Application packaging and execution
- **flyway-maven-plugin**: Database migration management
- **maven-compiler-plugin**: Java 17 compilation

## Development Environment

### Database Configuration
```yaml
# Development Database
URL: jdbc:postgresql://localhost:5432/ticketero_db
User: ticketero_user
Password: ticketero_pass
```

### Application Profiles
- **Default Profile**: Development configuration with local PostgreSQL
- **Test Profile**: H2 in-memory database with test-specific settings

## External Integrations

### Telegram Bot API
- REST client integration for bot notifications
- Environment variable configuration for bot tokens
- Retry mechanisms for API reliability

### Scheduler Configuration
- Spring's @Scheduled annotation for background processing
- Configurable intervals for queue processing and message delivery
- Async execution support

## Development Commands

### Application Lifecycle
```bash
# Build application
mvn clean compile

# Run tests
mvn test

# Package application
mvn clean package

# Run application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=test
```

### Database Management
```bash
# Run Flyway migrations
mvn flyway:migrate

# Validate migrations
mvn flyway:validate

# Clean database (development only)
mvn flyway:clean
```

### Docker Operations
```bash
# Build and start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

## Testing Framework

### Test Dependencies
- **JUnit 5**: Primary testing framework
- **Spring Boot Test**: Integration testing with application context
- **H2 Database**: In-memory testing database
- **Mockito**: Mocking framework (included in spring-boot-starter-test)

### Test Categories
- **Unit Tests**: Service layer business logic testing
- **Integration Tests**: Repository and controller testing
- **Validation Tests**: DTO and entity validation testing

## Configuration Management

### Environment Variables
- `TELEGRAM_BOT_TOKEN`: Telegram Bot API authentication
- `DATABASE_URL`: Production database connection string
- `SPRING_PROFILES_ACTIVE`: Active configuration profile

### Application Properties
- Database connection settings
- Scheduler intervals and timing
- Telegram API configuration
- Logging levels and patterns

## Security Considerations
- No sensitive credentials in source code
- Environment variable configuration for secrets
- Input validation with Bean Validation
- Exception handling without information leakage

## Performance Features
- Connection pooling for database operations
- Async processing for background tasks
- Retry mechanisms for external API calls
- Efficient JPA queries with proper indexing

## Monitoring & Observability
- Spring Boot Actuator endpoints for health checks
- Comprehensive audit logging
- Dashboard metrics and KPIs
- Exception tracking and error reporting

## Package Structure Convention
```
com.example.ticketero (prototype - change to com.{company}.ticketero for production)
├── config/          # Configuration classes
├── controller/      # REST endpoints
├── service/         # Business logic
├── repository/      # Data access
├── model/           # Entities, DTOs, enums
├── scheduler/       # Background processing
├── exception/       # Error handling
└── test/           # Internal testing utilities
```