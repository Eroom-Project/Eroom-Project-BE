package com.sparta.eroomprojectbe.domain.challenge.dto;

import lombok.Getter;

@Getter
public class ChallengeRequestDto {
    private String title;
    private String category;
    private String description;
    private String startDate;
    private String dueDate;
    private String frequency;
    private short limitAttendance;
    private String authExplanation;
    private String thumbnailImageUrl;

}
