package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class AuthAllResponseDto extends BaseResponseDto<List<AuthResponseDto>> {
    public AuthAllResponseDto(List<AuthResponseDto> data, String message, HttpStatus status){
        super(data,message,status);
    }
}
