package com.sparta.eroomprojectbe.domain.notification.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<Long, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private final Map<Long, Object> eventCache = new ConcurrentHashMap<>();


    @Override
    public SseEmitter save(Long memberId, SseEmitter emitter) {
        emitterMap.put(memberId, emitter);
        return emitter;
    }

    @Override
    public void saveEventCache(Long memberId, Object event) {
        eventCache.put(memberId, event);
    }

    @Override
    public Map<Long, SseEmitter> findAllEmitterStartWithByMemberId(Long memberId) {
        return emitterMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<Long, Object> findAllEventCacheStartWithByMemberId(Long memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteById(Long memberId) {
        emitterMap.remove(memberId);
    }

    @Override
    public void deleteAllEmitterStartWithId(Long memberId) {
        emitterMap.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        emitterMap.remove(key);
                    }
                }
        );
    }

    @Override
    public void deleteAllEventCacheStartWithId(Long memberId) {
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        eventCache.remove(key);
                    }
                }
        );
    }
}
