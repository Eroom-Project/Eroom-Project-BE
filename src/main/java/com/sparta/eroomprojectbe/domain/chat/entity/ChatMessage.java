package com.sparta.eroomprojectbe.domain.chat.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessage {
    private MessageType messagesType;
    private String message;
    private String sender;
    private LocalDateTime time;
    private String memberId;
    private String challengeId;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public MessageType getType() {
        return messagesType;
    }

    public void setType(MessageType type) {
        this.messagesType = type;
    }

//    public String getContent() {
//        return message;
//    }
//
//    public void setContent(String content) {
//        this.message = content;
//    }

    public void setSender(String sender) {
        this.sender = sender;
    }
}
