package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChallengerCreateResponseDto extends BaseResponseDto<Void> {
    public ChallengerCreateResponseDto(String message, HttpStatus status){
        super(null, message, status);
    }
}
