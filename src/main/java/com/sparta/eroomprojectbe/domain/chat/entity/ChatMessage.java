package com.sparta.eroomprojectbe.domain.chat.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChatMessage {
    private MessagesType messagesType;
    private String message;
    private String sender;
    private LocalDateTime time;
    private String memberId;
    private String challengeId;

    public enum MessagesType {
        CHAT,
        JOIN,
        LEAVE
    }

    public MessagesType getMessagesType() {
        return messagesType;
    }

    public void setMessagesType(MessagesType messagesType) {
        this.messagesType = messagesType;
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
