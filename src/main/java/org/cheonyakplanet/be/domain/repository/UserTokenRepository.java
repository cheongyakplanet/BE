package org.cheonyakplanet.be.domain.repository;

import java.util.Optional;

import org.cheonyakplanet.be.domain.entity.user.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTokenRepository extends JpaRepository<UserToken, Long> {

	Optional<UserToken> findByEmail(String token);

	Optional<UserToken> findByRefreshToken(String token);

	Optional<UserToken> findByAccessToken(String accessToken);

	void deleteByEmail(String email);
}
