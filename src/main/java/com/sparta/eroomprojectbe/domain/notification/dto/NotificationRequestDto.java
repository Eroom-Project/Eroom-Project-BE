package com.sparta.eroomprojectbe.domain.notification.dto;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationRequestDto {
    private Long challengeId;
    private Long authId;
    private NotificationType notificationType;
    private String content;
    private Member receiver;
}
