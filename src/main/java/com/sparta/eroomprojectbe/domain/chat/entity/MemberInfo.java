package com.sparta.eroomprojectbe.domain.chat.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberInfo {
    private String memberId;
    private String nickname;
    private String profileImageUrl;

    public MemberInfo(String memberId, String nickname, String profileImageUrl) {
        this.memberId=memberId;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
    }

    public String getNickname() {
        return nickname;
    }
}