package org.cheonyakplanet.be.domain.entity.user;

import java.util.Date;

import org.cheonyakplanet.be.domain.Stamped;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(catalog = "planet", name = "user_token")
public class UserToken extends Stamped {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String accessToken;

	@Column(nullable = false)
	private String refreshToken;

	@Column(nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date accessTokenExpiry;

	@Column(nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date refreshTokenExpiry;

	// 블랙리스트 여부. true면 사용 불가능(예: 로그아웃)
	@Column(nullable = false)
	@Builder.Default
	private boolean blacklisted = false;

	public UserToken(String email, String accessToken, String refreshToken, Date accessTokenExpiry,
		Date refreshTokenExpiry) {
		this.email = email;
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.accessTokenExpiry =
			(accessTokenExpiry != null) ? accessTokenExpiry : new Date(System.currentTimeMillis() + 60 * 60 * 1000L);
		this.refreshTokenExpiry = (refreshTokenExpiry != null) ? refreshTokenExpiry :
			new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L);
	}

	public void updateTokens(String accessToken, String refreshToken, Date accessTokenExpiry, Date refreshTokenExpiry) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
		this.accessTokenExpiry =
			(accessTokenExpiry != null) ? accessTokenExpiry : new Date(System.currentTimeMillis() + 60 * 60 * 1000L);
		this.refreshTokenExpiry = (refreshTokenExpiry != null) ? refreshTokenExpiry :
			new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L);
	}

	public void blacklist() {
		this.blacklisted = true;
	}
}
