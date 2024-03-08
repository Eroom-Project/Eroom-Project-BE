package com.sparta.eroomprojectbe.domain.auth.service;

import com.sparta.eroomprojectbe.domain.auth.dto.*;
import com.sparta.eroomprojectbe.domain.auth.entity.Auth;
import com.sparta.eroomprojectbe.domain.auth.repository.AuthRepository;
import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenge.service.ImageS3Service;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import com.sparta.eroomprojectbe.domain.notification.dto.NotificationRequestDto;
import com.sparta.eroomprojectbe.domain.notification.entity.NotificationType;
import com.sparta.eroomprojectbe.domain.notification.service.NotificationService;
import com.sparta.eroomprojectbe.global.rollenum.ChallengerRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
public class AuthService {
    private final AuthRepository authRepository;
    private final ChallengerRepository challengerRepository;
    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final ImageS3Service imageS3Service;

    public AuthService(AuthRepository authRepository, ChallengerRepository challengerRepository, ChallengeRepository challengeRepository, MemberRepository memberRepository, NotificationService notificationService, ImageS3Service imageS3Service) {
        this.authRepository = authRepository;
        this.challengerRepository = challengerRepository;
        this.challengeRepository = challengeRepository;
        this.memberRepository = memberRepository;
        this.notificationService = notificationService;
        this.imageS3Service = imageS3Service;
    }

    /**
     * 유저가 챌린지를 신청하는 서비스 메서드
     *
     * @param challengeId 신청하려는 챌린지 id
     * @param loginMember 로그인 멤버
     * @return 린지 신청 성공여부 message, httpStatus
     */
    @Transactional
    public CreateResponseDto createChallenger(Long challengeId, Member loginMember) {
        try {
            Challenge challenge = getChallengeById(challengeId);
            Member member = getMemberById(loginMember.getMemberId());
            boolean isAlreadyApplied = challengerRepository.existsByChallengeAndMember(challenge, member);
            if (isAlreadyApplied) {
                throw new IllegalArgumentException("이미 챌린지에 참여하고 있습니다.");
            }
            if (challenge.getCurrentAttendance() == challenge.getLimitAttendance()) {
                throw new IllegalArgumentException("인원이 초과되었습니다.");
            }
            Challenger challenger = new Challenger(challenge, member, ChallengerRole.CHALLENGER);
            Challenger savedChallenger = challengerRepository.save(challenger);
            if (savedChallenger.getChallengerId() != null) {
                challenge.incrementAttendance();

                // 알림 전송 로직
                Member creator = challengerRepository.findCreatorMemberByChallengeId(challengeId)
                        .orElseThrow(() -> new IllegalArgumentException("챌린지 생성자를 찾을 수 없습니다."));
                String content = member.getNickname() + "님이 " + challenge.getTitle() + "에 신청하셨습니다.";
                sendNotification(creator, NotificationType.REGISTER, content, challengeId, null);

                return new CreateResponseDto("챌린지 신청 성공", HttpStatus.CREATED);
            } else {
                return new CreateResponseDto("챌린지가 존재하지 않아 신청에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new CreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 챌린지 인증 등록하는 서비스 메서드
     *
     * @param requestDto  authContents,authImageUrl,authVideoUrl, authStatus
     * @param challengeId 인증하려는 challengeId
     * @param loginMember 로그인 멤버
     * @return 챌린지 인증 등록 성공여부 message, httpStatus
     */
    @Transactional
    public CreateResponseDto createMemberAuth(AuthRequestDto requestDto, MultipartFile file, Long challengeId, Member loginMember) {
        try {
            Challenge challenge = getChallengeById(challengeId);
            Member member = getMemberById(loginMember.getMemberId());
            Challenger challenger = getChallenger(challenge, member);

            String saveFile = (file != null) ? imageS3Service.saveFile(file) : "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fd2zkAR%2FbtsEYKQRgO5%2FjD2MchKeMu7gNiPOt187gK%2Fimg.png";
            Auth savedAuth = authRepository.save(new Auth(requestDto, saveFile, challenger));

            if (ChallengerRole.LEADER == challenger.getRole()) {
                challenger.getMember().incrementBricksCount();
            }
            return new CreateResponseDto("챌린지 인증 등록 성공", HttpStatus.CREATED);
        } catch (
                Exception e) {
            return new CreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 선택한 챌린지 인증 전체 조회
     *
     * @param challengeId 조회하려는 챌린저의 id
     * @param loginMember 로그인 멤버
     * @return 선택한 챌린지의 인증 List, 조회성공여부 message, httpStatus
     */
    @Transactional
    public AuthAllResponseDto getChallengerAuthList(Long challengeId, Member loginMember) {
        try {
            Challenge challenge = getChallengeById(challengeId);
            Member member = getMemberById(loginMember.getMemberId());

            List<Auth> authList = authRepository.findByChallenger_ChallengeOrderByModifiedAtDesc(challenge);

            Challenger challenger = getChallenger(challenge, member);

            if (!challenger.getMember().getMemberId().equals(member.getMemberId())) {
                return new AuthAllResponseDto(null, "해당 챌린지를 신청한 멤버가 아닙니다.", HttpStatus.BAD_REQUEST);
            }

            List<AuthResponseDto> authResponseList = authList.stream().map(AuthResponseDto::new).toList();
            MemberInfoResponseDto memberInfoResponseDto = new MemberInfoResponseDto(challenger.getRole(), member.getMemberId());
            AuthMemberInfoResponseDto authMemberInfoResponseDto = new AuthMemberInfoResponseDto(authResponseList, memberInfoResponseDto);
            return new AuthAllResponseDto(authMemberInfoResponseDto, "인증 전체 조회 성공", HttpStatus.OK);

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
     * @param loginMember 로그인 멤버
     * @return 인증 수정 후 data, 인증 수정 성공 여부 message, httpStatus
     */
    @Transactional
    public AuthDataResponseDto updateLeaderAuth(AuthLeaderRequestDto requestDto, Long challengeId, Long authId, Member loginMember) {

        Challenge challenge = getChallengeById(challengeId);
        Auth auth = getAuthById(authId);
        Member member = getMemberById(loginMember.getMemberId());
        Challenger challenger = getChallenger(challenge, member);
        try {

            if (!challenger.getMember().getMemberId().equals(member.getMemberId())) {
                return new AuthDataResponseDto(null, "해당 인증과 관련있는 맴버가 아닙니다.", HttpStatus.BAD_REQUEST);
            }

            if (challenger.getRole() == ChallengerRole.LEADER) {

                auth.leaderUpdate(auth, requestDto);
                auth.getChallenger().getMember().incrementBricksCount();
                AuthResponseDto responseDto = new AuthResponseDto(auth);

                // 알림 전송 로직
                if (requestDto.getAuthStatus().equals("APPROVED")) {
                    String content = auth.getChallenger().getMember() + "님의 인증글이 승인되었습니다.";
                    sendNotification(auth.getChallenger().getMember(), NotificationType.APPROVE, content, challengeId, authId);
                } else if (requestDto.getAuthStatus().equals("DENIED")) {
                    String content = auth.getChallenger().getMember() + "님의 인증글이 인증 조건을 만족시키지 못하였습니다. 인증글을 수정하여 주세요.";
                    sendNotification(auth.getChallenger().getMember(), NotificationType.DENY, content, challengeId, authId);
                }
                return new AuthDataResponseDto(responseDto, "챌린지 상태 수정 성공", HttpStatus.CREATED);
            } else {
                return new AuthDataResponseDto(null, "해당 권한이 없습니다.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new AuthDataResponseDto(null, "에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 챌린저가 챌린저인증 수정하는 서비스 메서드
     *
     * @param requestDto  authContents, authImageUrl, authVideoUrl, authStatus
     * @param challengeId 수정하려는 챌린저 아이디
     * @param authId      수정하려는 Auth 아이디
     * @param loginMember 로그인 멤버
     * @return 수정후 인증 내용 data, 수정성공여부 message, httpStatus
     */
    @Transactional
    public AuthDataResponseDto updateMemberAuth(AuthRequestDto requestDto, MultipartFile file, Long challengeId, Long authId, Member loginMember) { // 챌린지 인증 수정(member)
        try {
            // auth 존재 여부 확인
            Auth auth = getAuthById(authId);
            Challenge challenge = getChallengeById(challengeId);
            Member member = getMemberById(loginMember.getMemberId());

            String updateFile;
            if (file != null) {
                updateFile = imageS3Service.updateFile(auth.getAuthImageUrl(), file);
            } else {
                updateFile = auth.getAuthImageUrl();
            }

            Challenger challenger = getChallenger(challenge, member);
            if (auth.getAuthId() != null) {
                auth.update(requestDto, updateFile, challenger);
                AuthResponseDto responseDto = new AuthResponseDto(auth);
                if (member.getMemberId().equals(challenger.getMember().getMemberId())) {
                    return new AuthDataResponseDto(responseDto, "챌린지 인증 수정 성공", HttpStatus.OK);
                } else {
                    return new AuthDataResponseDto(null, "해당 인증을 작성하지 않았습니다", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new AuthDataResponseDto(null, "챌린지 인증 수정 실패", HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            return new AuthDataResponseDto(null, "에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 선택한 챌린지 인증 삭제하는 서비스 메서드
     *
     * @param challengeId 삭제하려는 인증을 담고있는 챌린지 id
     * @param authId      제하려는 인증 id
     * @param loginMember 작성자 id
     * @return 챌린지 인증 삭제 성공여부 data, HttpStatus
     */
    public CreateResponseDto deleteAuth(Long challengeId, Long authId, Member loginMember) {
        try {
            Auth auth = getAuthById(authId);
            Challenge challenge = getChallengeById(challengeId);
            Member member = getMemberById(loginMember.getMemberId());

            Challenger challenger = getChallenger(challenge, member);
            if (challenger.getRole() == ChallengerRole.LEADER) {
                imageS3Service.deleteFile(auth.getAuthImageUrl());
                authRepository.delete(auth);
                return new CreateResponseDto("챌린지 인증 삭제 성공", HttpStatus.OK);
            } else if (challenger.getMember().getMemberId() == member.getMemberId()) {
                imageS3Service.deleteFile(auth.getAuthImageUrl());
                authRepository.delete(auth);
                return new CreateResponseDto("챌린지 인증 삭제 성공", HttpStatus.OK);
            } else {
                return new CreateResponseDto("챌린지 인증 삭제 실패(리더가 아니거나, 작성자가 아닙니다)", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            return new CreateResponseDto("에러: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendNotification(Member receiver, NotificationType type, String content, Long challengeId, Long authId) {
        NotificationRequestDto notificationRequest = NotificationRequestDto.builder()
                .receiver(receiver)
                .notificationType(type)
                .content(content)
                .challengeId(challengeId)
                .authId(authId)
                .build();
        notificationService.send(notificationRequest);
    }

    /**
     * 챌린지 존재 여부 확인하는 메서드
     *
     * @param challengeId 찾으려는 챌린지 아이디
     * @return Challenge
     */
    private Challenge getChallengeById(Long challengeId) {
        return challengeRepository.findById(challengeId).orElseThrow(
                () -> new IllegalArgumentException("챌린지가 존재하지 않습니다.")
        );
    }

    /**
     * 멤버의 존재 여부를 확인하는 메서드
     *
     * @param memberId 찾으려는 멤버의 아이디
     * @return Member
     */
    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("멤버가 존재하지 않습니다.")
        );
    }

    /**
     * 멤버가 챌린지를 신청한지 확인하는 메서드
     *
     * @param challenge 신청한 챌린지
     * @param member    신청한 멤버
     * @return Challenger
     */
    private Challenger getChallenger(Challenge challenge, Member member) {
        return challengerRepository.findByChallengeAndMember(challenge, member)
                .orElseThrow(() -> new IllegalArgumentException("챌린지를 신청한 멤버가 아닙니다."));
    }

    /**
     * 인증의 존재 여부를 확인하는 메서드
     *
     * @param authId 찾으려는 인증의 아이디
     * @return Auth
     */
    private Auth getAuthById(Long authId) {
        return authRepository.findById(authId).orElseThrow(
                () -> new IllegalArgumentException("해당 인증이 존재하지 않습니다.")
        );
    }

}
