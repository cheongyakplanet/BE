package org.cheonyakplanet.be.infrastructure.security;

import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.repository.UserRepository;
import org.cheonyakplanet.be.presentation.exception.CustomException;
import org.cheonyakplanet.be.presentation.exception.ErrorCode;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;

	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		log.info("loadUserByUsername called with email: {}", email);
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.SIGN001, "일치하는 이메일 없음"));
		log.info("User found: {}", user);
		return new UserDetailsImpl(user);
	}
}
