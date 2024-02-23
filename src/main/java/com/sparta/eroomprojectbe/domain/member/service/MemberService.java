package com.sparta.eroomprojectbe.domain.member.service;

import com.sparta.eroomprojectbe.domain.challenge.service.ImageS3Service;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.member.dto.*;
import com.sparta.eroomprojectbe.domain.member.entity.EmailVerification;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.EmailVerificationRepository;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import com.sparta.eroomprojectbe.global.RefreshToken;
import com.sparta.eroomprojectbe.global.RefreshTokenRepository;
import com.sparta.eroomprojectbe.global.jwt.JwtUtil;
import com.sparta.eroomprojectbe.global.rollenum.MemberRoleEnum;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ChallengerRepository challengerRepository;
    private final ImageS3Service imageS3Service;
    private final EmailService emailService;
    private final EmailVerificationRepository emailVerificationRepository;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RefreshTokenRepository refreshTokenRepository, ChallengerRepository challengerRepository, ImageS3Service imageS3Service, EmailService emailService, EmailVerificationRepository emailVerificationRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenRepository = refreshTokenRepository;
        this.challengerRepository = challengerRepository;
        this.imageS3Service = imageS3Service;
        this.emailService = emailService;
        this.emailVerificationRepository = emailVerificationRepository;
    }


    @Transactional
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

    // 이메일 중복 확인
    public String emailCheck(String email){
        return memberRepository.existsByEmail(email) ? "중복된 email입니다." : "사용 가능한 email입니다.";
    }

    // 닉네임 중복 확인
    public String nicknameCheck(String nickname) {
        return memberRepository.existsByNickname(nickname) ? "중복된 닉네임입니다." : "사용 가능한 닉네임입니다.";
    }

    @Transactional
    public String reissueToken(String refreshToken, HttpServletResponse res) throws UnsupportedEncodingException {
        refreshToken = jwtUtil.substringToken(refreshToken);
        String userEmail = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();

        Optional<RefreshToken> storedRefreshToken = refreshTokenRepository.findByKeyEmail(userEmail);

        if (storedRefreshToken.isPresent()) {
            String storedToken = storedRefreshToken.get().getRefreshToken();

            // JWT 유효성 검사
            if (jwtUtil.validateToken(storedToken)) {
                // 새로운 Access Token 생성
                String newAccessToken = jwtUtil.createAccessToken(userEmail, MemberRoleEnum.USER);
                jwtUtil.addJwtToCookie(newAccessToken, res, JwtUtil.AUTHORIZATION_HEADER);

                return "토큰 재발급 성공";
            } else {
                refreshTokenRepository.delete(storedRefreshToken.get());
            }
        }

        throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다.");
    }


    @Transactional
    public String logout(HttpServletResponse response, String refreshToken) {

        refreshToken = refreshToken.substring(7);

        if (jwtUtil.validateToken(refreshToken)) {
            Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
            String userEmail = claims.getSubject();

            Optional<RefreshToken> storedRefreshToken = refreshTokenRepository.findByKeyEmail(userEmail);

            if (storedRefreshToken.isPresent() && storedRefreshToken.get().getRefreshToken().equals(refreshToken)) {
                refreshTokenRepository.delete(storedRefreshToken.get());

                deleteJwtCookie(response, JwtUtil.AUTHORIZATION_HEADER);
                deleteJwtCookie(response, JwtUtil.REFRESH_TOKEN_HEADER);

                return "로그아웃 성공";
            }
        }
        return "Refresh Token이 유효하지 않습니다.";
    }


    public MypageResponseDto getMypage(Member member) {
        MemberInfoDto memberInfo = new MemberInfoDto(member);

        List<ChallengeWithRoleDto> challenges = challengerRepository.findAllChallengesByMemberId(member.getMemberId());
        List<MypageChallengeDto> challengeList = challenges.stream().map(challengeWithRoleDto -> {
            Long challengeId = challengeWithRoleDto.getChallengeId();
            Optional<Member> creator = challengerRepository.findCreatorMemberByChallengeId(challengeId);
            String creatorNickname = creator.map(Member::getNickname).orElse("Unknown");
            return new MypageChallengeDto(challengeWithRoleDto, creatorNickname);
        }).collect(Collectors.toList());

        return new MypageResponseDto(memberInfo, challengeList);
    }

    @Transactional
    public String updateNickname(String nickname, Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다."));
        return findMember.updateNickname(nickname);
    }

    @Transactional
    public ProfileResponseDto updateProfile(ProfileRequestDto requestDto, MultipartFile file, Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다."));
        // 썸네일 이미지 업데이트 유무
        String updateFile;
        if(file != null){
            try {
                updateFile = imageS3Service.updateFile(findMember.getProfileImageUrl(),file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }else{
            updateFile = findMember.getProfileImageUrl();
        }
        // 패스워드 변경 유무
        String password;
        if(requestDto.getPassword() != ""){
            password = passwordEncoder.encode(requestDto.getPassword());
        }else{
            password = findMember.getPassword();
        }
        findMember.updateProfile(requestDto, password, updateFile);
        return new ProfileResponseDto(findMember);
    }


    public boolean checkPassword(Member member, String rawPassword) {
        return passwordEncoder.matches(rawPassword, member.getPassword());
    }

    private void deleteJwtCookie(HttpServletResponse response, String tokenName) {
        Cookie cookie = new Cookie(tokenName, null); // 쿠키의 이름과 빈 값을 가진 새 쿠키 생성
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0); // max-age를 0으로 설정하여 쿠키 삭제
        response.addCookie(cookie); // 수정된 쿠키를 응답에 추가
    }

}
