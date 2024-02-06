package com.sparta.eroomprojectbe.domain.auth.controller;

import com.sparta.eroomprojectbe.domain.auth.dto.AuthDataResponseDto;
import com.sparta.eroomprojectbe.domain.auth.dto.AuthRequestDto;
import com.sparta.eroomprojectbe.domain.auth.dto.AuthResponseDto;
import com.sparta.eroomprojectbe.domain.auth.dto.ChallengerCreateResponseDto;
import com.sparta.eroomprojectbe.domain.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenger")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService){this.authService = authService;}
    @GetMapping("/details") // 챌린지 인증(member) 전체 조회
    public ResponseEntity<List<AuthResponseDto>> getMemberAuthList() {
        List<AuthResponseDto> responseList = authService.getMemberAuthList();
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @GetMapping("/{challengerId}/details") // 해당 챌린지 인증(member) 전체 조회
    public ResponseEntity<List<AuthResponseDto>> getChallengerAuthList(@PathVariable Long challengerId) {
        List<AuthResponseDto> responseList = authService.getChallengerAuthList(challengerId);
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }

    @PutMapping("/{challengerId}/details/auth/{authId}") // 챌린지 인증 허가 및 불가 처리(leader)
    public ResponseEntity<AuthResponseDto> updateLeaderAuth(@RequestBody AuthRequestDto requestDto, @PathVariable Long challengerId,@PathVariable Long authId) {
        AuthResponseDto responseDto = authService.updateLeaderAuth(requestDto, challengerId, authId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 챌린지인증 수정하는 컨트롤러 메서드
     * @param requestDto authContents, authImageUrl, authVideoUrl, authStatus
     * @param challengerId 수정하려는 챌린저 아이디
     * @param authId 수정하려는 인증아이디
     * @return HttpStatus, (수정한 내용 data, 수정성공여부 message, httpStatus)
     */
    @PutMapping("/{challengerId}/auth/{authId}") // 챌린지 인증 수정(member)
    public ResponseEntity<AuthDataResponseDto> updateMemberAuth(@RequestBody AuthRequestDto requestDto,
                                                                @PathVariable Long challengerId,
                                                                @PathVariable Long authId) {
        AuthDataResponseDto responseDto = authService.updateMemberAuth(requestDto, challengerId, authId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
//    @PutMapping("/{challengerId}/auth/{authId}") // 챌린지 인증 수정(member)
//    public ResponseEntity<AuthDataResponseDto> updateMemberAuth(@RequestBody AuthRequestDto requestDto,
//                                                                @PathVariable Long challengerId,
//                                                                @PathVariable Long authId,
//                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        AuthDataResponseDto responseDto = authService.updateMemberAuth(requestDto, challengerId, authId, userDetails.getMember());
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }


    /**
     * 챌린지 인등 등록하는 컨트롤러 메서드
     * @param requestDto authContents,authImageUrl,authVideoUrl, authStatus
     * @param challengerId 인증하려는 challengerId
     * @return 챌린지 인증 등록 성공여부 message, httpStatus
     */
    @PostMapping("/{challengerId}/auth") // 챌린지 인증(member) 등록
    public ResponseEntity<ChallengerCreateResponseDto> createMemberAuth(@RequestBody AuthRequestDto requestDto,
                                                                        @PathVariable Long challengerId) {
        ChallengerCreateResponseDto responseDto = authService.createMemberAuth(requestDto, challengerId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

//    @PostMapping("/{challengerId}/details") // 챌린지 인증(member) 등록
//    public ResponseEntity<ChallengerCreateResponseDto> createMemberAuth(@RequestBody AuthRequestDto requestDto,
//                                                                        @PathVariable Long challengerId,
//                                                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        ChallengerCreateResponseDto responseDto = authService.createMemberAuth(requestDto, challengerId, userDetails.getMember());
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }

    /**
     * 유저가 챌린지를 신청하는 컨트롤러 메서드
     * @param challengeId 신청하려는 챌린지 id
     * @return 챌린지 신청 성공여부 message, httpStatus
     */
    @PostMapping("/{challengeId}")
    public ResponseEntity<ChallengerCreateResponseDto> createChallenger( @PathVariable Long challengeId) {
        ChallengerCreateResponseDto responseDto = authService.createChallenger(challengeId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
//    @PostMapping("/{challengeId}")
//    public ResponseEntity<ChallengerCreateResponseDto> createChallenger( @PathVariable Long challengeId,
//                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        ChallengerCreateResponseDto responseDto = authService.createChallenger(challengeId, userDetails.getMember());
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }
}
