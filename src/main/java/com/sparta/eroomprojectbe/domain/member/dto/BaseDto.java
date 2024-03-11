package com.sparta.eroomprojectbe.domain.member.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 일관성 있는 응답을 위한 base dto
 * @param <T>
 */
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

    public BaseDto(String message, T data) {
        this.message = message;
        this.data = data;
    }
}