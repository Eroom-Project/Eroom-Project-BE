package com.sparta.eroomprojectbe.domain.challenge.repository;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeRepository extends JpaRepository<Challenge,Long> {
}