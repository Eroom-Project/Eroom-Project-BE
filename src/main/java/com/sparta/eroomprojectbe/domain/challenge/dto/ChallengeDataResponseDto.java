package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * ChallengeResponseDto를 data라는 객체로 전해주고, 성공여부메시지와, 상태코드를 전달하는 dto
 */
@Getter
public class ChallengeDataResponseDto extends BaseChallengeResponseDto{
    private ChallengeResponseDto data;

    public ChallengeDataResponseDto(ChallengeResponseDto responseDto, String message, HttpStatus status) {
        super(message, status);
        this.data = responseDto;
    }
}
