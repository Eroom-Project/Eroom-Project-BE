package com.sparta.eroomprojectbe.domain.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.eroomprojectbe.domain.member.dto.*;
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

    @PostMapping("/api/signup")
    public ResponseEntity<BaseDto<SignupResponseDto>> signup(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + " : " + fieldError.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return ResponseEntity.badRequest().body(new BaseDto<>(null, errorMessage, HttpStatus.BAD_REQUEST));
        }

        try {
            SignupResponseDto signupResponseDto = memberService.signup(requestDto);
            return ResponseEntity.ok(new BaseDto<>(signupResponseDto, "회원가입 성공", HttpStatus.CREATED));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new BaseDto<>(null, e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PostMapping("/api/logout")
    public ResponseEntity<BaseDto<String>> logout(HttpServletResponse response, @CookieValue(name = "Refresh-token") String refreshToken){
        String message = memberService.logout(response, refreshToken);
        return ResponseEntity.ok(new BaseDto<>(null, message, HttpStatus.OK));
    }

    @GetMapping("/api/signup/email")
    public ResponseEntity<BaseDto<String>> emailCheck(@RequestParam String email) {
        String message = memberService.emailCheck(email);
        return ResponseEntity.ok(new BaseDto<>(null, message, HttpStatus.OK));
    }

    @GetMapping("/api/signup/nickname")
    public ResponseEntity<BaseDto<String>> nicknameCheck(@RequestParam String nickname) {
        String message = memberService.nicknameCheck(nickname);
        return ResponseEntity.ok(new BaseDto<>(null, message, HttpStatus.OK));
    }

    // 토큰 재발행
    @PostMapping("/api/token")
    public ResponseEntity<BaseDto<String>> reissueToken(@CookieValue(name = "Refresh-token") String refreshToken, HttpServletResponse res) throws UnsupportedEncodingException, UnsupportedEncodingException {
        String message = memberService.reissueToken(refreshToken, res);
        return ResponseEntity.ok(new BaseDto<>(null, message, HttpStatus.OK));
    }

    // 카카오 로그인
    @GetMapping("/auth/callback/kakao")
    public ResponseEntity<BaseDto<String>> kakaoLogin(@RequestParam String code,
                                             HttpServletResponse response) throws UnsupportedEncodingException, JsonProcessingException {
        String message = kakaoService.kakaoLogin(code, response);
        return ResponseEntity.ok(new BaseDto<>(null, message, HttpStatus.OK));
    }

    // 마이 페이지 조회
    @GetMapping("/api/mypage")
    public ResponseEntity<BaseDto<MypageResponseDto>> getMypage(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        MypageResponseDto data = memberService.getMypage(userDetails.getMember());
        return ResponseEntity.ok(new BaseDto<>(data, "", HttpStatus.OK));
    }

     // 마이 페이지 닉네임 수정
    @PutMapping("/api/member/profile/nickname")
    public ResponseEntity<BaseDto<String>> updateNickname(@RequestBody Map<String, String> nickname,
                                                                     @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String updatedNickname = memberService.updateNickname(nickname.get("nickname"), userDetails.getMember());
        return ResponseEntity.ok(new BaseDto<>(updatedNickname, "닉네임 수정 성공", HttpStatus.OK));
    }

    // 마이 페이지 프로필 이미지 수정
    @PutMapping("/api/member/profile/image")
    public ResponseEntity<BaseDto<String>> updateProfileImage(@RequestParam(value = "profileImageUrl", required = false) MultipartFile file,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String updatedNickname = memberService.updateProfileImage(file, userDetails.getMember());
        return ResponseEntity.ok(new BaseDto<>(updatedNickname, "프로필 이미지 수정 성공", HttpStatus.OK));
    }

    // 마이 페이지 비밀번호 수정
    @PutMapping("/api/member/profile/password")
    public ResponseEntity<BaseDto<String>> updatePassword(@RequestBody Map<String, String> password,
                                                          @AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberService.updatePassword(password.get("password"), userDetails.getMember());
        return ResponseEntity.ok(new BaseDto<>("", "비밀번호 수정 성공", HttpStatus.OK));
    }

    // 비밀번호 확인
    @GetMapping("/api/member/password")
    public ResponseEntity<BaseDto<String>> checkPassword(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestParam String password) {
        boolean isMatch = memberService.checkPassword(userDetails.getMember(), password);
        if (isMatch) {
            return ResponseEntity.ok(new BaseDto<>(null, "비밀번호가 일치합니다.", HttpStatus.OK));
        } else {
            return ResponseEntity.badRequest().body(new BaseDto<>(null, "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST));
        }
    }
}
