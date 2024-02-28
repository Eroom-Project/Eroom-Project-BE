//package com.sparta.eroomprojectbe.domain.chat.repository;
//
//import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
//import org.springframework.data.redis.core.ListOperations;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
//@Component
//public class ChatRoomRepository {
//
//    private final RedisTemplate<String, ChatMessage> redisTemplate;
//    private final ListOperations<String, ChatMessage> listOperations;
//
//    private static final String CHAT_ROOM_PREFIX = "chat_room:";
//
//    public ChatRoomRepository(RedisTemplate<String, ChatMessage> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//        this.listOperations = redisTemplate.opsForList();
//    }
//
//    public void saveChatMessage(String roomId, ChatMessage chatMessage) {
//        String key = CHAT_ROOM_PREFIX + roomId;
//        listOperations.leftPush(key, chatMessage);
//    }
//
//    public List<ChatMessage> getChatMessages(String roomId) {
//        String key = CHAT_ROOM_PREFIX + roomId;
//        return listOperations.range(key, 0, -1);
//    }
//}