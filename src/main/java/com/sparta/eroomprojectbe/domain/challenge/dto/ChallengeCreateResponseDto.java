package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 챌린지 생성하여 성공여부와 httpStatus를 봔한하는 dto
 */
@Getter
public class ChallengeCreateResponseDto extends BaseChallengeResponseDto<Void> {
    public ChallengeCreateResponseDto(String message, HttpStatus status) {
        super(null, message, status);
    }
}
