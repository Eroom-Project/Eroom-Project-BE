package com.sparta.eroomprojectbe.domain.chat.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 컨트롤러에서 정보를 주고받는 기본 Dto
 * @param <T> 주고받을 정보의 dto
 */
@Getter
public class ResponseDto<T> {
    private T data;
    private String message;
    private HttpStatus status;

    public ResponseDto(T data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }
}
