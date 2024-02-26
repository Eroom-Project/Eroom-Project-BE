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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

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
    public String checkEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return "유효하지 않은 이메일 형식입니다.";
        }
        return memberRepository.existsByEmail(email) ? "중복된 email입니다." : "사용 가능한 email입니다.";
    }

    // 닉네임 중복 확인
    public String checkNickname(String nickname) {
        if (nickname.length() < 3 || nickname.length() > 10) {
            return "닉네임은 3자 이상 10자 이하로 입력해 주세요.";
        }
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

        List<ChallengeWithRoleDto> challenges = challengerRepository.findAllChallengesByMemberIdOrderByChallengeCreatedAtDesc(member.getMemberId());
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
    public String updateProfileImage(MultipartFile file, Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다."));
        String updateFile = findMember.getProfileImageUrl();
        if (file != null) {
            try {
                updateFile = imageS3Service.updateFile(findMember.getProfileImageUrl(), file);
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 저장 중 문제가 발생하였습니다", e);
            }
        }
        return findMember.updateProfileImage(updateFile);
    }

    @Transactional
    public void updatePassword(String password, Member member) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 빈 값일 수 없습니다.");
        }
        Member findMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다."));
        String encodedPassword = passwordEncoder.encode(password);
        findMember.updatePassword(encodedPassword);
        memberRepository.save(findMember);
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

    @Transactional
    public void sendCodeToEmail(String toEmail) {
        this.checkDuplicatedEmail(toEmail);
        String authCode = this.createCode();

        // 이메일 내용 정의
        String title = "eroom 이메일 인증 번호";
        String content =
                "<div style='margin:30px;'>"
                        + "<h2>안녕하세요.</h2>"
                        + "<h2>이룸에 오신 것을 환영합니다.</h2>"
                        + "<br>"
                        + "<p>아래 인증번호를 복사해 인증번호 확인란에 입력해주세요.<p>"
                        + "<br>"
                        + "<p>감사합니다!<p>"
                        + "<br>"
                        + "<div align='center' style='border:1px solid black; font-family:verdana;'>"
                        + "<h3 style='color:blue;'>회원가입 인증번호입니다.</h3>"
                        + "<div style='font-size:130%'>"
                        + "인증 번호 : <strong>" + authCode + "</strong></div><br/>"
                        + "</div>";

        String sendMail = "eroom.challenge@gmail.com";
        emailService.sendEmail(sendMail, toEmail, title, content);

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5); // 이메일 5분 후 만료
        EmailVerification verification = new EmailVerification(toEmail, authCode, expirationTime);
        emailVerificationRepository.save(verification);
    }

    private void checkDuplicatedEmail(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
        }
    }

    private String createCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("인증번호를 만들던 중 오류가 발생했습니다.");
        }
    }

    @Transactional
    public boolean verifiedCode(String email, String authCode) {
        Optional<EmailVerification> verification = emailVerificationRepository.findByEmailAndAuthCode(email, authCode);

        boolean authResult = verification.isPresent() && verification.get().getExpirationTime().isAfter(LocalDateTime.now());
        return authResult;
    }


}
