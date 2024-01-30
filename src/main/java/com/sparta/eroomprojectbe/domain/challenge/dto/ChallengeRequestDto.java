package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;

/**
 * 챌린지를 생성할때 요청하는 dto
 */
@Getter
public class ChallengeRequestDto {
    private String title;
    private String category;
    private String description;
    private String startDate;
    private String dueDate;
    private String frequency;
    private int limitAttendance;
    private String authExplanation;
    private String thumbnailImageUrl;

}
