package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChallengeCreateResponseDto {
    private String message;
    private HttpStatus status;

    public ChallengeCreateResponseDto(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
