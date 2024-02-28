package com.sparta.eroomprojectbe.domain.notification.service;

import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.notification.dto.IsReadResponseDto;
import com.sparta.eroomprojectbe.domain.notification.repository.EmitterRepository;
import com.sparta.eroomprojectbe.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationService {
    // 기본 타임아웃 설정
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    public SseEmitter subscribe(Member member, Long userId) {
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

    private void sendToClient(Long id, Object data) {
        SseEmitter emitter = emitterRepository.get(id);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().id(String.valueOf(id)).name("sse").data(data));
            } catch (IOException exception) {
                emitterRepository.deleteById(id);
                throw new RuntimeException("연결 오류!");
            }
        }
    }

    private SseEmitter createEmitter(Long id) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(id, emitter);

        // Emitter가 완료될 때(모든 데이터가 성공적으로 전송된 상태) Emitter를 삭제한다.
        emitter.onCompletion(() -> emitterRepository.deleteById(id));
        // Emitter가 타임아웃 되었을 때(지정된 시간동안 어떠한 이벤트도 전송되지 않았을 때) Emitter를 삭제한다.
        emitter.onTimeout(() -> emitterRepository.deleteById(id));

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