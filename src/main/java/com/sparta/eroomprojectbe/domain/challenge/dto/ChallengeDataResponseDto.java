package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChallengeDataResponseDto {
    private ChallengeResponseDto data;
    private String message;
    private HttpStatus status;

    public ChallengeDataResponseDto(ChallengeResponseDto responseDto,
                                    String message, HttpStatus status){
        this.data = responseDto;
        this.message = message;
        this.status = status;
    }
}
