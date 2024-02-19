package com.sparta.eroomprojectbe.domain.chat.controller;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChallengerRepository challengerRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
        // 이벤트에서 Stomp 헤더 접근
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        // 챌린지 ID 가져오기
        Long challengeId = (Long) headerAccessor.getSessionAttributes().get("challengeId");
        // 회원 ID 가져오기
        Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId");

        if (challengeId != null && memberId != null) {
            // 챌린지와 회원 엔터티를 찾음
            Optional<Challenge> challengeOptional = challengeRepository.findById(challengeId);
            Optional<Member> memberOptional = memberRepository.findById(memberId);

            // 챌린지와 회원이 존재하는 경우에만 처리
            if (challengeOptional.isPresent() && memberOptional.isPresent()) {
                Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challengeOptional.get(), memberOptional.get());
                // 챌린저가 존재하는 경우 닉네임을 세션에 저장
                challengerOptional.ifPresent(challenger -> headerAccessor.getSessionAttributes().put("nickname", challenger.getMember().getNickname()));
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");
        if(nickname != null) {
            logger.info("User Disconnected : " + nickname);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(nickname);
            chatMessage.setTime(LocalDateTime.now());

            messagingTemplate.convertAndSend( "/sub/chat/challenge/{challengeId}", chatMessage);
        }
    }
}