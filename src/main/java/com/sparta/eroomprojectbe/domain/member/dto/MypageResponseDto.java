package com.sparta.eroomprojectbe.domain.member.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MypageResponseDto {

    private MemberInfoDto memberInfo;
    private List<MypageChallengeDto> challengeList;

    public MypageResponseDto(MemberInfoDto memberInfo, List<MypageChallengeDto> challengeDto) {
        this.memberInfo = memberInfo;
        this.challengeList = challengeDto;
    }
}
