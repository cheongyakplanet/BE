package org.cheonyakplanet.be.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.cheonyakplanet.be.domain.entity.comunity.Post;
import org.cheonyakplanet.be.domain.entity.comunity.PostCategory;
import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.entity.user.UserRoleEnum;
import org.cheonyakplanet.be.domain.entity.user.UserStatusEnum;
import org.cheonyakplanet.be.domain.repository.PostRepository;
import org.cheonyakplanet.be.domain.repository.UserRepository;
import org.cheonyakplanet.be.infrastructure.config.NewsConfig;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NewsConfig newsConfig;

    @Mock
    private NewsConfig.Api apiConfig;

    @Mock
    private NewsConfig.Scheduling schedulingConfig;


    @InjectMocks
    private NewsService newsService;

    private User systemUser;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        // Create system user
        systemUser = User.builder()
            .email("jjsus0307@gmail.com")
            .username("system")
            .password("password")
            .role(UserRoleEnum.USER)
            .status(UserStatusEnum.ACTIVE)
            .build();

        userDetails = new UserDetailsImpl(systemUser);
    }

    @Test
    @DisplayName("시스템 사용자 인증 실패")
    void crawlAndCreateNewsPosts_SystemUserNotFound() {
        // Given
        given(userRepository.findByEmail("jjsus0307@gmail.com"))
            .willReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> {
            newsService.crawlAndCreateNewsPosts();
        });
    }

    @Test
    @DisplayName("일간 요약 중복 생성 방지 - 이미 생성된 경우")
    void crawlAndCreateNewsPosts_DuplicatePrevention() {
        // Given
        Post existingPost = Post.builder()
            .title("[일간 부동산 뉴스] 2025년 06월 17일 - 5개 기사 요약")
            .username("system")
            .category(PostCategory.INFO_SHARE)
            .content("기존 요약")
            .build();
        existingPost.setCreatedAt(LocalDateTime.now());

        given(userRepository.findByEmail("jjsus0307@gmail.com"))
            .willReturn(Optional.of(systemUser));
        given(postRepository.findByTitleContainingAndUsernameAndDeletedAtIsNull(
            contains("[일간 부동산 뉴스]"), anyString()))
            .willReturn(List.of(existingPost));

        // When
        newsService.crawlAndCreateNewsPosts();

        // Then
        then(postRepository).should(never()).save(any(Post.class));
    }

    @Test
    @DisplayName("네이버 클라이언트 ID 추출 테스트")
    void getNaverClientId_ExtractFromConfig() {
        // Given
        given(newsConfig.getApi()).willReturn(apiConfig);
        given(apiConfig.getKey()).willReturn("my-client-id:my-secret");

        // When
        String clientId = ReflectionTestUtils.invokeMethod(newsService, "getNaverClientId");

        // Then
        assertEquals("my-client-id", clientId);
    }

    @Test
    @DisplayName("네이버 클라이언트 시크릿 추출 테스트")
    void getNaverClientSecret_ExtractFromConfig() {
        // Given
        given(newsConfig.getApi()).willReturn(apiConfig);
        given(apiConfig.getKey()).willReturn("my-client-id:my-secret");

        // When
        String clientSecret = ReflectionTestUtils.invokeMethod(newsService, "getNaverClientSecret");

        // Then
        assertEquals("my-secret", clientSecret);
    }

    @Test
    @DisplayName("API 키가 null인 경우 환경변수 폴백")
    void getNaverClientId_FallbackToEnv() {
        // Given
        given(newsConfig.getApi()).willReturn(apiConfig);
        given(apiConfig.getKey()).willReturn(null);

        // When
        String clientId = ReflectionTestUtils.invokeMethod(newsService, "getNaverClientId");

        // Then - 환경변수가 설정되지 않은 경우 null 반환
        assertNull(clientId);
    }

}