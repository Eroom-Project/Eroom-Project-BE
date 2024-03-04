package com.sparta.eroomprojectbe.global;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@RedisHash(value = "jwtRefreshToken", timeToLive = 60*60*24*7)
public class RefreshToken {
    @Id
    private String id;

    private String refreshToken;

    @Indexed
    private String keyEmail;

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
