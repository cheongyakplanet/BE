package org.cheonyakplanet.be.infrastructure.jwt;

import java.io.IOException;
import java.util.Optional;

import org.cheonyakplanet.be.application.dto.ApiResponse;
import org.cheonyakplanet.be.domain.entity.user.UserToken;
import org.cheonyakplanet.be.domain.repository.UserTokenRepository;
import org.cheonyakplanet.be.infrastructure.security.UserDetailsServiceImpl;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.cheonyakplanet.be.presentation.exception.ErrorData;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "JWT 검증 및 인가")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;
	private final UserDetailsServiceImpl userDetailsService;
	private final UserTokenRepository tokenRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws
		ServletException,
		IOException {
		try {
			String tokenValue = jwtUtil.getTokenFromRequest(req);
			log.info("Extracted Token: {}", tokenValue);

			if (StringUtils.hasText(tokenValue)) {
				// "Bearer " 접두어 포함해서 DB 조회
				Optional<UserToken> tokenEntityOpt = tokenRepository.findByAccessToken(
					JwtUtil.BEARER_PREFIX + tokenValue);
				if (tokenEntityOpt.isPresent() && tokenEntityOpt.get().isBlacklisted()) {
					log.error("Token is blacklisted");
					throw new CustomException(ErrorCode.AUTH006, "로그아웃된 토큰");
				}

				if (!jwtUtil.validateToken(tokenValue)) {
					throw new CustomException(ErrorCode.AUTH005, "Token validation failed");
				}

				Claims info = jwtUtil.getUserInfoFromToken(tokenValue);
				log.info("Claims: {}", info);
				setAuthentication(info.getSubject());
			}

			filterChain.doFilter(req, res);
		} catch (CustomException e) {
			// 필터 내에서 발생한 CustomException을 잡아서 JSON 형태로 응답 작성
			res.setContentType("application/json;charset=UTF-8");
			// 적절한 HTTP 상태 코드를 설정 (예: 인증 실패이므로 401)
			res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

			// 에러 응답 객체 생성 (ApiResponse, ErrorData는 기존에 사용하던 클래스)
			ErrorData errorData = new ErrorData(
				e.getErrorCode().getCode(),
				e.getErrorCode().getMessage(),
				e.getDetails()
			);
			ApiResponse<ErrorData> errorResponse = new ApiResponse<>("fail", errorData);

			// ObjectMapper를 사용해 JSON 문자열로 변환
			ObjectMapper mapper = new ObjectMapper();
			String jsonResponse = mapper.writeValueAsString(errorResponse);

			// 응답 작성 후 종료
			res.getWriter().write(jsonResponse);
			res.getWriter().flush();
			return;
		}
	}

	// 인증 처리
	public void setAuthentication(String username) {
		Authentication authentication = createAuthentication(username);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	// 인증 객체 생성
	private Authentication createAuthentication(String username) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
		return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
	}

}