package com.sparta.eroomprojectbe.domain.chat.repository;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@Component
public class ChatRoomRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ListOperations<String, Object> listOperations;

    private static final String CHAT_ROOM_PREFIX = "chat_room:";

    /**
     * ChatRoomRepository의 생성자
     *
     * @param redisTemplate Redis 연동을 위한 RedisTemplate 객체
     */
    public ChatRoomRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listOperations = redisTemplate.opsForList();
    }

    /**
     * challengeId에 해당하는 채팅방의 채팅 내역을 불러오는 메서드
     *
     * @param challengeId 채팅 내역을 가져올 채팅방의 고유 식별자
     * @return 채팅 내역을 담은 리스트
     */
    public List<Object> getChatHistory(String challengeId) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        return listOperations.range(key, 0, -1);
    }

    /**
     * challengeId에 해당하는 채팅방에 채팅 메시지를 저장하는 메서드
     * @param challengeId 채팅 메시지를 저장할 채팅방의 고유 식별자
     * @param chatMessage 저장할 채팅 메시지 객체
     */
    public void saveChatMessage(String challengeId, ChatMessage chatMessage) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        listOperations.rightPush(key, chatMessage);

        // 키의 TTL을 30일로 설정
        redisTemplate.expire(key, Duration.ofDays(30));
    }

    /**
     * 특정 챌린지방에서 messageId를 사용하여 메시지를 삭제하는 메서드
     * @param messageId   삭제할 메시지의 UUID 식별자
     * @param challengeId 챌린지 식별자
     * @return 삭제가 성공하면 true, 실패하면 false 반환
     */
    public boolean deleteMessageById(String challengeId, String messageId) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        List<Object> messages = listOperations.range(key, 0, -1);
        for (Object message : messages) {
            Map<String, Object> messageMap = (Map<String, Object>) message;
            if (messageMap.get("messageId").equals(messageId)) {
                listOperations.remove(key, 1, message);
                return true;
            }
        }
        return false; // 삭제 실패
    }
}

