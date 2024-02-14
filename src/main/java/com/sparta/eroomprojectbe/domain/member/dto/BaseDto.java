package com.sparta.eroomprojectbe.domain.member.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseDto<T> {
    private T data;
    private String message;
    private HttpStatus status;

    public BaseDto(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }
}