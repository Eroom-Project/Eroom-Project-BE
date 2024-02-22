package com.sparta.eroomprojectbe.domain.auth.dto;

import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import lombok.Getter;

/**
 * 로그인한 멤버의 정보를 전달하는 Dto
 */
@Getter
public class MemberInfoResponseDto {
    private ChallengerRole loginChallengeEnum;
    private Long loginMemberId;

    public MemberInfoResponseDto(ChallengerRole role, Long loginMemberId){
        this.loginChallengeEnum = role;
        this.loginMemberId = loginMemberId;
    }

}
