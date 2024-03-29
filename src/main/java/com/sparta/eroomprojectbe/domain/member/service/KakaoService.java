package com.sparta.eroomprojectbe.domain.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.eroomprojectbe.domain.member.dto.KakaoUserInfoDto;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import com.sparta.eroomprojectbe.global.refreshToken.RefreshTokenService;
import com.sparta.eroomprojectbe.global.jwt.JwtUtil;
import com.sparta.eroomprojectbe.global.jwt.UserDetailsImpl;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URI;

@Slf4j(topic = "KAKAO Login")
@Service
@Transactional
public class KakaoService {

    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Value("${kakao.client-id}")
    private String kakaoClientId;

    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;

    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;

    public KakaoService(MemberRepository memberRepository, RestTemplate restTemplate, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.memberRepository = memberRepository;
        this.restTemplate = restTemplate;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * 카카오 로그인 서비스 메서드
     *
     * @param code 카카오에서 발급한 인증 코드
     * @param response http 응답 객체
     * @return 로그인한 유저의 닉네임
     * @throws JsonProcessingException
     * @throws UnsupportedEncodingException
     */
    public String kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException, UnsupportedEncodingException {
        String kakaoAccessToken = getToken(code);
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);
        Member kakaoUser = registerKakaoUserIfNeeded(kakaoUserInfo);
        Authentication authentication = forceLogin(kakaoUser);

        String accessToken = jwtUtil.createAccessToken(kakaoUserInfo.getEmail(), kakaoUser.getRole());
        jwtUtil.addJwtToCookie(accessToken, response, JwtUtil.AUTHORIZATION_HEADER);

        String refreshToken = jwtUtil.createRefreshToken(kakaoUserInfo.getEmail());
        jwtUtil.addJwtToCookie(refreshToken, response, JwtUtil.REFRESH_TOKEN_HEADER);

        refreshTokenService.saveRefreshToken(kakaoUser.getEmail());
        return kakaoUser.getNickname();
    }

    /**
     * 발급된 인증코드를 인자로 받아 토큰을 추출하는 메서드
     *
     * @param code 카카오 발급 인증코드
     * @return 카카오 access token
     * @throws JsonProcessingException
     */
    private String getToken(String code) throws JsonProcessingException {
        log.info("인가코드: " + code);
        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token") //
                .encode()
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", kakaoRedirectUri);
        body.add("code", code);
        body.add("client_secret", kakaoClientSecret);

        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    /**
     * access token을 인자로 받아 유저 정보를 요청하는 메서드
     *
     * @param accessToken 카카오 발급 access token
     * @return kakaoUserInfoDto
     * @throws JsonProcessingException
     */
    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        log.info("accessToken: " + accessToken);

        // 요청 URL 만들기
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .build()
                .toUri();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RequestEntity<Void> requestEntity = RequestEntity
                .get(uri)
                .headers(headers)
                .build();

        // HTTP 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                requestEntity,
                String.class
        );

        JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String email = jsonNode.get("kakao_account")
                .get("email").asText();
        String profileImageUrl = jsonNode.get("kakao_account")
                .get("profile").get("profile_image_url").asText();

        log.info("카카오 사용자 정보: " + id + ", " + nickname + ", " + email);
        return new KakaoUserInfoDto(id, nickname, email, profileImageUrl);
    }

    /**
     * 카카오 유저 등록 메서드
     *
     * @param kakaoUserInfo 카카오로 로그인한 유저의 정보
     * @return 등록된 유저 객체
     */
    private Member registerKakaoUserIfNeeded(KakaoUserInfoDto kakaoUserInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        log.info("kakaoId : "+ kakaoId);
        Member kakaoUser = memberRepository.findByKakaoId(kakaoId).orElse(null);

        if (kakaoUser == null) {
            // 카카오 사용자 email 동일한 email 가진 회원이 있는지 확인
            String kakaoEmail = kakaoUserInfo.getEmail();
            Member sameEmailUser = memberRepository.findByEmail(kakaoEmail).orElse(null);

            if (sameEmailUser != null) {
                kakaoUser = sameEmailUser;
                // 기존 회원정보에 카카오 Id 추가
                kakaoUser = kakaoUser.kakaoIdUpdate(kakaoId);
            } else {
                kakaoUser = new Member(kakaoEmail, "", kakaoUserInfo.getNickname(), kakaoId, kakaoUserInfo.getProfileImageUrl());
            }

            memberRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

    /**
     * 카카오 로그인 유저의 정보를 스프링 시큐리티에 담는 메서드
     * @param kakaoUser 카카오 로그인 유저 객체
     * @return 해당 유저의 Authentication 객체
     */
    private Authentication forceLogin(Member kakaoUser) {
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}
