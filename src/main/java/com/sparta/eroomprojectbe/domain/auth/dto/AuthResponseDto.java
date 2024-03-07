package com.sparta.eroomprojectbe.domain.auth.dto;

import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 인증글에 대한 정보를 전달하는 Dto
 */
@Getter
public class AuthResponseDto {
    private Long authId;
    private String authContents;
    private String nickname;
    private String authImageUrl;
    private String authVideoUrl;
    private String authStatus;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long challengeId;
    private Long memberId;

    public AuthResponseDto(Auth auth) {
        this.authId = auth.getAuthId();
        this.nickname = auth.getChallenger().getMember().getNickname();
        this.authContents = auth.getAuthContents();
        this.authImageUrl = auth.getAuthImageUrl();
        this.authVideoUrl = auth.getAuthVideoUrl();
        this.authStatus = auth.getAuthStatus();
        this.createdAt = auth.getCreatedAt();
        this.modifiedAt = auth.getModifiedAt();
        this.challengeId = auth.getChallenger().getChallenge().getChallengeId();
        this.memberId = auth.getChallenger().getMember().getMemberId();
    }
}
