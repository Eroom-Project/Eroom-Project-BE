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

    /**
     * 클라이언트가 SSE를 구독하는 서비스 메서드.
     * 새로운 SseEmitter를 생성하고 Nginx 버퍼링 방지를 위해 필요한 Http 헤서를 설정.
     * 클라이언트가 놓친 이벤트도 함께 전송
     *
     * @param member 로그인한 유저 객체
     * @param lastEventId 유저가 마지막으로 수신한 이벤트의 id
     * @param response http 응답 객체
     * @return 구독한 클라이언트와 연결된 emitter 객체
     */
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

    /**
     * 알림 전송 서비스 메서드. sendNotification으로 연결됨.
     *
     * @param requestDto 알림에 필요한 정보들을 담은 request dto
     */
    public void send(NotificationRequestDto requestDto) {
        Notification notification = saveNotification(requestDto);
        sendNotification(requestDto, notification);
    }

    /**
     * 알림 전송 서비스 메서드. 주어진 정보를 바탕으로 특정 클라이언트에게 알림을 전송.
     * 전송 실패 시, 연결된 emitter 객체 삭제
     *
     * @param request 알림에 필요한 정보들을 담은 request dto
     * @param notification 알림 객체
     */
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

    /**
     * 특정 클라이언트에게 알림 전송하는 서비스 메서드.
     * emitter를 사용하여 실시간으로 알림 전송
     *
     * @param emitterId 해당 클라이언트와 연결시켜주는 emitter 식별자
     * @param responseDto 알림 응답 객체
     */
    private void sendToClient(String emitterId, NotificationResponseDto responseDto) {
        SseEmitter emitter = emitterRepository.findByEmitterId(emitterId);
        if (emitter != null) {
            try {
                log.info("Sending notification to client: {}", responseDto);
                emitter.send(SseEmitter.event().id(emitterId).name("sse").data(responseDto));
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

    /**
     * 회원 ID와 현재 시간을 조합하여 고유한 식별자를 생성하는 메서드
     * 각 회원을 특정 이벤트와 연결시킴
     *
     * @param member 알림을 수신할 회원 객체
     * @return 생성된 고유 식별자 문자열
     */
    private String makeTimeIncludeId(Member member) {
        return member.getMemberId() + "_" + System.currentTimeMillis();
    }

    /**
     * 회원이 놓친 이벤트를 전송하는 메서드
     *
     * @param lastEventId 회원이 마지막으로 수신한 이벤트 id
     * @param member
     * @param emitter
     */
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

    /**
     * 클라이언트 연결 초기에 503 에러가 뜨지 않도록 더미 데이터를 전송하는 메서드
     * 연결이 성공적으로 이루어졌는지 확인
     *
     * @param emitterId 해당 클라이언트와 고유하게 연결된 emitter id
     * @param emitter
     * @param eventId
     * @param data 더미 데이터
     */
    private void sendDummyData(String emitterId, SseEmitter emitter, String eventId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }

    /**
     * 새 알림 생성하고 저장하는 서비스 메서드
     *
     * @param requestDto 알림 생성에 필요한 정보를 담고 있는 dto
     * @return 알림 객체
     */
    @Transactional
    protected Notification saveNotification(NotificationRequestDto requestDto) {
        Notification notification = Notification.builder()
                .receiver(requestDto.getReceiver())
                .notificationType(requestDto.getNotificationType())
                .content(requestDto.getContent())
                .challengeId(requestDto.getChallengeId())
                .authId(requestDto.getAuthId())
                .build();
        return notificationRepository.save(notification);
    }
}