package com.sparta.eroomprojectbe.domain.auth.service;

import com.sparta.eroomprojectbe.domain.auth.dto.*;
import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import com.sparta.eroomprojectbe.domain.auth.repository.AuthRepository;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final ChallengerRepository challengerRepository;
    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;

    public AuthService(AuthRepository authRepository, ChallengerRepository challengerRepository, ChallengeRepository challengeRepository, MemberRepository memberRepository) {
        this.authRepository = authRepository;
        this.challengerRepository = challengerRepository;
        this.challengeRepository = challengeRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 챌린지 전체 조회
     *
     * @return 챌린지 인증 List, message, httpStatus
     */
//    public AuthAllResponseDto getMemberAuthList() { // 챌린지 인증(member) 전체 조회
//        try {
//            List<Auth> authList = authRepository.findAllByOrderByCreatedAtDesc();
//            List<AuthResponseDto> authResponseList = authList.stream().map(AuthResponseDto::new).toList();
//            return new AuthAllResponseDto(authResponseList, "챌린지 인증 전체 조회 성공", HttpStatus.OK);
//        } catch (Exception e) {
//            return new AuthAllResponseDto(null, "챌린지 인증 전체 조회 실패", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * 선택한 챌린지 인증 전체 조회
     *
     * @param challengeId 조회하려는 챌린저의 id
     * @return 선택한 챌린지의 인증 List, 조회성공여부 message, httpStatus
     */
    public AuthAllResponseDto getChallengerAuthList(Long challengeId, Long memberId) { // 해당 챌린지 인증(member) 전체 조회
        try {
            // Challenger DB에 존재하는 Challenger 인지 확인
            Challenge challenge = challengeRepository.findById(challengeId)
                    .orElseThrow(IllegalArgumentException::new);
            Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
            // Challenger 엔티티의 ChallengeId와 일치하는 Auth 리스트 조회
            List<Auth> authList = authRepository.findByChallenger_Challenge(challenge);
            Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challenge,member);
            if(challengerOptional.isPresent()){
                // Auth 엔티티들을 AuthResponseDto로 매핑하고 리스트로 반환
                List<AuthResponseDto> authResponseList = authList.stream().map(AuthResponseDto::new).toList();
                MemberInfoResponseDto memberInfoResponseDto = new MemberInfoResponseDto(challengerOptional.get().getRole(),member.getMemberId());
                AuthMemberInfoResponseDto authMemberInfoResponseDto = new AuthMemberInfoResponseDto(authResponseList, memberInfoResponseDto);
                return new AuthAllResponseDto(authMemberInfoResponseDto, "인증 전체 조회 성공", HttpStatus.OK);
            }else {
                return new AuthAllResponseDto(null, "인증 전체 조회 실패", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new AuthAllResponseDto(null, "인증 전체 조회 실패", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 리더가 챌린지 인증을 수정하는 서비스 메서드
     *
     * @param requestDto  DENIED, APPROVED
     * @param challengeId 인증하려는 챌린저 id
     * @param authId      인증하려는 auth id
     * @return 인증 수정 후 data, 인증 수정 성공 여부 message, httpStatus
     */
    @Transactional
    public AuthDataResponseDto updateLeaderAuth(AuthLeaderRequestDto requestDto, Long challengeId, Long authId, Long memberId) { // 챌린지 인증 허가 및 불가 처리(leader)
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new IllegalArgumentException("해당 챌린지가 존재하지 않습니다.")
        );
        Auth auth = authRepository.findById(authId).orElseThrow(
                () -> new IllegalArgumentException("해당 인증이 존재하지 않습니다.")
        );
        Member member = memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("해당 맴버가 존재하지 않습니다.")
        );
        Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challenge, member);
        if (challengerOptional.isPresent()) {
            try {
                if (challengerOptional.get().getRole() == ChallengerRole.LEADER) {
                    auth.leaderUpdate(auth, requestDto);
                    AuthResponseDto responseDto = new AuthResponseDto(auth);
                    return new AuthDataResponseDto(responseDto, "챌린지 상태 수정 성공", HttpStatus.OK);
                } else {
                    return new AuthDataResponseDto(null, "해당 권한이 없습니다.", HttpStatus.BAD_REQUEST);
                }
            } catch (Exception e) {
                return new AuthDataResponseDto(null, "에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new AuthDataResponseDto(null, "해당 챌린지를 신청하지 않았습니다.", HttpStatus.BAD_REQUEST);
        }

    }
//    @Transactional
//    public AuthDataResponseDto updateLeaderAuth(AuthLeaderRequestDto requestDto, Long challengeId, Long authId, Member member) { // 챌린지 인증 허가 및 불가 처리(leader)
//        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
//                ()-> new IllegalArgumentException("해당 챌린지를 신청하지 않습니다.")
//        );
//        Auth auth = authRepository.findById(authId).orElseThrow(
//                ()-> new IllegalArgumentException("해당 인증이 존재하지 않습니다.")
//        );
//        Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challenge, member);
//        if(challengerOptional.isPresent()){
//            try {
//                if(challengerOptional.get().getMember().getMemberId() != member.getMemberId()){
//                    return new AuthDataResponseDto(null,"해당 인증과 관련있는 맴버가 아닙니다.", HttpStatus.BAD_REQUEST);
//                }
//                if(challengerOptional.get().getRole() == ChallengerRole.LEADER){
//                    auth.leaderUpdate(auth,requestDto);
//                    AuthResponseDto responseDto = new AuthResponseDto(auth);
//                    return new AuthDataResponseDto(responseDto,"챌린지 상태 수정 성공", HttpStatus.CREATED);
//                }else {
//                    return new AuthDataResponseDto(null,"해당 권한이 없습니다.", HttpStatus.BAD_REQUEST);
//                }
//            }catch (Exception e){
//                return new AuthDataResponseDto(null,"에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//            }
//        }else {
//            return  new AuthDataResponseDto(null, "해당 챌린지를 신청한 멤버가 아닙니다.", HttpStatus.BAD_REQUEST);
//        }
//    }

    /**
     * 챌린저인증 수정하는 서비스 메서드
     *
     * @param requestDto  authContents, authImageUrl, authVideoUrl, authStatus
     * @param challengeId 수정하려는 챌린저 아이디
     * @param authId      수정하려는 Auth 아이디
     * @return 수정후 인증 내용 data, 수정성공여부 message, httpStatus
     */
    @Transactional
    public AuthDataResponseDto updateMemberAuth(AuthRequestDto requestDto, Long challengeId, Long authId, Long memberId) { // 챌린지 인증 수정(member)
        try {
            // auth 존재 여부 확인
            Auth auth = authRepository.findById(authId).orElseThrow(
                    () -> new IllegalArgumentException("해당 인증이 존재하지 않습니다.")
            );
            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                    () -> new IllegalArgumentException("해당 챌린지가 존재하지 않습니다.")
            );
            Member member = memberRepository.findById(memberId).orElseThrow(
                    () -> new IllegalArgumentException("해당 멤버가 존재 하지 않습니다.")
            );
            Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challenge, member);
            if (challengerOptional.isPresent()) {
                auth.update(requestDto, challengerOptional.get());
                AuthResponseDto responseDto = new AuthResponseDto(auth);
                if (auth != null && auth.getAuthId() != null) {
                    return new AuthDataResponseDto(responseDto, "챌린지 인증 수정 성공", HttpStatus.OK);
                } else {
                    return new AuthDataResponseDto(responseDto, "챌린지 인증 수정 실패", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new AuthDataResponseDto(null, "해당 챌린저가 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new AuthDataResponseDto(null, "에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    @Transactional
//    public AuthDataResponseDto updateMemberAuth(AuthRequestDto requestDto, Long challengeId, Long authId, Member member) { // 챌린지 인증 수정(member)
//        try {
//            // auth 존재 여부 확인
//            Auth auth = authRepository.findById(authId).orElseThrow(
//                    ()-> new IllegalArgumentException("해당 인증이 존재하지 않습니다.")
//            );
//            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
//                    ()-> new IllegalArgumentException("해당 챌린저가 존재하지 않습니다.")
//            );
//            Member loginmember = memberRepository.findById(member.getMemberId()).orElseThrow(
//                    ()-> new IllegalArgumentException("해당 멤버가 존재 하지 않습니다.")
//            );
//            Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challenge, loginmember);
//            if(challengerOptional.isPresent()){
//                auth.update(requestDto, challengerOptional.get());
//                AuthResponseDto responseDto = new AuthResponseDto(auth);
//                if(auth != null && auth.getAuthId() != null){
//                    if(loginmember.getMemberId() == challengerOptional.get().getMember().getMemberId()){
//                        return new AuthDataResponseDto(responseDto,"챌린지 인증 등록 성공", HttpStatus.CREATED);
//                    }else {
//                        return new AuthDataResponseDto(null,"해당 인증을 작성하지 않았습니다", HttpStatus.BAD_REQUEST);
//                    }
//                } else {
//                    return new AuthDataResponseDto(responseDto,"챌린지 인증 등록 실패", HttpStatus.INTERNAL_SERVER_ERROR);
//                }
//            }else {
//                return new AuthDataResponseDto(null, "해당 챌린지를 신천하지 않았습니다.", HttpStatus.BAD_REQUEST);
//            }
//        }catch (Exception e){
//            return new AuthDataResponseDto(null,"에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * 챌린지 인증 등록하는 서비스 메서드
     *
     * @param requestDto  authContents,authImageUrl,authVideoUrl, authStatus
     * @param challengeId 인증하려는 challengeId
     * @return 챌린지 인증 등록 성공여부 message, httpStatus
     */
    @Transactional
    public ChallengerCreateResponseDto createMemberAuth(AuthRequestDto requestDto, Long challengeId, Long memberId) { // 챌린지 인증(member) 등록
        try {
            // Challenge DB에 존재하는 Challenge 인지 확인
            Challenge challenge = challengeRepository.findById(challengeId)
                    .orElseThrow(IllegalArgumentException::new);
            Member member = memberRepository.findById(memberId).orElseThrow(IllegalArgumentException::new);
            Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challenge, member);
            if (challengerOptional.isPresent()) {
                Challenger challenger = challengerOptional.get();
                Auth savedAuth = authRepository.save(new Auth(requestDto, challenger));
                if (savedAuth != null && savedAuth.getAuthId() != null) {
                    return new ChallengerCreateResponseDto("챌린지 인증 등록 성공", HttpStatus.CREATED);
                } else {
                    return new ChallengerCreateResponseDto("챌린지 인증 등록 실패", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } else {
                return new ChallengerCreateResponseDto("챌린지 등록 필요", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ChallengerCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @Transactional
//    public ChallengerCreateResponseDto createMemberAuth(AuthRequestDto requestDto, Long challengeId, Member member) { // 챌린지 인증(member) 등록
//        try {
//            // Challenge DB에 존재하는 Challengeㄷ 인지 확인
//            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(IllegalArgumentException::new);
//            Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challenge,
//                    member);
//            if (challengerOptional.isPresent()) {
//                Challenger challenger = challengerOptional.get();
//                Auth savedAuth = authRepository.save(new Auth(requestDto, challenger));
//                if (savedAuth != null && savedAuth.getAuthId() != null) {
//                    return new ChallengerCreateResponseDto("챌린지 인증 등록 성공", HttpStatus.CREATED);
//                } else {
//                    return new ChallengerCreateResponseDto("챌린지 인증 등록 실패", HttpStatus.INTERNAL_SERVER_ERROR);
//                }
//            } else {
//                return new ChallengerCreateResponseDto("챌린지 인증 등록 실패", HttpStatus.BAD_REQUEST);
//            }
//        } catch (
//                Exception e) {
//            return new ChallengerCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

    /**
     * 유저가 챌린지를 신청하는 서비스 메서드
     *
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
            } else {
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

    /**
     * 선택한 챌린지 인증 삭제하는 서비스 메서드
     * @param challengeId 삭제하려는 인증을 담고있는 챌린지 id
     * @param authId 삭제하려는 인증 id
     * @param memberId 작성자 id
     * @return 챌린지 인증 삭제 성공여부 data, HttpStatus
     */
    public ChallengerCreateResponseDto deleteAuth(Long challengeId, Long authId, Long memberId) {
        try {
            // auth 존재 여부 확인
            Auth auth = authRepository.findById(authId).orElseThrow(
                    () -> new IllegalArgumentException("해당 인증이 존재하지 않습니다.")
            );
            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                    () -> new IllegalArgumentException("해당 챌린지가 존재하지 않습니다.")
            );
            Member member = memberRepository.findById(memberId).orElseThrow(
                    () -> new IllegalArgumentException("해당 멤버가 존재 하지 않습니다.")
            );
            Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challenge,member);
            if(challengerOptional.isPresent()){
                if (challengerOptional.get().getRole() == ChallengerRole.LEADER || challengerOptional.get().getMember() == member){
                    authRepository.delete(auth);
                    return new ChallengerCreateResponseDto("챌린지 인증 삭제 성공", HttpStatus.OK);
                }else {
                    return new ChallengerCreateResponseDto("챌린지 인증 삭제 실패", HttpStatus.BAD_REQUEST);
                }
            }else {
                return new ChallengerCreateResponseDto("챌린지 인증 삭제 실패", HttpStatus.NOT_FOUND);
            }
        }catch (Exception e){
            return new ChallengerCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
//    public ChallengerCreateResponseDto deleteAuth(Long challengeId, Long authId, Member member) {
//        try {
//            // auth 존재 여부 확인
//            Auth auth = authRepository.findById(authId).orElseThrow(
//                    () -> new IllegalArgumentException("해당 인증이 존재하지 않습니다.")
//            );
//            Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
//                    () -> new IllegalArgumentException("해당 챌린지가 존재하지 않습니다.")
//            );
//            Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challenge,member);
//            if(challengerOptional.isPresent()){
//                if (challengerOptional.get().getRole() == ChallengerRole.LEADER ){
//                    authRepository.delete(auth);
//                    return new ChallengerCreateResponseDto("챌린지 인증 삭제 성공", HttpStatus.OK);
//                }else if(challengerOptional.get().getMember() == member){
//                    authRepository.delete(auth);
//                    return new ChallengerCreateResponseDto("챌린지 인증 삭제 성공", HttpStatus.OK);
//                } else {
//                    return new ChallengerCreateResponseDto("챌린지 인증 삭제 실패", HttpStatus.BAD_REQUEST);
//                }
//            }else {
//                return new ChallengerCreateResponseDto("챌린지 인증 삭제 실패", HttpStatus.NOT_FOUND);
//            }
//        }catch (Exception e){
//            return new ChallengerCreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
