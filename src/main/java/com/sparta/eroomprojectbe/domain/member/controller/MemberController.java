package com.sparta.eroomprojectbe.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.eroomprojectbe.domain.member.dto.MypageResponseDto;
import com.sparta.eroomprojectbe.domain.member.dto.ProfileRequestDto;
import com.sparta.eroomprojectbe.domain.member.dto.ProfileResponseDto;
import com.sparta.eroomprojectbe.domain.member.dto.SignupRequestDto;
import com.sparta.eroomprojectbe.domain.member.service.KakaoService;
import com.sparta.eroomprojectbe.domain.member.service.MemberService;
import com.sparta.eroomprojectbe.global.jwt.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Slf4j
@RestController
public class MemberController {

    private final MemberService memberService;
    private final KakaoService kakaoService;

    public MemberController(MemberService memberService, KakaoService kakaoService) {
        this.memberService = memberService;
        this.kakaoService = kakaoService;
    }

    @PostMapping("/api/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {
        // Validation 예외처리
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        if (fieldErrors.size() > 0) {
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                log.error(fieldError.getField() + " 필드 : " + fieldError.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body("필요한 정보 중 일부가 누락되었습니다.");
        }
        return ResponseEntity.ok(memberService.signup(requestDto));
    }

    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, @CookieValue(name = "Refresh-token") String refreshToken){
        return memberService.logout(response, refreshToken);
    }

    // 이메일 중복 확인
    @GetMapping("/api/signup/email")
    public ResponseEntity<String> emailCheck(@RequestParam String email) {
        return ResponseEntity.ok(memberService.emailCheck(email));
    }

    // 닉네임 중복 확인
    @GetMapping("/api/signup/nickname")
    public ResponseEntity<String> nicknameCheck(@RequestParam String nickname) {
        return ResponseEntity.ok(memberService.nicknameCheck(nickname));
    }

    // 토큰 재발행
    @PostMapping("/api/token")
    public ResponseEntity<String> reissueToken(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse res) throws UnsupportedEncodingException, UnsupportedEncodingException {
        return memberService.reissueToken(userDetails.getMember().getEmail(), res);
    }

    // 카카오 로그인
    @GetMapping("/auth/callback/kakao")
    public ResponseEntity<String> kakaoLogin(@RequestParam String code,
                                             HttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        return ResponseEntity.ok(kakaoService.kakaoLogin(code, response));
    }

    // 마이 페이지 조회
    @GetMapping("/api/mypage")
    public ResponseEntity<MypageResponseDto> getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(memberService.getMypage(userDetails.getMember()));
    }

    // 마이 페이지 개인 정보 수정
    @PutMapping("/api/member/profile")
    public ResponseEntity<ProfileResponseDto> updateProfile(@RequestBody ProfileRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(memberService.updateProfile(requestDto, userDetails.getMember()));
    }

    // 비밀번호 확인 요청
    @GetMapping("/api/member/password")
    public ResponseEntity<String> checkPassword(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                                @RequestBody String password) {
        boolean isMatch = memberService.checkPassword(userDetails.getMember(), password);
        if (isMatch) {
            return ResponseEntity.ok("비밀번호가 일치합니다.");
        } else {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }
    }
}
