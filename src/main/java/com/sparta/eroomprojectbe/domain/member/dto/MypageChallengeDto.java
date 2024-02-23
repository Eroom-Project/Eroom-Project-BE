package com.sparta.eroomprojectbe.domain.member.dto;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MypageChallengeDto {
    private Long challengeId;
    private String title;
    private String creatorNickname;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String frequency;
    private int currentAttendance;
    private int limitAttendance;
    private String thumbnailImageUrl;
    private ChallengerRole challengerRole;

    private MypageChallengeDto(Challenge challenge) {
        this.challengeId = challenge.getChallengeId();
        this.title = challenge.getTitle();
        this.startDate = challenge.getStartDate();
        this.dueDate = challenge.getDueDate();
        this.frequency = challenge.getFrequency();
        this.currentAttendance = challenge.getCurrentAttendance();
        this.limitAttendance = challenge.getLimitAttendance();
        this.thumbnailImageUrl = challenge.getThumbnailImageUrl();
    }

    public MypageChallengeDto(ChallengeWithRoleDto challengeWithRoleDto, String creatorNickname) {
        this(challengeWithRoleDto.getChallenge());
        this.challengerRole = challengeWithRoleDto.getChallengerRole();
        this.creatorNickname = creatorNickname;
    }
}
