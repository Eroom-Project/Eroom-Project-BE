package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;

@Getter
public class ChallengeLoginResponseDto {
    private ChallengeResponseDto responseDto;
    private String loginMemberId;
    public ChallengeLoginResponseDto(ChallengeResponseDto challengeResponseDto, String loginMemberId) {
        this.responseDto = challengeResponseDto;
        this.loginMemberId = loginMemberId;
    }
}
