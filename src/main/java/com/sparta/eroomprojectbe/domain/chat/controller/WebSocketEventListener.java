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

import java.util.UUID;

/**
 * WebSocket 이벤트를 처리하는 리스너 클래스입니다.
 */
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    /**
     * 웹 소켓 연결 이벤트를 처리하는 메서드입니다.
     * @param event 세션 연결 이벤트 객체
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    /**
     * 웹 소켓 연결 종료 이벤트를 처리하는 메서드입니다.
     * @param event 세션 연결 종료 이벤트 객체
     */
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");
        String challengeId = (String) headerAccessor.getSessionAttributes().get("challengeId");
        String messageId = UUID.randomUUID().toString();

        if (nickname != null && challengeId != null) {
            logger.info("User Disconnected : " + nickname);

            // 채팅방에서 사용자를 제거하는 로직 호출
            chatRoomService.userLeftRoom(challengeId, nickname);

            // 사용자가 나갔음을 다른 클라이언트에 알리는 메시지 전송
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(nickname);
            chatMessage.setChallengeId(challengeId);
            chatMessage.setMessageId(messageId);

            // Redis에 채팅 메시지 저장
            chatRoomRepository.saveChatMessage(challengeId, chatMessage);

            messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), chatMessage);
        }
    }
}
