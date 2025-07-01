# CLAUDE.md

This file provides comprehensive guidance to Claude Code (claude.ai/code) when working with the CheonYakPlanet Korean real estate platform.

## Project Overview

CheonYakPlanet (청약플래닛) is a comprehensive Korean real estate subscription platform specializing in **public housing subscriptions (청약)** - Korea's government-regulated housing allocation system. The platform helps users navigate complex subscription processes, provides financial qualification matching, and offers AI-powered guidance for housing decisions.

### Business Domain Context

**Korean Real Estate Subscription System (청약 시스템):**
- **청약 (Subscription)**: Public housing subscription where citizens compete for newly constructed apartments at government-regulated prices
- **분양 (Bunyang)**: Developer-led apartment sales process
- **특별공급 (Special Supply)**: Reserved units for specific demographics (newlyweds, elderly, multi-child families)
- **일반공급 (General Supply)**: Regular subscription process for general public
- **순위제 (Ranking System)**: Priority based on subscription savings history (1순위, 2순위)
- **해당지역/기타지역**: Local area vs. other area eligibility requirements

**Core Business Features:**
1. **Subscription Discovery**: Real-time tracking of government housing announcements
2. **Eligibility Matching**: User financial profiles matched against subscription requirements
3. **Location Intelligence**: Infrastructure analysis (schools, stations, public facilities)
4. **Market Intelligence**: Automated news aggregation and price analysis
5. **AI Guidance**: Real-time chat assistance for subscription strategies (15 messages/day limit)
6. **Community Platform**: User discussions and experiences sharing

## Development Memories

- Implemented comprehensive JWT authentication with database token storage and blacklisting
- Developed WebSocket-based real-time chat with Gemini 2.0 AI assistant
- Created complex Korean address parsing and geocoding utility
- Integrated multiple government APIs for real-time housing subscription data
- Designed multi-layer architecture following Domain-Driven Design principles
- Implemented soft delete pattern with comprehensive audit trails across all entities
- Developed advanced geographic distance calculation for infrastructure proximity analysis
- Created a robust testing framework with 70%+ code coverage
- Built scheduled tasks for daily and monthly data synchronization
- Implemented comprehensive error handling with custom error codes
- to memorize

## Build and Development Commands

### Environment Prerequisites

**Java 17 Required**: This project requires Java 17. If you encounter "JAVA_HOME is not set" errors, set up the Java environment:

```bash
# Set Java environment (adjust path to your Java 17 installation)
export JAVA_HOME=/mnt/d/projects/planet/BE/jdk-17.0.7+7
export PATH=$JAVA_HOME/bin:$PATH

# Verify Java installation
java -version
# Should show: openjdk version "17.x.x"
```

### Running the Application
```bash
# Set Java environment first, then run
export JAVA_HOME=/mnt/d/projects/planet/BE/jdk-17.0.7+7 && export PATH=$JAVA_HOME/bin:$PATH && ./gradlew bootRun
```

### Building
```bash
# Full build with environment setup
export JAVA_HOME=/mnt/d/projects/planet/BE/jdk-17.0.7+7 && export PATH=$JAVA_HOME/bin:$PATH && ./gradlew build

# Build without daemon (recommended for CI/WSL environments)
export JAVA_HOME=/mnt/d/projects/planet/BE/jdk-17.0.7+7 && export PATH=$JAVA_HOME/bin:$PATH && ./gradlew build --no-daemon
```

### Testing
```bash
# Run all tests
export JAVA_HOME=/mnt/d/projects/planet/BE/jdk-17.0.7+7 && export PATH=$JAVA_HOME/bin:$PATH && ./gradlew test

# Run specific test class
export JAVA_HOME=/mnt/d/projects/planet/BE/jdk-17.0.7+7 && export PATH=$JAVA_HOME/bin:$PATH && ./gradlew test --tests ClassName

# Generate test coverage report
export JAVA_HOME=/mnt/d/projects/planet/BE/jdk-17.0.7+7 && export PATH=$JAVA_HOME/bin:$PATH && ./gradlew jacocoTestReport

# Generate IntelliJ-style HTML coverage report (outputs to ./htmlReport/index.html)
export JAVA_HOME=/mnt/d/projects/planet/BE/jdk-17.0.7+7 && export PATH=$JAVA_HOME/bin:$PATH && ./gradlew intellijStyleCoverageReport

# Verify coverage thresholds
export JAVA_HOME=/mnt/d/projects/planet/BE/jdk-17.0.7+7 && export PATH=$JAVA_HOME/bin:$PATH && ./gradlew jacocoTestCoverageVerification
```

### Common Build Issues

**"JAVA_HOME is not set" Error**: 
- Solution: Set JAVA_HOME environment variable before running Gradle commands
- Required Java version: 17.x.x (OpenJDK recommended)

**"gradle-wrapper.properties does not exist" Error**:
- Solution: Ensure `/gradle/wrapper/gradle-wrapper.properties` exists with correct Gradle distribution URL

**Lombok compilation warnings**:
- Solution: Ensure `@EqualsAndHashCode(callSuper = false)` is added to entity classes extending `Stamped`
- Solution: Use `@Builder.Default` for fields with default values in `@Builder` classes

### Database
- Uses MySQL database with catalog separation (`catalog = "planet"`)
- JPA/Hibernate for ORM with comprehensive soft delete support
- Database migrations handled through JPA schema updates
- All entities extend `Stamped` base class for automatic audit trails