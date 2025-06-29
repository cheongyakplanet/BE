package org.cheonyakplanet.be.presentation.controller;

import java.io.IOException;
import java.util.List;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.application.dto.user.InterestLocationDTO;
import org.cheonyakplanet.be.application.dto.user.LoginRequestDTO;
import org.cheonyakplanet.be.application.dto.user.MyPageDTO;
import org.cheonyakplanet.be.application.dto.user.SignupRequestDTO;
import org.cheonyakplanet.be.application.dto.user.TokenResponse;
import org.cheonyakplanet.be.application.dto.user.UserDTO;
import org.cheonyakplanet.be.application.dto.user.UserUpdateRequestDTO;
import org.cheonyakplanet.be.application.dto.user.UserUpdateResponseDTO;
import org.cheonyakplanet.be.infrastructure.cache.TokenCacheService;
import org.cheonyakplanet.be.application.service.UserService;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsImpl;
import org.springframework.http.HttpHeaders;
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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
	private final TokenCacheService tokenCacheService;

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

	@GetMapping("/kakao/callback")
	@Operation(summary = "소셜 로그인 - 카카오", description = "")
	public void kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
		String stateCode = userService.kakaoLogin(code);
		String redirectUrl = String.format(
			"https://www.cheongyakplanet.site?state=%s", stateCode);
			//"https://cheongyakplanet.vercel.app?state=%s", stateCode);
		response.sendRedirect(redirectUrl);
	}

	@GetMapping("/kakao/exchange")
	@Operation(summary = "상태 코드로 토큰 교환", description = "상태 코드를 사용하여 실제 인증 토큰 획득")
	public ResponseEntity<?> exchangeStateForTokens(@RequestParam("state") String stateCode) {
		TokenResponse tokens = tokenCacheService.getAndRemoveTokens(stateCode);
		return ResponseEntity.ok(new ApiResponse("success", tokens));
	}

	@PostMapping("/auth/refresh")
	@Operation(summary = "토큰 갱신")
	public ResponseEntity<ApiResponse> refreshAccessToken(@RequestParam String refreshToken) {
		Object result = userService.refreshAccessToken(refreshToken);
		return ResponseEntity.ok(new ApiResponse("success", result));
	}

	/**
	 * 마이 페이지 조회
	 * @param userDetails
	 * @return
	 */
	@GetMapping("/mypage")
	@Operation(summary = "마이페이지 조회", description = "사용자의 전체 정보를 반환",
		responses = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json",
				schema = @Schema(implementation = ApiResponse.class),
				examples = @ExampleObject(value = """
					{
					  "status": "success",
					  "data": {
					    "email": "test@test",
					    "username": "tester",
					    "interestLocals": [
					      "서울특별시 동대문구",
					      "서울특별시 서대문구",
					      "서울특별시 서초구",
					      "서울특별시 강북구",
					      "서울시 용산구"
					    ],
					    "property": null,
					    "income": 50000000,
					    "isMarried": true,
					    "numChild": 2,
					    "numHouse": null,
					    "status": "ACTIVE"
					  }
					}
					"""))
			)
		}

	)
	public ResponseEntity<?> getMyPage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
		MyPageDTO myPageDTO = userService.getMyPage(userDetails.getUsername());
		return ResponseEntity.ok(new ApiResponse("success", myPageDTO));
	}

	@PatchMapping("/mypage")
	@Operation(summary = "마이페이지 수정", description = "사용자 정보를 업데이트")
	public ResponseEntity<?> updateUserInfo(
		@AuthenticationPrincipal UserDetailsImpl userDetails,
		@RequestBody UserUpdateRequestDTO updateRequestDTO) {

		UserUpdateResponseDTO updatedUser = userService.updateUserInfo(userDetails, updateRequestDTO);

		return ResponseEntity.ok()
			.header(HttpHeaders.AUTHORIZATION, updatedUser.token())
			.body(new ApiResponse<>("success", updatedUser.user()));
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
		@RequestBody InterestLocationDTO interestLocationDTO) {

		ApiResponse response = userService.addInterestLocations(userDetails.getUsername(), interestLocationDTO);
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
