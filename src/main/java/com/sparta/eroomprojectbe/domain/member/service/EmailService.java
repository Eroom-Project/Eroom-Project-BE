package com.sparta.eroomprojectbe.domain.member.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendEmail(String sendEmail, String toEmail, String title, String content) {

        // HTML 형식의 이메일 전송하기 위한 설정
        MimeMessage message = emailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(sendEmail);
            helper.setTo(toEmail);
            helper.setSubject(title);
            // true로 전달해야 html형식으로 전송. 그렇지 않으면 단순 텍스트로 전송
            helper.setText(content, true);
            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
