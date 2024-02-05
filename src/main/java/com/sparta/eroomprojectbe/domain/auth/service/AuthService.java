package com.sparta.eroomprojectbe.domain.auth.service;

import com.sparta.eroomprojectbe.domain.auth.dto.AuthRequestDto;
import com.sparta.eroomprojectbe.domain.auth.dto.AuthResponseDto;
import com.sparta.eroomprojectbe.domain.auth.dto.ChallengerCreateResponseDto;
import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import com.sparta.eroomprojectbe.domain.auth.repository.AuthRepository;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final ChallengerRepository challengerRepository;

    private final ChallengeRepository challengeRepository;
    public AuthService(AuthRepository authRepository, ChallengerRepository challengerRepository, ChallengeRepository challengeRepository){
        this.authRepository=authRepository;
        this.challengerRepository=challengerRepository;
        this.challengeRepository = challengeRepository;
    }

    public List<AuthResponseDto> getMemberAuthList()  { // 챌린지 인증(member) 전체 조회
        List<Auth> authList = authRepository.findAllByOrderByCreatedAtDesc();
        List<AuthResponseDto> authResponseList = authList.stream().map(AuthResponseDto::new).toList();
        return authResponseList;
    }

    public List<AuthResponseDto> getChallengerAuthList(Long challengerId)  { // 해당 챌린지 인증(member) 전체 조회
        // Challenger DB에 존재하는 Challenger 인지 확인
        Challenger challenger = challengerRepository.findById(challengerId)
                .orElseThrow(IllegalArgumentException::new);

        // Challenger 엔티티의 ChallengeId와 일치하는 Auth 리스트 조회
        List<Auth> authList = authRepository.findAllByAuthIdOrderByCreatedAtDesc(challenger.getChallenge().getChallengeId());

        // Auth 엔티티들을 AuthResponseDto로 매핑하고 리스트로 반환
        List<AuthResponseDto> authResponseList = authList.stream().map(AuthResponseDto::new).toList();
        return authResponseList;
    }


    @Transactional
    public AuthResponseDto updateLeaderAuth(AuthRequestDto requestDto, Long challengerId, Long authId) { // 챌린지 인증 허가 및 불가 처리(leader)
        return null;
    }
    @Transactional
    public AuthResponseDto updateMemberAuth(AuthRequestDto requestDto, Long challengerId) { // 챌린지 인증 수정(member)
       return null;
    }

    @Transactional
    public AuthResponseDto createMemberAuth(AuthRequestDto requestDto, Long challengerId) { // 챌린지 인증(member) 등록
        // Challenger DB에 존재하는 Challenger 인지 확인
        Challenger challenger = challengerRepository.findById(challengerId)
                .orElseThrow(IllegalArgumentException::new);

        // Auth(챌린지 인증) 객체 생성 후 Auth DB에 저장
        Auth savedAuth = authRepository.save(new Auth(requestDto, challenger));
        return new AuthResponseDto(savedAuth);
    }

    /**
     * 유저가 챌린지를 신청하는 서비스 메서드
     * @param challengeId 신청하려는 챌린지 id
     * @return 챌린지 신청 성공여부 message, httpStatus
     */
    @Transactional
    public ChallengerCreateResponseDto createChallenger(Long challengeId) {
        try {
            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                    () -> new IllegalArgumentException("챌린지가 존재하지 않습니다.")
            );
            if (challenge.getCurrentAttendance() == challenge.getLimitAttendance()) {
                return new ChallengerCreateResponseDto("인원이 초과 되었습니다", HttpStatus.BAD_REQUEST);
            }
            Challenger challenger = new Challenger(challenge, ChallengerRole.CHALLENGER);
            Challenger savedChallenger = challengerRepository.save(challenger);

            if (savedChallenger != null && savedChallenger.getChallengerId() != null) {
                challenge.incrementAttendance();
                return new ChallengerCreateResponseDto("챌린지 신청 성공", HttpStatus.CREATED);
            } else  {
                return new ChallengerCreateResponseDto("챌린지 신청 실패", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            return new ChallengerCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @Transactional
//    public ChallengerCreateResponseDto createChallenger(Long challengeId, Member member) {
//        try {
//            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
//                    () -> new IllegalArgumentException("챌린지가 존재하지 않습니다.")
//            )
//            if (challenge.getCurrentAttendance() == challenge.getLimitAttendance()) {
//                throw new IllegalArgumentException("인원이 초과되었습니다.");
//            }
//            Challenger challenger = new Challenger(challenge,member, ChallengerRole.CHALLENGER);
//            Challenger savedChallenger = challengerRepository.save(challenger);
//
//            if (savedChallenger != null && savedChallenger.getChallengerId() != null) {
//                challenge.incrementAttendance();
//                return new ChallengerCreateResponseDto("챌린지 신청 성공", HttpStatus.CREATED);
//            } else  {
//                return new ChallengerCreateResponseDto("챌린지 신청 실패", HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//
//        } catch (Exception e) {
//            return new ChallengerCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
