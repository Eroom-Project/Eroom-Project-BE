package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class BaseResponseDto<T> {
    private T data;
    private String message;
    private HttpStatus status;

    public BaseResponseDto(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }
}
