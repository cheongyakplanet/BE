package org.cheonyakplanet.be.infrastructure.cache;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.cheonyakplanet.be.application.dto.user.TokenResponse;
import org.springframework.stereotype.Service;

@Service
public class TokenCacheService {

	// 토큰 정보와 만료 시간을 함께 저장
	private static class TokenInfo {
		private final TokenResponse tokens;
		private final LocalDateTime expiryTime;

		public TokenInfo(TokenResponse tokens, int expirySeconds) {
			this.tokens = tokens;
			this.expiryTime = LocalDateTime.now().plusSeconds(expirySeconds);
		}

		public boolean isExpired() {
			return LocalDateTime.now().isAfter(expiryTime);
		}

		@SuppressWarnings("unchecked")
		public <T> T getTokens() {
			return (T)tokens;
		}
	}

	private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

	public void storeTokens(String key, Object tokens, int expirySeconds) {
		tokenStore.put(key, new TokenInfo((TokenResponse)tokens, expirySeconds));
	}

	public <T> T getAndRemoveTokens(String key) {
		TokenInfo info = tokenStore.remove(key);
		if (info == null || info.isExpired())
			return null;
		return info.getTokens();
	}

	// 1) 인증 코드 저장소
	private final Map<String, String> verificationCodeStore = new ConcurrentHashMap<>();

	public void storeVerificationCode(String email, String code) {
		verificationCodeStore.put(email, code);
	}

	public String getVerificationCode(String email) {
		return verificationCodeStore.get(email);
	}

	public void removeVerificationCode(String email) {
		verificationCodeStore.remove(email);
	}

	// 2) 비밀번호 재설정 토큰 저장소
	private final Map<String, String> passwordResetTokenStore = new ConcurrentHashMap<>();

	public void storePasswordResetToken(String email, String token) {
		passwordResetTokenStore.put(email, token);
	}

	public String getPasswordResetToken(String email) {
		return passwordResetTokenStore.get(email);
	}

	public void removePasswordResetToken(String email) {
		passwordResetTokenStore.remove(email);
	}

	// (선택) 주기적 만료 정리
	public TokenCacheService() {
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(600_000);
					tokenStore.entrySet().removeIf(e -> e.getValue().isExpired());
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}).start();
	}

}