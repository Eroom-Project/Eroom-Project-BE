package com.sparta.eroomprojectbe.domain.auth.dto;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 선택한 챌린지에 모든 인증을 가져오는 Response Dto
 * 로그인을 한 사용자의 정보와 전체 인증글을 전달한다.
 */
@Getter
public class AuthAllResponseDto extends BaseResponseDto<AuthMemberInfoResponseDto> {
    public AuthAllResponseDto(AuthMemberInfoResponseDto data, String message, HttpStatus status){
        super(data,message,status);
    }
}
