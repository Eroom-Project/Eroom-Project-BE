package com.sparta.eroomprojectbe.domain.member.dto.mypage;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import lombok.Getter;

/**
 * 참여하는 챌린지와 챌린지에서의 role (LEADER or CHALLENGER)
 */
@Getter
public class ChallengeWithRoleDto {
    private Challenge challenge;
    private ChallengerRole challengerRole;

    public ChallengeWithRoleDto(Challenge challenge, ChallengerRole challengerRole) {
        this.challenge = challenge;
        this.challengerRole = challengerRole;
    }

    public Long getChallengeId() {
        return challenge.getChallengeId();
    }
}
