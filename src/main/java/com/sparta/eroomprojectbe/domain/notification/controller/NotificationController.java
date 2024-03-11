package com.sparta.eroomprojectbe.domain.notification.controller;

import com.sparta.eroomprojectbe.domain.notification.service.NotificationService;
import com.sparta.eroomprojectbe.global.jwt.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 알림 관련 요청 처리
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * 클라이언트의 실시간 알림 구독 요청을 처리하고 Server-Sent Events(SSE) 스트림 반환.
     *
     * @param userDetails 현재 사용자의 인증된 정보를 포함하는 UserDetailsImpl 객체
     * @param lastEventId 클라이언트가 마지막으로 수신한 이벤트의 ID
     * @param response    HTTP 응답 객체
     * @return Server-Sent Events(SSE) 스트림을 포함하는 ResponseEntity
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId, HttpServletResponse response) {
        return new ResponseEntity<>(notificationService.subscribe(userDetails.getMember(), lastEventId, response), HttpStatus.OK);
    }
}
