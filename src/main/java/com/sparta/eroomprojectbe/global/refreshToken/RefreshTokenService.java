package com.sparta.eroomprojectbe.global.refreshToken;

import com.sparta.eroomprojectbe.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    public String saveRefreshToken(String keyEmail) {
        String refreshToken = jwtUtil.createRefreshToken(keyEmail);
        Optional<RefreshToken> findToken = refreshTokenRepository.findById(keyEmail);
        if (findToken.isPresent()){
            findToken.get().updateToken(refreshToken);
            refreshTokenRepository.save(findToken.get());
        } else {
            refreshTokenRepository.save(new RefreshToken(keyEmail, refreshToken));
        }
        return refreshToken;
    }

    public void removeRefreshToken(String keyEmail) {
        refreshTokenRepository.findById(keyEmail)
                .ifPresent(refreshTokenRepository::delete);
    }

    public Optional<RefreshToken> getRefreshToken(String keyEmail) {
        return refreshTokenRepository.findById(keyEmail);
    }
}