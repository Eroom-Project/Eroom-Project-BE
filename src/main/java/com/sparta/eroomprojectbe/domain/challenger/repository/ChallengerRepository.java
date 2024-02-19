package com.sparta.eroomprojectbe.domain.challenger.repository;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.member.dto.ChallengeWithRoleDto;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ChallengerRepository extends JpaRepository<Challenger, Long> {
    Challenger findByChallengeIdAndMemberId(Long challengeId, Long memberId);

    Long countByChallenge_ChallengeId(Long challengeId);

    Long countByChallenge(Challenge challenge);

    @Query("SELECT c.member.memberId FROM Challenger c WHERE c.challenge.challengeId = :challengeId AND c.role = 'LEADER'")
    Optional<Long> findCreatorMemberIdByChallengeId(@Param("challengeId") Long challengeId);


    Optional<Challenger> findByChallengeAndMember(Challenge challenge, Member member);

    @Query("SELECT new com.sparta.eroomprojectbe.domain.member.dto.ChallengeWithRoleDto(c.challenge, c.role) FROM Challenger c WHERE c.member.memberId = :memberId")
    List<ChallengeWithRoleDto> findAllChallengesByMemberId(@Param("memberId") Long memberId);

    boolean existsByChallengeAndMember(Challenge challenge, Member member);

}


