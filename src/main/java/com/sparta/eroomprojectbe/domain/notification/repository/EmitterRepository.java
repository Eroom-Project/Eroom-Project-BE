package com.sparta.eroomprojectbe.domain.notification.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {
    // 모든 Emitters를 저장하는 ConcurrentHashMap
    // userId를 key 값으로, sseEmitter를 밸류 값으로 한다
    // pirvate final 이어야 하나?
    // 왜 concurrenthashmap 이지? 그냥 hashmap은 안 될까?
    private final Map<Long, SseEmitter> emitterHashMap = new ConcurrentHashMap<>();

    // 저장하는 로직 void로 둘까, or SseEmitter로 반환하게 둘까
    public void save(Long id, SseEmitter emitter) {
        emitterHashMap.put(id, emitter);
    }

    public void deleteById(Long id) {
        emitterHashMap.remove(id);
    }

    public SseEmitter get(Long id) {
        return emitterHashMap.get(id);
    }
}
