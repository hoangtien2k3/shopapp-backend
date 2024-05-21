package com.hoangtien2k3.shopappbackend.services;

import com.hoangtien2k3.shopappbackend.dtos.RefreshTokenDTO;
import com.hoangtien2k3.shopappbackend.dtos.UpdateUserDTO;
import com.hoangtien2k3.shopappbackend.dtos.UserDTO;
import com.hoangtien2k3.shopappbackend.exceptions.payload.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.responses.user.LoginResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    User createUser(UserDTO userDTO) throws Exception;

    String login(String phoneNumber, String password) throws Exception;

    User getUserDetailsFromToken(String token) throws Exception;

    User updateUser(Long userId, UpdateUserDTO updateUserDTO) throws Exception;

    LoginResponse refreshToken(RefreshTokenDTO refreshTokenDTO);

    Page<User> findAllUsers(String keyword, Pageable pageable);

    void resetPassword(Long userId, String newPassword) throws Exception;

    void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;
}
