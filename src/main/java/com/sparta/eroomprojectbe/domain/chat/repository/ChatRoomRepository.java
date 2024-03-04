package com.sparta.eroomprojectbe.domain.chat.repository;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@Component
public class ChatRoomRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ListOperations<String, Object> listOperations;

    private static final String CHAT_ROOM_PREFIX = "chat_room:";

    public ChatRoomRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listOperations = redisTemplate.opsForList();
    }

    // challengeId에 해당하는 채팅방의 채팅 내역을 불러오는 메서드
    public List<ChatMessage> getChatHistory(String challengeId) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        // 타입 변환 처리 - redis에서 조회한 객체 목록을 ChatMessage 타입의 객체 목록으로 변환
        List<Object> objects = listOperations.range(key, 0, -1);
        if (objects != null) {
            return objects.stream()
                    .filter(ChatMessage.class::isInstance) // ChatMessage 타입이면
                    .map(ChatMessage.class::cast) // cast 변환 수행
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    // challengeId에 해당하는 채팅방에 채팅 메시지를 저장하는 메서드
    public void saveChatMessage(String challengeId, ChatMessage chatMessage) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        listOperations.rightPush(key, chatMessage);

        // 키의 TTL을 30일로 설정
        redisTemplate.expire(key, Duration.ofDays(30));
    }

    // 특정 챌린지방에서 메시지 번호를 사용하여 메시지를 삭제하는 메서드
    public boolean deleteMessageByNumber(String challengeId, Long messageNumber) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        try {
            // Redis 리스트에서 해당 번호의 메시지를 제거합니다.
            listOperations.trim(key, messageNumber, messageNumber);
            return true; // 삭제 성공
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 삭제 실패
        }
    }
}