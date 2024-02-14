package com.sparta.eroomprojectbe.global;

import io.jsonwebtoken.Claims;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByKeyEmail(String userEmail);
}