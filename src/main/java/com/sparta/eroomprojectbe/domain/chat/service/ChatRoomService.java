package com.sparta.eroomprojectbe.domain.chat.service;

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

    /**
     * 사용자가 채팅방에 입장했을 때 실행되는 메서드
     * @param challengeId 채팅방의 고유 식별자
     * @param memberId 사용자의 고유 식별자
     * @param senderNickname 사용자의 닉네임
     * @param profileImageUrl 사용자의 프로필 이미지 URL
     */
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
        List<Object> chatHistory = chatRoomRepository.getChatHistory(challengeId);

        // 채팅방의 구독자들에게 이전 대화 내용 전송
        messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s/history/%s", challengeId, memberId), chatHistory);
        // 채팅방의 구독자들에게 현재 멤버 리스트 전송
        messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), currentMemberList);
    }

    /**
     * 사용자가 채팅방을 나갔을 때 실행되는 메서드
     * @param challengeId 채팅방의 고유 식별자
     * @param senderNickname 사용자의 닉네임
     */
    public void userLeftRoom(String challengeId, String senderNickname) {
        List<MemberInfo> currentMemberList = challengeRoomMemberLists.get(challengeId);
        if (currentMemberList != null) {
            currentMemberList.removeIf(memberInfo -> memberInfo.getNickname().equals(senderNickname));
            messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), currentMemberList);
        }
    }
}
