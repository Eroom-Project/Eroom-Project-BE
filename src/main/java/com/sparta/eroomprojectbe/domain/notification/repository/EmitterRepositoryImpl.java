package com.sparta.eroomprojectbe.domain.notification.repository;

import com.amazonaws.services.s3.transfer.Copy;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepositoryImpl implements EmitterRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId) {
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().split("_")[0].equals(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().split("_")[0].equals(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteById(String id) {
        emitters.remove(id);
    }

    @Override
    public void deleteAllEmitterStartWithId(String memberId) {
        Set<String> keysToDelete = emitters.keySet().stream()
                .filter(key -> key.startsWith(memberId))
                .collect(Collectors.toSet());
        keysToDelete.forEach(emitters::remove);
    }

    @Override
    public void deleteAllEventCacheStartWithId(String memberId) {
        Set<String> keysToDelete = eventCache.keySet().stream()
                .filter(key -> key.startsWith(memberId))
                .collect(Collectors.toSet());
        keysToDelete.forEach(eventCache::remove);
    }

    public SseEmitter findByEmitterId(String emitterId) {
        return emitters.get(emitterId);
    }

}
