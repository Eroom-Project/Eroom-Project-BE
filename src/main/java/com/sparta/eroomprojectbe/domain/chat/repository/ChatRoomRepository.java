package com.sparta.eroomprojectbe.domain.chat.repository;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        List<ChatMessage> chatHistory = listOperations.range(key, 0, -1);

        for (ChatMessage chatMessage : chatHistory) {
            // Redis에 저장된 LocalDateTime 데이터를 ISO 형식의 문자열로 변환하여 ChatMessage의 time 필드에 설정
            LocalDateTime time = chatMessage.getTime();
            String formattedTime = time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            chatMessage.setTime(LocalDateTime.parse(formattedTime));
        }

        return chatHistory;
    }

    // challengeId에 해당하는 채팅방에 채팅 메시지를 저장하는 메서드
    public void saveChatMessage(String challengeId, ChatMessage chatMessage) {
        String key = CHAT_ROOM_PREFIX + challengeId;
        listOperations.leftPush(key, chatMessage);
    }
}