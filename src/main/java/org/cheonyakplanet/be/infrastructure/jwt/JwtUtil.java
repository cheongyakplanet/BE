package org.cheonyakplanet.be.infrastructure.jwt;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import org.cheonyakplanet.be.domain.entity.user.UserRoleEnum;
import org.cheonyakplanet.be.domain.entity.user.UserToken;
import org.cheonyakplanet.be.domain.repository.UserTokenRepository;
import org.cheonyakplanet.be.domain.exception.CustomException;
import org.cheonyakplanet.be.domain.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

	// Header Key
	public static final String AUTHORIZATION_HEADER = "Authorization";
	// admin key
	public static final String AUTHORIZATION_KEY = "auth";
	// token prefix
	public static final String BEARER_PREFIX = "Bearer ";
	// token exp
	private final long TOKEN_TIME = 60 * 60 * 1000L; // 60min
	private final long REFRESH_TOKEN_TIME = 24 * 60 * 60 * 1000L; // 1 days

	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

	private final UserTokenRepository userTokenRepository;

	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	/**
	 * Access Token 생성
	 */
	public String createAccessToken(String email, UserRoleEnum role) {
		Date now = new Date();
		return BEARER_PREFIX + Jwts.builder()
			.setSubject(email)
			.claim(AUTHORIZATION_KEY, role)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + TOKEN_TIME))
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	/**
	 * Refresh Token 생성
	 */
	public String createRefreshToken(String email, UserRoleEnum role) {
		Date now = new Date();
		return BEARER_PREFIX + Jwts.builder()
			.setSubject(email)
			.claim(AUTHORIZATION_KEY, role)
			.setIssuedAt(now)
			.setExpiration(new Date(now.getTime() + REFRESH_TOKEN_TIME))
			.signWith(key, signatureAlgorithm)
			.compact();
	}

	/**
	 * Access Token과 Refresh Token 저장 (MySQL)
	 */
	@Transactional
	public void storeTokens(String email, String accessToken, String refreshToken) {
		Date accessExpiry = new Date(System.currentTimeMillis() + TOKEN_TIME);
		Date refreshExpiry = new Date(System.currentTimeMillis() + REFRESH_TOKEN_TIME);

		Optional<UserToken> existingToken = userTokenRepository.findByEmail(email);

		if (existingToken.isPresent()) {
			UserToken token = existingToken.get();
			token.setBlacklisted(false);
			existingToken.get().updateTokens(accessToken, refreshToken, accessExpiry, refreshExpiry);
			userTokenRepository.save(existingToken.get());
		} else {
			UserToken userToken = new UserToken(email, accessToken, refreshToken, accessExpiry, refreshExpiry);
			userToken.setBlacklisted(false);
			userTokenRepository.save(userToken);
		}
	}

	/**
	 * Access Token 가져오기 (MySQL)
	 */
	public String getAccessToken(String email) {
		return userTokenRepository.findByEmail(email).map(UserToken::getAccessToken).orElse(null);
	}

	/**
	 * Refresh Token 가져오기 (MySQL)
	 */
	public String getRefreshToken(String email) {
		return userTokenRepository.findByEmail(email).map(UserToken::getRefreshToken).orElse(null);
	}

	/**
	 * Refresh Token을 사용하여 새로운 Access Token 발급
	 */
	public String refreshAccessToken(String refreshToken) {
		Optional<UserToken> userToken = userTokenRepository.findByRefreshToken(refreshToken);

		if (userToken.isEmpty()) {
			throw new CustomException(ErrorCode.AUTH005, "Refresh Token이 유효하지 않습니다.");
		}

		String email = getUserInfoFromToken(refreshToken).getSubject();
		String newAccessToken = createAccessToken(email, UserRoleEnum.USER);

		// 새로운 Access Token을 MySQL에 저장
		userToken.get().setAccessToken(newAccessToken);
		userToken.get().setAccessTokenExpiry(new Date(System.currentTimeMillis() + TOKEN_TIME));
		userTokenRepository.save(userToken.get());

		return newAccessToken;
	}

	/**
	 * 토큰 검증
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (SecurityException | MalformedJwtException | SignatureException e) {
			throw new CustomException(ErrorCode.AUTH001, "Invalid JWT signature");
		} catch (ExpiredJwtException e) {
			throw new CustomException(ErrorCode.AUTH002, "Expired JWT token");
		} catch (UnsupportedJwtException e) {
			throw new CustomException(ErrorCode.AUTH003, "Unsupported JWT token");
		} catch (IllegalArgumentException e) {
			throw new CustomException(ErrorCode.AUTH004, "JWT claims is empty");
		}
	}

	/**
	 * 토큰에서 사용자 정보 가져오기
	 */
	public Claims getUserInfoFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	/**
	 * 로그아웃 시 토큰 삭제
	 */
	public void deleteTokens(String email) {
		userTokenRepository.deleteByEmail(email);
	}

	/**
	 * JWT 토큰 prefix 제거
	 *
	 * @param token
	 * @return
	 */
	public String substringToken(String token) {
		if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
			return token.substring(7);
		}
		throw new CustomException(ErrorCode.AUTH010, "토큰 없음");
	}

	/**
	 * Header에사 JWT 가져오기
	 *
	 * @param request
	 * @return
	 */
	public String getTokenFromRequest(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
			if (!StringUtils.hasText(header.substring(7)) || !validateToken(header.substring(7))) {
				throw new CustomException(ErrorCode.AUTH001, "Invalid or missing token");
			}
			return header.substring(7); // "Bearer " 이후의 토큰 반환
		}
		return null;
	}

}
