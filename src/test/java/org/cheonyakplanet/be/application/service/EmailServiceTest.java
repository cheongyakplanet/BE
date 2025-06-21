package org.cheonyakplanet.be.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.entity.user.UserRoleEnum;
import org.cheonyakplanet.be.domain.repository.UserRepository;
import org.cheonyakplanet.be.domain.exception.CustomException;
import org.cheonyakplanet.be.domain.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.AsyncResult;

@ExtendWith(MockitoExtension.class)
@DisplayName("이메일 서비스 테스트")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenCacheService tokenCacheService;

    @InjectMocks
    private EmailService emailService;

    private User testUser;
    private String testEmail;
    private String testVerificationCode;

    @BeforeEach
    void setUp() {
        // Given: 테스트용 데이터 설정
        testEmail = "test@test.com";
        testVerificationCode = "123456";
        
        testUser = User.builder()
                .email(testEmail)
                .password("password")
                .role(UserRoleEnum.USER)
                .username("테스트사용자")
                .build();
    }

    @Test
    @DisplayName("인증 코드 전송 성공 테스트")
    void givenValidEmail_whenSendVerificationCode_thenSendEmailSuccessfully() {
        // Given: 유효한 이메일 주소와 정상적인 메일 서비스
        willDoNothing().given(mailSender).send(any(SimpleMailMessage.class));
        willDoNothing().given(tokenCacheService).storeVerificationCode(eq(testEmail), anyString());

        // When: 인증 코드 전송 메서드 호출
        assertDoesNotThrow(() -> emailService.sendVerificationCode(testEmail, testVerificationCode));

        // Then: 메일이 전송되고 토큰이 캐시에 저장됨
        then(mailSender).should().send(any(SimpleMailMessage.class));
        then(tokenCacheService).should().storeVerificationCode(eq(testEmail), eq(testVerificationCode));
    }

    @Test
    @DisplayName("인증 코드 전송 실패 테스트 - 메일 서버 오류")
    void givenMailServerError_whenSendVerificationCode_thenThrowMailException() {
        // Given: 메일 서버에서 오류가 발생하는 시나리오
        willThrow(new MailException("메일 서버 연결 실패") {})
                .given(mailSender).send(any(SimpleMailMessage.class));

        // When & Then: 인증 코드 전송 시 MailException 발생
        assertThrows(MailException.class, 
                () -> emailService.sendVerificationCode(testEmail, testVerificationCode));
        
        then(mailSender).should().send(any(SimpleMailMessage.class));
        then(tokenCacheService).should(never()).storeVerificationCode(anyString(), anyString());
    }

    @Test
    @DisplayName("비동기 인증 코드 전송 성공 테스트")
    void givenValidEmail_whenSendVerificationCodeAsync_thenReturnCompletedFuture() {
        // Given: 유효한 이메일 주소
        willDoNothing().given(mailSender).send(any(SimpleMailMessage.class));
        willDoNothing().given(tokenCacheService).storeVerificationCode(eq(testEmail), anyString());

        // When: 비동기 인증 코드 전송 메서드 호출
        CompletableFuture<Void> result = emailService.sendVerificationCodeAsync(testEmail, testVerificationCode);

        // Then: CompletableFuture가 정상적으로 완료됨
        assertThat(result).isNotNull();
        assertDoesNotThrow(() -> result.get());
        then(mailSender).should().send(any(SimpleMailMessage.class));
        then(tokenCacheService).should().storeVerificationCode(eq(testEmail), eq(testVerificationCode));
    }

    @Test
    @DisplayName("인증 코드 검증 성공 테스트")
    void givenValidVerificationCode_whenVerifyCode_thenReturnTrue() {
        // Given: 유효한 인증 코드가 캐시에 저장된 상황
        given(tokenCacheService.getVerificationCode(testEmail)).willReturn(testVerificationCode);
        willDoNothing().given(tokenCacheService).removeVerificationCode(testEmail);

        // When: 인증 코드 검증 메서드 호출
        boolean result = emailService.verifyCode(testEmail, testVerificationCode);

        // Then: 검증 성공하고 캐시에서 코드 제거
        assertThat(result).isTrue();
        then(tokenCacheService).should().getVerificationCode(testEmail);
        then(tokenCacheService).should().removeVerificationCode(testEmail);
    }

    @Test
    @DisplayName("인증 코드 검증 실패 테스트 - 잘못된 코드")
    void givenInvalidVerificationCode_whenVerifyCode_thenReturnFalse() {
        // Given: 잘못된 인증 코드 입력
        String wrongCode = "000000";
        given(tokenCacheService.getVerificationCode(testEmail)).willReturn(testVerificationCode);

        // When: 인증 코드 검증 메서드 호출
        boolean result = emailService.verifyCode(testEmail, wrongCode);

        // Then: 검증 실패하고 캐시에서 코드 제거되지 않음
        assertThat(result).isFalse();
        then(tokenCacheService).should().getVerificationCode(testEmail);
        then(tokenCacheService).should(never()).removeVerificationCode(testEmail);
    }

    @Test
    @DisplayName("인증 코드 검증 실패 테스트 - 만료된 코드")
    void givenExpiredVerificationCode_whenVerifyCode_thenReturnFalse() {
        // Given: 만료되어 캐시에서 제거된 인증 코드
        given(tokenCacheService.getVerificationCode(testEmail)).willReturn(null);

        // When: 인증 코드 검증 메서드 호출
        boolean result = emailService.verifyCode(testEmail, testVerificationCode);

        // Then: 검증 실패
        assertThat(result).isFalse();
        then(tokenCacheService).should().getVerificationCode(testEmail);
        then(tokenCacheService).should(never()).removeVerificationCode(testEmail);
    }

    @Test
    @DisplayName("이메일 중복 확인 성공 테스트 - 중복되지 않은 이메일")
    void givenUniqueEmail_whenCheckEmailDuplicate_thenReturnNotDuplicated() {
        // Given: 데이터베이스에 존재하지 않는 이메일
        given(userRepository.findByEmail(testEmail)).willReturn(Optional.empty());

        // When: 이메일 중복 확인 메서드 호출
        boolean result = emailService.checkEmailDuplicate(testEmail);

        // Then: 중복되지 않음을 반환
        assertThat(result).isFalse();
        then(userRepository).should().findByEmail(testEmail);
    }

    @Test
    @DisplayName("이메일 중복 확인 성공 테스트 - 중복된 이메일")
    void givenDuplicateEmail_whenCheckEmailDuplicate_thenReturnDuplicated() {
        // Given: 데이터베이스에 이미 존재하는 이메일
        given(userRepository.findByEmail(testEmail)).willReturn(Optional.of(testUser));

        // When: 이메일 중복 확인 메서드 호출
        boolean result = emailService.checkEmailDuplicate(testEmail);

        // Then: 중복됨을 반환
        assertThat(result).isTrue();
        then(userRepository).should().findByEmail(testEmail);
    }

    @Test
    @DisplayName("랜덤 인증 코드 생성 테스트")
    void givenCodeGenerationRequest_whenGenerateVerificationCode_thenReturnSixDigitCode() {
        // Given: 인증 코드 생성 요청

        // When: 인증 코드 생성 메서드 호출
        String generatedCode = emailService.generateVerificationCode();

        // Then: 6자리 숫자로 구성된 인증 코드 반환
        assertThat(generatedCode).isNotNull();
        assertThat(generatedCode).hasSize(6);
        assertThat(generatedCode).matches("\\d{6}");
    }

    @Test
    @DisplayName("인증 코드 생성 고유성 테스트")
    void givenMultipleCodeGenerations_whenGenerateVerificationCode_thenReturnDifferentCodes() {
        // Given: 여러 번의 인증 코드 생성 요청

        // When: 인증 코드를 여러 번 생성
        String code1 = emailService.generateVerificationCode();
        String code2 = emailService.generateVerificationCode();
        String code3 = emailService.generateVerificationCode();

        // Then: 각각 다른 코드가 생성됨 (확률적으로 매우 높음)
        assertThat(code1).isNotEqualTo(code2);
        assertThat(code2).isNotEqualTo(code3);
        assertThat(code1).isNotEqualTo(code3);
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 전송 성공 테스트")
    void givenRegisteredUser_whenSendPasswordResetEmail_thenSendEmailSuccessfully() {
        // Given: 등록된 사용자에게 비밀번호 재설정 이메일 전송
        String resetToken = "reset-token-12345";
        given(userRepository.findByEmail(testEmail)).willReturn(Optional.of(testUser));
        willDoNothing().given(mailSender).send(any(SimpleMailMessage.class));
        willDoNothing().given(tokenCacheService).storePasswordResetToken(testEmail, resetToken);

        // When: 비밀번호 재설정 이메일 전송 메서드 호출
        assertDoesNotThrow(() -> emailService.sendPasswordResetEmail(testEmail, resetToken));

        // Then: 이메일이 전송되고 토큰이 캐시에 저장됨
        then(userRepository).should().findByEmail(testEmail);
        then(mailSender).should().send(any(SimpleMailMessage.class));
        then(tokenCacheService).should().storePasswordResetToken(testEmail, resetToken);
    }

    @Test
    @DisplayName("비밀번호 재설정 이메일 전송 실패 테스트 - 존재하지 않는 사용자")
    void givenNonExistentUser_whenSendPasswordResetEmail_thenThrowCustomException() {
        // Given: 존재하지 않는 사용자 이메일
        String resetToken = "reset-token-12345";
        given(userRepository.findByEmail(testEmail)).willReturn(Optional.empty());

        // When & Then: 비밀번호 재설정 이메일 전송 시 CustomException 발생
        CustomException exception = assertThrows(CustomException.class,
                () -> emailService.sendPasswordResetEmail(testEmail, resetToken));

        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER001);
        assertThat(exception.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
        then(mailSender).should(never()).send(any(SimpleMailMessage.class));
        then(tokenCacheService).should(never()).storePasswordResetToken(anyString(), anyString());
    }
}