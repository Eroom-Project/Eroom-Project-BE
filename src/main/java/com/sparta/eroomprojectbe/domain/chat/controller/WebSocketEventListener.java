package com.sparta.eroomprojectbe.domain.chat.controller;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");
        String challengeId = (String) headerAccessor.getSessionAttributes().get("challengeId");

        if (nickname != null && challengeId != null) {
            logger.info("User Disconnected : " + nickname);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setType(ChatMessage.MessageType.LEAVE);
            chatMessage.setSender(nickname);
            chatMessage.setChallengeId(challengeId);

            messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), chatMessage);
        }
    }
}
