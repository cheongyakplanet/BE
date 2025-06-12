# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is the backend service for "CheonYakPlanet" (청약플래닛), a Korean real estate subscription platform that helps users find and track public housing subscriptions, provides community features, and offers location-based infrastructure information.

## Build and Development Commands

### Running the Application
```bash
./gradlew bootRun
```

### Building
```bash
./gradlew build
```

### Testing
```bash
./gradlew test                    # Run all tests
./gradlew test --tests ClassName  # Run specific test class
./gradlew jacocoTestReport       # Generate test coverage report
```

### Database
- Uses MySQL database
- JPA/Hibernate for ORM with soft delete support
- Database migrations handled through JPA schema updates

## Architecture Overview

### Domain-Driven Design Structure
The application follows Clean Architecture principles with distinct layers:

**Domain Layer** (`src/main/java/org/cheonyakplanet/be/domain/`):
- **User**: Authentication, interest locations, financial profiles
- **Subscription**: Housing subscription data with price info, supply targets
- **Community**: Posts, comments, replies with hierarchical structure
- **Finance**: House loans and mortgage products
- **Infrastructure**: Stations, schools, public facilities
- **Chat**: Real-time messaging with AI integration

**Application Layer** (`src/main/java/org/cheonyakplanet/be/application/`):
- Services handle business logic with transaction management
- DTOs for data transformation between layers
- Clean separation of read/write operations

**Infrastructure Layer** (`src/main/java/org/cheonyakplanet/be/infrastructure/`):
- JWT-based authentication with database token storage
- WebSocket for real-time chat features
- External API integrations (Kakao, Government APIs, Gemini AI)
- Scheduled tasks for data updates

**Presentation Layer** (`src/main/java/org/cheonyakplanet/be/presentation/`):
- REST controllers with OpenAPI documentation
- Global exception handling with custom error codes
- Security filters and authentication

### Key Technical Components

**Authentication Flow**:
- JWT tokens with HS256 algorithm
- Access tokens (60 min) + Refresh tokens (24 hours)
- Database storage with blacklisting for secure logout
- WebSocket authentication via JWT handshake interceptor

**Real-time Features**:
- WebSocket-based chat with AI assistant (Gemini 2.0)
- Daily usage limits (15 messages per user)
- Session management and cleanup

**Data Processing**:
- Soft delete pattern with audit trails (`Stamped` base entity)
- Complex address parsing for Korean locations
- Geographic distance calculations
- Scheduled data updates (subscription info, real estate prices)

## Development Patterns

### Entity Design
- Use `@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})` for audit and soft delete
- Extend `Stamped` base class for automatic audit fields
- Use `@JsonManagedReference`/`@JsonBackReference` for bidirectional relationships

### Service Layer
- Mark read operations with `@Transactional(readOnly = true)` 
- Use `@Transactional` for write operations
- Consistent DTO transformation patterns with `fromEntity()` methods
- Custom exceptions with meaningful error codes (`ErrorCode` enum)

### Security
- All endpoints require JWT authentication except public APIs
- Use `UserDetailsImpl` to access current user context
- Custom error handling for JWT-related exceptions (AUTH001-AUTH010)

### External API Integration
- Use `WebClient` for reactive HTTP calls
- `RestTemplate` for traditional synchronous calls
- Proper error handling with fallback responses

## Git Conventions

Follow the established commit message format:
- `Feat`: New features
- `Fix`: Bug fixes  
- `Refactor`: Code refactoring
- `Test`: Test-related changes
- `Build`: Build system changes
- `Docs`: Documentation updates

## Testing
- JUnit 5 with Spring Boot Test
- Jacoco for coverage reports
- Repository layer tested with `@DataJpaTest`
- Controller layer tested with `@WebMvcTest`
- Service layer with mocked dependencies

## Monitoring and Observability
- Spring Boot Actuator enabled
- Prometheus metrics integration
- Comprehensive logging with Logback

## Key File Locations
- Main application: `src/main/java/org/cheonyakplanet/be/BeApplication.java`
- Configuration: `src/main/java/org/cheonyakplanet/be/infrastructure/config/`
- External scripts: `src/scripts/additional_info.py` (for data processing)
- Static resources: `src/main/resources/static/`
- Test resources: `src/test/java/org/cheonyakplanet/be/`