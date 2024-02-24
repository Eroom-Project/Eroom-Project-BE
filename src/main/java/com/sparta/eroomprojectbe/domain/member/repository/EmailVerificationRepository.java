package com.sparta.eroomprojectbe.domain.member.repository;

import com.sparta.eroomprojectbe.domain.member.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByEmailAndAuthCode(String email, String authCode);
}
