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
import com.hoangtien2k3.shopappbackend.utils.LocalizationUtils;
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
    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public User createUser(UserDTO userDTO) throws Exception {
        // register new user
        String phoneNumber = userDTO.getPhoneNumber();
        // kiểm tra xem số điện thoại đã tồn tại hay chưa
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DataIntegrityViolationException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PHONE_NUMBER_EXISTED));
        }
        Role role = roleRepository.findById(userDTO.getRoleId()).
                orElseThrow(() -> new DataNotFoundException("Role not found"));
        if (role.getName().equalsIgnoreCase(RoleType.ADMIN)) {
            throw new PermissionDenyException(
                    localizationUtils.getLocalizedMessage(MessageKeys.CAN_NOT_CREATE_ACCOUNT_ROLE_ADMIN));
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
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.PHONE_NUMBER_AND_PASSWORD_FAILED)
            );
        }
        User user = optionalUser.get();
        // check password
        if (user.getFacebookAccountId() == 0 && user.getGoogleAccountId() == 0) {
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PHONE_NUMBER_AND_PASSWORD_FAILED)
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
        return jwtTokenUtil.generateToken(user);
    }

    @Override
    public User getUserDetailsFromToken(String token) throws Exception {
        if (jwtTokenUtil.isTokenExpirated(token)) {
            throw new Exception("Token is expired");
        }

        String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
        Optional<User> optionalUser = userRepository.findByPhoneNumber(phoneNumber);

        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new DataNotFoundException("User not found");
        }
    }


    @Override
    @Transactional
    public User updateUser(Long userId, UserDTO userDTO) throws Exception {
        // find the exists user by userid
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        String phoneNumber = userDTO.getPhoneNumber();
        if (!existingUser.getPhoneNumber().equals(phoneNumber)) {
            throw new DataIntegrityViolationException("Phone number does not match");
        }

        Role updateRole = roleRepository.findById(userDTO.getRoleId())
                .orElseThrow(() -> new DataNotFoundException("Role not found"));

        if (updateRole.getName().equalsIgnoreCase(RoleType.ADMIN)) {
            throw new PermissionDenyException("Admin role cannot be updated");
        }

        // Update user info


        if (userDTO.getFullName() != null) {
            existingUser.setFullName(userDTO.getFullName());
        }
        if (userDTO.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        }
        if (userDTO.getAddress() != null) {
            existingUser.setAddress(userDTO.getAddress());
        }
        if (userDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userDTO.getDateOfBirth());
        }
        if (userDTO.getFacebookAccountId() > 0) {
            existingUser.setFacebookAccountId(userDTO.getFacebookAccountId());
        }
        if (userDTO.getGoogleAccountId() > 0) {
            existingUser.setGoogleAccountId(userDTO.getGoogleAccountId());
        }
//        existingUser.setRole(updateRole); // user khoong update được role

        // update the password
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            String newPassword = passwordEncoder.encode(userDTO.getPassword());
            existingUser.setPassword(newPassword);
        }

        return userRepository.save(existingUser);
    }
}
