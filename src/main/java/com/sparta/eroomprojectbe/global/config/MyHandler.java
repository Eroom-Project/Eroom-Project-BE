package com.sparta.eroomprojectbe.global.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyHandler extends TextWebSocketHandler {
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception { //웹소켓 연결이 수립된 후 이용하는 세션과 메세지
        System.out.println(message);
        System.out.println(message.getPayload());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception { //연결이 수립된 직후
        System.out.println("afterConnectionEstablished:" + session.toString());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception { //연결이 닫힌 직후
        super.afterConnectionClosed(session, status);
    }
}
