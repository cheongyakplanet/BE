package org.cheonyakplanet.be.presentation.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.user.InterestLocationDTO;
import org.cheonyakplanet.be.application.dto.user.LoginRequestDTO;
import org.cheonyakplanet.be.application.dto.user.MyPageDTO;
import org.cheonyakplanet.be.application.dto.user.SignupRequestDTO;
import org.cheonyakplanet.be.application.dto.user.UserDTO;
import org.cheonyakplanet.be.application.dto.user.UserUpdateRequestDTO;
import org.cheonyakplanet.be.application.service.UserService;
import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.entity.user.UserRoleEnum;
import org.cheonyakplanet.be.domain.entity.user.UserStatusEnum;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerAPITest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private UserService userService;

	@MockBean
	private JwtUtil jwtUtil;

	private UserDetailsImpl userDetails;
	private User user;
	private SignupRequestDTO signupRequestDTO;
	private LoginRequestDTO loginRequestDTO;
	private UserUpdateRequestDTO userUpdateRequestDTO;
	private MyPageDTO myPageDTO;
	private InterestLocationDTO interestLocationDTO;

	@BeforeEach
	void setUp() {
		// 테스트용 객체 생성
		user = new User("test@test.com", "password", UserRoleEnum.USER, "tester");
		userDetails = new UserDetailsImpl(user);

		// SecurityContext 설정
		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);

		// DTO 객체 초기화
		signupRequestDTO = SignupRequestDTO.builder()
			.email("user@example.com")
			.password("1234")
			.admin(false)
			.username("tester")
			.build();

		loginRequestDTO = new LoginRequestDTO("test@test", "1234");

		userUpdateRequestDTO = UserUpdateRequestDTO.builder()
			.username("updatedTester")
			.interestLocal1("서울특별시 강남구")
			.interestLocal2("서울특별시 종로구")
			.income(50000000)
			.isMarried(true)
			.numChild(2)
			.build();

		myPageDTO = MyPageDTO.builder()
			.email("test@test")
			.username("tester")
			.interestLocals(Arrays.asList(
				"서울특별시 동대문구",
				"서울특별시 서대문구",
				"서울특별시 서초구",
				"서울특별시 강북구",
				"서울시 용산구"
			))
			.income(50000000)
			.isMarried(true)
			.numChild(2)
			.status(UserStatusEnum.ACTIVE)
			.build();

		interestLocationDTO = new InterestLocationDTO();
		interestLocationDTO.setLocation("서울시 용산구");
	}

	@Test
	@DisplayName("회원가입 테스트")
	void signupTest() throws Exception {
		// given
		doNothing().when(userService).signup(any(SignupRequestDTO.class));

		// when & then
		mockMvc.perform(post("/api/member/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(signupRequestDTO))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.email").value("user@example.com"))
			.andExpect(jsonPath("$.data.username").value("tester"));

		verify(userService).signup(any(SignupRequestDTO.class));
	}

	@Test
	@DisplayName("로그인 테스트")
	void loginTest() throws Exception {
		// given
		Map<String, String> loginResponse = new HashMap<>();
		loginResponse.put("accessToken", "test-access-token");
		loginResponse.put("refreshToken", "test-refresh-token");
		given(userService.login(any(LoginRequestDTO.class))).willReturn(loginResponse);

		// when & then
		mockMvc.perform(post("/api/member/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(loginRequestDTO))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.accessToken").value("test-access-token"))
			.andExpect(jsonPath("$.data.refreshToken").value("test-refresh-token"));

		verify(userService).login(any(LoginRequestDTO.class));
	}

	@Test
	@DisplayName("로그아웃 테스트")
	void logoutTest() throws Exception {
		// given
		given(userService.logout(any())).willReturn("로그아웃 성공");

		// when & then
		mockMvc.perform(post("/api/member/logout")
				.with(user(userDetails))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value("로그아웃 성공"));

		verify(userService).logout(any());
	}

	// @Test
	// @DisplayName("토큰 갱신 테스트")
	// void refreshAccessTokenTest() throws Exception {
	// 	// given
	// 	Map<String, String> refreshResponse = new HashMap<>();
	// 	refreshResponse.put("accessToken", "new-access-token");
	// 	given(userService.refreshAccessToken(anyString())).willReturn(refreshResponse);
	//
	// 	// when & then
	// 	mockMvc.perform(post("/api/member/auth/refresh")
	// 			.param("refreshToken", "test-refresh-token")
	// 			.with(csrf()))
	// 		.andDo(print())
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"))
	// 		.andExpect(jsonPath("$.data.accessToken").value("new-access-token"));
	//
	// 	verify(userService).refreshAccessToken(anyString());
	// }

	@Test
	@DisplayName("마이페이지 조회 테스트")
	void getMyPageTest() throws Exception {
		// given
		given(userService.getMyPage(anyString())).willReturn(myPageDTO);

		// when & then
		mockMvc.perform(get("/api/member/mypage")
				.with(user(userDetails)))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.email").value("test@test"))
			.andExpect(jsonPath("$.data.username").value("tester"))
			.andExpect(jsonPath("$.data.interestLocals").isArray())
			.andExpect(jsonPath("$.data.interestLocals[0]").value("서울특별시 동대문구"));

		verify(userService).getMyPage(anyString());
	}

	@Test
	@DisplayName("마이페이지 수정 테스트")
	void updateUserInfoTest() throws Exception {
		// given
		UserDTO updatedUser = UserDTO.builder()
			.email("test@test")
			.username("updatedTester")
			.interestLocal1("서울특별시 강남구")
			.interestLocal2("서울특별시 종로구")
			.income(50000000)
			.isMarried(true)
			.numChild(2)
			.status(UserStatusEnum.ACTIVE)
			.build();
		given(userService.updateUserInfo(any(UserDetailsImpl.class), any(UserUpdateRequestDTO.class)))
			.willReturn(updatedUser);

		// when & then
		mockMvc.perform(patch("/api/member/mypage")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(userUpdateRequestDTO))
				.with(user(userDetails))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.username").value("updatedTester"));

		verify(userService).updateUserInfo(any(UserDetailsImpl.class), any(UserUpdateRequestDTO.class));
	}

	@Test
	@DisplayName("회원 탈퇴 테스트")
	void withdrawUserTest() throws Exception {
		// given
		doNothing().when(userService).withdrawUser(anyString());

		// when & then
		mockMvc.perform(delete("/api/member/mypage")
				.with(user(userDetails))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value("회원 탈퇴 완료"));

		verify(userService).withdrawUser(anyString());
	}

	// @Test
	// @DisplayName("아이디 찾기 테스트")
	// void findUserIdTest() throws Exception {
	// 	// given
	// 	ApiResponse response = new ApiResponse("success", Map.of("email", "user@test.com"));
	// 	given(userService.findUserId(anyString())).willReturn(response);
	//
	// 	// when & then
	// 	mockMvc.perform(post("/api/member/find-id")
	// 			.param("email", "user@test.com")
	// 			.with(csrf()))
	// 		.andDo(print())
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"))
	// 		.andExpect(jsonPath("$.data.email").value("user@test.com"));
	//
	// 	verify(userService).findUserId(anyString());
	// }

	@Test
	@DisplayName("비밀번호 찾기 테스트")
	void findUserPasswordTest() throws Exception {
		// given
		ApiResponse response = new ApiResponse("success",
			Map.of("verificationCode", "123456", "message", "인증 코드가 이메일로 전송되었습니다."));
		given(userService.findUserPassword(anyString(), anyString())).willReturn(response);

		// when & then
		mockMvc.perform(post("/api/member/find-password")
				.param("arg0", "user@test.com")
				.param("arg1", "tester")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.verificationCode").value("123456"))
			.andExpect(jsonPath("$.data.message").value("인증 코드가 이메일로 전송되었습니다."));

		verify(userService).findUserPassword(anyString(), anyString());
	}

	@Test
	@DisplayName("비밀번호 재설정 테스트")
	void resetPasswordTest() throws Exception {
		// given
		ApiResponse response = new ApiResponse("success", Map.of(
			"message", "비밀번호가 성공적으로 변경되었습니다.",
			"accessToken", "new-access-token",
			"refreshToken", "new-refresh-token"
		));
		given(userService.verifyCodeAndResetPassword(
			anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
			.willReturn(response);

		// when & then
		mockMvc.perform(post("/api/member/reset-password")
				.param("arg0", "user@test.com")
				.param("arg1", "tester")
				.param("arg2", "123456")
				.param("arg3", "123456")
				.param("arg4", "newPassword123")
				.param("arg5", "newPassword123")
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data.message").value("비밀번호가 성공적으로 변경되었습니다."));

		verify(userService).verifyCodeAndResetPassword(
			anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
	}

	@Test
	@DisplayName("관심 지역 추가 테스트")
	void addInterestLocationTest() throws Exception {
		// given
		ApiResponse response = new ApiResponse("success", "관심 지역이 추가되었습니다.");
		given(userService.addInterestLocations(anyString(), any(InterestLocationDTO.class)))
			.willReturn(response);

		// when & then
		mockMvc.perform(post("/api/member/location")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(interestLocationDTO))
				.with(user(userDetails))
				.with(csrf()))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value("success"))
			.andExpect(jsonPath("$.data").value("관심 지역이 추가되었습니다."));

		verify(userService).addInterestLocations(anyString(), any(InterestLocationDTO.class));
	}

	// @Test
	// @DisplayName("관심 지역 삭제 테스트")
	// void deleteInterestLocationTest() throws Exception {
	// 	// given
	// 	List<String> locations = Arrays.asList("서울시 용산구", "서울시 강남구");
	// 	ApiResponse response = new ApiResponse("success", "관심 지역이 삭제되었습니다.");
	// 	given(userService.deleteInterestLocations(anyString(), any(List.class)))
	// 		.willReturn(response);
	//
	// 	// when & then
	// 	mockMvc.perform(delete("/api/member/location")
	// 			.param("locations", locations.toArray(new String[0]))
	// 			.with(user(userDetails))
	// 			.with(csrf()))
	// 		.andDo(print())
	// 		.andExpect(status().isOk())
	// 		.andExpect(jsonPath("$.status").value("success"))
	// 		.andExpect(jsonPath("$.data").value("관심 지역이 삭제되었습니다."));
	//
	// 	verify(userService).deleteInterestLocations(anyString(), any(List.class));
	// }
}