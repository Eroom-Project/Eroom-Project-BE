package com.sparta.eroomprojectbe.global;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@Table(name = "T_REFRESH_TOKEN")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long refreshTokenId;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private String keyEmail;

    @Column(nullable = false)
    private Long expiration;

    public RefreshToken(String keyEmail, String refreshToken) {
        this.keyEmail = keyEmail;
        this.refreshToken = refreshToken;
        // expiration 초기화 로직 추가 필요
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    // updateToken 메소드 추가 필요
    public RefreshToken updateToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        // expiration 업데이트 로직 추가 필요
        return this;
    }
}
