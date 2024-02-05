package com.sparta.eroomprojectbe.domain.challenger.repository;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChallengerRepository extends JpaRepository<Challenger, Long> {
    Long countByChallenge_ChallengeId(Long challengeId);

    Long countByChallenge(Challenge challenge);

    @Query("SELECT c.member.memberId FROM Challenger c WHERE c.challenge.challengeId = :challengeId AND c.role = 'LEADER'")
    Optional<Long> findCreatorMemberIdByChallengeId(@Param("challengeId") Long challengeId);
}
