package com.sparta.eroomprojectbe.domain.member.dto;

/**
 * 로그인한 유저의 이메일을 반환하는 dto
 */
public class LoginResponseDto {
    private String loginMemberEmail;

    public LoginResponseDto (String loginMemberEmail){
        this.loginMemberEmail = loginMemberEmail;
    }
}
