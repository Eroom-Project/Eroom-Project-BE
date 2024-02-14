package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import org.checkerframework.checker.units.qual.A;
import org.springframework.http.HttpStatus;

@Getter
public class AuthDataResponseDto extends BaseResponseDto<AuthResponseDto>{
    public AuthDataResponseDto(AuthResponseDto data, String message, HttpStatus status){
        super(data, message, status);
    }
}
