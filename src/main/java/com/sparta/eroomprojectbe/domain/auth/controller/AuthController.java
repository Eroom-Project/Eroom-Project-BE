package com.sparta.eroomprojectbe.domain.auth.controller;

import com.sparta.eroomprojectbe.domain.auth.dto.*;
import com.sparta.eroomprojectbe.domain.auth.service.AuthService;
import com.sparta.eroomprojectbe.global.jwt.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/challenge")
public class AuthController {
    private final AuthService authService;
    private final AtomicLong lastRequestTime = new AtomicLong(0);
    private static final long MIN_REQUEST_INTERVAL = 2000;
    public AuthController(AuthService authService){this.authService = authService;}

    /**
     * 유저가 챌린지를 신청하는 컨트롤러 메서드
     * @param challengeId 신청하려는 챌린지 id
     * @param userDetails 로그인한 유저
     * @return 챌린지 신청 성공여부 message, httpStatus
     */
    @PostMapping("/{challengeId}")
    public ResponseEntity<BaseResponseDto<CreateResponseDto>> createChallenger(@PathVariable Long challengeId,
                                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CreateResponseDto responseDto = authService.createChallenger(challengeId, userDetails.getMember());
        return ResponseEntity.status(responseDto.getStatus()).body(new BaseResponseDto<>(null,responseDto.getMessage(),responseDto.getStatus()));
    }
    /**
     * 챌린지 인증 등록하는 컨트롤러 메서드
     * @param requestDto authContents,authImageUrl,authVideoUrl, authStatus
     * @param file 인증용 사진
     * @param challengeId 인증하려는 challengerId
     * @param userDetails 로그인 멤버
     * @return 챌린지 인증 등록 성공여부 message, httpStatus
     */
    @PostMapping("/{challengeId}/auth") // 챌린지 인증(member) 등록
    public ResponseEntity<BaseResponseDto<CreateResponseDto>> createMemberAuth(@RequestPart("authCreateData") AuthRequestDto requestDto,
                                                                               @RequestParam(value = "authImageUrl", required = false) MultipartFile file,
                                                                               @PathVariable Long challengeId,
                                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        long currentTime = System.currentTimeMillis();
        // 마지막 요청 시간에서 MIN_REQUEST_INTERVAL 이상 지났을 때만 처리
        if (currentTime - lastRequestTime.get() < MIN_REQUEST_INTERVAL) {
            CreateResponseDto responseDto = new CreateResponseDto("이전 요청이 아직 처리 중입니다.", HttpStatus.BAD_REQUEST);
            return ResponseEntity.status(responseDto.getStatus()).body(new BaseResponseDto<>(null, responseDto.getMessage(), responseDto.getStatus()));
        }
        lastRequestTime.set(currentTime);
        CreateResponseDto responseDto = authService.createMemberAuth(requestDto, file ,challengeId, userDetails.getMember());
        return ResponseEntity.status(responseDto.getStatus()).body(new BaseResponseDto<>(null,responseDto.getMessage(),responseDto.getStatus()));
    }
    /**
     * 해당 챌린지 인증 전체 조회하는 컨트롤러 메서드
     * @param challengeId 조회하려는 챌린저 id
     * @param userDetails 로그인 멤버
     * @return 해당 챌린지 List, 조회 성공여부 message, httpStatus
     */
    @GetMapping("/{challengeId}/auth") // 해당 챌린지 인증(member) 전체 조회
    public ResponseEntity<BaseResponseDto<AuthMemberInfoResponseDto>> getChallengerAuthList(@PathVariable Long challengeId,
                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        AuthAllResponseDto responseList = authService.getChallengerAuthList(challengeId, userDetails.getMember());
        return ResponseEntity.status(responseList.getStatus()).body(new BaseResponseDto<>(responseList.getData(),responseList.getMessage(),responseList.getStatus()));
    }
    /**
     * 리더가 챌린지 인증 허가 및 불가 처리하는 컨트롤러 메서드
     * @param requestDto DENIED, APPROVED
     * @param challengeId 선택한 챌린저 id
     * @param authId 변경하려는 인증 id
     * @param userDetails 로그인 멤버
     * @return 인증 수정 후 data, 인증 수정 성공 여부 message, httpStatus
     */
    @PutMapping("/{challengeId}/leader/auth/{authId}") // 챌린지 인증 허가 및 불가 처리(leader)
    public ResponseEntity<BaseResponseDto<AuthResponseDto>> updateLeaderAuth(@RequestBody AuthLeaderRequestDto requestDto,
                                                                @PathVariable Long challengeId,
                                                                @PathVariable Long authId,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        AuthDataResponseDto responseDto = authService.updateLeaderAuth(requestDto, challengeId, authId, userDetails.getMember());
        return ResponseEntity.status(responseDto.getStatus()).body(new BaseResponseDto<>(responseDto.getData(),responseDto.getMessage(),responseDto.getStatus()));
    }
    /**
     * 챌린저가 챌린지인증 수정하는 컨트롤러 메서드
     * @param requestDto authContents, authImageUrl, authVideoUrl, authStatus
     * @param file 수정하려는 파일
     * @param challengeId 수정하려는 인증의 챌린지 아이디
     * @param authId 수정하려는 인증아이디
     * @param userDetails 로그인 멤버
     * @return HttpStatus, (수정한 내용 data, 수정성공여부 message, httpStatus)
     */
    @PutMapping("/{challengeId}/auth/{authId}") // 챌린지 인증 수정(member)
    public ResponseEntity<BaseResponseDto<AuthResponseDto>> updateMemberAuth(@RequestPart("authUpdateData") AuthRequestDto requestDto,
                                                                @RequestPart(value = "authImageUrl", required = false) MultipartFile file,
                                                                @PathVariable Long challengeId,
                                                                @PathVariable Long authId,
                                                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        AuthDataResponseDto responseDto = authService.updateMemberAuth(requestDto, file, challengeId, authId, userDetails.getMember());
        return ResponseEntity.status(responseDto.getStatus()).body(new BaseResponseDto<>(responseDto.getData(),responseDto.getMessage(),responseDto.getStatus()));
    }
    /**
     * 챌린지인증 삭제하는 컨트롤러 메서드
     * @param challengeId 삭제하려는 인증의 챌린지 아이디
     * @param authId 삭제하려는 인증아이디
     * @param userDetails 로그인한 멤버
     * @return 삭제 성공여부 message, status
     */
    @DeleteMapping("/{challengeId}/auth/{authId}") // 챌린지 인증 수정(member)
    public ResponseEntity<BaseResponseDto<CreateResponseDto>> updateMemberAuth(@PathVariable Long challengeId,
                                                                               @PathVariable Long authId,
                                                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CreateResponseDto responseDto = authService.deleteAuth(challengeId, authId, userDetails.getMember());
        return ResponseEntity.status(responseDto.getStatus()).body(new BaseResponseDto<>(null, responseDto.getMessage(),responseDto.getStatus()));
    }

}
