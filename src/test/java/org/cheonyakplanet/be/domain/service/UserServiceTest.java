package org.cheonyakplanet.be.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Map;
import java.util.Optional;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.user.LoginRequestDTO;
import org.cheonyakplanet.be.application.service.UserService;
import org.cheonyakplanet.be.domain.entity.User;
import org.cheonyakplanet.be.domain.entity.UserRoleEnum;
import org.cheonyakplanet.be.domain.entity.UserToken;
import org.cheonyakplanet.be.domain.repository.UserRepository;
import org.cheonyakplanet.be.domain.repository.UserTokenRepository;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserTokenRepository userTokenRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private JwtUtil jwtUtil;

	@InjectMocks
	private UserService userService;

	@Test
	@DisplayName("로그인 성공 테스트: 토큰 반환 확인")
	void givenValidLoginCredentials_whenLogin_thenReturnTokens() {
		// Given: 유효한 로그인 요청 정보와 인증 성공 시나리오
		String email = "test@test.com";
		String password = "password";
		LoginRequestDTO requestDTO = new LoginRequestDTO(email, password);

		User user = new User(email, "encodedPassword", UserRoleEnum.USER, "TestUser");
		UserDetailsImpl userDetails = new UserDetailsImpl(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
			userDetails.getAuthorities());

		given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.willReturn(authentication);
		String accessToken = "access-token";
		String refreshToken = "refresh-token";
		given(jwtUtil.createAccessToken(email, UserRoleEnum.USER)).willReturn(accessToken);
		given(jwtUtil.createRefreshToken(email, UserRoleEnum.USER)).willReturn(refreshToken);
		willDoNothing().given(jwtUtil).storeTokens(email, accessToken, refreshToken);

		// When: 로그인 메서드 호출
		ApiResponse response = userService.login(requestDTO);

		// Then: 반환된 ApiResponse에 accessToken, refreshToken이 포함되어 있음
		assertNotNull(response);
		Map<String, Object> data = (Map<String, Object>)response.getData();
		assertEquals(accessToken, data.get("accessToken"));
		assertEquals(refreshToken, data.get("refreshToken"));

		then(authenticationManager).should().authenticate(any(UsernamePasswordAuthenticationToken.class));
		then(jwtUtil).should().createAccessToken(email, UserRoleEnum.USER);
		then(jwtUtil).should().createRefreshToken(email, UserRoleEnum.USER);
		then(jwtUtil).should().storeTokens(email, accessToken, refreshToken);
	}

	@Test
	@DisplayName("잘못된 로그인 정보 테스트: CustomException 발생 확인")
	void givenInvalidLoginCredentials_whenLogin_thenThrowCustomException() {
		// Given: 잘못된 로그인 정보로 인해 인증 실패하는 상황
		String email = "test@test.com";
		String password = "wrong-password";
		LoginRequestDTO requestDTO = new LoginRequestDTO(email, password);

		given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
			.willThrow(new RuntimeException("Authentication failed"));

		// When & Then: 로그인 시 CustomException 발생 확인
		CustomException exception = assertThrows(CustomException.class, () -> userService.login(requestDTO));
		assertEquals(ErrorCode.SIGN004, exception.getErrorCode());
		assertEquals("로그인 정보 불일치", exception.getMessage());

		then(jwtUtil).should(never()).createAccessToken(anyString(), any());
	}

	@Test
	@DisplayName("로그아웃 성공 테스트: 블랙리스트 처리 확인")
	void givenValidToken_whenLogout_thenReturnSuccessResponse() {
		// Given: 로그아웃 요청 시 헤더에 토큰 존재, 토큰 정보가 DB에 있음
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		String tokenValue = "token";
		String bearerToken = JwtUtil.BEARER_PREFIX + tokenValue;

		given(jwtUtil.getTokenFromRequest(request)).willReturn(tokenValue);
		UserToken userToken = new UserToken("test@test.com", bearerToken, "refresh-token", null, null);
		given(userTokenRepository.findByAccessToken(bearerToken)).willReturn(Optional.of(userToken));

		// When: 로그아웃 메서드 호출
		ApiResponse response = userService.logout(request);

		// Then: 로그아웃 성공 응답 및 토큰 블랙리스트 처리 확인
		assertNotNull(response);
		assertEquals("success", response.getStatus());
		assertEquals("로그아웃 성공", response.getData());

		then(userTokenRepository).should().findByAccessToken(bearerToken);
		then(userTokenRepository).should().save(userToken);
	}

	@Test
	@DisplayName("로그아웃 실패 테스트: DB에 토큰 정보 없음")
	void givenNonexistentToken_whenLogout_thenReturnFailResponse() {
		// Given: 로그아웃 요청 시 헤더에 토큰 존재하지만 DB에 토큰 정보가 없음
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		String tokenValue = "token";
		String bearerToken = JwtUtil.BEARER_PREFIX + tokenValue;

		given(jwtUtil.getTokenFromRequest(request)).willReturn(tokenValue);
		given(userTokenRepository.findByAccessToken(bearerToken)).willReturn(Optional.empty());

		// When: 로그아웃 메서드 호출
		ApiResponse response = userService.logout(request);

		// Then: 실패 응답 반환
		assertNotNull(response);
		assertEquals("fail", response.getStatus());
		assertEquals("토큰 정보가 존재하지 않습니다.", response.getData());

		then(userTokenRepository).should().findByAccessToken(bearerToken);
	}

}