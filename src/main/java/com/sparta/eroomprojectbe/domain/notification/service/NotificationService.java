package com.sparta.eroomprojectbe.domain.notification.service;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.notification.dto.IsReadResponseDto;
import com.sparta.eroomprojectbe.domain.notification.repository.EmitterRepository;
import com.sparta.eroomprojectbe.domain.notification.repository.EmitterRepositoryImpl;
import com.sparta.eroomprojectbe.domain.notification.repository.NotificationRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void notify(String emitterId, Object event) {
        sendToClient(emitterId, event);
    }

    private void sendToClient(String emitterId, Object data) {
        SseEmitter emitter = emitterRepository.findByEmitterId(emitterId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(emitterId).name("sse").data(data));
            } catch (IOException e) {
                // IOException이 발생하면 저장된 SseEmitter를 삭제하고 예외를 발생시킨다.
                emitterRepository.deleteById(emitterId);
                log.error("SSE 연결 오류 발생", e);
                throw new RuntimeException("연결 오류!");
            }
        }
    }

    @Transactional(readOnly = true)
    public IsReadResponseDto readFindNotification(Member member) {
        boolean isRead = !notificationRepository.existsByIsReadAndMember(false, member);
        return new IsReadResponseDto(isRead);
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
}