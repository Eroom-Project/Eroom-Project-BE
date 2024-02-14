package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AuthAllResponseDto extends BaseResponseDto<AuthMemberInfoResponseDto> {
    public AuthAllResponseDto(AuthMemberInfoResponseDto data, String message, HttpStatus status){
        super(data,message,status);
    }
}
