package com.sparta.eroomprojectbe.domain.notification.controller;

import com.sparta.eroomprojectbe.domain.member.dto.BaseDto;
import com.sparta.eroomprojectbe.domain.notification.service.NotificationService;
import com.sparta.eroomprojectbe.domain.notification.dto.IsReadResponseDto;
import com.sparta.eroomprojectbe.global.jwt.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                @PathVariable Long eventId, HttpServletResponse response) {
        return notificationService.subscribe(userDetails.getMember(), eventId);
    }

//    @GetMapping(value = "/api/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public SseEmitter subscribe(@AuthenticationPrincipal UserDetailsImpl userDetails,
//                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
//                                HttpServletResponse response){
//
//        response.setHeader("Connection", "keep-alive");
//        response.setHeader("Cache-Control", "no-cache");
//        response.setHeader("X-Accel-Buffering", "no");
//
//        return notificationService.subscribe(userDetails.getMember(), lastEventId);
//    }

    @PostMapping("/send-data/{id}")
    public void sendDataTest(@PathVariable Long id) {
        notificationService.notify(id, "data");
    }

    @GetMapping("/api/member/notification/read")
    public ResponseEntity<BaseDto<IsReadResponseDto>> readFindNotification(@AuthenticationPrincipal UserDetailsImpl userDetails){
        IsReadResponseDto response = notificationService.readFindNotification(userDetails.getMember());
        return new ResponseEntity<>(new BaseDto<>("알림 수신", response), HttpStatus.OK);
    }
}
