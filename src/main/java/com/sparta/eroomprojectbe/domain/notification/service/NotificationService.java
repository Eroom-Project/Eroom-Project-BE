package com.sparta.eroomprojectbe.domain.notification.service;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.notification.dto.NotificationRequestDto;
import com.sparta.eroomprojectbe.domain.notification.dto.NotificationResponseDto;
import com.sparta.eroomprojectbe.domain.notification.entity.Notification;
import com.sparta.eroomprojectbe.domain.notification.repository.EmitterRepositoryImpl;
import com.sparta.eroomprojectbe.domain.notification.repository.NotificationRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 기본 타임아웃 설정
    private final EmitterRepositoryImpl emitterRepository;
    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(Member member, String lastEventId, HttpServletResponse response) {
        String emitterId = makeTimeIncludeId(member);
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        response.setHeader("X-Accel-Buffering", "no"); // NGINX PROXY 에서의 필요설정 불필요한 버퍼링방지

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError((e) -> log.error("SSE Emitter Error: ", e));

        emitterRepository.save(emitterId, emitter);

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(member);
        sendDummyData(eventId, emitter, emitterId, "EventStream Created. [memberId=" + member.getMemberId() + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        resendLostData(lastEventId, member, emitter);
        return emitter;
    }

    public void send(NotificationRequestDto requestDto) {
        Notification notification = saveNotification(requestDto);
        sendNotification(requestDto, notification);
    }

    // 알림 보내기
    private void sendNotification(NotificationRequestDto request, Notification notification) {
        String receiverId = String.valueOf(request.getReceiver().getMemberId());
        // 유저의 모든 SseEmitter 가져옴
        Map<String, SseEmitter> emitters = emitterRepository
                .findAllEmitterStartWithByMemberId(receiverId);
        emitters.forEach((key, emitter) -> {
            NotificationResponseDto responseDto = NotificationResponseDto.of(notification);
            // 데이터 캐시 저장 (유실된 데이터 처리 위함)
            emitterRepository.saveEventCache(key, responseDto);
            // 데이터 전송
            sendToClient(key, responseDto);
        });
    }

    // 데이터 전송 로직 수정 (오버로딩 또는 기존 메서드 수정)
    private void sendToClient(String emitterId, NotificationResponseDto data) {
        SseEmitter emitter = emitterRepository.findByEmitterId(emitterId);
        if (emitter != null) {
            try {
                log.info("Sending notification to client: {}", data);
                emitter.send(SseEmitter.event().id(emitterId).name("sse").data(data));
                log.info("Notification sent successfully");
            } catch (IOException e) {
                emitterRepository.deleteById(emitterId);
                log.error("Failed to send notification", e);
                throw new RuntimeException("Connection error!");
            }
        } else {
            log.warn("No emitter found for ID: {}", emitterId);
        }
    }


    private String makeTimeIncludeId(Member member) {
        return member.getMemberId() + "_" + System.currentTimeMillis();
    }

    private void resendLostData(String lastEventId, Member member, SseEmitter emitter) {
        // 놓친 이벤트가 있다면
        if (!lastEventId.isEmpty()) {
            // 멈버 아이디를 기준으로 캐시된 모든 이벤트를 가져온다.
            Map<String, Object> cachedEvents = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(member.getMemberId()));

            // 모든 이벤트를 순회하며
            for (Map.Entry<String, Object> entry : cachedEvents.entrySet()) {
                // lastEventId보다 큰 ID(뒷 시간에 일어난 이벤트)만 필터링하여
                if (lastEventId.compareTo(entry.getKey()) < 0) {
                    try {
                        // 재전송한다.
                        emitter.send(SseEmitter.event().id(entry.getKey()).data(entry.getValue()));
                    } catch (IOException e) {
                        log.error("Resending lost data failed for memberId: {}", member.getMemberId(), e);
                    }
                }
            }
        }
    }

    private void sendDummyData(String emitterId, SseEmitter emitter, String eventId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    // 알람 저장
    @Transactional
    protected Notification saveNotification(NotificationRequestDto requestDto) {
        Notification notification = Notification.builder()
                .receiver(requestDto.getReceiver())
                .notificationType(requestDto.getNotificationType())
                .content(requestDto.getContent())
                .challengeId(requestDto.getChallengeId())
                .authId(requestDto.getAuthId())
                .isRead(false)
                .build();
        return notificationRepository.save(notification);
    }
}