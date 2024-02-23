package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;

import java.util.List;

/**
 * 인증글에 대한 정보와 현재 로그인한 사람의 정보를 전달하는 Dto
 */
@Getter
public class AuthMemberInfoResponseDto {
    private List<AuthResponseDto> authResponseDtoList;
    private MemberInfoResponseDto memberInfoResponseDto;

    public AuthMemberInfoResponseDto(List<AuthResponseDto> authResponseDtoList, MemberInfoResponseDto memberInfoResponseDto){
        this.authResponseDtoList = authResponseDtoList;
        this.memberInfoResponseDto = memberInfoResponseDto;
    }
}
