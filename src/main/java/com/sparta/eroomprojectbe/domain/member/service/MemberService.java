package com.sparta.eroomprojectbe.domain.member.service;

import com.sparta.eroomprojectbe.domain.member.dto.ProfileResponseDto;
import com.sparta.eroomprojectbe.domain.member.dto.SignupRequestDto;
import com.sparta.eroomprojectbe.domain.member.dto.SignupResponseDto;
import com.sparta.eroomprojectbe.domain.member.entity.Member;
import com.sparta.eroomprojectbe.domain.member.repository.MemberRepository;
import com.sparta.eroomprojectbe.global.RefreshToken;
import com.sparta.eroomprojectbe.global.RefreshTokenRepository;
import com.sparta.eroomprojectbe.global.jwt.JwtUtil;
import com.sparta.eroomprojectbe.global.rollenum.MemberRoleEnum;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        String email = requestDto.getEmail();
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 등록
        Member member = new Member(email, password, requestDto.getNickname());
        return new SignupResponseDto(memberRepository.save(member));
    }

    // 이메일 중복 확인
    public String emailCheck(String email){
        return memberRepository.existsByEmail(email) ? "중복된 email입니다." : "사용 가능한 email입니다.";
    }

    // 닉네임 중복 확인
    public String nicknameCheck(String nickname) {
        return memberRepository.existsByNickname(nickname) ? "중복된 닉네임입니다." : "사용 가능한 닉네임입니다.";
    }

    @Transactional
    public ResponseEntity<String> reissueToken(@CookieValue(name = JwtUtil.REFRESH_TOKEN_HEADER) String refreshToken, HttpServletResponse res) throws UnsupportedEncodingException {
        if (jwtUtil.validateToken(refreshToken)) {
            Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
            String userEmail = claims.getSubject();
            MemberRoleEnum userRole = (MemberRoleEnum) claims.get("roles");

            // DB에서 저장된 Refresh Token 가져오기
            Optional<RefreshToken> storedRefreshToken = refreshTokenRepository.findByKeyEmail(userEmail);

            // DB에 저장된 Refresh Token이 있고 일치한다면
            if (storedRefreshToken.isPresent() && storedRefreshToken.get().getRefreshToken().equals(refreshToken)) {
                // 사용자 정보로 새로운 Access Token 생성
                String newAccessToken = jwtUtil.createAccessToken(userEmail, userRole);
                jwtUtil.addJwtToCookie(newAccessToken, res, JwtUtil.AUTHORIZATION_HEADER);

                // 새로운 Access Token을 응답으로 반환
                return ResponseEntity.ok().build();
            }
        }
        // Refresh Token이 유효하지 않으면 적절한 응답을 반환하면서 / 쿠키만료 메서드를 jwtUtil에 따로 만들 것
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 유효하지 않습니다.");
    }

    @Transactional
    public ResponseEntity<String> logout(@CookieValue(name = "Refresh_token") String refreshToken) {
        if (jwtUtil.validateToken(refreshToken)) {
            Claims claims = jwtUtil.getUserInfoFromToken(refreshToken);
            String userEmail = claims.getSubject();

            Optional<RefreshToken> storedRefreshToken = refreshTokenRepository.findByKeyEmail(userEmail);

            if (storedRefreshToken.isPresent() && storedRefreshToken.get().getRefreshToken().equals(refreshToken)) {
                storedRefreshToken.get().setExpiration(System.currentTimeMillis());
                refreshTokenRepository.save(storedRefreshToken.get());

                return ResponseEntity.ok("로그아웃 성공");
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token이 유효하지 않습니다.");
    }


    public ProfileResponseDto getProfile(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail())
                .orElseThrow(()-> new EntityNotFoundException("해당 멤버를 찾을 수 없습니다."));
        return new ProfileResponseDto(findMember);
    }
}
