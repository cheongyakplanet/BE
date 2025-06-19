# Contributing Guidelines

## 🤝 Welcome Contributors

Thank you for your interest in contributing to CheonYakPlanet! This guide will help you understand our development process, coding standards, and how to submit quality contributions.

## 📋 Table of Contents

1. [Getting Started](#getting-started)
2. [Development Workflow](#development-workflow)
3. [Coding Standards](#coding-standards)
4. [Testing Requirements](#testing-requirements)
5. [Code Review Process](#code-review-process)
6. [Commit Message Guidelines](#commit-message-guidelines)
7. [Pull Request Guidelines](#pull-request-guidelines)
8. [Korean Business Domain Guidelines](#korean-business-domain-guidelines)

## 🚀 Getting Started

### Prerequisites
- Java 17+
- MySQL 8.0+
- Git 2.20+
- IDE (IntelliJ IDEA recommended)
- Basic understanding of Korean real estate domain

### Initial Setup
1. **Fork the Repository**
   ```bash
   # Fork on GitHub, then clone your fork
   git clone https://github.com/your-username/cheonyakplanet-be.git
   cd cheonyakplanet-be
   ```

2. **Set Up Development Environment**
   ```bash
   # Add upstream remote
   git remote add upstream https://github.com/original-org/cheonyakplanet-be.git
   
   # Install dependencies and run tests
   ./gradlew build
   ./gradlew test
   ```

3. **Configure Environment**
   - Copy `.env.example` to `.env`
   - Configure database connection
   - Set up external API keys (see [Deployment Guide](./deployment-guide.md))

4. **Read Documentation**
   - [Architecture Decisions](./architecture-decisions.md)
   - [Korean Business Rules](./korean-business-rules.md)
   - [Database Specifications](./database-specs.md)

## 🔄 Development Workflow

### Branch Strategy
We follow **Git Flow** with Korean feature naming:

```bash
# Feature branches (Korean names encouraged)
feature/청약-알림-시스템
feature/subscription-alert-system

# Bugfix branches
bugfix/fix-duplicate-subscription-error

# Hotfix branches
hotfix/security-jwt-vulnerability

# Release branches
release/v1.1.0
```

### Development Process
1. **Create Feature Branch**
   ```bash
   git checkout develop
   git pull upstream develop
   git checkout -b feature/your-feature-name
   ```

2. **Develop with TDD**
   - Write failing tests first
   - Implement minimum code to pass tests
   - Refactor while keeping tests green

3. **Follow Clean Architecture**
   - Domain entities remain framework-agnostic
   - Business logic in Application layer
   - External concerns in Infrastructure layer

4. **Commit Frequently**
   - Small, logical commits
   - Clear commit messages
   - Test before each commit

5. **Push and Create PR**
   ```bash
   git push origin feature/your-feature-name
   # Create Pull Request on GitHub
   ```

## 📝 Coding Standards

### Java Code Style

#### Class Structure
```java
@Entity
@Table(catalog = "planet", name = "example_entity")
@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})
public class ExampleEntity extends Stamped {
    
    // Static fields first
    private static final String CONSTANT_VALUE = "value";
    
    // Instance fields
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // Constructors
    public ExampleEntity() {}
    
    public ExampleEntity(String name) {
        this.name = name;
    }
    
    // Public methods
    public void performBusinessOperation() {
        validateBusinessRules();
        // Business logic here
    }
    
    // Private methods
    private void validateBusinessRules() {
        if (name == null || name.trim().isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "Name is required");
        }
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
```

#### Service Layer Standards
```java
@Service
@Transactional(readOnly = true)  // Default to read-only
@Slf4j
public class ExampleService {
    
    private final ExampleRepository repository;
    
    public ExampleService(ExampleRepository repository) {
        this.repository = repository;
    }
    
    @Transactional  // Override for write operations
    public ExampleDTO createExample(CreateExampleDTO dto, UserDetailsImpl userDetails) {
        // 1. Validation
        validateAuthentication(userDetails);
        validateInput(dto);
        
        // 2. Business logic
        ExampleEntity entity = ExampleEntity.builder()
            .name(dto.getName())
            .build();
        
        // 3. Persistence
        ExampleEntity saved = repository.save(entity);
        
        // 4. Return DTO
        return ExampleDTO.fromEntity(saved);
    }
    
    private void validateAuthentication(UserDetailsImpl userDetails) {
        if (userDetails == null) {
            throw new CustomException(ErrorCode.AUTH001, "인증이 필요한 서비스입니다");
        }
    }
    
    private void validateInput(CreateExampleDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.VALIDATION_ERROR, "이름은 필수입니다");
        }
    }
}
```

#### Controller Standards
```java
@RestController
@RequestMapping("/api/examples")
@Tag(name = "Example API", description = "예제 관리 API")
public class ExampleController extends BaseController {
    
    private final ExampleService service;
    
    public ExampleController(ExampleService service) {
        this.service = service;
    }
    
    @PostMapping
    @Operation(summary = "예제 생성", description = "새로운 예제를 생성합니다")
    public ResponseEntity<?> createExample(
            @RequestBody @Valid CreateExampleDTO dto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ExampleDTO result = service.createExample(dto, userDetails);
        return ResponseEntity.ok(success(result));
    }
}
```

### Naming Conventions

#### Korean Business Terms
Use Korean terms for business concepts:
```java
// Good - Korean business terms
private String houseNm;           // 주택명
private LocalDate rceptBgnde;     // 접수시작일
private String spsplyType;        // 특별공급유형

// Avoid - Direct English translation
private String houseName;
private LocalDate receptionStartDate;
private String specialSupplyType;
```

#### General Naming
- **Classes**: PascalCase (`UserService`, `SubscriptionInfo`)
- **Methods**: camelCase (`createUser`, `validateSubscription`)
- **Variables**: camelCase (`userName`, `subscriptionCount`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_RETRY_ATTEMPTS`)
- **Database Tables**: snake_case (`user_info`, `subscription_like`)

### Error Handling Standards

#### Custom Exceptions
```java
// Always use specific error codes
throw new CustomException(ErrorCode.SUB001, "청약 정보를 찾을 수 없습니다");

// Include additional context when helpful
throw new CustomException(ErrorCode.AUTH002, "권한이 없습니다", 
    "Required role: ADMIN, Current role: " + userRole);
```

#### Error Codes
Follow the established pattern:
- **AUTH001-010**: Authentication and authorization
- **SIGN001-010**: User registration and login
- **POST001-010**: Community posts and comments
- **SUB001-010**: Subscription-related errors

## 🧪 Testing Requirements

### Test Coverage Standards
- **Overall Coverage**: 70% minimum
- **Service Layer**: 80% line coverage, 75% branch coverage
- **Critical Business Logic**: 90%+ coverage
- **Error Paths**: All custom exception scenarios tested

### Test Structure (Given-When-Then)
```java
@ExtendWith(MockitoExtension.class)
@DisplayName("구독 서비스 테스트")
class SubscriptionServiceTest {
    
    @Mock
    private SubscriptionRepository repository;
    
    @InjectMocks
    private SubscriptionService service;
    
    @Test
    @DisplayName("구독 생성 - 성공: 유효한 입력값")
    void givenValidInput_whenCreateSubscription_thenReturnSubscriptionDTO() {
        // Given
        CreateSubscriptionDTO input = CreateSubscriptionDTO.builder()
            .houseName("래미안 강남포레스트")
            .region("서울특별시")
            .city("강남구")
            .build();
        
        SubscriptionInfo entity = createTestSubscription();
        given(repository.save(any(SubscriptionInfo.class))).willReturn(entity);
        
        // When
        SubscriptionDTO result = service.createSubscription(input);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getHouseName()).isEqualTo("래미안 강남포레스트");
        then(repository).should().save(any(SubscriptionInfo.class));
    }
    
    @Test
    @DisplayName("구독 생성 - 실패: 중복된 주택관리번호")
    void givenDuplicateHouseManageNo_whenCreateSubscription_thenThrowException() {
        // Given
        CreateSubscriptionDTO input = createDuplicateInput();
        given(repository.existsByHouseManageNo(anyString())).willReturn(true);
        
        // When & Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            service.createSubscription(input);
        });
        
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SUB003);
        then(repository).should(never()).save(any());
    }
    
    private SubscriptionInfo createTestSubscription() {
        return SubscriptionInfo.builder()
            .houseNm("래미안 강남포레스트")
            .houseManageNo("2024000001")
            .region("서울특별시")
            .city("강남구")
            .build();
    }
}
```

### Korean Domain Test Data
Use realistic Korean data in tests:
```java
private User createTestUser() {
    return User.builder()
        .email("test@cheonyakplanet.com")
        .username("청약초보")
        .role(UserRoleEnum.USER)
        .monthlyIncome(500)        // 500만원
        .isMarried(true)           // 기혼
        .numChild(2)               // 자녀 2명
        .hasHouse(false)           // 무주택
        .interestLocal1("서울특별시 강남구")
        .interestLocal2("경기도 성남시")
        .build();
}
```

## 👀 Code Review Process

### Before Submitting PR
- [ ] All tests pass (`./gradlew test`)
- [ ] Code coverage meets requirements (`./gradlew jacocoTestReport`)
- [ ] No obvious security vulnerabilities
- [ ] Korean business logic is accurate
- [ ] Documentation updated if needed

### Review Checklist

#### Architecture & Design
- [ ] Follows Clean Architecture principles
- [ ] Proper layer separation maintained
- [ ] Business logic isolated from framework concerns
- [ ] Appropriate design patterns used

#### Code Quality
- [ ] Code is readable and well-structured
- [ ] Naming conventions followed
- [ ] No code duplication
- [ ] Appropriate error handling
- [ ] Performance considerations addressed

#### Korean Domain Accuracy
- [ ] Korean business rules correctly implemented
- [ ] Appropriate Korean terminology used
- [ ] Government API integration follows standards
- [ ] User experience appropriate for Korean market

#### Testing
- [ ] Adequate test coverage
- [ ] Tests follow Given-When-Then pattern
- [ ] Korean domain test data used
- [ ] Edge cases and error scenarios covered

#### Security
- [ ] No sensitive data exposed
- [ ] Authentication properly implemented
- [ ] Input validation performed
- [ ] SQL injection prevention

### Review Response Guidelines

#### For Reviewers
- **Be Constructive**: Suggest improvements, don't just point out problems
- **Explain Why**: Help contributors understand the reasoning
- **Consider Korean Context**: Understand Korean business requirements
- **Be Timely**: Review within 24-48 hours
- **Approve When Ready**: Don't nitpick minor style issues

#### For Contributors
- **Respond Promptly**: Address feedback within 48 hours
- **Ask Questions**: Clarify when feedback is unclear
- **Make Requested Changes**: Don't argue unless you have strong technical reasons
- **Test Changes**: Ensure fixes don't break other functionality

## 📝 Commit Message Guidelines

### Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

### Types
- **feat**: New feature
- **fix**: Bug fix
- **refactor**: Code refactoring
- **test**: Adding or updating tests
- **docs**: Documentation updates
- **style**: Code style changes
- **perf**: Performance improvements
- **chore**: Maintenance tasks

### Examples
```bash
feat(subscription): add real-time subscription alerts

- Implement WebSocket-based notification system
- Add subscription preference settings
- Include email fallback for offline users

Closes #123

fix(auth): resolve JWT token refresh issue

- Fix token expiry calculation bug
- Add proper error handling for expired tokens
- Update token refresh endpoint tests

test(community): add comprehensive post creation tests

- Test Korean character handling in posts
- Add validation error scenarios
- Include pagination edge cases

refactor(news): improve content filtering pipeline

- Extract filtering logic into separate service
- Add configurable filtering rules
- Improve performance with parallel processing
```

## 🔍 Pull Request Guidelines

### PR Title Format
```
[Type] Brief description of changes

Examples:
[Feature] Add subscription price comparison tool
[Fix] Resolve Korean address parsing issue
[Refactor] Improve news content filtering performance
```

### PR Description Template
```markdown
## 📋 Summary
Brief description of what this PR does.

## 🎯 Type of Change
- [ ] Bug fix (non-breaking change which fixes an issue)
- [ ] New feature (non-breaking change which adds functionality)
- [ ] Breaking change (fix or feature that would cause existing functionality to not work as expected)
- [ ] Documentation update

## 🔧 Changes Made
- Change 1
- Change 2
- Change 3

## 🧪 Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed
- [ ] Korean business logic validated

## 📚 Documentation
- [ ] Code comments updated
- [ ] API documentation updated
- [ ] User documentation updated (if applicable)

## 🚀 Deployment Notes
Any special deployment considerations.

## 📱 Screenshots (if applicable)
Screenshots of UI changes.

## ✅ Checklist
- [ ] Code follows project coding standards
- [ ] Self-review completed
- [ ] Tests pass locally
- [ ] Korean business requirements met
- [ ] Breaking changes documented
```

### PR Size Guidelines
- **Small PRs Preferred**: <300 lines of code changes
- **Medium PRs**: 300-800 lines (needs good description)
- **Large PRs**: >800 lines (should be split if possible)

## 🏢 Korean Business Domain Guidelines

### Understanding Korean Real Estate
- **Study Documentation**: Read [Korean Business Rules](./korean-business-rules.md)
- **Government APIs**: Understand LH Corporation and Ministry of Land data structures
- **User Perspective**: Consider Korean user expectations and workflows
- **Regulatory Compliance**: Ensure features meet Korean legal requirements

### Korean Text Handling
```java
// Good - Proper Korean text validation
private void validateKoreanText(String text) {
    if (text == null || text.trim().isEmpty()) {
        throw new CustomException(ErrorCode.VALIDATION001, "입력값이 필요합니다");
    }
    
    // Check for Korean characters if needed
    if (!text.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣].*")) {
        throw new CustomException(ErrorCode.VALIDATION002, "한글 입력이 필요합니다");
    }
}

// Database columns for Korean text
@Column(columnDefinition = "TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
private String koreanContent;
```

### Korean Date and Number Formats
```java
// Date formatting for Korean users
DateTimeFormatter koreanDateFormat = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

// Number formatting for Korean currency
DecimalFormat koreanCurrency = new DecimalFormat("#,###원");
```

## 🆘 Getting Help

### Communication Channels
- **GitHub Issues**: For bug reports and feature requests
- **GitHub Discussions**: For questions and general discussion
- **Code Reviews**: For implementation guidance
- **Documentation**: Check existing docs before asking

### When to Ask for Help
- Korean business logic clarification needed
- Architecture decisions unclear
- External API integration issues
- Testing strategy questions
- Performance concerns

## 📈 Contribution Recognition

### Types of Contributions
- **Code**: Features, bug fixes, performance improvements
- **Documentation**: Guides, API docs, Korean business explanations
- **Testing**: Test improvements, edge case discovery
- **Reviews**: Code review feedback and mentoring
- **Korean Domain**: Business rule clarification and validation

### Recognition
- Contributors listed in release notes
- Special recognition for Korean domain expertise
- Mentoring opportunities for significant contributors

---

## 📚 Additional Resources

- **[CLAUDE.md](../CLAUDE.md)**: Complete project understanding
- **[Architecture Decisions](./architecture-decisions.md)**: System design rationale
- **[API Documentation](./api-documentation.md)**: REST API reference
- **[Testing Guide](./testing-guide.md)**: Detailed testing strategies
- **[Korean Business Rules](./korean-business-rules.md)**: Domain knowledge

---

**Contributing Guidelines Version**: 1.0  
**Last Updated**: 2024-06-19  
**Next Review**: 2024-09-19

Thank you for contributing to CheonYakPlanet! 🏠🇰🇷