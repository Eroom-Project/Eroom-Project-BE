package com.sparta.eroomprojectbe.domain.chat.repository;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@Component
public class ChatRoomRepository {

    private final RedisTemplate<String, ChatMessage> redisTemplate;
    private final ListOperations<String, ChatMessage> listOperations;

    private static final String CHAT_ROOM_PREFIX = "chat_room:";

    public ChatRoomRepository(RedisTemplate<String, ChatMessage> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listOperations = redisTemplate.opsForList();
    }

    // challengeId에 해당하는 채팅방의 채팅 내역을 불러오는 메서드
    public List<ChatMessage> getChatHistory(String challengeId) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        return listOperations.range(key, 0, -1);
    }

    // challengeId에 해당하는 채팅방에 채팅 메시지를 저장하는 메서드
    public void saveChatMessage(String challengeId, ChatMessage chatMessage) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        listOperations.leftPush(key, chatMessage);
    }
}