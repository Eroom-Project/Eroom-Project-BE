package com.sparta.eroomprojectbe.global;

import com.sparta.eroomprojectbe.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveRefreshToken(String keyEmail) {
        String refreshToken = jwtUtil.createRefreshToken(keyEmail);
        Optional<RefreshToken> findToken = refreshTokenRepository.findByKeyEmail(keyEmail);
        if (findToken.isPresent()){
            findToken.get().updateToken(refreshToken);
        } else {
            refreshTokenRepository.save(new RefreshToken(keyEmail, refreshToken));
        }
    }

    @Transactional
    public void removeRefreshToken(String keyEmail) {
        refreshTokenRepository.findByKeyEmail(keyEmail)
                .ifPresent(refreshTokenRepository::delete);
    }

    public Optional<RefreshToken> getRefreshToken(String keyEmail) {
        return refreshTokenRepository.findByKeyEmail(keyEmail);
    }
}