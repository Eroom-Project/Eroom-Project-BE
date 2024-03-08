package com.sparta.eroomprojectbe.domain.member.dto.mypage;

import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ProfileRequestDto {
    private String email;
    private String password;
    private String nickname;
    private MultipartFile file;
}
