package com.sparta.eroomprojectbe.domain.auth.controller;

import com.sparta.eroomprojectbe.domain.auth.dto.*;
import com.sparta.eroomprojectbe.domain.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/challenge")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService){this.authService = authService;}

    /**
     * 챌린지 인증 전체 조회하는 컨트롤러 메서드
     * @return 챌린지 List, 조회 성공여부 message, httpStatus
     */
    //이건 어디다 쓰는 걸까용?
    @GetMapping("/details") // 챌린지 인증(member) 전체 조회
    public ResponseEntity<AuthAllResponseDto> getMemberAuthList() {
        AuthAllResponseDto responseList = authService.getMemberAuthList();
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }
//    @GetMapping("/details") // 챌린지 인증(member) 전체 조회
//    @Secured("ROLE_Member")
//    public ResponseEntity<AuthAllResponseDto> getMemberAuthList() {
//        AuthAllResponseDto responseList = authService.getMemberAuthList();
//        return ResponseEntity.status(HttpStatus.OK).body(responseList);
//    }

    /**
     * 해당 챌린지 인증 전체 조회하는 컨트롤러 메서드
     * @param challengeId 조회하려는 챌린저 id
     * @return 해당 챌린지 List, 조회 성공여부 message, httpStatus
     */
    @GetMapping("/{challengeId}/auth") // 해당 챌린지 인증(member) 전체 조회
    public ResponseEntity<AuthAllResponseDto> getChallengerAuthList(@PathVariable Long challengeId) {
        AuthAllResponseDto responseList = authService.getChallengerAuthList(challengeId);
        return ResponseEntity.status(HttpStatus.OK).body(responseList);
    }
//    @GetMapping("/{challengeId}/details") // 해당 챌린지 인증(member) 전체 조회
//    @Secured("ROLE_Member")
//    public ResponseEntity<AuthAllResponseDto> getChallengerAuthList(@PathVariable Long challengeId) {
//        AuthAllResponseDto responseList = authService.getChallengerAuthList(challengeId);
//        return ResponseEntity.status(HttpStatus.OK).body(responseList);
//    }

    /**
     * 리더가 챌린지 인증 허가 및 불가 처리하는 컨트롤러 메서드
     * @param requestDto DENIED, APPROVED
     * @param challengeId 선택한 챌린저 id
     * @param authId 변경하려는 인증 id
     * @return 인증 수정 후 data, 인증 수정 성공 여부 message, httpStatus
     */
    @PutMapping("/{challengeId}/leader/auth/{authId}/member/{memberId}") // 챌린지 인증 허가 및 불가 처리(leader)
    public ResponseEntity<AuthDataResponseDto> updateLeaderAuth(@RequestBody AuthLeaderRequestDto requestDto,
                                                            @PathVariable Long challengeId,
                                                            @PathVariable Long memberId,
                                                            @PathVariable Long authId) {
        AuthDataResponseDto responseDto = authService.updateLeaderAuth(requestDto, challengeId, authId, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
//    @PutMapping("/{challengerId}/details/auth/{authId}") // 챌린지 인증 허가 및 불가 처리(leader)
//    @Secured("ROLE_Member")
//    public ResponseEntity<AuthDataResponseDto> updateLeaderAuth(@RequestBody AuthLeaderRequestDto requestDto,
//                                                                @PathVariable Long challengerId,
//                                                                @PathVariable Long authId,
//                                                                @AuthenticationPrincipal UserDetailsImpl userDetails)) {
//        AuthDataResponseDto responseDto = authService.updateLeaderAuth(requestDto, challengerId, authId, userDetails.getMember());
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }

    /**
     * 챌린지인증 수정하는 컨트롤러 메서드
     * @param requestDto authContents, authImageUrl, authVideoUrl, authStatus
     * @param challengeId 수정하려는 인증의 챌린지 아이디
     * @param authId 수정하려는 인증아이디
     * @return HttpStatus, (수정한 내용 data, 수정성공여부 message, httpStatus)
     */
    @PutMapping("/{challengeId}/auth/{authId}/member/{memberId}") // 챌린지 인증 수정(member)
    public ResponseEntity<AuthDataResponseDto> updateMemberAuth(@RequestBody AuthRequestDto requestDto,
                                                                @PathVariable Long challengeId,
                                                                @PathVariable Long memberId,
                                                                @PathVariable Long authId) {
        AuthDataResponseDto responseDto = authService.updateMemberAuth(requestDto, challengeId, authId, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
//    @PutMapping("/{challengerId}/auth/{authId}") // 챌린지 인증 수정(member)
//    @Secured("ROLE_Member")
//    public ResponseEntity<AuthDataResponseDto> updateMemberAuth(@RequestBody AuthRequestDto requestDto,
//                                                                @PathVariable Long challengeId,
//                                                                @PathVariable Long authId,
//                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        AuthDataResponseDto responseDto = authService.updateMemberAuth(requestDto, challengeId, authId, userDetails.getMember());
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }


    /**
     * 챌린지 인등 등록하는 컨트롤러 메서드
     * @param requestDto authContents,authImageUrl,authVideoUrl, authStatus
     * @param challengeId 인증하려는 challengerId
     * @return 챌린지 인증 등록 성공여부 message, httpStatus
     */
    @PostMapping("/{challengeId}/auth/member/{memberId}") // 챌린지 인증(member) 등록
    public ResponseEntity<ChallengerCreateResponseDto> createMemberAuth(@RequestBody AuthRequestDto requestDto,
                                                                        @PathVariable Long challengeId,
                                                                        @PathVariable Long memberId) {
        ChallengerCreateResponseDto responseDto = authService.createMemberAuth(requestDto, challengeId, memberId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

//    @PostMapping("/{challengeId}/auth") // 챌린지 인증(member) 등록
//    @Secured("ROLE_Member")
//    public ResponseEntity<ChallengerCreateResponseDto> createMemberAuth(@RequestBody AuthRequestDto requestDto,
//                                                                        @PathVariable Long challengeId,
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
//    @Secured("ROLE_Member")
//    public ResponseEntity<ChallengerCreateResponseDto> createChallenger( @PathVariable Long challengeId,
//                                                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {
//        ChallengerCreateResponseDto responseDto = authService.createChallenger(challengeId, userDetails.getMember());
//        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
//    }
}
