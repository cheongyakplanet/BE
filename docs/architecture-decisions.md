# Architecture Decision Records (ADR)

## 📋 Overview

This document captures key architectural decisions made during the development of CheonYakPlanet, providing context, rationale, and consequences for future reference.

## 📚 Table of Contents

1. [ADR-001: Clean Architecture Adoption](#adr-001-clean-architecture-adoption)
2. [ADR-002: JWT Authentication with Database Storage](#adr-002-jwt-authentication-with-database-storage)
3. [ADR-003: Soft Delete Pattern Implementation](#adr-003-soft-delete-pattern-implementation)
4. [ADR-004: Korean Business Domain Modeling](#adr-004-korean-business-domain-modeling)
5. [ADR-005: WebSocket for Real-time Chat](#adr-005-websocket-for-real-time-chat)
6. [ADR-006: External API Integration Strategy](#adr-006-external-api-integration-strategy)
7. [ADR-007: Database Catalog Separation](#adr-007-database-catalog-separation)
8. [ADR-008: Async Processing for Data Synchronization](#adr-008-async-processing-for-data-synchronization)
9. [ADR-009: Comprehensive Audit Trail System](#adr-009-comprehensive-audit-trail-system)
10. [ADR-010: News Content Filtering Pipeline](#adr-010-news-content-filtering-pipeline)

---

## ADR-001: Clean Architecture Adoption

**Status**: ✅ Accepted  
**Date**: 2024-06-01  
**Deciders**: Development Team

### Context
We needed to establish a scalable, maintainable architecture for a complex Korean real estate platform with multiple external integrations and complex business rules.

### Decision
Adopt Clean Architecture (Hexagonal Architecture) with distinct layers:
- **Domain Layer**: Business entities and rules
- **Application Layer**: Use cases and business logic orchestration
- **Infrastructure Layer**: External concerns (database, APIs, frameworks)
- **Presentation Layer**: Controllers and user interface adapters

### Rationale
- **Separation of Concerns**: Clear boundaries between business logic and technical implementation
- **Testability**: Easy to unit test business logic without infrastructure dependencies
- **Flexibility**: Can swap implementations without affecting core business logic
- **Korean Domain Complexity**: Need clear modeling of complex subscription rules
- **External Dependencies**: Multiple government APIs require isolation

### Consequences
**Positive:**
- High testability with isolated business logic
- Easy to understand and maintain code structure
- Flexible integration with external systems
- Clear separation between Korean business rules and technical implementation

**Negative:**
- Additional complexity for simple operations
- More boilerplate code compared to traditional layered architecture
- Learning curve for team members unfamiliar with Clean Architecture

**Neutral:**
- Requires discipline to maintain architectural boundaries

---

## ADR-002: JWT Authentication with Database Storage

**Status**: ✅ Accepted  
**Date**: 2024-06-02  
**Deciders**: Security Team, Backend Team

### Context
Need secure authentication system supporting both web and mobile clients, with ability to revoke tokens and track user sessions.

### Decision
Implement JWT (JSON Web Tokens) with database storage:
- **Access Tokens**: 60-minute lifespan, HS256 algorithm
- **Refresh Tokens**: 24-hour lifespan
- **Database Storage**: Store tokens in `user_token` table
- **Blacklisting**: Support token revocation via blacklist flag

### Rationale
- **Stateless Authentication**: JWT tokens contain user information
- **Security**: Database storage enables token revocation and blacklisting
- **Scalability**: Can distribute across multiple server instances
- **Mobile Support**: Tokens work well with mobile applications
- **Korean Government APIs**: Many require secure authentication patterns

### Consequences
**Positive:**
- Secure token-based authentication
- Ability to revoke tokens for security
- Good performance with stateless verification
- Supports multiple client types

**Negative:**
- Database dependency for token validation
- Slightly more complex than pure stateless JWT
- Token storage requires cleanup of expired entries

**Neutral:**
- Standard industry practice for secure web applications

---

## ADR-003: Soft Delete Pattern Implementation

**Status**: ✅ Accepted  
**Date**: 2024-06-03  
**Deciders**: Database Team, Business Team

### Context
Korean real estate regulations require maintaining historical data for audit purposes. User data and subscription information must be preserved even when "deleted" by users.

### Decision
Implement comprehensive soft delete pattern:
- Add `deleted_at` and `deleted_by` fields to all entities
- Use `SoftDeleteListener` for automated handling
- All queries include `WHERE deleted_at IS NULL`
- Maintain referential integrity through soft deletes

### Rationale
- **Regulatory Compliance**: Korean data retention requirements
- **Audit Trail**: Complete history of all operations
- **Data Recovery**: Ability to restore accidentally deleted data
- **Business Intelligence**: Historical analysis capabilities
- **User Trust**: Users can recover their data if needed

### Consequences
**Positive:**
- Full audit trail for compliance
- Data recovery capabilities
- Referential integrity maintained
- Historical analysis possible

**Negative:**
- Increased database storage requirements
- More complex queries (always include deleted_at check)
- Potential for data confusion if not handled properly

**Neutral:**
- Standard practice for enterprise applications

---

## ADR-004: Korean Business Domain Modeling

**Status**: ✅ Accepted  
**Date**: 2024-06-04  
**Deciders**: Business Analysts, Domain Experts

### Context
Korean real estate subscription system (청약) has complex rules and terminology that must be accurately modeled in the system.

### Decision
Create domain-specific entities and value objects:
- Use Korean field names in database (e.g., `house_nm`, `rcept_bgnde`)
- Model complex ranking system (1순위/2순위)
- Special supply categories (다자녀, 신혼부부, 생애최초)
- Administrative hierarchy (시/도, 시/군/구, 읍/면/동)
- Financial eligibility rules integrated into user profiles

### Rationale
- **Domain Accuracy**: Reflect real Korean business processes
- **User Understanding**: Korean users understand familiar terminology
- **Government API Alignment**: Match government data structures
- **Business Rules**: Complex eligibility calculations require accurate modeling
- **Regulatory Compliance**: Must match official Korean housing rules

### Consequences
**Positive:**
- Accurate representation of Korean real estate domain
- Easy integration with government APIs
- Clear business rule implementation
- User-friendly Korean terminology

**Negative:**
- Learning curve for non-Korean developers
- Documentation must explain Korean business concepts
- Complex business rules require careful testing

**Neutral:**
- Domain-driven design principles applied to Korean context

---

## ADR-005: WebSocket for Real-time Chat

**Status**: ✅ Accepted  
**Date**: 2024-06-05  
**Deciders**: Frontend Team, Backend Team

### Context
Need real-time AI chat assistance for users with Korean real estate questions. Must support low-latency communication while maintaining security.

### Decision
Implement WebSocket-based chat system:
- JWT authentication via handshake interceptor
- Daily usage limits (15 messages per user)
- Session management with automatic cleanup
- Integration with Gemini 2.0 AI for Korean real estate expertise

### Rationale
- **Real-time Communication**: Immediate response for user questions
- **Security**: JWT-based authentication for secure connections
- **Performance**: WebSocket overhead lower than polling
- **User Experience**: Feels like real chat conversation
- **Resource Management**: Usage limits prevent abuse

### Consequences
**Positive:**
- Excellent user experience with real-time responses
- Secure authentication integration
- Efficient resource usage
- Scalable to many concurrent users

**Negative:**
- More complex than traditional REST APIs
- Requires WebSocket infrastructure support
- Connection state management complexity

**Neutral:**
- Standard for real-time web applications

---

## ADR-006: External API Integration Strategy

**Status**: ✅ Accepted  
**Date**: 2024-06-06  
**Deciders**: Integration Team, Infrastructure Team

### Context
Must integrate with multiple Korean government and commercial APIs for subscription data, news, maps, and financial information.

### Decision
Implement resilient API integration strategy:
- **Retry Mechanisms**: Configurable retry with exponential backoff
- **Circuit Breaker Pattern**: Fail fast when external services are down
- **Rate Limiting**: Respect external API rate limits
- **Async Processing**: Use CompletableFuture for parallel API calls
- **Incremental Updates**: Track processed data to avoid duplicates

### Rationale
- **Reliability**: External APIs may be unreliable or rate-limited
- **Performance**: Parallel processing improves data synchronization speed
- **Cost Management**: Avoid unnecessary API calls
- **Data Integrity**: Incremental updates prevent duplicate processing
- **Korean Government APIs**: Often have strict rate limits and availability windows

### Consequences
**Positive:**
- Robust handling of external API failures
- Efficient data synchronization
- Cost-effective API usage
- Scalable to additional external services

**Negative:**
- Increased complexity in integration code
- More difficult to debug integration issues
- Requires monitoring and alerting for external dependencies

**Neutral:**
- Industry best practices for external API integration

---

## ADR-007: Database Catalog Separation

**Status**: ✅ Accepted  
**Date**: 2024-06-07  
**Deciders**: Database Team, Operations Team

### Context
Need clear organization of database objects and potential future multi-tenancy support for different Korean regions or business units.

### Decision
Use MySQL catalog separation with `catalog = "planet"`:
- All application tables in dedicated catalog
- Clear namespace separation from system tables
- Future-proofing for multi-tenant architecture
- Simplified backup and migration procedures

### Rationale
- **Organization**: Clear separation of application data
- **Security**: Application cannot access system tables
- **Backup Strategy**: Catalog-level backup capabilities
- **Multi-tenancy**: Foundation for future regional separation
- **Korean Deployment**: May need regional data isolation

### Consequences
**Positive:**
- Clean database organization
- Enhanced security through separation
- Flexible deployment options
- Future-ready for multi-tenancy

**Negative:**
- Additional configuration complexity
- All queries must specify catalog
- Slightly more verbose entity annotations

**Neutral:**
- Standard practice for enterprise applications

---

## ADR-008: Async Processing for Data Synchronization

**Status**: ✅ Accepted  
**Date**: 2024-06-08  
**Deciders**: Performance Team, Operations Team

### Context
Government API data synchronization processes large amounts of data and must not block user-facing operations. Korean government APIs have limited availability windows.

### Decision
Implement comprehensive async processing:
- **Scheduled Tasks**: Daily/weekly/monthly data synchronization
- **Thread Pool Management**: Optimized for available CPU cores
- **CompletableFuture**: Parallel processing of API calls
- **Transaction Isolation**: Each API call in separate transaction
- **Progress Tracking**: Monitor synchronization progress

### Rationale
- **Performance**: Don't block user operations during data sync
- **Reliability**: Isolated transactions prevent partial failures
- **Resource Utilization**: Efficient use of available processing power
- **Korean Government APIs**: Must respect API windows and rate limits
- **User Experience**: System remains responsive during background processing

### Consequences
**Positive:**
- Excellent system responsiveness
- Efficient resource utilization
- Robust error handling and recovery
- Scalable to additional data sources

**Negative:**
- Increased complexity in task management
- More difficult to debug async operations
- Requires monitoring of background processes

**Neutral:**
- Essential for enterprise-grade performance

---

## ADR-009: Comprehensive Audit Trail System

**Status**: ✅ Accepted  
**Date**: 2024-06-09  
**Deciders**: Compliance Team, Security Team

### Context
Korean financial and real estate regulations require complete audit trails. Users need transparency about their data usage and system changes.

### Decision
Implement comprehensive audit system:
- **Stamped Base Class**: All entities inherit audit fields
- **Automatic Tracking**: CreatedBy, UpdatedBy, CreatedAt, UpdatedAt
- **Spring Data JPA Auditing**: Automatic population of audit fields
- **User Context**: Track which user performed each operation
- **Soft Delete Integration**: Maintain audit trail through deletions

### Rationale
- **Regulatory Compliance**: Korean data protection and financial regulations
- **Security**: Complete tracking of all system changes
- **Debugging**: Ability to trace issues back to specific operations
- **User Trust**: Transparency about data handling
- **Business Intelligence**: Understanding user behavior patterns

### Consequences
**Positive:**
- Full regulatory compliance
- Complete operational transparency
- Excellent debugging capabilities
- Strong security posture

**Negative:**
- Additional storage requirements for audit data
- Slight performance overhead for audit field population
- More complex data model

**Neutral:**
- Required for enterprise applications in regulated industries

---

## ADR-010: News Content Filtering Pipeline

**Status**: ✅ Accepted  
**Date**: 2024-06-10  
**Deciders**: Content Team, Business Team

### Context
Korean real estate news aggregation requires sophisticated filtering to provide high-quality, relevant content while avoiding spam and promotional material.

### Decision
Implement multi-stage content filtering pipeline:
- **Source Validation**: 30+ trusted Korean news domains
- **Keyword Relevance**: 20+ real estate keywords with scoring
- **Anti-Spam Filtering**: Strong/weak advertising keyword detection
- **Content Quality**: Minimum length and format requirements
- **Automated Categorization**: Policy, subscription, market news
- **Daily Summarization**: Structured markdown reports

### Rationale
- **Content Quality**: Provide high-value information to users
- **User Trust**: Avoid spam and promotional content
- **Korean Market**: Understand specific Korean real estate terminology
- **Automation**: Scale content processing without manual review
- **Business Value**: Categorized news provides better user experience

### Consequences
**Positive:**
- High-quality, relevant news content
- Automated content curation saves manual effort
- Improved user engagement with categorized content
- Spam and advertising effectively filtered

**Negative:**
- Complex filtering logic requires maintenance
- May occasionally filter legitimate content
- Keyword lists need periodic updates

**Neutral:**
- Essential for automated content aggregation platforms

---

## 🔄 Decision Review Process

### Review Schedule
- **Monthly**: Review recent decisions for effectiveness
- **Quarterly**: Evaluate major architectural choices
- **Annually**: Comprehensive architecture review

### Evaluation Criteria
- **Performance Impact**: How decisions affect system performance
- **Maintenance Burden**: Ongoing development and operational costs
- **Business Value**: Alignment with business objectives
- **Technical Debt**: Accumulation of technical complexity
- **Korean Market Fit**: Effectiveness in Korean real estate domain

### Decision Modification Process
1. **Identify Issue**: Document problems with current approach
2. **Analyze Alternatives**: Evaluate alternative solutions
3. **Impact Assessment**: Understand consequences of change
4. **Team Consensus**: Get agreement from relevant stakeholders
5. **Implementation Plan**: Create migration strategy if needed
6. **Update Documentation**: Revise ADR with new decision

## 📚 Related Documentation

- **[CLAUDE.md](../CLAUDE.md)**: Complete project understanding guide
- **[Database Specifications](./database-specs.md)**: Implementation details for database decisions
- **[API Documentation](./api-documentation.md)**: API design reflecting architectural choices
- **[Security Guidelines](./security.md)**: Security implementation details
- **[Korean Business Rules](./korean-business-rules.md)**: Domain knowledge supporting decisions

---

**Architecture Decisions Version**: 1.0  
**Last Updated**: 2024-06-19  
**Next Review**: 2024-07-19