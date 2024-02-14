package com.sparta.eroomprojectbe.domain.member.dto;

import lombok.Getter;

@Getter
public class ProfileRequestDto {
    private String email;
    private String password;
    private String nickname;
    private String profileImageUrl;
}
