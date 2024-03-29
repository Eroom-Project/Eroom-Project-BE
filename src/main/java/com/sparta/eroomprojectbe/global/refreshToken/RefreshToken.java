package com.sparta.eroomprojectbe.global.refreshToken;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@RedisHash(value = "jwtRefreshToken", timeToLive = 60*60*24*7)
public class RefreshToken {
    @Id
    private String keyEmail;

    private String refreshToken;

    @TimeToLive
    private long expiration; // 초 단위

    public RefreshToken(String keyEmail, String refreshToken) {
        this.keyEmail = keyEmail;
        this.refreshToken = refreshToken;
        this.expiration = 604800L;
    }

    public void updateToken(String newRefreshToken) {
        this.refreshToken = newRefreshToken;
        this.expiration = 604800L;
    }
}
