package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.components.JwtTokenUtil;
import com.hoangtien2k3.shopappbackend.dtos.UserDTO;
import com.hoangtien2k3.shopappbackend.exceptions.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.exceptions.PermissionDenyException;
import com.hoangtien2k3.shopappbackend.mapper.UserMapper;
import com.hoangtien2k3.shopappbackend.models.Role;
import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.repositories.RoleRepository;
import com.hoangtien2k3.shopappbackend.repositories.UserRepository;
import com.hoangtien2k3.shopappbackend.services.UserService;
import com.hoangtien2k3.shopappbackend.utils.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public User createUser(UserDTO userDTO) throws Exception {
        // register new user
        String phoneNumber = userDTO.getPhoneNumber();
        // kiểm tra xem số điện thoại đã tồn tại hay chưa
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number already exists");
        }
        Role role = roleRepository.findById(userDTO.getRoleId()).
                orElseThrow(() -> new DataNotFoundException("Role not found"));
        if (role.getName().toUpperCase().equals(RoleType.ADMIN)) {
            throw new PermissionDenyException("You can't register an account with role: " + role.getName().toUpperCase());
        }

        // convert userDTO -> user
        User newUser = userMapper.toUser(userDTO);
        newUser.setRole(role);

        // kiểm tra xem nếu có accontId, không yêu cầu passowrd
        if (userDTO.getFacebookAccountId() == 0 && userDTO.getGoogleAccountId() == 0) {
            String password = userDTO.getPassword();
            String encodedPassword = passwordEncoder.encode(password);
            newUser.setPassword(encodedPassword);
        }

        return userRepository.save(newUser);
    }

    @Override
    public String login(String phoneNumber, String password) throws Exception {
        // exists by user
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException("Wrong phone number or password");
        }

        // get user
        User user = optionalUser.get();
        // check password
        if (user.getFacebookAccountId() == 0 && user.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Wrong phone number or password");
            }
        }

        // authenticated with java spring security
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getPhoneNumber(),
                        password,
                        user.getAuthorities()
                )
        );

        return jwtTokenUtil.generateToken(user);
    }
}
