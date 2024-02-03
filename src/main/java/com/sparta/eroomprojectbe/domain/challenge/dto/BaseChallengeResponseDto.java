package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseChallengeResponseDto {
    private String message;
    private HttpStatus status;

    public BaseChallengeResponseDto(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
