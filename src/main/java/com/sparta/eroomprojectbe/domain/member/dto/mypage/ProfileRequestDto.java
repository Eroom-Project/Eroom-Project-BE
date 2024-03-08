package com.sparta.eroomprojectbe.domain.member.dto.mypage;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

/**
 * 유저 프로필 request dto
 */
@Getter
public class ProfileRequestDto {
    private String email;
    private String password;
    private String nickname;
    private MultipartFile file;
}
