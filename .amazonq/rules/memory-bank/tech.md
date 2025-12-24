# Sistema Ticketero - Technology Stack

## Core Technologies

### Backend Framework
- **Spring Boot 3.2.11**: Main application framework
- **Java 17**: Programming language and runtime
- **Maven**: Build system and dependency management

### Database & Persistence
- **PostgreSQL**: Primary database for production
- **Spring Data JPA**: Object-relational mapping
- **Hibernate**: JPA implementation with PostgreSQL dialect
- **Flyway 9.22.3**: Database migration management
- **H2 Database**: In-memory database for testing

### External Integrations
- **Telegram Bot API**: Customer notification system
- **RestTemplate**: HTTP client for external API calls
- **Spring Retry**: Resilient communication with exponential backoff

### Development & Testing
- **Lombok**: Boilerplate code reduction
- **Spring Boot Test**: Testing framework
- **JUnit**: Unit testing
- **Spring Boot Actuator**: Application monitoring and health checks

## Build Configuration

### Maven Dependencies
```xml
<!-- Core Spring Boot -->
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-validation
spring-boot-starter-actuator

<!-- Database -->
postgresql (runtime)
flyway-core
h2 (test scope)

<!-- Utilities -->
lombok (optional)
spring-retry
spring-aspects
```

### Java Configuration
- **Source/Target**: Java 17
- **Encoding**: UTF-8
- **Compiler**: Maven Compiler Plugin

## Development Commands

### Build & Run
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Run application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Database Management
```bash
# Run Flyway migrations
mvn flyway:migrate

# Validate migrations
mvn flyway:validate

# Show migration info
mvn flyway:info

# Clean database (development only)
mvn flyway:clean
```

### Docker Operations
```bash
# Build and start services
docker-compose up -d

# Stop services
docker-compose down

# View logs
docker-compose logs -f

# Rebuild containers
docker-compose up --build
```

## Environment Configuration

### Database Connection
```yaml
# Development (default)
DATABASE_HOST: localhost
DATABASE_PORT: 5432
DATABASE_NAME: ticketero_db
DATABASE_USER: ticketero_user
DATABASE_PASSWORD: ticketero_pass
```

### Telegram Integration
```yaml
# Bot configuration
TELEGRAM_BOT_TOKEN: "123456789:ABC-DEF1234ghIkl-zyx57W2v1u123ew11"
TELEGRAM_TIMEOUT: 30000
```

### Scheduler Configuration
```yaml
# Processing intervals
SCHEDULER_MESSAGE_RATE: 60000  # 60 seconds
SCHEDULER_QUEUE_RATE: 5000     # 5 seconds
```

### Application Settings
```yaml
# Server configuration
SERVER_PORT: 8080

# Audit retention
AUDIT_RETENTION_DAYS: 2555  # 7 years

# Queue management
NO_SHOW_TIMEOUT: 5          # 5 minutes
MAX_CONCURRENT_TICKETS: 3   # Per advisor
```

## Development Profiles

### Default (Development)
- Local PostgreSQL database
- Debug logging enabled
- Development Telegram token
- Fast scheduler intervals for testing

### Test Profile
- H2 in-memory database
- Test-specific configurations
- Mock external services
- Reduced timeouts for faster tests

### Production Profile
- External database configuration
- Production logging levels
- Production API tokens
- Optimized scheduler intervals

## IDE Setup Requirements

### Required Plugins/Extensions
- **Lombok**: For annotation processing
- **Spring Boot**: For enhanced Spring support
- **Database**: PostgreSQL driver support

### Recommended Settings
- **Java 17** as project SDK
- **Maven** as build tool
- **UTF-8** file encoding
- **Spring Boot** run configurations

## Monitoring & Observability

### Spring Boot Actuator Endpoints
- `/actuator/health`: Application health status
- `/actuator/info`: Application information
- `/actuator/metrics`: Application metrics
- `/actuator/env`: Environment properties

### Logging Configuration
- **Console Pattern**: Timestamp and message format
- **File Pattern**: Thread, level, logger, and message
- **Log Levels**: Configurable per package
- **Audit Logging**: Separate audit trail logging