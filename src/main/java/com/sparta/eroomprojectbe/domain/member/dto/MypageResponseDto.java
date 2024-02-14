package com.sparta.eroomprojectbe.domain.member.dto;

import com.sparta.eroomprojectbe.domain.challenge.dto.BaseChallengeResponseDto;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import org.springframework.http.HttpStatus;

import java.util.List;

public class MypageResponseDto extends BaseChallengeResponseDto<List<DataDto>> {
    public MypageResponseDto(List<DataDto> data, String message, HttpStatus status) {
        super(data, message, status);
    }
}
