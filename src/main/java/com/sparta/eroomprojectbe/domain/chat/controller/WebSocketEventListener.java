package com.sparta.eroomprojectbe.domain.chat.controller;

import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
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

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChallengerRepository challengerRepository;
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Long challengeId = (Long) headerAccessor.getSessionAttributes().get("challengeId"); // 챌린지 ID 가져오기

        if (challengeId != null) {
            // WebSocket 연결 시 챌린지 참여 여부 확인
            Long memberId = (Long) headerAccessor.getSessionAttributes().get("memberId"); // 회원 ID 가져오기
            Challenger challenger = challengerRepository.findByChallengeIdAndMemberId(challengeId, memberId);
            if (challenger != null) {
                // 챌린지에 해당 회원이 참여 중인 경우
                String nickname = challenger.getMember().getNickname(); // 회원의 닉네임 가져오기
                headerAccessor.getSessionAttributes().put("nickname", nickname);
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