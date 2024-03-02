package com.sparta.eroomprojectbe.domain.member.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.TreeMap;

@RestController
public class HealthCheckController {

    @Value("${server.env}")
    private String env;

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.serverAddress}")
    private String serverAddress;

    @Value("${serverName}")
    private String serverName;

    /**
     * 서버의 health check하는 엔드포인트
     * @return 서버의 상태 및 환경 설정 정보를 담은 응답 엔터티
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> responseData = new TreeMap<>();
        responseData.put("serverName", serverName);
        responseData.put("serverPort", serverPort);
        responseData.put("serverAddress", serverAddress);
        responseData.put("env", env);
        return ResponseEntity.ok(responseData);
    }

    /**
     * 서버의 환경 설정 정보를 확인하는 엔드포인트
     * @return 서버의 환경 설정 정보를 담은 응답 엔터티
     */
    @GetMapping("/env")
    public ResponseEntity<?> getEnv() {
        return ResponseEntity.ok(env);
    }
}