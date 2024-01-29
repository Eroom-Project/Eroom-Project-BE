package com.sparta.eroomprojectbe.domain.challenge.controller;

import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeRequestDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.service.ChallengeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ChallengeController {

    private final ChallengeService challengeService;

    public ChallengeController(ChallengeService challengeService) {
        this.challengeService = challengeService;
    }

    @PostMapping("/challenge")
    public ResponseEntity<ChallengeResponseDto> createChallenge(@RequestBody ChallengeRequestDto requestDto){
        try {
            ChallengeResponseDto responseDto = challengeService.createChallenge(requestDto);
            return ResponseEntity.status(responseDto.getStatus())
                    .body(responseDto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChallengeResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }


}
