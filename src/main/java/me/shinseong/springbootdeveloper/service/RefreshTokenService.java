package me.shinseong.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.shinseong.springbootdeveloper.domain.RefreshToken;
import me.shinseong.springbootdeveloper.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
