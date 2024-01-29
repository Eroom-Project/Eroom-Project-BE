package com.sparta.eroomprojectbe.domain.challenge.service;

import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeRequestDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeCreateResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.dto.ChallengeResponseDto;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public ChallengeService(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
    }

    public ChallengeCreateResponseDto createChallenge(ChallengeRequestDto requestDto) {
        try {
            Challenge challenge = new Challenge(requestDto);
            Challenge savedChallenge = challengeRepository.save(challenge);

            if (savedChallenge != null && savedChallenge.getChallengeId() != null) {
                return new ChallengeCreateResponseDto("챌린지 이룸 생성 성공", HttpStatus.CREATED);
            } else {
                return new ChallengeCreateResponseDto("챌린지 이룸 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ChallengeCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ChallengeResponseDto getChallenge(Long challengeId) {
        Optional<Challenge> optionalChallenge = challengeRepository.findById(challengeId);
        Challenge challenge = optionalChallenge.orElseThrow(
                ()-> new IllegalArgumentException("해당 챌린지가 존재하지 않습니다.")
        );
        ChallengeResponseDto challengeResponseDto = new ChallengeResponseDto(challenge, "해당 챌린지 조회 성공",
                HttpStatus.OK);
        return challengeResponseDto;
    }
}
