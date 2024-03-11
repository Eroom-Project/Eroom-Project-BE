package com.sparta.eroomprojectbe.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 메일 전송 관련 설정
 * 구글 smtp 프로토콜 사용
 */
@Configuration
public class EmailConfig {
    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender() {
        Properties mailProperties = new Properties();
        mailProperties.put("mail.transport.protocol", "smtp"); // 구글의 smtp 프로토콜로 전송
        mailProperties.put("mail.smtp.auth", "true"); // smtp 서버에 이메일을 보낼 때 인증이 필요함. 무단 액세스로부터 계정 보호
        mailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // 메일 전송에 ssl 기반 소켓 사용하는 설정
        mailProperties.put("mail.smtp.starttls.enable", "true"); // tls 관련 설정. 메일 클라이언트와 서버 사이 연결 암호화. 데이터 안전 전송
        mailProperties.put("mail.smtp.debug", "true"); // 디버깅 가능
        mailProperties.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // ssl 인증서 유효함을 인증함
        mailProperties.put("mail.smtp.ssl.protocols", "TLSv1.2"); // tls 1.2 버전 사용하여 ssl 보안 프로토콜 지정

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setJavaMailProperties(mailProperties);
        mailSender.setHost(host);
        mailSender.setPort(port);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");

        return mailSender;
    }
}
