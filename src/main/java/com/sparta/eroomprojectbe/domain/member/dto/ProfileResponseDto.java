package com.sparta.eroomprojectbe.domain.member.dto;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class ProfileResponseDto {
    private String email;
    private String nickname;
    private String profileImageUrl;

    public ProfileResponseDto(Member findMember) {
        this.email = findMember.getEmail();
        this.nickname = findMember.getNickname();
        this.profileImageUrl = findMember.getProfileImageUrl();
    }
}
