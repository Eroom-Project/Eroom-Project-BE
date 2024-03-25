package com.sparta.eroomprojectbe.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.eroomprojectbe.domain.member.dto.*;
import com.sparta.eroomprojectbe.domain.member.dto.mypage.MypageResponseDto;
import com.sparta.eroomprojectbe.domain.member.service.KakaoService;
import com.sparta.eroomprojectbe.domain.member.service.MemberService;
import com.sparta.eroomprojectbe.global.jwt.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class MemberController {

    private final MemberService memberService;
    private final KakaoService kakaoService;

    public MemberController(MemberService memberService, KakaoService kakaoService) {
        this.memberService = memberService;
        this.kakaoService = kakaoService;
    }

    /**
     * 회원가입 컨트롤러 메서드
     *
     * @param requestDto 회원가입 시 필요한 정보들을 담은 dto
     * @return 회원가입한 유저의 정보, 회원가입 성공여부 message, httpStatus
     */
    @PostMapping("/api/signup")
    public ResponseEntity<BaseDto<SignupResponseDto>> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        SignupResponseDto signupResponseDto = memberService.signup(requestDto);
        BaseDto<SignupResponseDto> response = new BaseDto<>(signupResponseDto, "회원가입 성공", HttpStatus.CREATED);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 로그아웃 컨트롤러 메서드
     *
     * @param response http 응답 객체
     * @param refreshToken 유저 확인을 위한 리프레시 토큰
     * @return 로그아웃 성공여부 message, httpStatus
     */
    @PostMapping("/api/logout")
    public ResponseEntity<BaseDto<String>> logout(HttpServletResponse response, @CookieValue(name = "Refresh-token") String refreshToken){
        String message = memberService.logout(response, refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(null, message, HttpStatus.OK));
    }

    /**
     * 이메일 중복확인 컨트롤러 메서드
     *
     * @param email 유저가 사용하려는 이메일
     * @return 이메일 중복여부 message, httpStatus
     */
    @GetMapping("/api/signup/email")
    public ResponseEntity<BaseDto<String>> checkEmail(@RequestParam String email) {
        String message = memberService.checkEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(null, message, HttpStatus.OK));
    }

    /**
     * 닉네임 중복확인 컨트롤러 메서드
     *
     * @param nickname 유저가 사용하려는 닉네임
     * @return 닉네임 중복여부 message, httpStatus
     */
    @GetMapping("/api/signup/nickname")
    public ResponseEntity<BaseDto<String>> checkNickname(@RequestParam String nickname) {
        String message = memberService.checkNickname(nickname);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(null, message, HttpStatus.OK));
    }

    /**
     * 토큰 재발급 컨트롤러 메서드
     *
     * @param refreshToken access token 재발급을 위한 컨트롤러 메서드
     * @param response http 응답 객체
     * @return 토큰 재발급 성공여부 message, httpStatus
     * @throws UnsupportedEncodingException
     */
    @PostMapping("/api/token")
    public ResponseEntity<BaseDto<String>> reissueToken(@CookieValue(name = "Refresh-token") String refreshToken, HttpServletResponse response) throws UnsupportedEncodingException {
        String message = memberService.reissueToken(refreshToken, response);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(null, message, HttpStatus.OK));
    }

    /**
     * 카카오 로그인 컨트롤러 메서드
     *
     * @param code 카카오 인증 코드
     * @param response http 응답 객체
     * @return 로그인 성공여부 message, httpStatus
     * @throws UnsupportedEncodingException
     * @throws JsonProcessingException
     */
    @GetMapping("/auth/callback/kakao")
    public ResponseEntity<BaseDto<String>> kakaoLogin(@RequestParam String code,
                                             HttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        String message = kakaoService.kakaoLogin(code, response);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(null, message, HttpStatus.OK));
    }

    /**
     * 마이페이지 조회 컨트롤러 메서드
     *
     * @param userDetails 로그인한 유저의 정보
     * @return MypageResponseDto, httpStatus
     */
    @GetMapping("/api/mypage")
    public ResponseEntity<BaseDto<MypageResponseDto>> getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        MypageResponseDto data = memberService.getMypage(userDetails.getMember());
        return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(data, "", HttpStatus.OK));
    }

    /**
     * 닉네임 수정 컨트롤러 메서드
     *
     * @param nickname 유저가 사용하려는 닉네임
     * @param userDetails 로그인한 유저의 정보
     * @return updatedNickname, 닉네임 수정 성공여부 message, httpStatus
     */
    @PutMapping("/api/member/profile/nickname")
    public ResponseEntity<BaseDto<String>> updateNickname(@RequestParam("profileNickname") String nickname,
                                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String updatedNickname = memberService.updateNickname(nickname, userDetails.getMember());
        return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(updatedNickname, "닉네임 수정 성공", HttpStatus.OK));
    }

    /**
     * 프로필 이미지 수정 컨트롤러 메서드
     *
     * @param file 유저가 사용하려는 프로필 이미지
     * @param userDetails 로그인한 유저의 정보
     * @return updatedProfileImage, 프로필 이미지 수정 성공여부 message, httpStatus
     */
    @PutMapping("/api/member/profile/image")
    public ResponseEntity<BaseDto<String>> updateProfileImage(@RequestParam(value = "profileImageUrl", required = false) MultipartFile file,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String updatedProfileImage = memberService.updateProfileImage(file, userDetails.getMember());
        return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(updatedProfileImage, "프로필 이미지 수정 성공", HttpStatus.OK));
    }

    /**
     * 비밀번호 수정 컨트롤러 메서드
     *
     * @param password 유저가 사용하려는 비밀번호
     * @param userDetails 로그인한 유저의 정보
     * @return 비밀번호 수정 성공여부 message, httpStatus
     */
    @PutMapping("/api/member/profile/password")
    public ResponseEntity<BaseDto<String>> updatePassword(@RequestBody Map<String, String> password,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.updatePassword(password.get("password"), userDetails.getMember());
        return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(null, "비밀번호 수정 성공", HttpStatus.OK));
    }

    /**
     * 회원 정보 수정을 위한 비밀번호 확인 컨트롤러 메서드
     *
     * @param userDetails 로그인한 유저의 정보
     * @param password 유저의 현재 비밀번호
     * @return 비밀번호 일치여부 message, httpStatus
     */
    @GetMapping("/api/member/password")
    public ResponseEntity<BaseDto<String>> checkPassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String password) {
        boolean isMatch = memberService.checkPassword(userDetails.getMember(), password);
        if (isMatch) {
            return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(null, "비밀번호가 일치합니다.", HttpStatus.OK));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseDto<>(null, "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST));
        }
    }

    /**
     * 인증 코드 요청 컨트롤러 메서드
     *
     * @param email 인증 코드를 전송받을 이메일
     * @return 가입 여부 및 인증코드 전송 message, httpStatus
     */
    @PostMapping("/emails/verification-requests")
    public ResponseEntity<BaseDto<String>> sendMessage(@RequestParam("email") @Valid String email) {
        String message = memberService.sendCodeToEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(null, message, HttpStatus.OK));
    }

    /**
     * 인증 코드 확인 컨트롤러 메서드
     *
     * @param email 인증 코드를 전송받은 이메일
     * @param authCode 전송받은 인증 코드
     * @return 인증시간 초과 여부 및 인증 코드 확인 message, httpStatus
     */
    @GetMapping("/emails/verifications")
    public ResponseEntity<BaseDto<String>> verificationEmail(@RequestParam("email") @Valid String email,
                                                              @RequestParam("code") String authCode) {
        String message = memberService.verifiedCode(email, authCode);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseDto<>(null, message, HttpStatus.OK));
    }
}

