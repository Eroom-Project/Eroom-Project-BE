package com.sparta.eroomprojectbe.domain.chat.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfo {
    private String nickname;
    private String profileImageUrl;

    public MemberInfo(String nickname, String profileImageUrl) {
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public String getNickname() {
        return nickname;
    }
}