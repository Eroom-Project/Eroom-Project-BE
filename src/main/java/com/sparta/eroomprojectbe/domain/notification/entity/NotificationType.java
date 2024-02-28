package com.sparta.eroomprojectbe.domain.notification.entity;

public enum NotificationType {
    REGISTER(" 챌린지 신청이 도착했습니다."),
    APPROVE("인증글이 승인되었습니다."),
    DENY(" 인증글이 인증 조건을 통과하지 못했습니다.");

    private String content;

    NotificationType(String content) {
        this.content = content;
    }
}
