package com.sparta.eroomprojectbe.domain.notification.repository;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    boolean existsByIsReadAndReceiver(boolean b, Member member);
}
