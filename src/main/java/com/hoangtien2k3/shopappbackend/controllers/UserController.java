package com.hoangtien2k3.shopappbackend.controllers;

import com.hoangtien2k3.shopappbackend.dtos.*;
import com.hoangtien2k3.shopappbackend.dtos.UserLoginDTO;
import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.responses.user.LoginResponse;
import com.hoangtien2k3.shopappbackend.responses.user.RegisterResponse;
import com.hoangtien2k3.shopappbackend.responses.user.UserResponse;
import com.hoangtien2k3.shopappbackend.services.UserService;
import com.hoangtien2k3.shopappbackend.utils.LocalizationUtils;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final LocalizationUtils localizationUtils;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> createUser(@RequestBody @Valid UserDTO userDTO,
                                                       BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(
                        RegisterResponse.builder().message(errorMessages).build()
                );
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(RegisterResponse.builder()
                                .message(List.of(localizationUtils.getLocalizedMessage(MessageKeys.ERROR_MESSAGE, MessageKeys.PASSWORD_NOT_MATCH)))
                                .build()
                );
            }

            User newUser = userService.createUser(userDTO);
            return ResponseEntity.ok().body(
                    RegisterResponse.builder()
                            .message(List.of(localizationUtils.getLocalizedMessage(MessageKeys.REGISTER_SUCCESS)))
                            .user(newUser)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RegisterResponse.builder()
                    .message(
                            List.of(localizationUtils.getLocalizedMessage(MessageKeys.ERROR_MESSAGE, e.getMessage()))
                    )
                    .build()
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> userLoginDTO(
            @RequestBody @Valid UserLoginDTO userLoginDTO,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(
                        LoginResponse.builder().message(errorMessages).build()
                );
            }

            String token = userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword());
            return ResponseEntity.ok().body(
                    LoginResponse.builder()
                            .message(List.of(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_SUCCESS)))
                            .token(token)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(List.of(localizationUtils.getLocalizedMessage(MessageKeys.LOGIN_FAILED, e.getMessage())))
                            .token(null)
                            .build()
            );
        }
    }

    // Lấy ra thông tin chi tiết của người dùng thông qua token truyền vào
    @PostMapping("/details")
    public ResponseEntity<?> getUserDetails(
            @RequestHeader("Authorization") String token
    ) {
        try {
            String extractedToken = token.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(UserResponse.fromUser(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/details/{userId}")
    public ResponseEntity<?> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody @Valid UserDTO userDTO,
            @RequestHeader("Authorization") String token
    ) {
        try {
            String extractedToken = token.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            if (!user.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updateUser = userService.updateUser(userId, userDTO);
            return ResponseEntity.ok(UserResponse.fromUser(updateUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
