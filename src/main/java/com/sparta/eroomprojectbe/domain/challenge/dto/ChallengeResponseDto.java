package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChallengeResponseDto {
    private String message;
    private HttpStatus status;

    public ChallengeResponseDto(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
