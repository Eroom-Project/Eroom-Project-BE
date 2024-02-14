package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * 전체 챌린지를 조회하여 반환하는 dto
 */
@Getter
public class ChallengeAllResponseDto extends BaseChallengeResponseDto<List<ChallengeResponseDto>> {
    public ChallengeAllResponseDto(List<ChallengeResponseDto> data, String message, HttpStatus status) {
        super(data, message, status);
    }
}
