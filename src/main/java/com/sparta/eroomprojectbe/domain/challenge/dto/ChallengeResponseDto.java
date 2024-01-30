package com.sparta.eroomprojectbe.domain.challenge.dto;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

/**
 * 선택한 챌린지를 조회하여 반환하는 dto
 */
@Getter
public class ChallengeResponseDto {

    private Long challengeId;
    private Long memberId;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String frequency;
    private Long currentAttendance;
    private int limitAttendance;
    private String authExplanation;
    private String thumbnailImageUrl;

    public ChallengeResponseDto(Challenge challenge, Long currentAttendance) {
        this.challengeId = challenge.getChallengeId();
        this.title = challenge.getTitle();
        this.description = challenge.getDescription();
        this.startDate = challenge.getStartDate();
        this.dueDate = challenge.getDueDate();
        this.frequency = challenge.getFrequency();
        this.currentAttendance = currentAttendance;
        this.limitAttendance = challenge.getLimitAttendance();
        this.authExplanation = challenge.getAuthExplanation();
        this.thumbnailImageUrl = challenge.getThumbnailImageUrl();
    }

}
