package com.sparta.eroomprojectbe.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.eroomprojectbe.domain.member.dto.LoginRequestDto;
import com.sparta.eroomprojectbe.domain.member.dto.MemberInfoDto;
import com.sparta.eroomprojectbe.domain.member.dto.SignupRequestDto;
import com.sparta.eroomprojectbe.domain.member.dto.SignupResponseDto;
import com.sparta.eroomprojectbe.domain.member.service.KakaoService;
import com.sparta.eroomprojectbe.domain.member.service.MemberService;
import com.sparta.eroomprojectbe.global.jwt.JwtUtil;
import com.sparta.eroomprojectbe.global.jwt.UserDetailsImpl;
import com.sparta.eroomprojectbe.global.rollenum.MemberRoleEnum;
import jakarta.servlet.http.Cookie;
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
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;
    private final KakaoService kakaoService;

    public MemberController(MemberService memberService, KakaoService kakaoService) {
        this.memberService = memberService;
        this.kakaoService = kakaoService;
    }

    @PostMapping("/signup")
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

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "Refresh_token") String refreshToken){
        return ResponseEntity.ok(memberService.logout(refreshToken));
    }

    // 이메일 중복 확인
    @PostMapping("/auth/signup/email")
    public ResponseEntity<String> emailCheck(@RequestBody String email) {
        return ResponseEntity.ok(memberService.emailCheck(email));
    }

    // 닉네임 중복 확인
    @PostMapping("/auth/signup/nickname")
    public ResponseEntity<String> nicknameCheck(@RequestBody String nickname) {
        return ResponseEntity.ok(memberService.nicknameCheck(nickname));
    }

    // 토큰 재발행
    @PostMapping("/token")
    public ResponseEntity<String> reissueToken(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse res) throws UnsupportedEncodingException, UnsupportedEncodingException {
        return memberService.reissueToken(userDetails.getMember().getEmail(), res);
    }

    @GetMapping("/auth/callback/kakao")
    public String kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        String token = kakaoService.kakaoLogin(code);
        Cookie cookie = new Cookie(JwtUtil.AUTHORIZATION_HEADER, token.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/";
    }
//
//    // 카카오 로그인
//    @GetMapping("/auth/callback/kakao")
//    public ResponseEntity<String> kakaoLogin(@RequestParam String code,
//                                             HttpServletResponse response) {
//        return ResponseUtil.response(kakaoService.kakaoLogin(code, response));
//    }

}
