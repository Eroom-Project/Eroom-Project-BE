package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 인증글을 작성할 때 요청하는 정보를 전달하는 Dto
 */
@Getter
@NoArgsConstructor
public class AuthRequestDto {
    private String authContents;
    private String authVideoUrl;
    private MultipartFile file;
    private String authStatus;
}
