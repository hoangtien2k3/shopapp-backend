package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.components.JwtTokenUtil;
import com.hoangtien2k3.shopappbackend.components.TranslateMessages;
import com.hoangtien2k3.shopappbackend.dtos.RefreshTokenDTO;
import com.hoangtien2k3.shopappbackend.dtos.UpdateUserDTO;
import com.hoangtien2k3.shopappbackend.dtos.UserDTO;
import com.hoangtien2k3.shopappbackend.exceptions.payload.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.exceptions.payload.PermissionDenyException;
import com.hoangtien2k3.shopappbackend.mapper.UserMapper;
import com.hoangtien2k3.shopappbackend.models.Role;
import com.hoangtien2k3.shopappbackend.models.Token;
import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.repositories.RoleRepository;
import com.hoangtien2k3.shopappbackend.repositories.UserRepository;
import com.hoangtien2k3.shopappbackend.responses.user.LoginResponse;
import com.hoangtien2k3.shopappbackend.services.UserService;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import com.hoangtien2k3.shopappbackend.utils.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends TranslateMessages implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenService refreshTokenService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public User createUser(UserDTO userDTO)
            throws DataIntegrityViolationException, PermissionDenyException {
        // register new user
        String phoneNumber = userDTO.getPhoneNumber();
        // kiểm tra xem số điện thoại đã tồn tại hay chưa
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException(
                    translate(MessageKeys.PHONE_NUMBER_EXISTED));
        }
        Role role = roleRepository.findById(userDTO.getRoleId()).
                orElseThrow(() -> new DataNotFoundException("Role not found"));
        if (role.getName().equalsIgnoreCase(RoleType.ADMIN)) {
            throw new PermissionDenyException(
                    translate(MessageKeys.CAN_NOT_CREATE_ACCOUNT_ROLE_ADMIN));
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
    public LoginResponse login(String phoneNumber, String password) throws DataNotFoundException {
        // exists by user
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);
        if (optionalUser.isEmpty()) {
            throw new DataNotFoundException(
                    translate(MessageKeys.PHONE_NUMBER_AND_PASSWORD_FAILED)
            );
        }
        User user = optionalUser.get();
        // check password
        if (user.getFacebookAccountId() == 0 && user.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException(
                        translate(MessageKeys.PHONE_NUMBER_AND_PASSWORD_FAILED)
                );
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

        // generate token and refreshToken
        var token = jwtTokenUtil.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(token, user.getPhoneNumber());

        return LoginResponse.builder()
                .token(token)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }


    // dùng refresh_token để tạo lại token mới
    @Override
    public LoginResponse refreshToken(RefreshTokenDTO refreshTokenDTO) {
        Token token = refreshTokenService.verifyRefreshToken(refreshTokenDTO.getRefreshToken());

        //check refreshToken isExpired
        if (token.getExpirationTime().isBefore(Instant.now())) {
            throw new PermissionDenyException(translate(MessageKeys.APP_PERMISSION_DENY_EXCEPTION));
        }

        // generate new token by refreshToken
        return LoginResponse.builder()
                .token(jwtTokenUtil.generateToken(token.getUser()))
                .refreshToken(token.getRefreshToken())
                .build();
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpirated(token)) {
            throw new Exception(translate(MessageKeys.TOKEN_EXPIRATION_TIME));
        }

        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new DataNotFoundException(translate(MessageKeys.USER_NOT_FOUND));
        }
    }

    @Override
    @Transactional
    public User updateUser(Long userId, UpdateUserDTO updateUserDTO)
            throws DataIntegrityViolationException, DataNotFoundException {
        // find the exists user by userid
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(translate(MessageKeys.USER_NOT_FOUND)));

        String phoneNumber = updateUserDTO.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(phoneNumber)
                && userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException(translate(MessageKeys.PASSWORD_NOT_MATCH));
        }

        if (updateUserDTO.getFullName() != null) {
            existingUser.setFullName(updateUserDTO.getFullName());
        }
        if (updateUserDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updateUserDTO.getPhoneNumber());
        }
        if (updateUserDTO.getAddress() != null) {
            existingUser.setAddress(updateUserDTO.getAddress());
        }
        if (updateUserDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
        }
        if (updateUserDTO.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(updateUserDTO.getFacebookAccountId());
        }
        if (updateUserDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(updateUserDTO.getGoogleAccountId());
        }

        // update the password
        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
            String newPassword = passwordEncoder.encode(updateUserDTO.getPassword());
            existingUser.setPassword(newPassword);
        }

        return userRepository.save(existingUser);
    }
}
