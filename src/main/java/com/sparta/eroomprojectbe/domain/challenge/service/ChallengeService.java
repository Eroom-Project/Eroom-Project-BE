package com.sparta.eroomprojectbe.domain.challenge.service;

import com.sparta.eroomprojectbe.domain.challenge.dto.*;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenger.Role.CategoryRole;
import com.sparta.eroomprojectbe.domain.challenger.Role.ChallengerRole;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengerRepository challengerRepository;
//    private final MemberRepository memberRepository;
    private final ImageS3Service imageS3Service;

    public ChallengeService(ChallengeRepository challengeRepository, ChallengerRepository challengerRepository, ImageS3Service imageS3Service) {
        this.challengeRepository = challengeRepository;
        this.challengerRepository = challengerRepository;
        this.imageS3Service = imageS3Service;
    }
//    public ChallengeService(ChallengeRepository challengeRepository, ChallengerRepository challengerRepository, MemberRepository memberRepository) {
//        this.challengeRepository = challengeRepository;
//        this.challengerRepository = challengerRepository;
//        this.memberRepository = memberRepository;
//    }

    /**
     * 챌린지를 생성하는 서비스 메서드
     *
     * @param requestDto title, description, startDate, dueDate, frequency, limitation, thumbnailImgUrl
     * @return 성공여부 message, httpStatus
     */
    @Transactional
    public ChallengeCreateResponseDto createChallenge(ChallengeRequestDto requestDto, MultipartFile file) {
        try {
            Challenge challenge = new Challenge(requestDto, imageS3Service.saveFile(file));
            Challenge savedChallenge = challengeRepository.save(challenge);
            if (savedChallenge != null && savedChallenge.getChallengeId() != null) {
                savedChallenge.incrementAttendance();
                return new ChallengeCreateResponseDto("챌린지 이룸 생성 성공", HttpStatus.CREATED);
            } else {
                return new ChallengeCreateResponseDto("챌린지 이룸 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ChallengeCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    public ChallengeCreateResponseDto createChallenge(ChallengeRequestDto requestDto, Member member) {
//        try {
//            Member createMember = MemberRepository.findById(member.getMemberId()).ElseTrow(
//                ()-> new IllegalArgumentException("해당 사용자가 존재하지 않습니다.")
//            );

//            Challenge challenge = new Challenge(requestDto);
//            Challenge savedChallenge = challengeRepository.save(challenge);
//
//            if (savedChallenge != null && savedChallenge.getChallengeId() != null) {
//                Challenger challenger = new Challenger(challenge,member,ChallengerRole.LEADER);
//                challengerRepository.save(challenger);
//                return new ChallengeCreateResponseDto("챌린지 이룸 생성 성공", HttpStatus.CREATED);
//            } else {
//                return new ChallengeCreateResponseDto("챌린지 이룸 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        } catch (Exception e) {
//            return new ChallengeCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * 선택한 챌린지 조회하는 서비스 메서드
     *
     * @param challengeId 선택한 challenge 아이디
     * @return 선택한 챌린지 data, 성공여부 message, httpStatus
     */
    public ChallengeDataResponseDto getChallenge(Long challengeId) {
        Optional<Challenge> optionalChallenge = challengeRepository.findById(challengeId);
        Challenge challenge = optionalChallenge.orElseThrow(
                () -> new IllegalArgumentException("해당 챌린지가 존재하지 않습니다.")
        );

        Long currentAttendance = challengerRepository.countByChallenge_ChallengeId(challengeId);
        ChallengeResponseDto challengeResponseDto = new ChallengeResponseDto(challenge, currentAttendance, findLeaderId(challenge));
        ChallengeDataResponseDto responseDto = new ChallengeDataResponseDto(challengeResponseDto, "선택한 첼린지 조회 성공", HttpStatus.OK);
        return responseDto;
    }

    /**
     * 인기순으로 조회하는 서비스 명령어
     *
     * @return 인기순으로 정렬된 챌린지 리스트, 조회성공여부 메세지, httpStatus
     */
    public ChallengeAllResponseDto getPopularChallenge() {
        try {
            List<Challenge> popularChallenges = challengeRepository.findChallengesOrderedByPopularity();
            List<ChallengeResponseDto> popularChallengeResponseDtoList = popularChallenges.stream()
                    .map(challenge -> new ChallengeResponseDto(challenge, calculateCurrentAttendance(challenge), findLeaderId(challenge)))
                    .collect(Collectors.toList());
            return new ChallengeAllResponseDto(popularChallengeResponseDtoList, "인기순으로 조회 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ChallengeAllResponseDto(null, "인기순으로 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 카테고리별로 조회하는 서비스 메서드
     *
     * @param category IT, 외국어, 수학, 과학, 인문, 예체능, 기타
     * @return param값과 일치하는 카테고리를 가진 챌린지 리스트, 조회 성공 여부 메세지, httpStatus
     */
    public ChallengeAllResponseDto getCategoryChallenge(CategoryRole category) {
        try {
            List<Challenge> categoryChallenges = challengeRepository.findByCategory(category.name());
            List<ChallengeResponseDto> categoryChallengeResponseDtoList = categoryChallenges.stream()
                    .map(challenge -> new ChallengeResponseDto(challenge, calculateCurrentAttendance(challenge), findLeaderId(challenge)))
                    .collect(Collectors.toList());
            return new ChallengeAllResponseDto(categoryChallengeResponseDtoList, "카테고리별로 챌린지 조회 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ChallengeAllResponseDto(null, "카테고리별로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 검색어로 조회하는 서비스 명령어
     *
     * @param query 검색하려는 키워드
     * @return title, category, description에 query값을 포함하는 챌린지 리스트, 조회성공여부 메세지, httpStatus
     */
    public ChallengeAllResponseDto getQueryChallenge(String query) {
        try {
            List<Challenge> queryChallenges = challengeRepository.findByCategoryContainingOrTitleContainingOrDescriptionContaining(query, query, query);
            List<ChallengeResponseDto> queryChallengeResponseDtoList = queryChallenges.stream()
                    .map(challenge -> new ChallengeResponseDto(challenge, calculateCurrentAttendance(challenge), findLeaderId(challenge)))
                    .collect(Collectors.toList());
            return new ChallengeAllResponseDto(queryChallengeResponseDtoList, "키워드로 챌린지 조회 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ChallengeAllResponseDto(null, "키워드로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 최신순으로 조회하는 서비스 메서드
     *
     * @return 최신순을 기준으로 하여 정렬하는 챌린지 리스트, 조회성공여부 메세지, httpStatus
     */
    public ChallengeAllResponseDto getLatestChallenge() {
        try {
            List<Challenge> latestChallenges = challengeRepository.findByOrderByCreatedAtDesc();
            List<ChallengeResponseDto> latestChallengeResponseDtoList = latestChallenges.stream()
                    .map(challenge -> new ChallengeResponseDto(challenge, calculateCurrentAttendance(challenge), findLeaderId(challenge)))
                    .collect(Collectors.toList());
            return new ChallengeAllResponseDto(latestChallengeResponseDtoList, "최신순으로 챌린지 조회 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ChallengeAllResponseDto(null, "최신순으로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 챌린지 수정하는 서비스 메서드
     *
     * @param challengeId 수정을 할 챌린지 id
     *                    //     * @param requestDto title, description, startDate, dueDate, frequency, limitation, thumbnailImgUrl
     * @return 수정한 챌린지 data, 수정 성공여부 message, httpStatus
     */
    @Transactional
    public ChallengeDataResponseDto updateChallenge(Long challengeId, ChallengeRequestDto requestDto, MultipartFile file) {
        try {
            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                    () -> new IllegalArgumentException("선택한 챌린지는 존재하지 않습니다.")
            );
            // 이미지 새로 바꿀때와 안바꿀때를 나눠야 하나?02/08
            challenge.update(requestDto, imageS3Service.updateFile(challenge.getThumbnailImageUrl(),file));
            Long currentAttendance = challengerRepository.countByChallenge_ChallengeId(challengeId);
            ChallengeResponseDto responseDto = new ChallengeResponseDto(challenge, currentAttendance);
            return new ChallengeDataResponseDto(responseDto, "챌린지 수정 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ChallengeDataResponseDto(null, "챌린지 수정 중 오류 발생: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @Transactional
//    public ChallengeDataResponseDto updateChallenge(Long challengeId, ChallengeRequestDto requestDto, Member member) {
//        try {
//            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
//                    () -> new IllegalArgumentException("선택한 챌린지는 존재하지 않습니다.")
//            )
//            if(member.getMemberId() != findLeaderId(challenge)){
//                  throw new IllegalArgumentException("해당 챌린지를 생성한 사용자가 아닙니다")
//            }
//            challenge.update(requestDto);
//            Long currentAttendance = challengerRepository.countByChallenge_ChallengeId(challengeId);
//            ChallengeResponseDto responseDto = new ChallengeResponseDto(challenge,currentAttendance, member.getMemberId());
//            return new ChallengeDataResponseDto(responseDto, "챌린지 수정 성공", HttpStatus.OK);
//        } catch (Exception e) {
//            return new ChallengeDataResponseDto(null, "챌린지 수정 중 오류 발생: " + e.getMessage(),
//                    HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * 선택한 챌린지를 삭제하는 서비스 메서드
     *
     * @param challengeId 삭제하려는 챌린지 id
     * @return 삭제 성공여부 메세지, httpStatus
     */
    public ChallengeCreateResponseDto deleteChallenge(Long challengeId) {
        try {
            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                    () -> new IllegalArgumentException("선택한 챌린지가 존재하지 않습니다.")
            );
            challengeRepository.delete(challenge);
            return new ChallengeCreateResponseDto("챌린지 이룸 삭제 성공", HttpStatus.OK);
        } catch (DataAccessException e) {
            return new ChallengeCreateResponseDto("오류: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    public ChallengeCreateResponseDto deleteChallenge(Long challengeId, Member member) {
//        try {
//            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
//                    () -> new IllegalArgumentException("선택한 챌린지가 존재하지 않습니다.")
//            );
//            if(member.getMemberId() != findLeaderId(challenge)){
//                  throw new IllegalArgumentException("해당 챌린지를 생성한 사용자가 아닙니다")
//            }
//            challengeRepository.delete(challenge);
//            return new ChallengeCreateResponseDto("챌린지 이룸 삭제 성공", HttpStatus.OK);
//        } catch (DataAccessException e) {
//            return new ChallengeCreateResponseDto("오류: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * 현재참여인원을 계산해 주는 메서드
     *
     * @param challenge 현재 챌린지
     * @return 현재 참여중인 참여자 수
     */
    private Long calculateCurrentAttendance(Challenge challenge) {
        return challengerRepository.countByChallenge(challenge);
    }

    /**
     * 챌린지를 작성한 멤버의 memberId를 가져옴
     *
     * @param challenge 조회하려는 챌린지
     * @return 선택한 챌린지의 생성자 memberid
     */
    private Long findLeaderId(Challenge challenge) {
        return challengerRepository.findCreatorMemberIdByChallengeId(challenge.getChallengeId()).orElse(null);
    }
}
