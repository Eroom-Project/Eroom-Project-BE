package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AuthRequestDto {
    private String authContents;
    private String authImageUrl;
    private String authVideoUrl;
    private String authStatus;
}
