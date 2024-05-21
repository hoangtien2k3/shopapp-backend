package com.hoangtien2k3.shopappbackend.repositories;

import com.hoangtien2k3.shopappbackend.models.Token;
import com.hoangtien2k3.shopappbackend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Token findByToken(String token);

    Optional<Token> findByUserId(Long userId);

    List<Token> findByUser(User user);

    Optional<Token> findByRefreshToken(String refreshToken);
}
