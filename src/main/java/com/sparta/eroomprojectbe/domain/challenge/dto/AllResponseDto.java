package com.sparta.eroomprojectbe.domain.challenge.dto;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * 전체 챌린지를 조회하여 반환하는 dto
 */
@Getter
public class AllResponseDto extends BaseResponseDto<Page<ChallengeResponseDto>> {
    private Page<Challenge> latestChallengesPage;
    public AllResponseDto(Page<ChallengeResponseDto> data, String message, HttpStatus status) {
        super(data, message, status);
    }

//    public AllResponseDto(List<ChallengeResponseDto> data, String message, HttpStatus status, Page<Challenge> latestChallengesPage) {
//        super(data, message, status);
//        this.latestChallengesPage = latestChallengesPage;
//    }
}
