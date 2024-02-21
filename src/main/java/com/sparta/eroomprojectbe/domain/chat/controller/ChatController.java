package com.sparta.eroomprojectbe.domain.chat.controller;

import com.sparta.eroomprojectbe.domain.challenge.entity.Challenge;
import com.sparta.eroomprojectbe.domain.challenge.repository.ChallengeRepository;
import com.sparta.eroomprojectbe.domain.challenger.entity.Challenger;
import com.sparta.eroomprojectbe.domain.challenger.repository.ChallengerRepository;
import com.sparta.eroomprojectbe.domain.chat.entity.ChatMessage;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Optional;


@Controller
public class ChatController {
    private final ChallengerRepository challengerRepository;
    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;

    public ChatController(ChallengerRepository challengerRepository, ChallengeRepository challengeRepository, MemberRepository memberRepository) {
        this.challengerRepository = challengerRepository;
        this.challengeRepository = challengeRepository;
        this.memberRepository = memberRepository;
    }

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/chat.sendMessage/{challengeId}")
    public void sendMessage(@Payload ChatMessage chatMessage,
                            @DestinationVariable("challengeId") String challengeId) {
        // 회원 ID 가져오기
        String challengeIdString = chatMessage.getChallengeId();
        String memberIdString = chatMessage.getMemberId();

        if (challengeIdString != null && memberIdString != null) {
            // String 값을 Long 값으로 변환
            Long challenge = Long.parseLong(challengeIdString);
            Long member = Long.parseLong(memberIdString);

            // 챌린지와 회원을 찾음
            Optional<Challenge> challengeOptional = challengeRepository.findById(challenge);
            Optional<Member> memberOptional = memberRepository.findById(member);

            // 챌린지와 회원이 존재하는 경우에만 처리
            if (challengeOptional.isPresent() && memberOptional.isPresent()) {
                Optional<Challenger> challengerOptional = challengerRepository.findByChallengeAndMember(challengeOptional.get(), memberOptional.get());

                challengerOptional.ifPresent(challenger -> {
                    String senderNickname = challenger.getMember().getNickname();
                    chatMessage.setSender(senderNickname);
                });
                chatMessage.setTime(LocalDateTime.now());
                chatMessage.setMessagesType(ChatMessage.MessagesType.CHAT);
                messagingTemplate.convertAndSend(String.format("/sub/chat/challenge/%s", challengeId), chatMessage);
            }
        }
    }
}

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