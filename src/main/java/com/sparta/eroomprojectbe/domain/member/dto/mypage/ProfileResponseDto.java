package com.sparta.eroomprojectbe.domain.member.dto.mypage;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import lombok.Getter;

/**
 * 유저 프로필 response dto
 */
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
