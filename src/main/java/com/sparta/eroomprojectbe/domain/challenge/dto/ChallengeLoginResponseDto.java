package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;

/**
 * 로그인한 멤버의 정보와 챌린지 정보를 전달하는 Dto
 */
@Getter
public class ChallengeLoginResponseDto {
    private ChallengeResponseDto responseDto;
    private String loginMemberId;
    private String loginMemberProfileImageUrl;
    private String loginMemberNickname;

    public ChallengeLoginResponseDto(ChallengeResponseDto challengeResponseDto, String loginMemberId) {
        this.responseDto = challengeResponseDto;
        this.loginMemberId = loginMemberId;
    }

    public ChallengeLoginResponseDto(ChallengeResponseDto challengeResponseDto, String loginMemberId,
                                     String loginMemberProfileImageUrl, String loginMemberNickname) {
        this.responseDto = challengeResponseDto;
        this.loginMemberId = loginMemberId;
        this.loginMemberProfileImageUrl = loginMemberProfileImageUrl;
        this.loginMemberNickname = loginMemberNickname;
    }
}
