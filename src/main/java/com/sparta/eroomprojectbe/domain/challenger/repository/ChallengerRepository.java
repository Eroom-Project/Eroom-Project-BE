package com.sparta.eroomprojectbe.domain.challenger.repository;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengerRepository extends JpaRepository<Challenger, Long> {
    Long countByChallenge_ChallengeId(Long challengeId);

    Long countByChallenge(Challenge challenge);

    Optional<Challenger> findByChallengeAndMember(Challenge challenge, Member member);

    @Query("SELECT DISTINCT c.challenge FROM Challenger c WHERE c.member.memberId = :memberId")
    List<Challenge> findAllChallengesByMemberId(@Param("memberId") Long memberId);
}