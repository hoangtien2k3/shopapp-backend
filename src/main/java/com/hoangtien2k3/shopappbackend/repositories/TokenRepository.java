package com.hoangtien2k3.shopappbackend.repositories;

import com.hoangtien2k3.shopappbackend.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}
