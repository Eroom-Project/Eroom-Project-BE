package com.sparta.eroomprojectbe.domain.member.dto;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.global.rollenum.MemberRoleEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupResponseDto {
    private Long memberId;
    private String email;
    private String nickname;
    private MemberRoleEnum role;

    public SignupResponseDto(Member member){
        this.memberId = member.getMemberId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.role = member.getRole();
    }
}
