package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

/**
 * 전체 챌린지를 조회하여 반환하는 dto
 */
@Getter
public class AllResponseDto extends BaseResponseDto<Page<ChallengeResponseDto>> {

    public AllResponseDto(Page<ChallengeResponseDto> data, String message, HttpStatus status) {
        super(data, message, status);
    }

}
