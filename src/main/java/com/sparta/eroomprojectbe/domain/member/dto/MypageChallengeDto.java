package com.sparta.eroomprojectbe.domain.member.dto;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;

import java.time.LocalDate;

public class MypageChallengeDto {
    private Long challengeId;
    private String title;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String frequency;
    private int currentAttendance;
    private int limitAttendance;
    private String thumbnailImageUrl;

    public MypageChallengeDto(Challenge challenge) {
        this.challengeId = challenge.getChallengeId();
        this.title = challenge.getTitle();
        this.startDate = challenge.getStartDate();
        this.dueDate = challenge.getDueDate();
        this.frequency = challenge.getFrequency();
        this.currentAttendance = challenge.getCurrentAttendance();
        this.limitAttendance = challenge.getLimitAttendance();
        this.thumbnailImageUrl = challenge.getThumbnailImageUrl();
    }
}
