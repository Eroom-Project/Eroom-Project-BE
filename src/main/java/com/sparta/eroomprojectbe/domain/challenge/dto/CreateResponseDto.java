package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 챌린지 생성하여 성공여부와 httpStatus를 봔한하는 dto
 */
@Getter
public class CreateResponseDto {
    private String message;
    private HttpStatus status;

    public CreateResponseDto(String message, HttpStatus status) {
        this.message = message;
        this.status = status;
    }
}
