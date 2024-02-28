package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ChallengeUpdateDto extends BaseResponseDto<ChallengeLoginResponseDto> {
    public ChallengeUpdateDto(ChallengeLoginResponseDto data, String message, HttpStatus status){
        super(data,message,status);
    }
}
