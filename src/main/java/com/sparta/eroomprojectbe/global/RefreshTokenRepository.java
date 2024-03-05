package com.sparta.eroomprojectbe.global;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

@EnableRedisRepositories
@Configuration
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
