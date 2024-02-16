package com.sparta.eroomprojectbe.domain.member.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class MypageResponseDto {

    private MemberInfoDto memberInfo;
    private MyroomInfoDto myroomInfo;
    private List<BricksInfoDto> bricksInfoList;
    private List<MypageChallengeDto> challengeList;

    public MypageResponseDto(MemberInfoDto memberInfo, MyroomInfoDto myroomInfoDto, List<BricksInfoDto> bricksInfoDtoList, List<MypageChallengeDto> challengeDto) {
        this.memberInfo = memberInfo;
        this.myroomInfo = myroomInfoDto;
        this.bricksInfoList = bricksInfoDtoList;
        this.challengeList = challengeDto;
    }
}
