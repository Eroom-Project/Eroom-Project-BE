package com.sparta.eroomprojectbe.domain.auth.service;

import com.sparta.eroomprojectbe.domain.auth.dto.AuthRequestDto;
import com.sparta.eroomprojectbe.domain.auth.dto.AuthResponseDto;
import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import com.sparta.eroomprojectbe.domain.auth.repository.AuthRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    public AuthService(AuthRepository authRepository){this.authRepository=authRepository;}

    public List<AuthResponseDto> getMemberAuthList(Long challengerId)  { // 해당 챌린지 인증(member) 전체 조회
        List<Auth> authList = authRepository.findAllByAuthIdOrderByCreatedAtDesc(challengerId);
        List<AuthResponseDto> authResponseList = authList.stream().map(AuthResponseDto::new).toList();
        return authResponseList;
    }

    public AuthResponseDto updateLeaderAuth(AuthRequestDto requestDto, Long challengerId, Long authId) { // 챌린지 인증 허가 및 불가 처리(leader)
        return null;
    }

    public AuthResponseDto updateMemberAuth(AuthRequestDto requestDto, Long challengerId) { // 챌린지 인증 수정(member)
       return null;
    }

    public AuthResponseDto createMemberAuth(AuthRequestDto requestDto, Long challengerId) { // 챌린지 인증(member)
        return null;
    }

    public AuthResponseDto createChallenger(AuthRequestDto requestDto, Long challengerId) { //챌린지 신청
        return null;
    }
}
