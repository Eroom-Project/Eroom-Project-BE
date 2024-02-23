package com.sparta.eroomprojectbe.domain.chat.service;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatRoomService {
    private Map<String, List<ChatMessage.MemberInfo>> challengeRoomMemberLists = new HashMap<>();

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    public void userJoinedRoom(String challengeId, String senderNickname, String profileImageUrl) {
        List<ChatMessage.MemberInfo> currentMemberList = challengeRoomMemberLists.computeIfAbsent(challengeId, k -> new ArrayList<>());
        ChatMessage.MemberInfo memberInfo = new ChatMessage.MemberInfo(senderNickname, profileImageUrl);
        currentMemberList.add(memberInfo);
        broadcastCurrentMemberList(challengeId);
    }

    public void userLeftRoom(String challengeId, String senderNickname) {
        List<ChatMessage.MemberInfo> currentMemberList = challengeRoomMemberLists.get(challengeId);
        if (currentMemberList != null) {
            Iterator<ChatMessage.MemberInfo> iterator = currentMemberList.iterator();
            while (iterator.hasNext()) {
                ChatMessage.MemberInfo memberInfo = iterator.next();
                if (memberInfo.getNickname().equals(senderNickname)) {
                    iterator.remove(); // 해당 멤버 삭제
                }
            }
            broadcastCurrentMemberList(challengeId);
        }
    }

    private void broadcastCurrentMemberList(String challengeId) {
        List<ChatMessage.MemberInfo> currentMemberList = challengeRoomMemberLists.get(challengeId);
        if (currentMemberList != null) {
            messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), currentMemberList);
        } else {
            // 현재 멤버 리스트가 null인 경우 빈 리스트를 전송
            messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), new ArrayList<>());
        }
    }
}
