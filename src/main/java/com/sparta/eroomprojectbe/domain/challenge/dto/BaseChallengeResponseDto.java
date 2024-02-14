package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseChallengeResponseDto<T> {
    private T data;
    private String message;
    private HttpStatus status;

    public BaseChallengeResponseDto(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }
}