package com.sparta.eroomprojectbe.domain.chat.service;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import com.sparta.eroomprojectbe.domain.chat.repository.ChatRoomRepository;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ChatMessageService {
    private final ChallengerRepository challengerRepository;
    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;
    private final ChatRoomService chatRoomService;
    private final RedisTemplate<String, ChatMessage> redisTemplate;
    private final ChatRoomRepository chatRoomRepository;

    public ChatMessageService(ChallengerRepository challengerRepository, ChallengeRepository challengeRepository, MemberRepository memberRepository, ChatRoomService chatRoomService, RedisTemplate<String, ChatMessage> redisTemplate, ChatRoomRepository chatRoomRepository) {
        this.challengerRepository = challengerRepository;
        this.challengeRepository = challengeRepository;
        this.memberRepository = memberRepository;
        this.chatRoomService = chatRoomService;
        this.redisTemplate = redisTemplate;
        this.chatRoomRepository=chatRoomRepository;
    }
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;


    public void saveMessage(String challengeId, ChatMessage chatMessage, Message<?> message) {
        // 회원 ID 가져오기
        String challengeIdString = chatMessage.getChallengeId();
        String memberIdString = chatMessage.getMemberId();

        if (challengeIdString != null && memberIdString != null) {
            // String 값을 Long 값으로 변환
            Long challenge = Long.parseLong(challengeIdString);
            Long member = Long.parseLong(memberIdString);

            // 챌린지와 회원을 찾음
            Optional<Challenge> challengeOptional = challengeRepository.findById(challenge);
            Optional<Member> memberOptional = memberRepository.findById(member);

            // 챌린지와 회원이 존재하는 경우에만 처리
            if (challengeOptional.isPresent() && memberOptional.isPresent()) {
                Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challengeOptional.get(), memberOptional.get());

                challengerOptional.ifPresent(challenger -> {
                    // 닉네임 가져오기
                    String senderNickname = challenger.getMember().getNickname();
                    chatMessage.setSender(senderNickname);

                    // 프로필 이미지 URL 가져오기
                    String profileImageUrl = challenger.getMember().getProfileImageUrl();
                    chatMessage.setProfileImageUrl(profileImageUrl);

                    // WebSocket 세션에 속성 저장
                    StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);
                    headerAccessor.getSessionAttributes().put("challengeId", challengeId);
                    headerAccessor.getSessionAttributes().put("memberId", memberIdString);
                    headerAccessor.getSessionAttributes().put("nickname", senderNickname);
                    headerAccessor.getSessionAttributes().put("profileImageUrl", profileImageUrl);


                    // Redis에 채팅 메시지 저장
                    chatRoomRepository.saveChatMessage(challengeId, chatMessage);

                    switch (chatMessage.getType()) {
                        case JOIN -> {
                            System.out.println("MessagesType : JOIN");
                            // 사용자가 챌린지 방에 입장할 때 ChatRoomService를 통해 currentMemberList에 추가
                            chatRoomService.userJoinedRoom(challengeId, memberIdString, senderNickname, profileImageUrl);
                        }
                    }
                });
                messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), chatMessage);
            }
        }
    }
}