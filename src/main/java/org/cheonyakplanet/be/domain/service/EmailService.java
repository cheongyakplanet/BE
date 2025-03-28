package org.cheonyakplanet.be.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;

  public void sendVerificationCode(String toEmail, String code) {

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(toEmail);
    message.setSubject("[청약 인사이트] 비밀번호 찾기 인증 코드");
    message.setText("인증 코드: " + code);

    mailSender.send(message);
  }
}
