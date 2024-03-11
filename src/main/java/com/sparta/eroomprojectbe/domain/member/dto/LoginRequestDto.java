package com.sparta.eroomprojectbe.domain.member.dto;

import lombok.Getter;

/**
 * 로그인 request dto
 */
@Getter
public class LoginRequestDto {
    private String email;
    private String password;
}
