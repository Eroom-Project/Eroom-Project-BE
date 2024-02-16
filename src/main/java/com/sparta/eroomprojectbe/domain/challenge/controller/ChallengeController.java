package com.sparta.eroomprojectbe.domain.challenge.controller;

import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeAllResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeCreateResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeDataResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeRequestDto;
import com.sparta.eroomprojectbe.domain.challenge.service.ChallengeService;
import com.sparta.eroomprojectbe.domain.challenger.Role.CategoryRole;
import com.sparta.eroomprojectbe.domain.challenger.Role.SortRole;
import com.sparta.eroomprojectbe.global.jwt.UserDetailsImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }
    /**
     *
     * @param requestDto title, description, startDate, dueDate, frequency, limitation, thumbnailImgUrl
     * @param file 업로드 파일
     * @param userDetails 로그인한 유저
     * @return 챌린지 생성 성공 여부 message, httpStatus
     */
    @PostMapping("/challenge")
    public ResponseEntity<ChallengeCreateResponseDto> createChallenge(@RequestPart("challengeCreateData") ChallengeRequestDto requestDto,
                                                                      @RequestParam(value = "thumbnailImageUrl", required = false) MultipartFile file,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        try {
            ChallengeCreateResponseDto responseDto = challengeService.createChallenge(requestDto,file,userDetails.getMember());
            return ResponseEntity.status(responseDto.getStatus())
                    .body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChallengeCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * 선택한 챌린지 조회하는 컨트롤러 메서드
     *
     * @param challengeId 선택한 챌린지 아이디
     * @return 조회한 챌린지 data, 선택한 챌린지 조회 성공 여부 message, httpStatus
     */
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<ChallengeDataResponseDto> getChallenge(@PathVariable Long challengeId) {
        try {
            ChallengeDataResponseDto responseDto = challengeService.getChallenge(challengeId);
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ChallengeDataResponseDto(null, e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChallengeDataResponseDto(null, "오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    /**
     * 전체 챌린지를 조회하는 컨트롤러 메서드
     * @param sortBy 최신수, 인기순
     * @param category IT, FOREIGN_LANGUAGE, MATH, SCIENCE, HUMANITIES, ARTS_AND_PHYSICAL_EDUCATION, ETC
     * @param query 검색하려는 단어
     * @return 전체 챌린지 list, 조회 성공여부 메세지, httpStatus
     */
    @GetMapping("/challenge/all")
    public ResponseEntity<ChallengeAllResponseDto> getAllChallenge(@RequestParam(required = false) SortRole sortBy,
                                                                   @RequestParam(required = false) CategoryRole category,
                                                                   @RequestParam(required = false) String query) {

        ChallengeAllResponseDto responseDto;
        try {
            if (sortBy != null) {
                switch (sortBy) {
                    case POPULAR:
                        responseDto = challengeService.getPopularChallenge();
                        break;
                    case LATEST:
                        responseDto = challengeService.getLatestChallenge();
                        break;
                    default:
                        responseDto = challengeService.getLatestChallenge();
                }
            } else if (category != null) {
                responseDto = challengeService.getCategoryChallenge(category);
            } else if (query != null && !query.isEmpty()) {
                responseDto = challengeService.getQueryChallenge(query);
            } else {
                responseDto = challengeService.getLatestChallenge();
            }
            return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChallengeAllResponseDto(null, "발생된 오류: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    /**
     *
     * @param challengeId 수정하려는 챌린지 id
     * @param file 수정하려는 업로드 파일
     * @param requestDto  title, description, startDate, dueDate, frequency, limitation, thumbnailImgUrl
     * @param userDetails 로그인한 유저
     * @return 수정한 챌린지 내용, 수정 성공 여부 메세지, httpStatus
     */
    @PutMapping("/challenge/{challengeId}")
    public ResponseEntity<ChallengeDataResponseDto> updateChallenge(@PathVariable Long challengeId,
                                                                    @RequestPart(value = "thumbnailImageUrl", required = false) MultipartFile file,
                                                                    @RequestPart("ChallengeUpdateData") ChallengeRequestDto requestDto,
                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails){
        try {
            ChallengeDataResponseDto responseDto = challengeService.updateChallenge(challengeId, requestDto,file, userDetails.getMember());
            return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChallengeDataResponseDto(null, "수정 중 오류가 발생했습니다.",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    /**
     * 챌린지 삭제하는 컨트롤러 메서드
     * @param challengeId 삭제하려는 챌린지 id
     * @param userDetails 로그인한 유저
     * @return 삭제 성공 여부 메세지, httpStatus
     */
    @DeleteMapping("/challenge/{challengeId}")
    public ResponseEntity<ChallengeCreateResponseDto> deleteChallenge(@PathVariable Long challengeId,
                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        ChallengeCreateResponseDto responseDto = challengeService.deleteChallenge(challengeId, userDetails.getMember());
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }

}
