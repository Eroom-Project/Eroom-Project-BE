package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor
public class AuthRequestDto {
    private String authContents;
    private String authVideoUrl;
    private MultipartFile file;
    private String authStatus;
}
