package com.sparta.eroomprojectbe.domain.member.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "Mail Verification")
public class EmailService {

    private final JavaMailSender emailSender;


    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    /**
     * 이메일 전송 서비스 메서드
     *
     * @param sendEmail 보내는 사람
     * @param toEmail 받는 사람
     * @param title 메일 제목
     * @param content 메일 내용
     */
    public void sendEmail(String sendEmail, String toEmail, String title, String content) {

        // HTML 형식의 이메일 전송하기 위한 설정
        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(sendEmail);
            helper.setTo(toEmail);
            helper.setSubject(title);
            // true로 설정해야 html을 읽어내 전송. 그렇지 않으면 html도 단순 텍스트로 전송
            helper.setText(content, true);
            emailSender.send(message);
        } catch (MessagingException | jakarta.mail.MessagingException e) {
            log.error("이메일 전송 실패: 수신자={}, 제목={}, 에러메시지={}", toEmail, title, e.getMessage());
        }
    }
}
