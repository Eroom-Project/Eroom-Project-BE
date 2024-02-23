package com.sparta.eroomprojectbe.domain.challenge.dto;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * 선택한 챌린지를 조회하여 반환하는 dto
 */
@Getter
public class ChallengeResponseDto {

    private Long challengeId;
    private String nickname;
    private Long createMemberId;
    private List<Long> currentMemberIdList;
    private String category;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private String frequency;
    private short currentAttendance;
    private int limitAttendance;
    private String authExplanation;
    private String thumbnailImageUrl;


    public ChallengeResponseDto(Challenge challenge, Member member, List<Long> currentMemberIds) {
        this.challengeId = challenge.getChallengeId();
        this.createMemberId = member.getMemberId();
        this.nickname = member.getNickname();
        this.currentMemberIdList = currentMemberIds;
        this.title = challenge.getTitle();
        this.category = challenge.getCategory();
        this.description = challenge.getDescription();
        this.startDate = challenge.getStartDate();
        this.dueDate = challenge.getDueDate();
        this.frequency = challenge.getFrequency();
        this.currentAttendance = challenge.getCurrentAttendance();
        this.limitAttendance = challenge.getLimitAttendance();
        this.authExplanation = challenge.getAuthExplanation();
        this.thumbnailImageUrl = challenge.getThumbnailImageUrl();
    }
}
