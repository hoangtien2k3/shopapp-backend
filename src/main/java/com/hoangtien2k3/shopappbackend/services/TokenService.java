package com.hoangtien2k3.shopappbackend.services;

import com.hoangtien2k3.shopappbackend.models.Token;
import com.hoangtien2k3.shopappbackend.models.User;

public interface TokenService {
    Token addTokenEndRefreshToken(User user, String token, boolean isMobile);
}
