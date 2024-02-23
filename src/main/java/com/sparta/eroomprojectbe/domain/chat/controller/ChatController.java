package com.sparta.eroomprojectbe.domain.chat.controller;

import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import com.sparta.eroomprojectbe.domain.chat.service.ChatMessageService;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;


@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;

    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @MessageMapping("/chat.sendMessage/{challengeId}")
    public void sendMessage(@Payload ChatMessage chatMessage,
                            @DestinationVariable("challengeId") String challengeId,
                            Message<?> message) {
        chatMessageService.saveMessage(challengeId, chatMessage, message);
    }

//    @GetMapping("/api/chat/dupuser")
//    public ResponseEntity<ResponseDto<String>> getChatMember(@Valid @RequestBody EnterRequestDto requestDto) {
//        String message = ChatMessageService.getChattingMember(requestDto);
//        return ResponseEntity.ok(new ResponseDto<>(null, message, HttpStatus.OK));
//    }
}



//        switch (chatMessage.getMessagesType()) {
//            case JOIN -> {
//                System.out.println("MessagesType : JOIN");
//                messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), chatMessage);
//            }
//            case CHAT -> {
//                System.out.println("MessagesType : CHAT");
//                messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), chatMessage);
//            }
//            case LEAVE -> {
//                System.out.println("MessagesType : LEAVE");
//                messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), chatMessage);
//            }
//        }
//
//    @MessageMapping("/chat.sendMessage")
//    @SendTo("/sub/chat/challenge/{challengeId}")
//    public ChatMessage sendMessage(@Payload ChatMessage chatMessage){
//        // 회원 ID 가져오기
//        String challengeIdString = chatMessage.getChallengeId();
//        String memberIdString = chatMessage.getMemberId();
//
//        if (challengeIdString != null && memberIdString != null) {
//            // String 값을 Long 값으로 변환
//            Long challenge = Long.parseLong(challengeIdString);
//            Long member = Long.parseLong(memberIdString);
//
//            // 챌린지와 회원을 찾음
//            Optional<Challenge> challengeOptional = challengeRepository.findById(challenge);
//            Optional<Member> memberOptional = memberRepository.findById(member);
//
//            // 챌린지와 회원이 존재하는 경우에만 처리
//            if (challengeOptional.isPresent() && memberOptional.isPresent()) {
//                Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challengeOptional.get(), memberOptional.get());
//
//                challengerOptional.ifPresent(challenger -> {
//                    String senderNickname = challenger.getMember().getNickname();
//                    chatMessage.setSender(senderNickname);
//                    chatMessage.setTime(LocalDateTime.now());
//                });
//            }
//        }
//        return chatMessage; // 채팅 메시지 반환
//    }
//}