# Test Coverage Guide

이 문서는 JaCoCo를 사용한 테스트 커버리지 분석 가이드입니다.

## JaCoCo 설정

프로젝트에 JaCoCo가 이미 설정되어 있으며, 다음과 같은 기능을 제공합니다:

### 커버리지 리포트 생성
```bash
./gradlew test jacocoTestReport
```

### 커버리지 검증 (최소 기준 확인)
```bash
./gradlew jacocoTestCoverageVerification
```

### 전체 검증 (테스트 + 커버리지)
```bash
./gradlew check
```

## 커버리지 기준

### 전체 프로젝트
- **최소 커버리지**: 70%

### 서비스 클래스
- **라인 커버리지**: 80%
- **브랜치 커버리지**: 75%

## 제외 대상

다음 클래스/패키지는 커버리지 계산에서 제외됩니다:

### 설정 클래스
- `**/config/**` - Spring 설정 클래스들
- `BeApplication.class` - 메인 애플리케이션 클래스

### 데이터 클래스
- `**/dto/**` - 데이터 전송 객체
- `**/entity/**` - JPA 엔티티 클래스

### 예외 처리
- `**/exception/**` - 커스텀 예외 클래스들

### 상수 및 열거형
- `UserRoleEnum`, `UserStatusEnum`
- `PostCategory`, `ReactionType`
- `ErrorCode`

### 생성된 코드
- `**/*Builder.class` - Lombok 생성 빌더
- `**/*$*.class` - 내부 클래스

## 리포트 확인

### HTML 리포트
```
build/reports/jacoco/test/html/index.html
```
브라우저에서 열어 시각적으로 커버리지를 확인할 수 있습니다.

### XML 리포트
```
build/reports/jacoco/test/jacocoTestReport.xml
```
CI/CD 도구나 SonarQube 등에서 사용할 수 있는 표준 형식입니다.

### CSV 리포트
```
build/reports/jacoco/test/jacocoTestReport.csv
```
스프레드시트나 데이터 분석에 사용할 수 있는 형식입니다.

## Given-When-Then 테스트 패턴

프로젝트의 모든 테스트는 Given-When-Then 패턴을 따릅니다:

```java
@Test
@DisplayName("사용자 생성 성공 테스트")
void givenValidUserData_whenCreateUser_thenReturnCreatedUser() {
    // Given: 테스트 전제조건 설정
    String email = "test@test.com";
    SignupRequestDTO requestDTO = new SignupRequestDTO(email, "password", "username");
    given(userRepository.findByEmail(email)).willReturn(Optional.empty());
    
    // When: 테스트 대상 메서드 실행
    User result = userService.signup(requestDTO);
    
    // Then: 결과 검증
    assertThat(result.getEmail()).isEqualTo(email);
    then(userRepository).should().save(any(User.class));
}
```

## 커버리지 향상 방법

### 1. 서비스 레이어 테스트
- 모든 public 메서드에 대한 테스트 작성
- 성공/실패 시나리오 모두 커버
- 예외 상황 테스트 포함

### 2. 브랜치 커버리지
- if/else 문의 모든 경로 테스트
- switch 문의 모든 case 테스트
- 조건문 조합 테스트

### 3. 경계값 테스트
- null 체크
- 빈 컬렉션 처리
- 유효성 검증 로직

## 예시 테스트 구조

### UserServiceTest
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;
    
    // 성공 시나리오 테스트
    @Test void givenValidData_whenAction_thenSuccess() { }
    
    // 실패 시나리오 테스트  
    @Test void givenInvalidData_whenAction_thenThrowException() { }
    
    // 경계값 테스트
    @Test void givenEdgeCase_whenAction_thenHandleCorrectly() { }
}
```

## 테스트 실행 권장사항

### 개발 중
```bash
# 특정 클래스 테스트
./gradlew test --tests UserServiceTest

# 특정 메서드 테스트
./gradlew test --tests UserServiceTest.givenValidData_whenCreateUser_thenSuccess
```

### 커밋 전
```bash
# 전체 테스트 + 커버리지 검증
./gradlew check
```

### CI/CD 파이프라인
```bash
# 테스트 실행 + 리포트 생성
./gradlew test jacocoTestReport

# 커버리지 기준 검증
./gradlew jacocoTestCoverageVerification
```

## 트러블슈팅

### 커버리지가 기준에 미달하는 경우
1. `build/reports/jacoco/test/html/index.html`에서 상세 리포트 확인
2. 커버되지 않은 라인/브랜치 식별
3. 해당 부분에 대한 테스트 추가

### 테스트 실행 실패
1. Java 17이 설치되어 있는지 확인
2. `JAVA_HOME` 환경변수 설정 확인
3. 의존성 충돌 여부 확인

### 성능 이슈
1. 테스트 데이터 최소화
2. Mock 객체 적절히 활용
3. 무거운 통합 테스트는 별도 분리

## 추가 도구 연동

### SonarQube
```bash
./gradlew sonarqube -Dsonar.host.url=http://localhost:9000
```

### IDE 플러그인
- IntelliJ IDEA: JaCoCo 플러그인 설치
- VS Code: Coverage Gutters 확장

### GitHub Actions
```yaml
- name: Test with Gradle
  run: ./gradlew test jacocoTestReport
  
- name: Upload coverage to Codecov
  uses: codecov/codecov-action@v3
```