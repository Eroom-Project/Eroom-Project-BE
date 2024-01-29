package com.sparta.eroomprojectbe.domain.challenge.controller;

import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeRequestDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeCreateResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.service.ChallengeService;
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
     * 챌린지 생성 메서드
     * @param requestDto title, description, startDate, dueDate, frequency, limitation, thumbnailImgUrl
     * @return message, HttpStatus
     */
    @PostMapping("/challenge")
    public ResponseEntity<ChallengeCreateResponseDto> createChallenge(@RequestBody ChallengeRequestDto requestDto){
        try {
            ChallengeCreateResponseDto responseDto = challengeService.createChallenge(requestDto);
            return ResponseEntity.status(responseDto.getStatus())
                    .body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChallengeCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * 선택한 챌린지 조회하기
     * @param challengeId
     * @return 조회한 챌린지 data, message, httpStatus
     */
    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<ChallengeResponseDto> getChallenge(@PathVariable Long challengeId){
        try {
            ChallengeResponseDto responseDto = challengeService.getChallenge(challengeId);
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ChallengeResponseDto(null, e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChallengeResponseDto(null, "오류 발생: " + e.getMessage(),
                            HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

}
