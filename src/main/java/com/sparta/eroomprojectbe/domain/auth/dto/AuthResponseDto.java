package com.sparta.eroomprojectbe.domain.auth.dto;

import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class AuthResponseDto {
    private Long authId;
    private String authContents;
    private String authImageUrl;
    private String authVideoUrl;
    private String authStatus;
    private LocalDateTime createdAt;
    private Long challengeId;

    public AuthResponseDto(Auth auth){
        this.authId = auth.getAuthId();
        this.authContents = auth.getAuthContents();
        this.authImageUrl = auth.getAuthImageUrl();
        this.authVideoUrl = auth.getAuthVideoUrl();
        this.authStatus = auth.getAuthStatus();
        this.createdAt = auth.getCreatedAt();
        this.challengeId = auth.getChallenger().getChallenge().getChallengeId();
    }
}
