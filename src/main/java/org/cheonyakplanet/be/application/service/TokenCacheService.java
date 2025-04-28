package org.cheonyakplanet.be.application.service;

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

		public TokenResponse getTokens() {
			return tokens;
		}
	}

	// 상태 코드를 키로 사용하는 토큰 저장소
	private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

	// 주기적으로 만료된 토큰 정리 (선택 사항)
	public TokenCacheService() {
		// 10분마다 만료된 토큰 정리
		new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(600000); // 10분
					cleanupExpiredTokens();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}).start();
	}

	// 토큰 저장
	public void storeTokens(String stateCode, TokenResponse tokens, int expirySeconds) {
		tokenStore.put(stateCode, new TokenInfo(tokens, expirySeconds));
	}

	// 토큰 조회 및 삭제
	public TokenResponse getAndRemoveTokens(String stateCode) {
		TokenInfo tokenInfo = tokenStore.remove(stateCode);

		if (tokenInfo == null) {
			return null;
		}

		if (tokenInfo.isExpired()) {
			return null;
		}

		return tokenInfo.getTokens();
	}

	// 만료된 토큰 정리
	private void cleanupExpiredTokens() {
		tokenStore.entrySet().removeIf(entry -> entry.getValue().isExpired());
	}
}
