package com.sparta.eroomprojectbe.domain.auth.service;

import com.sparta.eroomprojectbe.domain.auth.dto.AuthRequestDto;
import com.sparta.eroomprojectbe.domain.auth.dto.AuthResponseDto;
import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import com.sparta.eroomprojectbe.domain.auth.repository.AuthRepository;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final ChallengerRepository challengerRepository;
    public AuthService(AuthRepository authRepository, ChallengerRepository challengerRepository){
        this.authRepository=authRepository;
        this.challengerRepository=challengerRepository;
    }

    public List<AuthResponseDto> getMemberAuthList()  { // 챌린지 인증(member) 전체 조회
        List<Auth> authList = authRepository.findAllByOrderByCreatedAtDesc();
        List<AuthResponseDto> authResponseList = authList.stream().map(AuthResponseDto::new).toList();
        return authResponseList;
    }

    public List<AuthResponseDto> getChallengerAuthList(Long challengerId)  { // 해당 챌린지 인증(member) 전체 조회
        // Challenger DB에 존재하는 Challenger 인지 확인
        Challenger challenger = challengerRepository.findById(challengerId)
                .orElseThrow(IllegalArgumentException::new);

        // Challenger 엔티티의 ChallengeId와 일치하는 Auth 리스트 조회
        List<Auth> authList = authRepository.findAllByAuthIdOrderByCreatedAtDesc(challenger.getChallenge().getChallengeId());

        // Auth 엔티티들을 AuthResponseDto로 매핑하고 리스트로 반환
        List<AuthResponseDto> authResponseList = authList.stream().map(AuthResponseDto::new).toList();
        return authResponseList;
    }


    @Transactional
    public AuthResponseDto updateLeaderAuth(AuthRequestDto requestDto, Long challengerId, Long authId) { // 챌린지 인증 허가 및 불가 처리(leader)
        return null;
    }
    @Transactional
    public AuthResponseDto updateMemberAuth(AuthRequestDto requestDto, Long challengerId) { // 챌린지 인증 수정(member)
       return null;
    }

    @Transactional
    public AuthResponseDto createMemberAuth(AuthRequestDto requestDto, Long challengerId) { // 챌린지 인증(member) 등록
        // Challenger DB에 존재하는 Challenger 인지 확인
        Challenger challenger = challengerRepository.findById(challengerId)
                .orElseThrow(IllegalArgumentException::new);

        // Auth(챌린지 인증) 객체 생성 후 Auth DB에 저장
        Auth savedAuth = authRepository.save(new Auth(requestDto, challenger));
        return new AuthResponseDto(savedAuth);
    }

    @Transactional
    public AuthResponseDto createChallenger(AuthRequestDto requestDto, Long challengerId) { //챌린지 신청
        return null;
    }
}
