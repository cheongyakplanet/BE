package org.cheonyakplanet.be.application.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
	@Schema(description = "발급된 Access Token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
	private final String accessToken;

	@Schema(description = "발급된 Refresh Token", example = "dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4=")
	private final String refreshToken;
}
