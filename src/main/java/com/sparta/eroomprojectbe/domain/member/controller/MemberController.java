package com.sparta.eroomprojectbe.domain.member.controller;

import com.sparta.eroomprojectbe.domain.member.dto.SignupRequestDto;
import com.sparta.eroomprojectbe.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
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
    @PostMapping("/signup/email")
    public ResponseEntity<String> emailCheck(@RequestBody String email) {
        return ResponseEntity.ok(memberService.emailCheck(email));
    }

    // 닉네임 중복 확인
    @PostMapping("/signup/nickname")
    public ResponseEntity<String> nicknameCheck(@RequestBody String nickname) {
        return ResponseEntity.ok(memberService.nicknameCheck(nickname));
    }


//    // 토큰 재발행
//    @PostMapping("/token")
//    public ResponseEntity<String> reissueToken(@AuthenticationPrincipal UserDetailsImpl userDetails, HttpServletResponse res){
//        return memberService.reissueToken(userDetails.getMember().getEmail(), res);
//    }
}
