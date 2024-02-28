package com.sparta.eroomprojectbe.domain.notification.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 왜 protected인가?
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="notification_id")
    private Long id;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "member_id")
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    private Member member;
//
//    @Builder
//    public Notification(String content, Long groupId, Boolean isRead, NotificationType notificationType, Member member) {
//        this.content = content;
//        this.groupId = groupId;
//        this.isRead = isRead;
//        this.notificationType = notificationType;
//        this.member = member;
//    }
}
