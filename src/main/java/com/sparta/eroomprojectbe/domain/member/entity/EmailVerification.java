package com.sparta.eroomprojectbe.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Getter
@NoArgsConstructor
public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String authCode;

    @Column(nullable = false)
    private ZonedDateTime expirationTime;

    public EmailVerification(String toEmail, String authCode, ZonedDateTime expirationTime) {
        this.email = toEmail;
        this.authCode = authCode;
        this.expirationTime = expirationTime;
    }

    public void update(String authCode, ZonedDateTime expirationTime) {
        this.authCode = authCode;
        this.expirationTime = expirationTime;
    }
}
