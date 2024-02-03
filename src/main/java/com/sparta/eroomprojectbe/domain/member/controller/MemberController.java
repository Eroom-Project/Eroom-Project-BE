package com.sparta.eroomprojectbe.domain.member.controller;

import com.sparta.eroomprojectbe.domain.member.dto.MemberInfoDto;
import com.sparta.eroomprojectbe.domain.member.dto.SignupRequestDto;
import com.sparta.eroomprojectbe.domain.member.dto.SignupResponseDto;
import com.sparta.eroomprojectbe.domain.member.service.MemberService;
import com.sparta.eroomprojectbe.global.jwt.UserDetailsImpl;
import com.sparta.eroomprojectbe.global.rollenum.MemberRoleEnum;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // 회원 관련 정보 받기
    @GetMapping("/user-info")
    @ResponseBody
    public MemberInfoDto getMemberInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        String userEmail = userDetails.getMember().getEmail();
        MemberRoleEnum role = userDetails.getMember().getRole();

        boolean isAdmin = (role == MemberRoleEnum.ADMIN);

        return new MemberInfoDto(userEmail, isAdmin);
    }
}
