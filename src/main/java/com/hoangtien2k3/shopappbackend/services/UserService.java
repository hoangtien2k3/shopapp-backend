package com.hoangtien2k3.shopappbackend.services;

import com.hoangtien2k3.shopappbackend.dtos.UserDTO;
import com.hoangtien2k3.shopappbackend.models.User;

public interface UserService {
    User createUser(UserDTO userDTO) throws Exception;
    String login(String phoneNumber, String password) throws Exception;
}
