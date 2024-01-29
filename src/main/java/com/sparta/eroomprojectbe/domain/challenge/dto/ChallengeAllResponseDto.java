package com.sparta.eroomprojectbe.domain.challenge.dto;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

/**
 * 전체 챌린지를 조회하여 반환하는 dto
 */
@Getter
public class ChallengeAllResponseDto {
    private List<Challenge> data;
    private String message;
    private HttpStatus status;

    public ChallengeAllResponseDto(List<Challenge> allChallenges, String message, HttpStatus status) {
        this.data = allChallenges;
        this.message = message;
        this.status = status;
    }
}
