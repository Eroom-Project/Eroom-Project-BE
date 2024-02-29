package com.sparta.eroomprojectbe.domain.chat.controller;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import com.sparta.eroomprojectbe.domain.chat.repository.ChatRoomRepository;
import com.sparta.eroomprojectbe.domain.chat.service.ChatRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashSet;
import java.util.Set;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
    private Set<String> connectedChallengeIds = new HashSet<>();
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");

        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getSessionAttributes().containsKey("challengeId")) {
            String challengeId = (String) headerAccessor.getSessionAttributes().get("challengeId");

            // 해당 challengeId에 대한 연결이 처음 발생한 것인지 확인
            if (!isConnectionForChallengeIdEstablished(challengeId)) {
                // 처음 연결된 경우에만 채팅 내역 불러와 전송
                Iterable<ChatMessage> chatHistory = chatRoomRepository.getChatHistory(challengeId);
                for (ChatMessage chatMessage : chatHistory) {
                    messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), chatMessage);
                }

                // 연결이 처음 발생했음을 기록
                recordConnectionForChallengeId(challengeId);
            }
        }
    }

    private boolean isConnectionForChallengeIdEstablished(String challengeId) {
        return connectedChallengeIds.contains(challengeId);
    }

    private void recordConnectionForChallengeId(String challengeId) {
        connectedChallengeIds.add(challengeId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");
        String challengeId = (String) headerAccessor.getSessionAttributes().get("challengeId");

        if (nickname != null && challengeId != null) {
            logger.info("User Disconnected : " + nickname);

            // 채팅방에서 사용자를 제거하는 로직 호출
            chatRoomService.userLeftRoom(challengeId, nickname);

            // 사용자가 나갔음을 다른 클라이언트에 알리는 메시지 전송
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(nickname);
            chatMessage.setChallengeId(challengeId);

            messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), chatMessage);
        }
    }
}
