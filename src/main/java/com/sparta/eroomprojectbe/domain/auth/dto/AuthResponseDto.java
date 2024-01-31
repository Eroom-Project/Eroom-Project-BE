package com.sparta.eroomprojectbe.domain.auth.dto;

import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
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


}
