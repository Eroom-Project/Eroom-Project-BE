package com.sparta.eroomprojectbe.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    // Stomp 엔드포인트 등록: 특정 도메인에서만 웹소켓 연결을 허용
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp") // 연결될 Endpoint
                .setAllowedOriginPatterns("*") // 해당 도메인에서만 웹소켓 연결 허용. 개발 중에는 모두 허용하되 필요에 따라 조정
                .withSockJS() // websocket 관련 자바스크립트 라이브러리 SockJS 설정
                .setHeartbeatTime(1000); // 클라이언트 - 서버 연결 상태 확인 주기 : 1초
    }

    @Override
    // 메시지 브로커 설정
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/sub"); // 수신 메시지를 받는 데 사용되는 ULR /queue는 개별 메시지, /sub는 해당 주제를 구독한 이들의 전체 메시지
        registry.setApplicationDestinationPrefixes("/pub"); // 송신 메시지를 전송하는 데 사용되는 URL

    }

    @Override
    // 웹소켓 전송 설정 조정
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(64 * 1024); // 메시지 최대 크기 default : 64 * 1024
        registration.setSendTimeLimit(10 * 10000); // 메시지 전송 시간 default : 10 * 10000
        registration.setSendBufferSizeLimit(512 * 1024); // 버퍼 사이즈 default : 512 * 1024
    }
}