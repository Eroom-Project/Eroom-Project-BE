package com.sparta.eroomprojectbe.domain.chat.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfo {
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