package com.sparta.eroomprojectbe.domain.challenge.controller;

import com.sparta.eroomprojectbe.domain.challenge.dto.*;
import com.sparta.eroomprojectbe.domain.challenge.service.ChallengeService;
import com.sparta.eroomprojectbe.domain.challenger.Role.CategoryRole;
import com.sparta.eroomprojectbe.domain.challenger.Role.SortRole;
import com.sparta.eroomprojectbe.global.jwt.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    /**
     * 챌린지를 생성하는 컨트롤러 메서드
     *
     * @param requestDto  title, description, startDate, dueDate, frequency, limitation, thumbnailImgUrl
     * @param file        업로드 파일
     * @param userDetails 로그인한 유저
     * @return 챌린지 생성 성공 여부 message, httpStatus
     */
    @PostMapping("/challenge")
    public ResponseEntity<BaseResponseDto<Void>> createChallenge(@RequestPart("challengeCreateData") ChallengeRequestDto requestDto,
                                                                 @RequestParam(value = "thumbnailImageUrl", required = false) MultipartFile file,
                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            CreateResponseDto responseDto = challengeService.createChallenge(requestDto, file, userDetails.getMember());
            return ResponseEntity.status(responseDto.getStatus()).body(new BaseResponseDto<>(null, responseDto.getMessage(), responseDto.getStatus()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponseDto<>(null, "에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * 선택한 챌린지 조회하는 컨트롤러 메서드
     *
     * @param challengeId 선택한 챌린지 아이디
     * @return 조회한 챌린지 data, 선택한 챌린지 조회 성공 여부 message, httpStatus
     */
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<BaseResponseDto<ChallengeLoginResponseDto>> getChallenge(@PathVariable Long challengeId,
                                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String loginMemberId;
        String loginMemberProfileImageUrl;
        String loginMemberNickname;
        if (userDetails != null) {
            loginMemberId = "" + userDetails.getMember().getMemberId();
            loginMemberProfileImageUrl = "" + userDetails.getMember().getProfileImageUrl();
            loginMemberNickname = "" + userDetails.getMember().getNickname();
        } else {
            loginMemberId = "No members logged in";
            loginMemberProfileImageUrl = "No members logged in";
            loginMemberNickname = "No members logged in";
        }
        try {
            ChallengeLoginResponseDto responseDto = challengeService.getChallenge(challengeId, loginMemberId, loginMemberProfileImageUrl, loginMemberNickname);
            return ResponseEntity.ok(new BaseResponseDto<>(responseDto, "선택한 챌린지 조회 성공", HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new BaseResponseDto<>(null, e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDto<>(null, "오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * 전체 챌린지를 조회하는 컨트롤러 메서드
     *
     * @param sortBy   최신순, 인기순
     * @param category IT, FOREIGN_LANGUAGE, MATH, SCIENCE, HUMANITIES, ARTS_AND_PHYSICAL_EDUCATION, ETC
     * @param query    검색하려는 단어
     * @param page     페이지 넘버(기본값 : 0)
     * @param size     원하는 챌린지 갯수(기본값 : 12)
     * @return 전체 챌린지 list, 조회 성공여부 메세지, httpStatus
     */
    @GetMapping("/challenge")
    public ResponseEntity<BaseResponseDto<Page<ChallengeResponseDto>>> getAllChallenge(
            @RequestParam(required = false) SortRole sortBy,
            @RequestParam(required = false) CategoryRole category,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        AllResponseDto responseDto;
        try {
            if (sortBy != null) {
                responseDto = switch (sortBy) {
                    case POPULAR -> challengeService.getPopularChallenge(page, size);
                    case LATEST -> challengeService.getLatestChallenge(page, size);
                    default -> challengeService.getLatestChallenge(page, size);
                };
            } else if (category != null) {
                responseDto = challengeService.getCategoryChallenge(category, page, size);
            } else if (query != null && !query.isEmpty()) {
                responseDto = challengeService.getQueryChallenge(query, page, size);
            } else {
                responseDto = challengeService.getLatestChallenge(page, size);
            }

            return ResponseEntity.status(responseDto.getStatus())
                    .body(new BaseResponseDto<>(responseDto.getData(), responseDto.getMessage(), responseDto.getStatus()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDto<>(null, "발생된 오류: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * @param challengeId 수정하려는 챌린지 id
     * @param file        수정하려는 업로드 파일
     * @param requestDto  title, description, startDate, dueDate, frequency, limitation, thumbnailImgUrl
     * @param userDetails 로그인한 유저
     * @return 수정한 챌린지 내용, 수정 성공 여부 메세지, httpStatus
     */
    @PutMapping("/challenge/{challengeId}")
    public ResponseEntity<BaseResponseDto<ChallengeLoginResponseDto>> updateChallenge(@PathVariable Long challengeId,
                                                                                      @RequestPart(value = "thumbnailImageUrl", required = false) MultipartFile file,
                                                                                      @RequestPart("ChallengeUpdateData") ChallengeRequestDto requestDto,
                                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {

        ChallengeUpdateDto responseDto = challengeService.updateChallenge(challengeId, requestDto, file, userDetails.getMember());
        return ResponseEntity.status(responseDto.getStatus()).body(new BaseResponseDto<>(responseDto.getData(), responseDto.getMessage(), responseDto.getStatus()));
    }

    /**
     * 챌린지 삭제하는 컨트롤러 메서드
     *
     * @param challengeId 삭제하려는 챌린지 id
     * @param userDetails 로그인한 유저
     * @return 삭제 성공 여부 메세지, httpStatus
     */
    @DeleteMapping("/challenge/{challengeId}")
    public ResponseEntity<BaseResponseDto<Void>> deleteChallenge(@PathVariable Long challengeId,
                                                                 @AuthenticationPrincipal UserDetailsImpl userDetails) {
        CreateResponseDto responseDto = challengeService.deleteChallenge(challengeId, userDetails.getMember());
        return ResponseEntity.status(responseDto.getStatus()).body(new BaseResponseDto<>(null, responseDto.getMessage(), responseDto.getStatus()));
    }

}
