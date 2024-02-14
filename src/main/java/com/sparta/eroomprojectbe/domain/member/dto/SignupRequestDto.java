package com.sparta.eroomprojectbe.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,15}$",
//            ^(?=.*[a-zA-Z])(?=.*\d)[A-Za-z\d@$!%*?&]{8,15}$
//            ^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,15}$
            message = "최소 8자 이상, 15자 이하이며 알파벳, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;

    @NotBlank
    private String nickname;
}
