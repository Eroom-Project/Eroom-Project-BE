package com.sparta.eroomprojectbe.global;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long refreshTokenId;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String keyEmail;

    public RefreshToken(String refreshToken, String keyEmail) {
        this.refreshToken = refreshToken;
        this.keyEmail = keyEmail;
    }

    // updateToken 메소드 추가 필요
    public RefreshToken updateToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        return this;
    }
}
