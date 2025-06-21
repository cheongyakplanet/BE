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
./gradlew jacocoTestCoverageVerification  # Verify coverage thresholds
```

### Database
- Uses MySQL database with catalog separation (`catalog = "planet"`)
- JPA/Hibernate for ORM with comprehensive soft delete support
- Database migrations handled through JPA schema updates
- All entities extend `Stamped` base class for automatic audit trails

## Architecture Overview

### Domain-Driven Design Structure
The application follows Clean Architecture principles with distinct layers:

**Domain Layer** (`src/main/java/org/cheonyakplanet/be/domain/`):
- **User**: Authentication, interest locations (up to 5 regions), financial profiles (property, income, marriage status, children, house ownership)
- **Subscription**: Korean government housing subscription data with complex eligibility rules, price info, special/general supply targets
- **Community**: Posts, comments, replies with hierarchical structure and reaction system
- **Finance**: Korean bank house loans and specialized housing mortgage products
- **Infrastructure**: Korean administrative regions (시군구), stations, schools, public facilities with distance calculations
- **Chat**: Real-time AI messaging with Gemini 2.0, daily usage limits (15 messages), session management

**Application Layer** (`src/main/java/org/cheonyakplanet/be/application/`):
- Services handle business logic with transaction management
- DTOs for data transformation between layers
- Clean separation of read/write operations

**Infrastructure Layer** (`src/main/java/org/cheonyakplanet/be/infrastructure/`):
- JWT-based authentication with database token storage and blacklisting
- WebSocket for real-time chat with JWT handshake authentication
- External API integrations:
  - **Government APIs**: 한국토지주택공사(LH), 국토교통부 실거래가 API
  - **Kakao APIs**: Address geocoding and coordinate mapping
  - **Naver News API**: Real estate news aggregation
  - **Gemini AI**: Conversational assistance
- Scheduled tasks for automated data synchronization (daily/weekly/monthly)

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
- Complex Korean address parsing with administrative district codes (시군구 코드)
- Geographic distance calculations for infrastructure proximity
- Automated data synchronization:
  - Daily: Government subscription data updates (1:00 AM KST)
  - Monthly: Real estate transaction prices (Last day 2:00 AM KST)
  - Daily: News aggregation and summarization (12:30 AM KST)

**Korean Government API Integration**:
- **LH Corporation API** (`sub.apt.api.url`): Public housing subscription announcements
- **Ministry of Land API** (`realestate.api.url`): Real estate transaction data
- **Administrative District Codes**: Integration with Korean 시군구 hierarchy
- **Incremental Updates**: Prevents duplicate data processing

## Entity Architecture Deep Dive

### Base Entity Pattern - Stamped Class
**ALL entities extend Stamped** for comprehensive audit trails:
```java
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})
public class Stamped {
    @CreatedDate
    private LocalDateTime createdAt;
    
    @CreatedBy  
    private String createdBy;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @LastModifiedBy
    private String updatedBy;
    
    private LocalDateTime deletedAt;    // Soft delete support
    private String deletedBy;           // Audit trail for deletions
}
```

### Key Entity Relationships

**User Entity** - Central profile with financial data:
```java
@Entity
@Table(catalog = "planet", name = "user_info")
public class User extends Stamped {
    // Authentication
    private String email;           // Unique identifier
    private UserRoleEnum role;      // USER/ADMIN
    private UserStatusEnum status;  // ACTIVE/WITHDRAWN
    
    // Financial profile for subscription eligibility
    private Double property;        // 부동산 자산
    private Integer income;         // 월소득
    private Boolean isMarried;      // 결혼 여부
    private Integer numChild;       // 자녀 수
    private Integer numHouse;       // 주택 보유 수
    
    // Interest locations (up to 5)
    private String interestLocal1;
    private String interestLocal2;
    private String interestLocal3;
    private String interestLocal4;
    private String interestLocal5;
}
```

**SubscriptionInfo Entity** - Korean housing subscription data:
```java
@Entity
@Table(catalog = "planet", name = "subscription_info")
public class SubscriptionInfo extends Stamped {
    private String houseNm;              // 주택명
    private String houseManageNo;        // 주택관리번호 (Government ID)
    private String pblancNo;             // 공고번호
    
    // Korean address hierarchy
    private String region;               // 시/도
    private String city;                 // 시/군/구
    private String district;             // 읍/면/동
    
    // Subscription periods
    private LocalDate rceptBgnde;        // 청약 접수 시작일
    private LocalDate rceptEndde;        // 청약 접수 종료일
    private LocalDate spsplyRceptBgnde;  // 특별공급 접수 시작일
    private LocalDate spsplyRceptEndde;  // 특별공급 접수 종료일
    
    // Ranking system (1순위, 2순위)
    private LocalDate gnrlRnk1CrspareaRcptde;  // 1순위 해당지역
    private LocalDate gnrlRnk1EtcAreaRcptde;   // 1순위 기타지역
    
    // Related entities with proper JSON handling
    @OneToMany(mappedBy = "subscriptionInfo", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SubscriptionPriceInfo> subscriptionPriceInfo;
    
    @OneToMany(mappedBy = "subscriptionInfo", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SubscriptionSpecialSupplyTarget> subscriptionSpecialSupplyTarget;
}
```

**Community Hierarchy** - Posts → Comments → Replies:
```java
@Entity
@Table(catalog = "planet", name = "Post")
public class Post extends Stamped {
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private Long views;
    private int likes;
    @Enumerated(EnumType.STRING)
    private PostCategory category;  // REVIEW, SUBSCRIPTION_INFO, etc.
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();
    
    public void countViews() {
        this.views = (this.views == null ? 1L : this.views + 1);
    }
}
```

## Service Layer Architecture

### Transaction Management Patterns
```java
@Transactional(readOnly = true)     // For read operations
@Transactional                      // For write operations
@Transactional(propagation = Propagation.REQUIRES_NEW)  // For async operations
```

### Service Responsibilities

**UserService** - Authentication & Profile Management:
- JWT token generation/validation
- Interest location management (up to 5 regions)
- Financial profile for subscription eligibility
- Password reset with email verification

**CommunityService** - Community Platform:
- Post/Comment/Reply hierarchy management
- Reaction system (Like/Dislike)
- View tracking and pagination
- Soft delete with audit trails

**SubscriptionService** - Government Data Integration:
- LH Corporation API integration
- Incremental data updates
- Address parsing and geocoding
- **Separate SubscriptionQueryService** for read operations (CQRS pattern)

**NewsService** - AI-Powered News Aggregation:
- Naver News API integration with 20+ keywords
- Advanced content filtering (anti-spam, domain validation)
- Automated categorization (정책, 청약/분양, 기타)
- Daily summary generation

**FinanceService** - Korean Financial Products:
- FSS (Financial Supervisory Service) API integration
- Mortgage vs Rental house loan separation
- Rate comparison and filtering

**InfrastructureService** - Location Intelligence:
- Haversine distance calculations
- Infrastructure proximity (stations, schools, facilities)
- Geographic bounding box optimization

### External API Integration Patterns

**Government APIs**:
```java
// LH Corporation - Housing subscription data
String requestUrl = subAptApiUrl + "?serviceKey=" + apiKey;

// Ministry of Land - Real estate prices  
String requestUrl = realestateApiUrl + "?serviceKey=" + apiKey;

// Incremental processing
Set<String> existingIds = repository.findAllHouseManageNos();
if (existingIds.contains(newId)) continue;
```

**Kakao Geocoding**:
```java
Mono<String> responseMono = webClient.get()
    .uri(uriBuilder -> uriBuilder
        .path("/v2/local/search/address.json")
        .queryParam("query", address)
        .build())
    .retrieve()
    .bodyToMono(String.class);
```

**Async Processing**:
```java
List<CompletableFuture<Void>> futures = new ArrayList<>();
for (SggCode code : codes) {
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        processCode(code);
    }, taskExecutor);
    futures.add(future);
}
```

## Controller Layer Patterns

### Base Controller Structure
```java
@RestController
@RequestMapping("/api/endpoint")
public class ExampleController extends BaseController {
    
    @PostMapping("/create")
    public ResponseEntity<?> create(
        @RequestBody RequestDTO requestDTO,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ResponseDTO result = service.create(requestDTO, userDetails);
        return ResponseEntity.ok(success(result));
    }
}
```

### Authentication Patterns
```java
// Protected endpoints
@AuthenticationPrincipal UserDetailsImpl userDetails

// Admin-only endpoints  
@PreAuthorize("hasRole('ADMIN')")

// Public endpoints (no authentication required)
// No annotation needed
```

### Response Patterns
```java
// Success response
return ResponseEntity.ok(success(data));

// Error response (handled by GlobalExceptionHandler)
throw new CustomException(ErrorCode.USER001, "사용자를 찾을 수 없습니다");
```

## Configuration Architecture

### Security Configuration (WebSecurityConfig)
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    // JWT-based stateless authentication
    // Custom authentication/authorization filters
    // CORS configuration
    // Public endpoint definitions
}
```

### WebSocket Configuration
```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatHandler, "/ws/chat")
            .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))
            .setAllowedOrigins("*");
    }
}
```

### Async Configuration
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("RealEstate-Exec-");
        return executor;
    }
}
```

## JWT Security Implementation

### Token Structure
```java
// Access Token: 60 minutes
// Refresh Token: 24 hours
// Algorithm: HS256
// Database storage in UserToken entity
```

### Authentication Flow
```java
public Object login(LoginRequestDTO requestDTO) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password));
    
    String accessToken = jwtUtil.createAccessToken(email, role);
    String refreshToken = jwtUtil.createRefreshToken(email, role);
    jwtUtil.storeTokens(email, accessToken, refreshToken);
    
    return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
}
```

### Security Filters
- **JwtAuthenticationFilter**: Login processing
- **JwtAuthorizationFilter**: Token validation
- **JwtHandshakeInterceptor**: WebSocket authentication
- **JwtExceptionFilter**: JWT error handling

## Korean Real Estate Domain Patterns

### Address Parsing
```java
private String[] parseAddress(String address) {
    Pattern pattern = Pattern.compile(
        "^(\\S+시|\\S+도|\\S+특별자치시)\\s?" +     // 시도
        "(\\S+구|\\S+군|\\S+시)?\\s?" +              // 시군구
        "((?:\\S+동(?:\\d*가)?|\\S+읍|\\S+면)\\s?)?" + // 읍면동
        "(.+)?$");                                    // 상세주소
    // Returns [region, city, district, detail]
}
```

### Financial Eligibility Matching
```java
// User financial profile matching against subscription requirements
// Income verification for loan products
// Property ownership impact on subscription priority
// Marriage status and children count for special supply
```

### Distance Calculations
```java
private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    // Haversine formula for precise geographic distance
    // Used for infrastructure proximity analysis
    // 1km radius searches with bounding box optimization
}
```

## DTO and Data Transformation Patterns

### Standard DTO Structure
```java
@Builder
public class ExampleDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
    
    public static ExampleDTO fromEntity(ExampleEntity entity) {
        return ExampleDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .createdAt(entity.getCreatedAt())
            .build();
    }
}
```

### API Response Wrapper
```java
public class ApiResponse<T> {
    private String status;  // "success" or "fail"
    private T data;         // Response data or error details
}
```

## Exception Handling Architecture

### Error Code System
```java
public enum ErrorCode {
    // Authentication & Authorization
    AUTH001("인증이 필요한 서비스입니다"),
    AUTH002("권한이 없습니다"),
    AUTH003("유효하지 않은 JWT 서명"),
    
    // User Management
    SIGN001("이미 존재하는 회원입니다"),
    SIGN002("존재하지 않는 회원입니다"),
    SIGN003("비밀번호가 일치하지 않습니다"),
    
    // Community
    POST001("게시글을 찾을 수 없습니다"),
    POST002("게시글 작성 권한이 없습니다"),
    COMMENT001("댓글을 찾을 수 없습니다"),
    
    // Subscription
    SUB001("청약 정보를 찾을 수 없습니다"),
    SUB002("청약 기간이 아닙니다");
}
```

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException e) {
        ErrorData errorData = new ErrorData(e.getErrorCode().getCode(), 
                                          e.getMessage(), e.getDetails());
        return ResponseEntity.ok(new ApiResponse<>("fail", errorData));
    }
}
```

## Testing Architecture

### Testing Framework Stack
- **JUnit 5** with Spring Boot Test
- **Mockito** with BDDMockito for behavior-driven testing
- **AssertJ** for fluent assertions
- **Spring Security Test** for authentication testing

### Essential Testing Imports
**Service Layer Tests:**
```java
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("서비스명 테스트")
class ServiceTest {
    @Mock private Repository repository;
    @InjectMocks private Service service;
}
```

**Controller Tests:**
```java
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockBean private Service service;
}
```

### Test File Organization
```
src/test/java/org/cheonyakplanet/be/
├── BeApplicationTests.java                    # Context loading
├── application/service/                       # Service layer tests
│   ├── CommunityServiceTest.java
│   ├── UserServiceTest.java
│   ├── NewsServiceTest.java
│   └── FinanceServiceTest.java
├── infrastructure/scheduler/                  # Infrastructure tests
│   └── SchedulerTest.java
└── presentation/                             # Controller tests
    ├── controller/
    │   ├── UserControllerAPITest.java
    │   ├── CommunityControllerAPITest.java
    │   └── InfoControllerAPITest.java
    └── exception/
        └── GlobalExceptionHandlerTest.java
```

### Korean Domain Test Data Patterns
```java
// Korean real estate test data
private User createTestUser() {
    return User.builder()
        .email("test@cheonyakplanet.com")
        .username("청약초보")
        .role(UserRoleEnum.USER)
        .status(UserStatusEnum.ACTIVE)
        .monthlyIncome(500)           // 500만원
        .isMarried(true)              // 기혼
        .numChild(2)                  // 자녀 2명
        .hasHouse(false)              // 무주택
        .build();
}

private SubscriptionInfo createTestSubscription() {
    return SubscriptionInfo.builder()
        .houseNm("래미안 강남포레스트")
        .houseManageNo("2024000001")
        .pblancNo("2024강남01")
        .region("서울특별시")
        .city("강남구")
        .district("대치동")
        .rceptBgnde(LocalDate.parse("2024-07-01"))
        .rceptEndde(LocalDate.parse("2024-07-03"))
        .build();
}

private Post createTestPost() {
    return Post.builder()
        .title("강남구 청약 질문")
        .content("강남구 신규 분양 아파트 청약 자격 조건이 궁금합니다.")
        .category(PostCategory.SUBSCRIPTION_INQUIRY)
        .username("청약초보")
        .views(100L)
        .likes(5)
        .build();
}
```

### Test Method Structure (Given-When-Then)
```java
@Test
@DisplayName("게시글 생성 - 성공")
void givenValidPostData_whenCreatePost_thenReturnPostDTO() {
    // Given
    PostCreateDTO postCreateDTO = PostCreateDTO.builder()
        .title("강남구 청약 질문")
        .content("청약 자격 조건이 궁금합니다")
        .category(PostCategory.SUBSCRIPTION_INQUIRY)
        .build();
    
    Post savedPost = createTestPost();
    given(postRepository.save(any(Post.class))).willReturn(savedPost);
    
    // When
    PostDTO result = communityService.createPost(postCreateDTO, userDetails);
    
    // Then
    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("강남구 청약 질문");
    assertThat(result.getCategory()).isEqualTo(PostCategory.SUBSCRIPTION_INQUIRY);
    then(postRepository).should().save(any(Post.class));
}
```

### Authentication Testing Patterns
```java
@BeforeEach
void setUp() {
    User user = new User("test@cheonyakplanet.com", "password", UserRoleEnum.USER, "청약초보");
    userDetails = new UserDetailsImpl(user);
    
    // SecurityContext 설정
    UsernamePasswordAuthenticationToken authentication = 
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
    securityContext.setAuthentication(authentication);
    SecurityContextHolder.setContext(securityContext);
}

@Test
@DisplayName("인증된 사용자 API 테스트")
void authenticatedUserTest() throws Exception {
    mockMvc.perform(post("/api/community/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDTO))
            .with(user(userDetails))
            .with(csrf()))
        .andExpected(status().isOk())
        .andExpected(jsonPath("$.status").value("success"));
}
```

### Test Coverage Requirements
- **Overall Coverage**: 70% minimum
- **Service Layer**: 80% line coverage, 75% branch coverage  
- **Critical Business Logic**: 90%+ coverage
- **Error Handling**: All custom exception paths tested

## Scheduling and Background Tasks

### Automated Data Processing
```java
@Component
public class Scheduler {
    @Scheduled(cron = "0 0 1 ? * *", zone = "Asia/Seoul")  // Daily 1:00 AM
    public void dailySubscriptionUpdate() {
        subscriptionService.updateSubAPT();
    }
    
    @Scheduled(cron = "0 0 2 L * ?", zone = "Asia/Seoul")  // Monthly last day 2:00 AM
    public void monthlyRealPriceUpdate() {
        financeService.collectRealPrice(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")));
    }
    
    @Scheduled(cron = "0 30 0 * * ?", zone = "Asia/Seoul") // Daily 12:30 AM
    public void dailyNewsUpdate() {
        newsService.crawlDailyNews();
    }
}
```

## Development Patterns & Best Practices

### Entity Design Guidelines
```java
@Entity
@Table(catalog = "planet", name = "table_name")
@EntityListeners({AuditingEntityListener.class, SoftDeleteListener.class})
public class ExampleEntity extends Stamped {
    // Use Lombok @Builder for construction
    // Use @JsonManagedReference/@JsonBackReference for relationships
    // Use proper cascade operations
    // Include business methods when appropriate
}
```

### Service Layer Guidelines
```java
@Service
@Transactional(readOnly = true)  // Default to read-only
public class ExampleService {
    
    @Transactional               // Override for write operations
    public void writeOperation() {
        // Business logic
        // Exception handling with custom error codes
        // DTO transformation
    }
}
```

### Repository Naming Conventions
```java
public interface ExampleRepository extends JpaRepository<ExampleEntity, Long> {
    // Use descriptive method names
    Optional<ExampleEntity> findByFieldAndDeletedAtIsNull(String field);
    
    // Use @Query for complex operations
    @Query("SELECT e FROM ExampleEntity e WHERE e.field = :value AND e.deletedAt IS NULL")
    List<ExampleEntity> findActiveByField(@Param("value") String value);
}
```

## Common Patterns to Follow

### Error Handling Pattern
```java
// Always use custom exceptions with specific error codes
if (condition) {
    throw new CustomException(ErrorCode.SPECIFIC_ERROR, "구체적인 오류 메시지");
}
```

### Korean Text Handling
```java
// Use TEXT columns for Korean content
@Column(columnDefinition = "TEXT")
private String content;

// Use proper Korean field names in DTOs
private String marriageStatus;  // "기혼", "미혼"
private Integer numberOfChildren; // 자녀 수
```

### Authentication Requirement
```java
// Always validate authentication for protected operations
private void validateAuthentication(UserDetailsImpl userDetails) {
    if (userDetails == null) {
        throw new CustomException(ErrorCode.AUTH001, "인증이 필요한 서비스입니다");
    }
}
```

## Key File Locations

- **Main Application**: `src/main/java/org/cheonyakplanet/be/BeApplication.java`
- **Configuration**: `src/main/java/org/cheonyakplanet/be/infrastructure/config/`
- **Entities**: `src/main/java/org/cheonyakplanet/be/domain/entity/`
- **Services**: `src/main/java/org/cheonyakplanet/be/application/service/`
- **Controllers**: `src/main/java/org/cheonyakplanet/be/presentation/controller/`
- **DTOs**: `src/main/java/org/cheonyakplanet/be/application/dto/`
- **Security**: `src/main/java/org/cheonyakplanet/be/infrastructure/jwt/`
- **Exception Handling**: `src/main/java/org/cheonyakplanet/be/presentation/exception/`
- **External Scripts**: `src/scripts/additional_info.py`
- **Test Resources**: `src/test/java/org/cheonyakplanet/be/`

## Git Conventions

Follow the established commit message format:
- `feat`: New features
- `fix`: Bug fixes  
- `refactor`: Code refactoring
- `test`: Test-related changes
- `build`: Build system changes
- `docs`: Documentation updates
- `chore`: Maintenance tasks

## Repository Cleanup Guidelines

### Files and Directories to NEVER Commit
**Build Artifacts:**
- `build/` - Gradle build output
- `out/` - IDE build output  
- `htmlReport/` - JaCoCo test coverage reports
- `*.exec` - JaCoCo execution data files

**Environment-Specific Files:**
- `venv/`, `env/`, `.venv/` - Python virtual environments
- `*.log`, `logs/` - Log files
- `app.log` - Application log files

**Temporary Files:**
- `*.tmp`, `*.temp`, `*~` - Temporary files
- `test.txt` - Test files
- `.DS_Store`, `Thumbs.db` - OS-generated files

## Monitoring and Observability

- **Spring Boot Actuator**: Health checks and metrics endpoints
- **Prometheus Integration**: Performance monitoring
- **Comprehensive Logging**: Logback with structured logging
- **Error Tracking**: Complete audit trails for debugging

---

This CLAUDE.md provides comprehensive guidance for understanding and working with the CheonYakPlanet Korean real estate platform. The architecture demonstrates enterprise-level patterns with sophisticated Korean business domain modeling, comprehensive security, real-time features, and robust testing strategies.