package com.sparta.eroomprojectbe.domain.notification.dto;

import com.sparta.eroomprojectbe.domain.notification.entity.Notification;
import com.sparta.eroomprojectbe.domain.notification.entity.NotificationType;
import lombok.Getter;

@Getter
public class NotificationResponseDto {
    private Long notificationId;
    private Long challengeId;
    private Long authId;
    private NotificationType notificationType;
    private String content;

    public NotificationResponseDto(Notification notification) {
        this.notificationId = notification.getId();
        this.challengeId = notification.getChallengeId();
        this.authId = notification.getAuthId();
        this.notificationType = notification.getNotificationType();
        this.content = notification.getContent();
    }

    public static NotificationResponseDto of(Notification notification) {
        return new NotificationResponseDto(notification);
    }
}
