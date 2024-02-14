package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class AuthMemberInfoResponseDto {
    private List<AuthResponseDto> authResponseDtoList;
    private MemberInfoResponseDto memberInfoResponseDto;

    public AuthMemberInfoResponseDto(List<AuthResponseDto> authResponseDtoList, MemberInfoResponseDto memberInfoResponseDto){
        this.authResponseDtoList = authResponseDtoList;
        this.memberInfoResponseDto = memberInfoResponseDto;
    }
}
