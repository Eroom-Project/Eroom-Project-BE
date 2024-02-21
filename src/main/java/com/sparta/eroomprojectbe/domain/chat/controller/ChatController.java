package com.sparta.eroomprojectbe.domain.chat.controller;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {
    @MessageMapping("/chat.sendMessage")
    @SendTo("/sub/chat/challenge/{challengeId}")
    public String sendMessage(@Payload ChatMessage chatMessage) {
        chatMessage.setTime(LocalDateTime.now());
        return "메시지";
//        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/sub/chat/challenge/{challengeId}")
    public String addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        chatMessage.setTime(LocalDateTime.now());
        headerAccessor.getSessionAttributes().put("nickname", chatMessage.getSender());
        return "nickname";
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