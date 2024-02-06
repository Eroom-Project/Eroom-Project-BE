package com.sparta.eroomprojectbe.domain.auth.repository;

import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    List<Auth> findAllByChallengerOrderByCreatedAtDesc(Challenger challenger); //해당 챌린지 인증 최신순 정렬

    List<Auth> findAllByOrderByCreatedAtDesc();
}
