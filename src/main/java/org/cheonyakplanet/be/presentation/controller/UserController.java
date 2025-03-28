package org.cheonyakplanet.be.presentation.controller;

import java.io.IOException;
import java.util.List;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.user.LoginRequestDTO;
import org.cheonyakplanet.be.application.dto.user.SignupRequestDTO;
import org.cheonyakplanet.be.application.dto.user.UserDTO;
import org.cheonyakplanet.be.application.dto.user.UserUpdateRequestDTO;
import org.cheonyakplanet.be.domain.service.UserService;
import org.cheonyakplanet.be.infrastructure.jwt.JwtUtil;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class UserController {

	private final UserService userService;
	private final JwtUtil jwtUtil;

	/**
	 * 회원가입
	 *
	 * @param requestDTO
	 * @return
	 */
	@PostMapping("/signup")
	@Operation(summary = "회원가입")
	public ResponseEntity<?> signup(@RequestBody SignupRequestDTO requestDTO) {
		userService.signup(requestDTO);
		return ResponseEntity.ok(new ApiResponse("success", requestDTO));
	}

	/**
	 * 로그인
	 *
	 * @param requestDto
	 * @return
	 */
	@PostMapping("/login")
	@Operation(summary = "로그인", description = "이메일 입력")
	public ResponseEntity<?> login(@RequestBody LoginRequestDTO requestDto) {
		Object result = userService.login(requestDto);
		return ResponseEntity.ok(new ApiResponse("success", result));
	}

	/**
	 * 로그아웃
	 *
	 * @param request
	 * @param
	 * @return
	 */
	@PostMapping("/logout")
	@Operation(summary = "로그아웃", description = "사용자 로그아웃 처리")
	public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
		Object result = userService.logout(request);
		return ResponseEntity.ok(new ApiResponse("success", result));
	}

	@GetMapping("/kako/callback")
	@Operation(summary = "소셜 로그인 - 카카오", description = "미완성")
	public void kakaoLogin(@RequestParam String code, HttpServletResponse response) throws IOException {
		String email = userService.kakaoLogin(code);

		// 2. 저장된 Access Token 가져오기
		String accessToken = jwtUtil.getAccessToken(email);

		// 3. 프론트엔드로 리다이렉트 (Access Token 포함)
		response.sendRedirect("https://frontend-domain.com/oauth/callback?accessToken=" + accessToken);

	}

	@PostMapping("/auth/refresh")
	@Operation(summary = "토큰 갱신")
	public ResponseEntity<ApiResponse> refreshAccessToken(@RequestParam String refreshToken) {
		Object result = userService.refreshAccessToken(refreshToken);
		return ResponseEntity.ok(new ApiResponse("success", result));
	}

	@GetMapping("/mypage")
	@Operation(summary = "마이페이지 조회", description = "사용자의 전체 정보를 반환")
	public ResponseEntity<?> getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		UserDTO userDTO = userService.getMyPage(userDetails.getUsername());
		return ResponseEntity.ok(new ApiResponse("success", userDTO));
	}

	@PatchMapping("/mypage")
	@Operation(summary = "마이페이지 수정", description = "사용자 정보를 업데이트")
	public ResponseEntity<?> updateUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody UserUpdateRequestDTO updateRequestDTO) {

		UserDTO updatedUser = userService.updateUserInfo(userDetails, updateRequestDTO);
		return ResponseEntity.ok(new ApiResponse("success", updatedUser));
	}

	@DeleteMapping("/mypage")
	@Operation(summary = "회원 탈퇴", description = "회원 탈퇴 후 데이터를 비활성화 처리")
	public ResponseEntity<?> withdrawUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {

		userService.withdrawUser(userDetails.getUsername());
		return ResponseEntity.ok(new ApiResponse("success", "회원 탈퇴 완료"));
	}

	@PostMapping("/find-id")
	@Operation(summary = "아이디 찾기", description = "이메일을 입력하면 가입된 계정 여부를 확인")
	public ResponseEntity<?> findUserId(@RequestParam String email) {

		ApiResponse response = userService.findUserId(email);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/find-password")
	@Operation(summary = "비밀번호 찾기", description = "이메일과 이름을 입력하면 인증 코드가 전송됨")
	public ResponseEntity<?> findUserPassword(@RequestParam("arg0") String email,
		@RequestParam("arg1") String username) {

		ApiResponse response = userService.findUserPassword(email, username);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/reset-password")
	@Operation(summary = "비밀번호 재설정", description = "인증 코드 검증 후 비밀번호 변경 및 자동 로그인")
	public ResponseEntity<?> resetPassword(
		@RequestParam("arg0") String email,
		@RequestParam("arg1") String username,
		@RequestParam("arg2") String inputCode,
		@RequestParam("arg3") String verificationCode,
		@RequestParam("arg4") String newPassword,
		@RequestParam("arg5") String confirmPassword) {

		ApiResponse response = userService.verifyCodeAndResetPassword(
			email, username, inputCode, verificationCode, newPassword, confirmPassword);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/location")
	@Operation(summary = "관심 지역 추가", description = "사용자의 관심 지역을 추가")
	public ResponseEntity<?> addInterestLocation(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam List<String> location) {

		ApiResponse response = userService.addInterestLocations(userDetails.getUsername(), location);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/location")
	@Operation(summary = "관심 지역 삭제", description = "사용자의 여러 관심 지역을 한 번에 삭제")
	public ResponseEntity<?> deleteInterestLocation(@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestParam List<String> locations) {

		ApiResponse response = userService.deleteInterestLocations(userDetails.getUsername(), locations);
		return ResponseEntity.ok(response);
	}
}
