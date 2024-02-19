package com.sparta.eroomprojectbe.domain.chat.controller;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {
    @MessageMapping("/chat.sendMessage")
    @SendTo("/sub/chat/challenge/{challengeId}")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTime(LocalDateTime.now());
        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/sub/chat/challenge/{challengeId}")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
        chatMessage.setTime(LocalDateTime.now());
        return chatMessage;
    }
//    @MessageMapping("/chat.sendMessage")
//    @SendTo("/sub/public")
//    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
//        return chatMessage;
//    }
//
//    @MessageMapping("/chat.addUser")
//    @SendTo("/sub/public")
//    public ChatMessage addUser(@Payload ChatMessage chatMessage,
//                               SimpMessageHeaderAccessor headerAccessor) {
//        // Add username in web socket session
//        headerAccessor.getSessionAttributes().put("nickname", chatMessage.getSender());
//        return chatMessage;
//    }

}