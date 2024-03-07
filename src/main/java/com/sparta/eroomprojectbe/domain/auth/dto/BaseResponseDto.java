package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 컨트롤러단에서 사용되는 Dto
 *
 * @param <T> 전달하고자 하는 Dto의 타입
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
