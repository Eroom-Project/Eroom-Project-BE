package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ChallengeResponseDto를 data라는 객체로 전해주고, 성공여부메시지와, 상태코드를 전달하는 dto
 */
@Getter
public class ChallengeDataResponseDto {
    private ChallengeResponseDto data;
    private String message;
    private HttpStatus status;

    public ChallengeDataResponseDto(ChallengeResponseDto responseDto,
                                    String message, HttpStatus status) {
        this.data = responseDto;
        this.message = message;
        this.status = status;
    }
}
