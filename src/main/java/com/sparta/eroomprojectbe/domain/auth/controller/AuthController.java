package com.sparta.eroomprojectbe.domain.auth.controller;

import com.sparta.eroomprojectbe.domain.auth.dto.AuthRequestDto;
import com.sparta.eroomprojectbe.domain.auth.dto.AuthResponseDto;
import com.sparta.eroomprojectbe.domain.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenger")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService){this.authService = authService;}

    @GetMapping("/{challengerId}/details") // 해당 챌린지 인증(member) 전체 조회
    public ResponseEntity<List<AuthResponseDto>> getMemberAuthList(@PathVariable Long challengerId) {
        List<AuthResponseDto> responseList = authService.getMemberAuthList(challengerId);
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PutMapping("/{challengerId}/details/auth/{authId}") // 챌린지 인증 허가 및 불가 처리(leader)
    public ResponseEntity<AuthResponseDto> updateLeaderAuth(@RequestBody AuthRequestDto requestDto, @PathVariable Long challengerId,@PathVariable Long authId) {
        AuthResponseDto responseDto = authService.updateLeaderAuth(requestDto, challengerId, authId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PutMapping("/{challengerId}/details") // 챌린지 인증 수정(member)
    public ResponseEntity<AuthResponseDto> updateMemberAuth(@RequestBody AuthRequestDto requestDto, @PathVariable Long challengerId) {
        AuthResponseDto responseDto = authService.updateMemberAuth(requestDto, challengerId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/{challengerId}/details") // 챌린지 인증(member)
    public ResponseEntity<AuthResponseDto> createMemberAuth(@RequestBody AuthRequestDto requestDto, @PathVariable Long challengerId) {
        AuthResponseDto responseDto = authService.createMemberAuth(requestDto, challengerId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping("/{challengerId}") //챌린지 신청
    public ResponseEntity<AuthResponseDto> createChallenger(@RequestBody AuthRequestDto requestDto, @PathVariable Long challengerId) {
        AuthResponseDto responseDto = authService.createChallenger(requestDto, challengerId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
