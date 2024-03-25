package com.sparta.eroomprojectbe.domain.member.service;

import com.sparta.eroomprojectbe.domain.challenge.service.ImageS3Service;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.member.dto.*;
import com.sparta.eroomprojectbe.domain.member.dto.mypage.ChallengeWithRoleDto;
import com.sparta.eroomprojectbe.domain.member.dto.mypage.MemberInfoDto;
import com.sparta.eroomprojectbe.domain.member.dto.mypage.MypageChallengeDto;
import com.sparta.eroomprojectbe.domain.member.dto.mypage.MypageResponseDto;
import com.sparta.eroomprojectbe.domain.member.entity.EmailVerification;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.EmailVerificationRepository;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import com.sparta.eroomprojectbe.global.error.EroomException;
import com.sparta.eroomprojectbe.global.error.ErrorCode;
import com.sparta.eroomprojectbe.global.refreshToken.RefreshToken;
import com.sparta.eroomprojectbe.global.refreshToken.RefreshTokenService;
import com.sparta.eroomprojectbe.global.jwt.JwtUtil;
import com.sparta.eroomprojectbe.global.rollenum.MemberRoleEnum;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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
    private final RefreshTokenService refreshTokenService;
    private final ChallengerRepository challengerRepository;
    private final ImageS3Service imageS3Service;
    private final EmailService emailService;
    private final EmailVerificationRepository emailVerificationRepository;


    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, RefreshTokenService refreshTokenService, ChallengerRepository challengerRepository, ImageS3Service imageS3Service, EmailService emailService, EmailVerificationRepository emailVerificationRepository) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.challengerRepository = challengerRepository;
        this.imageS3Service = imageS3Service;
        this.emailService = emailService;
        this.emailVerificationRepository = emailVerificationRepository;
    }

    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private Pattern pattern = Pattern.compile(EMAIL_PATTERN);

    /**
     * 회원가입 서비스 메서드
     *
     * @param requestDto 회원가입 시 필요한 정보를 담은 dto
     * @return 이메일 중복 여부 및 회원가입한 유저의 정보를 담은 response dto
     */
    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        String email = requestDto.getEmail();
        if (memberRepository.existsByEmail(email)) {
            throw new EroomException(ErrorCode.DUPLICATED_EMAIL);
        }

        // 사용자 등록
        Member member = new Member(email, password, requestDto.getNickname());
        return new SignupResponseDto(memberRepository.save(member));
    }

    /**
     * 이메일 중복 확인 서비스 메서드
     *
     * @param email 유저가 사용하려는 이메일
     * @return 이메일 중복 여부 message
     */
    public String checkEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            return "유효하지 않은 이메일 형식입니다.";
        }
        return memberRepository.existsByEmail(email) ? "중복된 email입니다." : "사용 가능한 email입니다.";
    }

    /**
     * 닉네임 중복 확인 서비스 메서드
     *
     * @param nickname 유저가 사용하려는 닉네임
     * @return 닉네임 중복 여부 message
     */
    public String checkNickname(String nickname) {
        if (nickname.length() < 3 || nickname.length() > 10) {
            return "닉네임은 3자 이상 10자 이하로 입력해 주세요.";
        }
        return memberRepository.existsByNickname(nickname) ? "중복된 닉네임입니다." : "사용 가능한 닉네임입니다.";
    }

    /**
     * access token 재발급 서비스 메서드
     *
     * @param refreshToken 유저 확인을 위한 refresh token
     * @param response http 응답 객체
     * @return refresh token 유효 여부 및 토큰 재발급 성공 message
     */
    @Transactional
    public String reissueToken(String refreshToken, HttpServletResponse response) throws UnsupportedEncodingException {
        refreshToken = jwtUtil.substringToken(refreshToken);
        String userEmail = jwtUtil.getUserInfoFromToken(refreshToken).getSubject();

        Optional<RefreshToken> storedRefreshToken = refreshTokenService.getRefreshToken(userEmail);

        if (storedRefreshToken.isEmpty()) {
            throw new EroomException(ErrorCode.REFRESHTOKEN_NOT_FOUND);
        }

        String storedToken = storedRefreshToken.get().getRefreshToken();

        // JWT 유효성 검사
        if (!jwtUtil.validateToken(storedToken)) {
            refreshTokenService.removeRefreshToken(userEmail);
            throw new EroomException(ErrorCode.EXPIRATION_REFRESHTOKEN);
        }

        // 새로운 Access Token 생성
        String newAccessToken = jwtUtil.createAccessToken(userEmail, MemberRoleEnum.USER);
        jwtUtil.addJwtToCookie(newAccessToken, response, JwtUtil.AUTHORIZATION_HEADER);

        return "토큰 재발급 성공";
    }

    /**
     * 로그아웃 서비스 메서드
     *
     * @param response http 응답 객체
     * @param refreshToken 유저 확인을 위한 refresh token
     * @return refresh token 유효 여부 및 로그아웃 성공 메서드
     */
    @Transactional
    public String logout(HttpServletResponse response, String refreshToken) {
        refreshToken = refreshToken.substring(7);

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new EroomException(ErrorCode.INVALID_REFRESHTOKEN);
        }

        Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
        String userEmail = claims.getSubject();

        Optional<RefreshToken> storedRefreshToken = refreshTokenService.getRefreshToken(userEmail);

        if (storedRefreshToken.isEmpty() || !storedRefreshToken.get().getRefreshToken().equals(refreshToken)) {
            throw new EroomException(ErrorCode.REFRESHTOKEN_NOT_FOUND);
        }

        refreshTokenService.removeRefreshToken(userEmail);
        deleteJwtCookie(response, JwtUtil.AUTHORIZATION_HEADER);
        deleteJwtCookie(response, JwtUtil.REFRESH_TOKEN_HEADER);

        return "로그아웃 성공";
    }

    /**
     * 마이페이지 조회 서비스 메서드
     *
     * @param member 로그인한 유저 객체
     * @return 유저 정보 및 유저와 관련된 챌린지 리스트를 담은 response dto
     */
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

    /**
     * 닉네임 수정 서비스 메서드
     *
     * @param nickname 유저가 사용하려는 닉네임
     * @param member 로그인한 유저 객체
     * @return 수정된 닉네임
     */
    @Transactional
    public String updateNickname(String nickname, Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new EroomException(ErrorCode.NOT_FOUND_MEMBER));
        if (memberRepository.existsByNickname(nickname)) {
            throw new EroomException(ErrorCode.DUPLICATED_NICKNAME);
        }
        return findMember.updateNickname(nickname);
    }

    /**
     * 프로필 이미지 수정 서비스 메서드
     *
     * @param file 유저가 사용하려는 프로필 이미지
     * @param member 로그인한 유저 객체
     * @return 수정된 프로필 이미지 url
     */
    @Transactional
    public String updateProfileImage(MultipartFile file, Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new EroomException(ErrorCode.NOT_FOUND_MEMBER));
        String updateFile = findMember.getProfileImageUrl();
        if (file != null) {
            try {
                updateFile = imageS3Service.updateFile(findMember.getProfileImageUrl(), file);
            } catch (IOException e) {
                throw new EroomException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        return findMember.updateProfileImage(updateFile);
    }

    /**
     * 비밀번호 수정 서비스 메서드
     *
     * @param password 유저가 사용하려는 비밀번호
     * @param member 로그인한 유저 객체
     */
    @Transactional
    public void updatePassword(String password, Member member) {
        if (password == null || password.trim().isEmpty()) {
            throw new EroomException(ErrorCode.NOT_VALID_PASSWORD);
        }
        Member findMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(() -> new EroomException(ErrorCode.NOT_FOUND_MEMBER));
        String encodedPassword = passwordEncoder.encode(password);
        findMember.updatePassword(encodedPassword);
        memberRepository.save(findMember);
    }

    /**
     * 비밀번호 확인 서비스 메서드
     *
     * @param member 로그인한 유저 객체
     * @param rawPassword 유저의 현재 비밀번호
     * @return 비밀번호 일치 여부
     */
    public boolean checkPassword(Member member, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new EroomException(ErrorCode.NOT_VALID_PASSWORD);
        }
        return true;
    }

    /**
     * 쿠키 삭제 메서드 (로그아웃에 사용)
     *
     * @param response http 응답 객체
     * @param tokenName Authorization or Refresh-token
     */
    private void deleteJwtCookie(HttpServletResponse response, String tokenName) {
        Cookie cookie = new Cookie(tokenName, null); // 쿠키의 이름과 빈 값을 가진 새 쿠키 생성
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(0); // max-age를 0으로 설정하여 쿠키 삭제
        response.addCookie(cookie); // 수정된 쿠키를 응답에 추가
    }

    /**
     * 인증 코드 발송 서비스 메서드
     *
     * @param toEmail 받는 사람
     * @return 가입 여부 및 인증 코드 전송 완료 message
     */
    @Transactional
    public String sendCodeToEmail(String toEmail) {
        boolean memberIsPresent = memberRepository.existsByEmail(toEmail);
        if (memberIsPresent) {
            return "이미 가입된 아이디입니다.";
        }
        String authCode = this.createCode();

        // 이메일 내용 정의
        String title = "eroom 이메일 인증 번호";
        String content =
                "<div style='font-family: Arial, Helvetica, sans-serif; color: #333; background-color: #ffffff; padding: 40px; border-radius: 15px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); text-align: center;'>"
                        + "<h2 style='color: #4a7c59; font-size: 22px;'>🎉 안녕하세요, 이룸에 오신 것을 환영합니다! 🎉</h2>"
                        + "<p style='font-size: 16px;'>5분 내에 아래 <strong>인증번호</strong>를 복사하여 인증번호 확인란에 입력해주세요.</p>"
                        + "<div style='margin: 30px auto; padding: 20px; background-color: #e6f9d4; display: inline-block;'>"
                        + "<h3 style='color: #333; font-size: 18px;'>회원가입 인증번호입니다.</h3>"
                        + "<p style='background-color: #d4f7c5; color: #4a7c59; font-size: 24px; padding: 10px 20px; border-radius: 10px; display: inline-block; margin: 0;'>" + authCode + "</p>"
                        + "</div>"
                        + "<p style='font-size: 16px; margin-top: 40px;'>이 코드를 요청하지 않은 경우, 이 이메일을 무시해도 됩니다.<br>다른 사용자가 실수로 이메일 주소를 입력했을 수 있습니다.</p>"
                        + "</div>";



        String sendMail = "eroom.challenge@gmail.com";
        emailService.sendEmail(sendMail, toEmail, title, content);

        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        EmailVerification verification = emailVerificationRepository.findByEmail(toEmail)
                .orElse(new EmailVerification(toEmail, authCode, expirationTime));

        verification.update(authCode, expirationTime);
        emailVerificationRepository.save(verification);
        return "인증 메일을 전송하였습니다.";
    }

    /**
     * 인증 코드를 생성하는 메서드 (send code to email에 사용)
     *
     * @return 인증코드 6자리
     */
    private String createCode() {
        int length = 6;
        try {
            // 인증 번호를 만들 때 그냥 무작위 번호가 아닐 텐데
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

    /**
     * 인증 코드 확인 서비스 메서드
     *
     * @param email 인증 코드를 발급받은 이메일
     * @param authCode 인증 코드
     * @return 인증 시간 초과여부 및 인증 완료 message
     */
    public String verifiedCode(String email, String authCode) {
        Optional<EmailVerification> verification = emailVerificationRepository.findByEmailAndAuthCode(email, authCode);

        if (!verification.isPresent()) {
            throw new EroomException(ErrorCode.VERIFICATION_CODE_NOT_FOUND);
        }
        LocalDateTime now = LocalDateTime.now();

        if (verification.get().getExpirationTime().isAfter(now)) {
            emailVerificationRepository.deleteByEmail(email);
            return "인증이 완료되었습니다.";
        } else {
            throw new EroomException(ErrorCode.VERIFICATION_CODE_EXPIRED);
        }
    }
}
