package com.sparta.eroomprojectbe.domain.challenge.controller;

import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeAllResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeCreateResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeDataResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeRequestDto;
import com.sparta.eroomprojectbe.domain.challenge.service.ChallengeService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    /**
     * 챌린지 생성하는 컨트롤러 메서드
     *
     * @param requestDto title, description, startDate, dueDate, frequency, limitation, thumbnailImgUrl
     * @return message, HttpStatus
     */
    @PostMapping("/challenge")
    public ResponseEntity<ChallengeCreateResponseDto> createChallenge(@RequestBody ChallengeRequestDto requestDto) {
        try {
            ChallengeCreateResponseDto responseDto = challengeService.createChallenge(requestDto);
            return ResponseEntity.status(responseDto.getStatus())
                    .body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChallengeCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
//    @PostMapping("/challenge")
//    public ResponseEntity<ChallengeCreateResponseDto> createChallenge(@RequestBody ChallengeRequestDto requestDto,
//                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
//        try {
//            ChallengeCreateResponseDto responseDto = challengeService.createChallenge(requestDto,userDetails.getMember());
//            return ResponseEntity.status(responseDto.getStatus())
//                    .body(responseDto);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ChallengeCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
//        }
//    }

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
     * @return 전체 챌린지 list, 조회 성공여부 메세지, httpStatus
     */
    @GetMapping("/challenge")
    public ResponseEntity<ChallengeAllResponseDto> getAllChallenge(@RequestParam(required = false) String sortBy,
                                                                   @RequestParam(required = false) String category,
                                                                   @RequestParam(required = false) String query) {

        ChallengeAllResponseDto responseDto;
        try {
            if (sortBy != null && !sortBy.isEmpty()) {
                if ("popular".equals(sortBy)) {
                    responseDto = challengeService.getPopularChallenge();
                } else {
                    responseDto = challengeService.getLatestChallenge();
                }
            } else if (category != null && !category.isEmpty()) {
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
     * 챌린지 수정을 하는 컨트롤러 메서드
     *
     * @param challengeId 수정하려는 챌린지 id
     *                    //     * @param requestDto title, description, startDate, dueDate, frequency, limitation, thumbnailImgUrl
     * @return 수정한 챌린지 내용, 수정 성공 여부 메세지, httpStatus
     */
    @PutMapping("/challenge/{challengeId}")
    public ResponseEntity<ChallengeDataResponseDto> updateChallenge(@PathVariable Long challengeId,
                                                                    @RequestBody ChallengeRequestDto requestDto) {
        try {
            ChallengeDataResponseDto responseDto = challengeService.updateChallenge(challengeId, requestDto);
            return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChallengeDataResponseDto(null, "수정 중 오류가 발생했습니다.",
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
//    @PutMapping("/challenge/{challengeId}")
//    public ResponseEntity<ChallengeDataResponseDto> updateChallenge(@PathVariable Long challengeId,
//                                                                    @RequestBody ChallengeRequestDto requestDto,
//                                                                    @AuthenticationPrincipal UserDetailsImpl userDetails){
//        try {
//            ChallengeDataResponseDto responseDto = challengeService.updateChallenge(challengeId, requestDto,userDetails.getMember());
//            return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
//        } catch (Exception e){
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(new ChallengeDataResponseDto(null, "수정 중 오류가 발생했습니다.",
//                            HttpStatus.INTERNAL_SERVER_ERROR));
//        }
//    }


    /**
     * 챌린지 삭제하는 컨트롤러 메서드
     *
     * @param challengeId 삭제하려는 챌린지 id
     * @return 삭제 성공 여부 메세지, httpStatus
     */
    @DeleteMapping("/challenge/{challengeId}")
    public ResponseEntity<ChallengeCreateResponseDto> deleteChallenge(@PathVariable Long challengeId) {
        ChallengeCreateResponseDto responseDto = challengeService.deleteChallenge(challengeId);
        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
    }
//    @DeleteMapping("/challenge/{challengeId}")
//    public ResponseEntity<ChallengeCreateResponseDto> deleteChallenge(@PathVariable Long challengeId,
//                                                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
//        ChallengeCreateResponseDto responseDto = challengeService.deleteChallenge(challengeId, userDetials.getMember());
//        return ResponseEntity.status(responseDto.getStatus()).body(responseDto);
//    }

}
