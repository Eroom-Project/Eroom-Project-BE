package com.sparta.eroomprojectbe.global;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@RedisHash(value = "jwtToken", timeToLive = 60*60*24*7)
public class RefreshToken {
    @Id
    private Long refreshTokenId;

    private String refreshToken;

    private String keyEmail;

    public RefreshToken(String keyEmail, String refreshToken) {
        this.keyEmail = keyEmail;
        this.refreshToken = refreshToken;
    }

    public void updateToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
    }
}
