package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리더가 인증글을 수정하는 내용을 전달하는 Dto
 */
@Getter
@NoArgsConstructor
public class AuthLeaderRequestDto {
    private String authStatus;

    public AuthLeaderRequestDto(String authStatus){
        this.authStatus = authStatus;
    }
}
