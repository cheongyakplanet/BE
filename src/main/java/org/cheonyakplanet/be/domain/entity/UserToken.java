package org.cheonyakplanet.be.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.cheonyakplanet.be.domain.Stamped;

import java.util.Date;


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

    public UserToken(String email, String accessToken, String refreshToken, Date accessTokenExpiry, Date refreshTokenExpiry) {
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiry = (accessTokenExpiry != null) ? accessTokenExpiry : new Date(System.currentTimeMillis() + 60 * 60 * 1000L);
        this.refreshTokenExpiry = (refreshTokenExpiry != null) ? refreshTokenExpiry : new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L);
    }

    public void updateTokens(String accessToken, String refreshToken, Date accessTokenExpiry, Date refreshTokenExpiry) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.accessTokenExpiry = (accessTokenExpiry != null) ? accessTokenExpiry : new Date(System.currentTimeMillis() + 60 * 60 * 1000L);
        this.refreshTokenExpiry = (refreshTokenExpiry != null) ? refreshTokenExpiry : new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L);
    }

    public void blacklist() {
        this.blacklisted = true;
    }
}
