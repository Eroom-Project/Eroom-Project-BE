package com.sparta.eroomprojectbe.domain.notification.entity;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.net.ssl.SSLSession;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 왜 protected인가?
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="notification_id")
    private Long id;

    @Column(nullable = false)
    private Long challengeId;

    @Column
    private Long authId;

    @Column(nullable = false)
    private String content;

//    @Embedded
//    private NotificationContent content;

    @Column(nullable = false)
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member receiver;

    public Notification(String content, Long challengeId, Boolean isRead, NotificationType notificationType, Member member) {
        this.content = content;
        this.challengeId = challengeId;
        this.isRead = isRead;
        this.notificationType = notificationType;
        this.receiver = member;
    }

    public Notification(String content, Long challengeId, Long authId, Boolean isRead, NotificationType notificationType, Member member) {
        this.content = content;
        this.challengeId = challengeId;
        this.authId = authId;
        this.isRead = isRead;
        this.notificationType = notificationType;
        this.receiver = member;
    }

    public Member getMember() {
        return receiver;
    }
}
