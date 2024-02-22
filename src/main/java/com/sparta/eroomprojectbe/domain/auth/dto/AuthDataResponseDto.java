package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 인증글에 대한 정보와 메세지, 상태를 전달하는 Dto
 */
@Getter
public class AuthDataResponseDto extends BaseResponseDto<AuthResponseDto>{
    public AuthDataResponseDto(AuthResponseDto data, String message, HttpStatus status){
        super(data, message, status);
    }
}
