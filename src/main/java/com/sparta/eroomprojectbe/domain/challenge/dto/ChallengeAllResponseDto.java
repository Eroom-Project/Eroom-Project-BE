package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * 전체 챌린지를 조회하여 반환하는 dto
 */
@Getter
public class ChallengeAllResponseDto extends BaseChallengeResponseDto{
    private List<ChallengeResponseDto> data;

    public ChallengeAllResponseDto(List<ChallengeResponseDto> challengeResponseDtoList, String message, HttpStatus status) {
        super(message, status);
        this.data = challengeResponseDtoList;
    }
}
