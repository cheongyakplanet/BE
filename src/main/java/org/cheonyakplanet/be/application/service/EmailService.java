package org.cheonyakplanet.be.application.service;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.cheonyakplanet.be.domain.entity.user.User;
import org.cheonyakplanet.be.domain.exception.CustomException;
import org.cheonyakplanet.be.domain.exception.ErrorCode;
import org.cheonyakplanet.be.domain.repository.UserRepository;
import org.cheonyakplanet.be.infrastructure.cache.TokenCacheService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;

	private final TokenCacheService tokenCacheService;
	private final UserRepository userRepository;

	public void sendVerificationCode(String toEmail, String code) {

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("[청약 인사이트] 비밀번호 찾기 인증 코드");
		message.setText("인증 코드: " + code);

		mailSender.send(message);
		tokenCacheService.storeVerificationCode(toEmail, code);
	}

	public CompletableFuture<Void> sendVerificationCodeAsync(String toEmail, String code) {
		return CompletableFuture.runAsync(() -> sendVerificationCode(toEmail, code));
	}

	public boolean verifyCode(String email, String inputCode) {
		String saved = tokenCacheService.getVerificationCode(email);
		if (saved == null) {
			return false;
		}
		if (saved.equals(inputCode)) {
			tokenCacheService.removeVerificationCode(email);
			return true;
		}
		return false;
	}

	public boolean checkEmailDuplicate(String email) {
		return userRepository.findByEmail(email).isPresent();
	}

	public String generateVerificationCode() {
		int code = new Random().nextInt(900_000) + 100_000;
		return String.valueOf(code);
	}

	public void sendPasswordResetEmail(String email, String resetToken) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new CustomException(ErrorCode.USER001, "사용자를 찾을 수 없습니다."));

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(email);
		msg.setSubject("[청약 인사이트] 비밀번호 재설정 인증 토큰");
		msg.setText("비밀번호 재설정 인증 토큰: " + resetToken);

		mailSender.send(msg);
		tokenCacheService.storePasswordResetToken(email, resetToken);
	}

}
