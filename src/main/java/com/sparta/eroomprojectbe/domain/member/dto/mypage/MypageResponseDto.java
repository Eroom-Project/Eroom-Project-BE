package com.sparta.eroomprojectbe.domain.member.dto.mypage;

import lombok.Getter;

import java.util.List;

/**
 * 마이페이지 조회에 필요한 dto (유저 정보 및 챌린지 리스트)
 */
@Getter
public class MypageResponseDto {

    private MemberInfoDto memberInfo;
    private List<MypageChallengeDto> challengeList;

    public MypageResponseDto(MemberInfoDto memberInfo, List<MypageChallengeDto> challengeDto) {
        this.memberInfo = memberInfo;
        this.challengeList = challengeDto;
    }
}
