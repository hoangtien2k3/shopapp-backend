package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.components.JwtTokenUtil;
import com.hoangtien2k3.shopappbackend.exceptions.payload.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.models.Token;
import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.repositories.TokenRepository;
import com.hoangtien2k3.shopappbackend.repositories.UserRepository;
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
    private final JwtTokenUtil jwtTokenUtil;

    public Token createRefreshToken(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new DataNotFoundException("User not found with phone number: " + phoneNumber));

        // Kiểm tra xem người dùng đã có token chưa
        Optional<Token> existingTokenOptional = tokenRepository.findByUserId(user.getId());

        Token token;
        if (existingTokenOptional.isPresent()) {
            // Nếu đã có token, cập nhật thời gian hết hạn
            token = existingTokenOptional.get();
            token.setExpirationTime(Instant.now().plusMillis(5 * 60 * 60 * 1000));
        } else {
            // Nếu chưa có token, tạo mới
            token = Token.builder()
                    .user(user)
                    .refreshToken(UUID.randomUUID().toString())
                    .token(UUID.randomUUID().toString()) // Có thể đặt giá trị token tương ứng
                    .tokenType("Bearer")
                    .expirationTime(Instant.now().plusMillis(5 * 60 * 60 * 1000))
                    .revoked(false)
                    .expired(false)
                    .build();
        }

        // Lưu token vào cơ sở dữ liệu
        tokenRepository.save(token);
        return token;
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