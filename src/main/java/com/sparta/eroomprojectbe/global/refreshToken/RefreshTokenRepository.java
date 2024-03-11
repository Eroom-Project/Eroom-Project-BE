package com.sparta.eroomprojectbe.global.refreshToken;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

@EnableRedisRepositories
@Configuration
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
