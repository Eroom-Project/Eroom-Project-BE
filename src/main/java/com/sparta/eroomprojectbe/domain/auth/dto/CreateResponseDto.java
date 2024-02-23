package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 생성,수정,삭제를 성공여부에 상태와 메세지를 전달하는 Dto
 */
@Getter
public class CreateResponseDto {
    private  String message;
    private HttpStatus status;
    public CreateResponseDto(String message, HttpStatus status){
        this.message = message;
        this.status = status;
    }
}
