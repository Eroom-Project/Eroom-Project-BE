package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 컨트롤러에서 정보를 주고받는 기본 Dto
 * @param <T> 주고받을 정보의 dto
 */
@Getter
public class BaseResponseDto<T> {
    private T data;
    private String message;
    private HttpStatus status;

    public BaseResponseDto(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }
}