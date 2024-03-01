package com.sparta.eroomprojectbe.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import com.sparta.eroomprojectbe.domain.chat.entity.MemberInfo;
import com.sparta.eroomprojectbe.domain.chat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatRoomService {
    private Map<String, List<MemberInfo>> challengeRoomMemberLists = new HashMap<>();

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public void userJoinedRoom(String challengeId, String memberId, String senderNickname, String profileImageUrl) {

        List<MemberInfo> currentMemberList = challengeRoomMemberLists.get(challengeId);

        // 현재 멤버 리스트가 없는 경우 새로운 리스트 생성
        if (currentMemberList == null) {
            currentMemberList = new ArrayList<>();
            challengeRoomMemberLists.put(challengeId, currentMemberList);
        }
        // 새로운 멤버 정보 추가
        MemberInfo memberInfo = new MemberInfo(memberId, senderNickname, profileImageUrl);
        currentMemberList.add(memberInfo);

        // 해당 채팅방의 이전 대화 내용 불러오기
        List<ChatMessage> chatHistory = chatRoomRepository.getChatHistory(challengeId);

        // ChatMessage 객체의 시간 필드를 역직렬화하여 LocalDateTime 형식으로 변환합니다.
        List<Map<String, Object>> serializedChatHistory = new ArrayList<>();
        for (ChatMessage chatMessage : chatHistory) {
            Map<String, Object> serializedMessage = objectMapper.convertValue(chatMessage, Map.class);
            serializedMessage.put("time", chatMessage.getTime().toString()); // 시간 필드를 문자열로 변환하여 추가합니다.
            serializedChatHistory.add(serializedMessage);
        }

        // 채팅방의 구독자들에게 이전 대화 내용 전송
        messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s/history", challengeId), serializedChatHistory);
        // 채팅방의 구독자들에게 현재 멤버 리스트 전송
        messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), currentMemberList);
    }
//        // senderNickname이 이미 존재하는지 확인
//        boolean isExisting = currentMemberList.stream()
//                .anyMatch(memberInfo -> memberInfo.getNickname().equals(senderNickname));
//
//        // senderNickname이 이미 존재하지 않는 경우에만 추가
//        if (!isExisting) {
//            MemberInfo memberInfo = new MemberInfo(memberId, senderNickname, profileImageUrl);
//            currentMemberList.add(memberInfo);
//        }

    public void userLeftRoom(String challengeId, String senderNickname) {
        List<MemberInfo> currentMemberList = challengeRoomMemberLists.get(challengeId);
        if (currentMemberList != null) {
            currentMemberList.removeIf(memberInfo -> memberInfo.getNickname().equals(senderNickname));
            messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), currentMemberList);
        }
    }
}
