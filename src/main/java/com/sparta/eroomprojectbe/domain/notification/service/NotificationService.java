package com.sparta.eroomprojectbe.domain.notification.service;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.notification.dto.IsReadResponseDto;
import com.sparta.eroomprojectbe.domain.notification.repository.EmitterRepository;
import com.sparta.eroomprojectbe.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    // 기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
    private final static String NOTIFICATION_NAME = "notify";

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(Member member, Long userId) {
        // 새로운 SseEmitter를 만든 후 저장하는 메서드인 createEmitter()를 호출한다.
        SseEmitter emitter = createEmitter(userId);

        sendToClient(userId, "EventStream Created. [userId=" + userId + "]");
        return emitter;
    }

//    public SseEmitter subscribe(Member member, String lastEventId) {
//        String emitterId = makeTimeIncludeId(member);
//
//        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));
//
//        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
//        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
//
//        String eventId = makeTimeIncludeId(member);
//        sendDummyData(emitterId, emitter, eventId, "EventStream Created. [memberId="+member.getMemberId()+"]");
//
//        if (hasLostData(lastEventId)){
//            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(member.getMemberId()));
//            events.entrySet().stream()
//                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
//                    .forEach(entry -> sendDummyData(emitterId, emitter, entry.getKey(), entry.getValue()));
//        }
//        return emitter;
//    }

    public void notify(Long userId, Object event) {
        sendToClient(userId, event);
    }

    private void sendToClient(Long userId, Object data) {
        // 유저 ID로 SseEmitter를 찾아 이벤트를 발생 시킨다.
        SseEmitter emitter = emitterRepository.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(userId)).name("sse").data(data));
            } catch (IOException exception) {
                // IOException이 발생하면 저장된 SseEmitter를 삭제하고 예외를 발생시킨다.
                emitterRepository.deleteById(userId);
                log.info("No emitter found");
                throw new RuntimeException("연결 오류!");
            }
        }
    }

    private SseEmitter createEmitter(Long userId) {
        // 새로운 SseEmitter를 만든다
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        // 유저 ID로 SseEmitter를 저장한다.
        emitterRepository.save(userId, emitter);

        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(userId));
        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> emitterRepository.deleteById(userId));

        // 503 Service Unavailable 오류가 발생하지 않도록 첫 데이터를 보낸다.
        try {
            emitter.send(SseEmitter.event().id("").name(NOTIFICATION_NAME).data("Connection completed"));
        } catch (IOException exception) {
            throw new IllegalArgumentException();
        }
        return emitter;
    }

    @Transactional(readOnly = true)
    public IsReadResponseDto readFindNotification(Member member) {
        if (notificationRepository.existsByIsReadAndMember(false, member)) {
            return new IsReadResponseDto(false);
        }
        return new IsReadResponseDto(true);
    }

//    private String makeTimeIncludeId(Member member) {
//        return member.getMemberId() + "_" + System.currentTimeMillis();
//    }
}