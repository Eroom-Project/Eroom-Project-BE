package com.sparta.eroomprojectbe.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoDto {
    private Long id;
    private String nickname;
    private String email;
    private String profileImageUrl;

    public KakaoUserInfoDto(Long id, String nickname, String email, String profileImageUrl) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }
}