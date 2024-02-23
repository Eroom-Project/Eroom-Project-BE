package com.sparta.eroomprojectbe.domain.chat.service;

import com.sparta.eroomprojectbe.domain.chat.entity.MemberInfo;
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

    public void userJoinedRoom(String challengeId, String senderNickname, String profileImageUrl) {
        List<MemberInfo> currentMemberList = challengeRoomMemberLists.get(challengeId);

        // 현재 멤버 리스트가 없는 경우 새로운 리스트 생성
        if (currentMemberList == null) {
            currentMemberList = new ArrayList<>();
            challengeRoomMemberLists.put(challengeId, currentMemberList);
        }

        // senderNickname이 이미 존재하는지 확인
        boolean isExisting = currentMemberList.stream()
                .anyMatch(memberInfo -> memberInfo.getNickname().equals(senderNickname));



        // senderNickname이 이미 존재하지 않는 경우에만 추가
        if (!isExisting) {
            MemberInfo memberInfo = new MemberInfo(senderNickname, profileImageUrl);
            currentMemberList.add(memberInfo);
        }else {
            // 중복된 멤버이므로 클라이언트에게 메시지를 보냄
            messagingTemplate.convertAndSendToUser(senderNickname, "/queue/duplicate-member", "중복된 멤버입니다");
        }

        messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), currentMemberList);
    }

    public void userLeftRoom(String challengeId, String senderNickname) {
        List<MemberInfo> currentMemberList = challengeRoomMemberLists.get(challengeId);
        if (currentMemberList != null) {
            currentMemberList.removeIf(memberInfo -> memberInfo.getNickname().equals(senderNickname));
            messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), currentMemberList);
        }
    }
}
