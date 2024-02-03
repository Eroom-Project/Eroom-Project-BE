package com.sparta.eroomprojectbe.domain.member.service;

import com.sparta.eroomprojectbe.domain.member.dto.SignupRequestDto;
import com.sparta.eroomprojectbe.domain.member.dto.SignupResponseDto;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public SignupResponseDto signup(SignupRequestDto requestDto) {
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        String email = requestDto.getEmail();
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 등록
        Member member = new Member(email, password, requestDto.getNickname());
        return new SignupResponseDto(memberRepository.save(member));
    }
}
