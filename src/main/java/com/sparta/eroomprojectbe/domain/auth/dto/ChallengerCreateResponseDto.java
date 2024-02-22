package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChallengerCreateResponseDto {
    private  String message;
    private HttpStatus status;
    public ChallengerCreateResponseDto(String message, HttpStatus status){
        this.message = message;
        this.status = status;
    }
}
