package com.sparta.eroomprojectbe.domain.member.dto.mypage;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 유저의 정보를 담은 dto
 */
@Getter
@AllArgsConstructor
public class MemberInfoDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String profileImageUrl;
    private Long bricksCount;

    public MemberInfoDto(Member member) {
        this.memberId = member.getMemberId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImageUrl();
        this.bricksCount = member.getBricksCount();
    }
}
