package com.sparta.eroomprojectbe.domain.challenger.repository;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.member.dto.mypage.ChallengeWithRoleDto;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengerRepository extends JpaRepository<Challenger, Long> {


    @Query("SELECT c.member FROM Challenger c WHERE c.challenge.challengeId = :challengeId AND c.role = 'LEADER'")
    Optional<Member> findCreatorMemberByChallengeId(@Param("challengeId") Long challengeId);

    Optional<Challenger> findByChallengeAndMember(Challenge challenge, Member member);

    @Query("SELECT new com.sparta.eroomprojectbe.domain.member.dto.ChallengeWithRoleDto(c.challenge, c.role) FROM Challenger c WHERE c.member.memberId = :memberId ORDER BY c.challenge.createdAt DESC")
    List<ChallengeWithRoleDto> findAllChallengesByMemberIdOrderByChallengeCreatedAtDesc(@Param("memberId") Long memberId);

    @Query("SELECT c.member.memberId FROM Challenger c WHERE c.challenge = :challenge")
    List<Long> findMemberIdsByChallenge(@Param("challenge") Challenge challenge);

    boolean existsByChallengeAndMember(Challenge challenge, Member member);
}
