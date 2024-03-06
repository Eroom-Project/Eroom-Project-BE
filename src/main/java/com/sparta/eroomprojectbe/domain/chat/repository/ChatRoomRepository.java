package com.sparta.eroomprojectbe.domain.chat.repository;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Slf4j
@Repository
@Component
public class ChatRoomRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ListOperations<String, Object> listOperations;

    private static final String CHAT_ROOM_PREFIX = "chat_room:";

    /**
     * ChatRoomRepository의 생성자
     * @param redisTemplate Redis 연동을 위한 RedisTemplate 객체
     */
    public ChatRoomRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.listOperations = redisTemplate.opsForList();
    }

    /**
     * challengeId에 해당하는 채팅방의 채팅 내역을 불러오는 메서드
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
        try {
            // Redis 리스트에서 해당 메시지의 인덱스 조회
            Long index = findMessageIndex(key, messageId);

            // 인덱스를 찾은 경우에만 해당 요소를 삭제
            if (index != null) {
                // 인덱스를 사용하여 해당 요소를 삭제
                redisTemplate.opsForList().remove(key, 1, index);
                return true; // 삭제 성공
            } else {
                return false; // 삭제 실패
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // 삭제 실패
        }
    }

    // messageId를 사용하여 메시지의 인덱스를 찾는 메서드
    private Long findMessageIndex(String key, String messageId) throws JSONException {
        List<Object> messages = listOperations.range(key, 0, -1);
        for (int i = 0; i < messages.size(); i++) {
            String jsonMessage = (String) messages.get(i);
            JSONObject jsonObject = new JSONObject(jsonMessage);
            String storedMessageId = jsonObject.getString("messageId");
            if (messageId.equals(storedMessageId)) {
                return Long.valueOf(i); // 메시지의 인덱스를 반환
            }
        }
        return null; // messageId와 일치하는 메시지를 찾지 못한 경우
    }


//    /**
//     * 특정 챌린지방에서 messageId를 사용하여 메시지를 삭제하는 메서드
//     * @param messageId 삭제할 메시지의 UUID 식별자
//     * @return 삭제가 성공하면 true, 실패하면 false 반환
//     */
//    public boolean deleteMessageById(String challengeId, String messageId) {
//        String key = CHAT_ROOM_PREFIX + challengeId;
//        try {
//            // Redis 리스트에서 해당 메시지를 제거합니다.
//            listOperations.remove(key, 0, messageId);
//            // Redis에서 삭제 후 리스트 가져오기
//            List<Object> afterDeletion = listOperations.range(key, 0, -1);
//            return !afterDeletion.contains(messageId); // 삭제 성공
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false; // 삭제 실패
//        }
//    }
}