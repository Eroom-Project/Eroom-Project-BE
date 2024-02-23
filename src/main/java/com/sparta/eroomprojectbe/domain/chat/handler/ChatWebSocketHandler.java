package com.sparta.eroomprojectbe.domain.chat.handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 클라이언트 연결 시 세션을 맵에 추가합니다.
        String userId = getUserId(session);
        sessions.put(userId, session);

        // 중복 접속 여부 확인
        if (isDuplicateConnection(userId)) {
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Duplicate connection detected"));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 클라이언트로부터 메시지를 수신할 때의 동작을 정의합니다.
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 클라이언트 연결 종료 시 해당 세션을 맵에서 제거합니다.
        String userId = getUserId(session);
        sessions.remove(userId);
    }

    private String getUserId(WebSocketSession session) {
        // 실제로는 세션에서 사용자를 식별할 수 있는 정보를 추출합니다.
        // 여기서는 간단하게 세션 ID를 사용합니다.
        return session.getId();
    }

    private boolean isDuplicateConnection(String userId) {
        // 중복 접속 여부를 확인합니다.
        return sessions.containsKey(userId);
    }
}
