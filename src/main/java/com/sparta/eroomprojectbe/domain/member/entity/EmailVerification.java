package com.sparta.eroomprojectbe.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

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
    private LocalDateTime expirationTime;

    public EmailVerification(String toEmail, String authCode, LocalDateTime expirationTime) {
        this.email = toEmail;
        this.authCode = authCode;
        this.expirationTime = expirationTime;
    }
}
