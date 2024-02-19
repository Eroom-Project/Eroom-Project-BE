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

    @Column(nullable = false, unique = true)
    private String keyEmail;

    public RefreshToken(String refreshToken, String keyEmail) {
        this.refreshToken = refreshToken;
        this.keyEmail = keyEmail;
    }

    public void updateToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }
}
