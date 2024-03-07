package com.sparta.eroomprojectbe.domain.challenge.service;

import com.sparta.eroomprojectbe.domain.challenge.dto.*;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenger.Role.CategoryRole;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengerRepository challengerRepository;
    private final MemberRepository memberRepository;
    private final ImageS3Service imageS3Service;

    public ChallengeService(ChallengeRepository challengeRepository, ChallengerRepository challengerRepository, MemberRepository memberRepository, ImageS3Service imageS3Service) {
        this.challengeRepository = challengeRepository;
        this.challengerRepository = challengerRepository;
        this.memberRepository = memberRepository;
        this.imageS3Service = imageS3Service;
    }

    /**
     * 챌린지를 생성하는 서비스 메서드
     *
     * @param requestDto title, description, startDate, dueDate, frequency, limitation
     * @param file       업로드할 파일
     * @param member     로그인 한 멤버
     * @return 챌린지 생성 성공여부 message, httpStatus
     */
    @Transactional
    public CreateResponseDto createChallenge(ChallengeRequestDto requestDto, MultipartFile file, Member member) {
        try {
            Member createMember = memberRepository.findById(member.getMemberId()).orElseThrow(
                    () -> new IllegalArgumentException("해당 맴버가 존재하지 않습니다.")
            );
            String saveFile;
            if (file != null) {
                saveFile = imageS3Service.saveFile(file);
            } else {
                String[] randomImageUrls = {
                        "https://github.com/Eroom-Project/Eroom-Project-BE/assets/148944859/32d9e5fb-6c72-4bb0-a212-151b4dedbe46",
                        "https://github.com/Eroom-Project/Eroom-Project-BE/assets/148944859/58ffd7e2-3626-40f7-9cdd-3b44f277b0cf",
                        "https://github.com/Eroom-Project/Eroom-Project-BE/assets/148944859/34bcaa30-85cb-4aa5-9ec9-16cfffaf42fb",
                        "https://github.com/Eroom-Project/Eroom-Project-BE/assets/148944859/39c2edbb-630d-4789-a665-0153d596eab5",
                        "https://github.com/Eroom-Project/Eroom-Project-BE/assets/148944859/2f74e8d5-826a-4770-a991-77d72015339e"
                };

                Random random = new Random();
                saveFile = randomImageUrls[random.nextInt(randomImageUrls.length)];
            }
            Challenge challenge = new Challenge(requestDto, saveFile);
            Challenge savedChallenge = challengeRepository.save(challenge);
            if (savedChallenge.getChallengeId() != null) {
                Challenger challenger = new Challenger(challenge, member, ChallengerRole.LEADER);
                challengerRepository.save(challenger);
                challenge.incrementAttendance();
                return new CreateResponseDto("챌린지 이룸 생성 성공", HttpStatus.CREATED);
            } else {
                return new CreateResponseDto("챌린지 이룸 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new CreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 선택한 챌린지 조회하는 서비스 메서드
     *
     * @param challengeId   선택한 challenge 아이디
     * @param loginMemberId
     * @return 선택한 챌린지 data, 성공여부 message, httpStatus
     */
    public ChallengeLoginResponseDto getChallenge(Long challengeId, String loginMemberId, String loginMemberProfileImageUrl, String loginMemberNickname) {
        Optional<Challenge> optionalChallenge = challengeRepository.findById(challengeId);
        Challenge challenge = optionalChallenge.orElseThrow(
                () -> new IllegalArgumentException("해당 챌린지가 존재하지 않습니다.")
        );
        ChallengeResponseDto challengeResponseDto = new ChallengeResponseDto(challenge, findLeaderId(challenge), findCurrentMemberIds(optionalChallenge.get()));
        ChallengeLoginResponseDto challengeLoginResponseDto = new ChallengeLoginResponseDto(challengeResponseDto, loginMemberId, loginMemberProfileImageUrl, loginMemberNickname);
        return challengeLoginResponseDto;
    }

//    /**
//     * 인기순으로 조회하는 서비스 명령어
//     *
//     * @return 인기순으로 정렬된 챌린지 리스트, 조회성공여부 메세지, httpStatus
//     */
//    public AllResponseDto getPopularChallenge() {
//        try {
//            List<Challenge> popularChallenges = challengeRepository.findChallengesOrderedByPopularity();
//            List<ChallengeResponseDto> popularChallengeResponseDtoList = popularChallenges.stream()
//                    .map(challenge -> new ChallengeResponseDto(challenge, findLeaderId(challenge), findCurrentMemberIds(challenge)))
//                    .collect(Collectors.toList());
//            return new AllResponseDto(popularChallengeResponseDtoList, "인기순으로 조회 성공", HttpStatus.OK);
//        } catch (Exception e) {
//            return new AllResponseDto(null, "인기순으로 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    /**
//     * 카테고리별로 조회하는 서비스 메서드
//     *
//     * @param category IT, 외국어, 수학, 과학, 인문, 예체능, 기타
//     * @return param값과 일치하는 카테고리를 가진 챌린지 리스트, 조회 성공 여부 메세지, httpStatus
//     */
//    public AllResponseDto getCategoryChallenge(CategoryRole category) {
//        try {
//            List<Challenge> categoryChallenges = challengeRepository.findByCategory(category.name());
//            List<ChallengeResponseDto> categoryChallengeResponseDtoList = categoryChallenges.stream()
//                    .map(challenge -> new ChallengeResponseDto(challenge, findLeaderId(challenge), findCurrentMemberIds(challenge)))
//                    .collect(Collectors.toList());
//            return new AllResponseDto(categoryChallengeResponseDtoList, "카테고리별로 챌린지 조회 성공", HttpStatus.OK);
//        } catch (Exception e) {
//            return new AllResponseDto(null, "카테고리별로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    /**
//     * 검색어로 조회하는 서비스 명령어
//     *
//     * @param query 검색하려는 키워드
//     * @return title, category, description에 query값을 포함하는 챌린지 리스트, 조회성공여부 메세지, httpStatus
//     */
//    public AllResponseDto getQueryChallenge(String query) {
//        try {
//            List<Challenge> queryChallenges = challengeRepository.findByCategoryContainingOrTitleContainingOrDescriptionContaining(query, query, query);
//            List<ChallengeResponseDto> queryChallengeResponseDtoList = queryChallenges.stream()
//                    .map(challenge -> new ChallengeResponseDto(challenge, findLeaderId(challenge), findCurrentMemberIds(challenge)))
//                    .collect(Collectors.toList());
//            return new AllResponseDto(queryChallengeResponseDtoList, "키워드로 챌린지 조회 성공", HttpStatus.OK);
//        } catch (Exception e) {
//            return new AllResponseDto(null, "키워드로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    /**
//     * 최신순으로 조회하는 서비스 메서드
//     *
//     * @return 최신순을 기준으로 하여 정렬하는 챌린지 리스트, 조회성공여부 메세지, httpStatus
//     */
//    public AllResponseDto getLatestChallenge() {
//        try {
//            List<Challenge> latestChallenges = challengeRepository.findByOrderByCreatedAtDesc();
//            List<ChallengeResponseDto> latestChallengeResponseDtoList = latestChallenges.stream()
//                    .map(challenge -> new ChallengeResponseDto(challenge, findLeaderId(challenge), findCurrentMemberIds(challenge)))
//                    .collect(Collectors.toList());
//            return new AllResponseDto(latestChallengeResponseDtoList, "최신순으로 챌린지 조회 성공", HttpStatus.OK);
//        } catch (Exception e) {
//            return new AllResponseDto(null, "최신순으로 챌린지 조회 중 오류 발생: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * 선택한 챌린지를 수정하는 서비스 메서드
     *
     * @param challengeId 수정을 할 챌린지 id
     * @param requestDto  requestDto title, description, startDate, dueDate, frequency, limitation
     * @param file        수정하려고 업로드할 파일
     * @param member      로그인 한 멤버
     * @return 수정한 챌린지 data, 수정 성공여부 message, httpStatus
     */
    @Transactional
    public ChallengeUpdateDto updateChallenge(Long challengeId, ChallengeRequestDto requestDto,
                                              MultipartFile file, Member member) throws IOException {
        try {
            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                    () -> new IllegalArgumentException("선택한 챌린지는 존재하지 않습니다.")
            );
            if (member.getMemberId() != findLeaderId(challenge).getMemberId()) {
                throw new IllegalArgumentException("해당 챌린지를 생성한 사용자가 아닙니다");
            }
            String updateFile;
            if (file == null) {
                updateFile = challenge.getThumbnailImageUrl();
            } else {
                updateFile = imageS3Service.updateFile(challenge.getThumbnailImageUrl(), file);
            }
            challenge.update(requestDto, updateFile);
            ChallengeResponseDto responseDto = new ChallengeResponseDto(challenge, member, findCurrentMemberIds(challenge));
            ChallengeLoginResponseDto loginResponseDto = new ChallengeLoginResponseDto(responseDto, "" + member.getMemberId());
            return new ChallengeUpdateDto(loginResponseDto, "챌린지 수정 성공", HttpStatus.OK);
        } catch (Exception e) {
            return new ChallengeUpdateDto(null, "챌린지 수정 중 오류 발생:" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 선택한 첼린지를 삭제하는 서비스 메서드
     *
     * @param challengeId 삭제하려는 챌린지 id
     * @param member      로그인한 멤버
     * @return 삭제 성공여부 메세지, httpStatus
     */
    public CreateResponseDto deleteChallenge(Long challengeId, Member member) {
        try {
            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                    () -> new IllegalArgumentException("선택한 챌린지가 존재하지 않습니다.")
            );
            if (member.getMemberId() != findLeaderId(challenge).getMemberId()) {
                throw new IllegalArgumentException("해당 챌린지를 생성한 사용자가 아닙니다");
            }
            imageS3Service.deleteFile(challenge.getThumbnailImageUrl());
            challengeRepository.delete(challenge);
            return new CreateResponseDto("챌린지 이룸 삭제 성공", HttpStatus.OK);
        } catch (DataAccessException e) {
            return new CreateResponseDto("오류: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 챌린지를 작성한 멤버의 memberId를 가져옴
     *
     * @param challenge 조회하려는 챌린지
     * @return 선택한 챌린지의 생성자 memberid
     */
    private Member findLeaderId(Challenge challenge) {
        return challengerRepository.findCreatorMemberByChallengeId(challenge.getChallengeId()).orElse(null);
    }

    private List<Long> findCurrentMemberIds(Challenge challenge) {
        return challengerRepository.findMemberIdsByChallenge(challenge);
    }

    public AllResponseDto getPopularChallenge(int page, int size) {
        try {
            // Pageable 객체를 생성하여 페이징 처리
            Pageable pageable = PageRequest.of(page, size);

            // JPA Repository를 사용하여 페이징된 데이터를 가져옴
            Page<Challenge> popularChallengesPage = challengeRepository.findChallengesOrderedByPopularity(pageable);

            // 가져온 페이지에서 ChallengeResponseDto로 변환
            List<ChallengeResponseDto> popularChallengeResponseDtoList = popularChallengesPage.getContent().stream()
                    .map(challenge -> new ChallengeResponseDto(challenge, findLeaderId(challenge), findCurrentMemberIds(challenge)))
                    .collect(Collectors.toList());

            // 페이징된 데이터와 메시지를 포함한 응답 생성
            Page<ChallengeResponseDto> challengeResponseDtoPage = new PageImpl<>(popularChallengeResponseDtoList, pageable, popularChallengesPage.getTotalElements());
            return new AllResponseDto(challengeResponseDtoPage,"챌린지 인기순으로 조회 성공",HttpStatus.OK);
        } catch (Exception e) {
            return new AllResponseDto(null,"인기순으로 챌린지 조회 중 오류 발생: " + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AllResponseDto getLatestChallenge(int page, int size) {
        try {
            // Pageable 객체를 생성하여 페이징 처리
            Pageable pageable = PageRequest.of(page, size);

            // JPA Repository를 사용하여 페이징된 데이터를 가져옴
            Page<Challenge> latestChallengesPage = challengeRepository.findByOrderByCreatedAtDesc(pageable);

            // 가져온 페이지에서 ChallengeResponseDto로 변환
            List<ChallengeResponseDto> latestChallengeResponseDtoList = latestChallengesPage.getContent().stream()
                    .map(challenge -> new ChallengeResponseDto(challenge, findLeaderId(challenge), findCurrentMemberIds(challenge)))
                    .collect(Collectors.toList());

            // 페이징된 데이터와 메시지를 포함한 응답 생성
            Page<ChallengeResponseDto> challengeResponseDtoPage = new PageImpl<>(latestChallengeResponseDtoList, pageable, latestChallengesPage.getTotalElements());
            return new AllResponseDto(challengeResponseDtoPage,"챌린지 최신순으로 조회 성공",HttpStatus.OK);
        } catch (Exception e) {
            return new AllResponseDto(null,"최신순으로 챌린지 조회 중 오류 발생: " + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AllResponseDto getCategoryChallenge(CategoryRole category, int page, int size) {
        try {
            // Pageable 객체를 생성하여 페이징 처리
            Pageable pageable = PageRequest.of(page, size);

            // JPA Repository를 사용하여 페이징된 데이터를 가져옴
            Page<Challenge> categoryChallengesPage = challengeRepository.findByCategory(category.name(), pageable);

            // 가져온 페이지에서 ChallengeResponseDto로 변환
            List<ChallengeResponseDto> categoryChallengeResponseDtoList = categoryChallengesPage.getContent().stream()
                    .map(challenge -> new ChallengeResponseDto(challenge, findLeaderId(challenge), findCurrentMemberIds(challenge)))
                    .collect(Collectors.toList());

            // 페이징된 데이터와 메시지를 포함한 응답 생성
            Page<ChallengeResponseDto> challengeResponseDtoPage = new PageImpl<>(categoryChallengeResponseDtoList, pageable, categoryChallengesPage.getTotalElements());
            return new AllResponseDto(challengeResponseDtoPage,"카테고리별로 조회 성공",HttpStatus.OK);
        } catch (Exception e) {
            return new AllResponseDto(null,"카테고리별로 챌린지 조회 중 오류 발생: " + e.getMessage(),HttpStatus.OK);
        }
    }
    public AllResponseDto getQueryChallenge(String query, int page, int size) {
        try {
            // Pageable 객체를 생성하여 페이징 처리
            Pageable pageable = PageRequest.of(page, size);

            // JPA Repository를 사용하여 페이징된 데이터를 가져옴
            Page<Challenge> queryChallengesPage = challengeRepository.findByCategoryContainingOrTitleContainingOrDescriptionContaining(query, query, query, pageable);

            // 가져온 페이지에서 ChallengeResponseDto로 변환
            List<ChallengeResponseDto> queryChallengeResponseDtoList = queryChallengesPage.getContent().stream()
                    .map(challenge -> new ChallengeResponseDto(challenge, findLeaderId(challenge), findCurrentMemberIds(challenge)))
                    .collect(Collectors.toList());

            // 페이징된 데이터와 메시지를 포함한 응답 생성
            Page<ChallengeResponseDto> challengeResponseDtos = new PageImpl<>(queryChallengeResponseDtoList, pageable, queryChallengesPage.getTotalElements());
            return new AllResponseDto(challengeResponseDtos,"키워드로 챌린지 조회 성공",HttpStatus.OK);
        } catch (Exception e) {
            return new AllResponseDto(null,"키워드로 챌린지 조회 중 오류 발생: " + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
