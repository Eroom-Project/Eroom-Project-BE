package com.sparta.eroomprojectbe.domain.notification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public interface EmitterRepository {
    SseEmitter save(Long memberId, SseEmitter emitter);
    void saveEventCache(Long memberId, Object event);
    Map<Long, SseEmitter> findAllEmitterStartWithByMemberId(Long memberId);
    Map<Long, Object> findAllEventCacheStartWithByMemberId(Long memberId);
    void deleteById(Long memberId);
    void deleteAllEmitterStartWithId(Long memberId);
    void deleteAllEventCacheStartWithId(Long memberId);
}
