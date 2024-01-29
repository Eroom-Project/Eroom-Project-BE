package com.sparta.eroomprojectbe.domain.challenge.dto;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChallengeResponseDto {

    private Challenge data;
    private String message;
    private HttpStatus status;

    public ChallengeResponseDto(Challenge data, String message, HttpStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

}
