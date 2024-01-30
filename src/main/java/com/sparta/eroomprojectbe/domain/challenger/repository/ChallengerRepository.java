package com.sparta.eroomprojectbe.domain.challenger.repository;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengerRepository extends JpaRepository<Challenger, Long> {

    long countByChallenge_ChallengeId(Long challengeId);

    Long countByChallenge(Challenge challenge);
}
