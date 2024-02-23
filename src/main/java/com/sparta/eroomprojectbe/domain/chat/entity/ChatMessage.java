package com.sparta.eroomprojectbe.domain.chat.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ChatMessage {
    private MessageType type;
    private String message;
    private String sender;
    private LocalDateTime time;
    private String memberId;
    private String challengeId;
    private String profileImageUrl;
    private List<MemberInfo> currentMemberList;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
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



    public static class MemberInfo {
        private String sender;
        private String profileImageUrl;

        public MemberInfo(String sender, String profileImageUrl) {
            this.sender = sender;
            this.profileImageUrl = profileImageUrl;
        }

        public String getSender() {
            return sender;
        }
    }
}
