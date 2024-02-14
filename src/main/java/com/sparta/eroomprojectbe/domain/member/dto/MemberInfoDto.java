package com.sparta.eroomprojectbe.domain.member.dto;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfoDto {
    private Long memberId;
    private String email;
    private String nickname;
    private String profileImageUrl;

    public MemberInfoDto(Member member) {
        this.memberId = member.getMemberId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.profileImageUrl = member.getProfileImageUrl();
    }
}
