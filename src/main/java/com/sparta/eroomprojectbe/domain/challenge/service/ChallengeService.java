package com.sparta.eroomprojectbe.domain.challenge.service;

import com.sparta.eroomprojectbe.domain.challenge.dto.*;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengerRepository challengerRepository;

    public ChallengeService(ChallengeRepository challengeRepository, ChallengerRepository challengerRepository) {
        this.challengeRepository = challengeRepository;
        this.challengerRepository = challengerRepository;
    }

    /**
     * 챌린지를 생성하는 서비스 메서드
     * @param requestDto title, description, startDate, dueDate, frequency, limitation, thumbnailImgUrl
     * @return 성공여부 message, httpStatus
     */
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

    /**
     * 선택한 챌린지 조회하는 서비스 메서드
     * @param challengeId 선택한 challenge 아이디
     * @return 선택한 챌린지 data, 성공여부 message, httpStatus
     */
    public ChallengeDataResponseDto getChallenge(Long challengeId) {
        Optional<Challenge> optionalChallenge = challengeRepository.findById(challengeId);
        Challenge challenge = optionalChallenge.orElseThrow(
                ()-> new IllegalArgumentException("해당 챌린지가 존재하지 않습니다.")
        );
        Long currentAttendance = challengerRepository.countByChallenge_ChallengeId(challengeId);
        ChallengeResponseDto challengeResponseDto = new ChallengeResponseDto(challenge, currentAttendance);
        ChallengeDataResponseDto responseDto = new ChallengeDataResponseDto(challengeResponseDto, "선택한 첼린지 조회 성공", HttpStatus.OK);
        return responseDto;
    }
    //인기순으로 조회
//    public ChallengeAllResponseDto getPopularChallenge() {
//        try {
//            List<Challenge> popularChallenges = challengeRepository.findChallengesOrderedByPopularity();
//            return new ChallengeAllResponseDto(popularChallenges, "인기순으로 조회 성공", HttpStatus.OK);
//        } catch (Exception e) {
//            // Handle exceptions and return an appropriate ChallengeAllResponseDto
//            return new ChallengeAllResponseDto(null, "인기순으로 조히 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
    public ChallengeAllResponseDto getPopularChallenge() {
        try {
            List<Challenge> popularChallenges = challengeRepository.findChallengesOrderedByPopularity();
            List<ChallengeResponseDto> popularChallengeResponseDtoList = popularChallenges.stream()
                    .map(challenge -> new ChallengeResponseDto(challenge, calculateCurrentAttendance(challenge)))
                    .collect(Collectors.toList());
            return new ChallengeAllResponseDto(popularChallengeResponseDtoList, "인기순으로 조회 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ChallengeAllResponseDto(null, "인기순으로 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    public ChallengeAllResponseDto getCategoryChallenge(String category) {
//        try {
//            List<Challenge> categoryChallenges = challengeRepository.findByCategory(category);
//            return new ChallengeAllResponseDto(categoryChallenges, "카테고리별로 챌린지 조회 성공", HttpStatus.OK);
//        } catch (Exception e) {
//            // Handle exceptions and return an appropriate ChallengeAllResponseDto
//            return new ChallengeAllResponseDto(null, "카테고리별로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public ChallengeAllResponseDto getQueryChallenge(String query) {
//        try {
//            List<Challenge> categoryChallenges = challengeRepository.findByCategoryContainingOrTitleContainingOrDescriptionContaining(query,query,query);
//            return new ChallengeAllResponseDto(categoryChallenges, "키워드로 챌린지 조회 성공", HttpStatus.OK);
//        } catch (Exception e) {
//            // Handle exceptions and return an appropriate ChallengeAllResponseDto
//            return new ChallengeAllResponseDto(null, "키워드로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    public ChallengeAllResponseDto getLatestChallenge() {
//        try {
//            List<Challenge> categoryChallenges = challengeRepository.findByOrderByCreatedAtDesc();
//            return new ChallengeAllResponseDto(categoryChallenges, "최신순으로 챌린지 조회 성공", HttpStatus.OK);
//        } catch (Exception e) {
//            // Handle exceptions and return an appropriate ChallengeAllResponseDto
//            return new ChallengeAllResponseDto(null, "최신순으로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
public ChallengeAllResponseDto getCategoryChallenge(String category) {
    try {
        List<Challenge> categoryChallenges = challengeRepository.findByCategory(category);
        List<ChallengeResponseDto> categoryChallengeResponseDtoList = categoryChallenges.stream()
                .map(challenge -> new ChallengeResponseDto(challenge, calculateCurrentAttendance(challenge)))
                .collect(Collectors.toList());
        return new ChallengeAllResponseDto(categoryChallengeResponseDtoList, "카테고리별로 챌린지 조회 성공", HttpStatus.OK);
    } catch (Exception e) {
        return new ChallengeAllResponseDto(null, "카테고리별로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

    public ChallengeAllResponseDto getQueryChallenge(String query) {
        try {
            List<Challenge> queryChallenges = challengeRepository.findByCategoryContainingOrTitleContainingOrDescriptionContaining(query, query, query);
            List<ChallengeResponseDto> queryChallengeResponseDtoList = queryChallenges.stream()
                    .map(challenge -> new ChallengeResponseDto(challenge, calculateCurrentAttendance(challenge)))
                    .collect(Collectors.toList());
            return new ChallengeAllResponseDto(queryChallengeResponseDtoList, "키워드로 챌린지 조회 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ChallengeAllResponseDto(null, "키워드로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ChallengeAllResponseDto getLatestChallenge() {
        try {
            List<Challenge> latestChallenges = challengeRepository.findByOrderByCreatedAtDesc();
            List<ChallengeResponseDto> latestChallengeResponseDtoList = latestChallenges.stream()
                    .map(challenge -> new ChallengeResponseDto(challenge, calculateCurrentAttendance(challenge)))
                    .collect(Collectors.toList());
            return new ChallengeAllResponseDto(latestChallengeResponseDtoList, "최신순으로 챌린지 조회 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ChallengeAllResponseDto(null, "최신순으로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Long calculateCurrentAttendance(Challenge challenge) {
        return challengerRepository.countByChallenge(challenge);
    }



    /**
     * 챌린지 수정하는 서비스 메서드
     * @param challengeId 수정을 할 챌린지 id
//     * @param requestDto title, description, startDate, dueDate, frequency, limitation, thumbnailImgUrl
     * @return 수정한 챌린지 data, 수정 성공여부 message, httpStatus
     */
//    @Transactional
//    public ChallengeResponseDto updateChallenge(Long challengeId, ChallengeRequestDto requestDto) {
//        try {
//            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
//                    ()-> new IllegalArgumentException("선택한 챌린지는 존재하지 않습니다.")
//            );
//            challenge.update(requestDto);
//            return new ChallengeResponseDto(challenge,"챌린지 수정 성공", HttpStatus.OK);
//        } catch (Exception e) {
//            return new ChallengeResponseDto(null, "챌린지 수정 중 오류 발생: " + e.getMessage(),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    public ChallengeCreateResponseDto deleteChallenge(Long challengeId) {
        try {
            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                    ()-> new IllegalArgumentException("선택한 챌린지가 존재하지 않습니다.")
            );
            challengeRepository.delete(challenge);
            return new ChallengeCreateResponseDto("챌린지 이룸 삭제 성공", HttpStatus.OK);
        } catch (DataAccessException e){
            return new ChallengeCreateResponseDto("오류: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
