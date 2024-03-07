package com.sparta.eroomprojectbe.domain.chat.controller;

import com.sparta.eroomprojectbe.domain.challenge.dto.BaseResponseDto;
import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import com.sparta.eroomprojectbe.domain.chat.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ChatController {

    private final ChatMessageService chatMessageService;

    @Autowired
    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    /**
     * 채팅 메시지 보내는 메서드
     * @param chatMessage type, message, sender, time, memberId, challengeId, profileImageUrl, currentMemberList
     * @param challengeId 접속한 챌린지 방 id
     * @param message 사용자가 보내는 메시지 내용
     */
    @MessageMapping("/chat.sendMessage/{challengeId}")
    public void sendMessage(@Payload ChatMessage chatMessage,
                            @DestinationVariable("challengeId") String challengeId,
                            Message<?> message) {
        chatMessageService.saveMessage(challengeId, chatMessage, message);
    }

    /**
     * 특정 challenge와 연관된 메시지 ID를 사용하여 채팅 메시지를 삭제
     * @param challengeId 삭제할 챌린지의 ID
     * @param messageId   삭제할 메시지의 ID
     * @return 삭제 작업 결과를 포함하는 BaseResponseDto를 담은 ResponseEntity
     */
    @DeleteMapping("/api/chat/{challengeId}/{messageId}")
    public ResponseEntity<BaseResponseDto<String>> deleteChatMessage(@PathVariable String challengeId,
                                                                     @PathVariable String messageId) {
        boolean deleteSuccess = chatMessageService.deleteChatMessage(challengeId, messageId);
        if (deleteSuccess) {
            return ResponseEntity.ok().body(new BaseResponseDto<>(null, "채팅 메시지가 성공적으로 삭제되었습니다.", HttpStatus.OK));
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponseDto<>(null, "채팅 메시지 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
