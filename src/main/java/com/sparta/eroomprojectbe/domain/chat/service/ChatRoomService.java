package com.sparta.eroomprojectbe.domain.chat.service;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatRoomService {
    private Map<String, List<ChatMessage.MemberInfo>> challengeRoomMemberLists = new HashMap<>();

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    public void userJoinedRoom(String challengeId, String senderNickname, String profileImageUrl) {
        List<ChatMessage.MemberInfo> currentMemberList = challengeRoomMemberLists.get(challengeId);

        // 현재 멤버 리스트가 없는 경우 새로운 리스트 생성
        if (currentMemberList == null) {
            currentMemberList = new ArrayList<>();
            challengeRoomMemberLists.put(challengeId, currentMemberList);
        }

        ChatMessage.MemberInfo memberInfo = new ChatMessage.MemberInfo(senderNickname, profileImageUrl);
        currentMemberList.add(memberInfo);

        messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), currentMemberList);
    }

    public void userLeftRoom(String challengeId, String senderNickname) {
        List<ChatMessage.MemberInfo> currentMemberList = challengeRoomMemberLists.get(challengeId);
        if (currentMemberList != null) {
            currentMemberList.removeIf(memberInfo -> memberInfo.getSender().equals(senderNickname));
            messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), currentMemberList);
        }
    }
}
