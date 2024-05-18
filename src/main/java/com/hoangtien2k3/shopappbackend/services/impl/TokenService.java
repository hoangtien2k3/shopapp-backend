package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.components.JwtTokenUtil;
import com.hoangtien2k3.shopappbackend.exceptions.payload.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.models.Token;
import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.repositories.TokenRepository;
import com.hoangtien2k3.shopappbackend.repositories.UserRepository;
import com.hoangtien2k3.shopappbackend.utils.LocalizationUtils;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final LocalizationUtils localizationUtils;
    private final JwtTokenUtil jwtTokenUtil;

    public Token createRefreshToken(String token,String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new DataNotFoundException(localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, phoneNumber)));

        // Kiểm tra xem người dùng đã có token chưa
        Optional<Token> existingTokenOptional = tokenRepository.findByUserId(user.getId());

        Token newToken;
        if (existingTokenOptional.isPresent()) {
            // Nếu đã có token, cập nhật thời gian hết hạn
            newToken = existingTokenOptional.get();
            newToken.setExpirationTime(Instant.now().plusMillis(5 * 60 * 60 * 1000));
        } else {
            // Nếu chưa có token, tạo mới
            newToken = Token.builder()
                    .user(user)
                    .refreshToken(UUID.randomUUID().toString())
                    .token(token)
                    .tokenType("Bearer")
                    .expirationTime(Instant.now().plusMillis(5 * 60 * 60 * 1000))
                    .revoked(false)
                    .expired(false)
                    .build();
        }

        // Lưu token vào cơ sở dữ liệu
        tokenRepository.save(newToken);
        return newToken;
    }

    public Token verifyRefreshToken(String refreshToken) {
        Token token = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (token.getExpirationTime().compareTo(Instant.now()) < 0) {
            tokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }

        return token;
    }

}