package com.sparta.eroomprojectbe.domain.auth.repository;

import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthRepository extends JpaRepository<Auth, Long> {
    List<Auth> findAllByAuthIdOrderByCreatedAtDesc(Long challengerId); //해당 챌린지 최신순 정렬
}
