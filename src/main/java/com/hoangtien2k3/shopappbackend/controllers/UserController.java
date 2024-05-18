package com.hoangtien2k3.shopappbackend.controllers;

import com.hoangtien2k3.shopappbackend.dtos.*;
import com.hoangtien2k3.shopappbackend.dtos.UserLoginDTO;
import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.responses.ApiResponse;
import com.hoangtien2k3.shopappbackend.responses.user.LoginResponse;
import com.hoangtien2k3.shopappbackend.responses.user.UserResponse;
import com.hoangtien2k3.shopappbackend.services.UserService;
import com.hoangtien2k3.shopappbackend.utils.LocalizationUtils;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @Transactional
    public ResponseEntity<ApiResponse<User>> createUser(@RequestBody @Valid UserDTO userDTO,
                                                        BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(
                        ApiResponse.<User>builder().success(false)
                                .errors(errorMessages.stream()
                                        .map(this::translate)
                                        .toList()).build()
                );
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(ApiResponse.<User>builder()
                        .success(false)
                        .error(translate(MessageKeys.PASSWORD_NOT_MATCH)).build()
                );
            }

            User newUser = userService.createUser(userDTO);
            return ResponseEntity.ok().body(
                    ApiResponse.<User>builder().success(true)
                            .message(translate(MessageKeys.REGISTER_SUCCESS))
                            .payload(newUser)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<User>builder().success(false)
                    .message(translate(MessageKeys.ERROR_MESSAGE, e.getMessage())).build()
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> userLoginDTO(
            @RequestBody @Valid UserLoginDTO userLoginDTO,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(
                        ApiResponse.<LoginResponse>builder()
                                .success(false)
                                .message(translate(MessageKeys.LOGIN_FAILED))
                                .errors(errorMessages.stream()
                                        .map(this::translate)
                                        .toList()).build()
                );
            }

            LoginResponse loginResponse = userService.login(userLoginDTO.getPhoneNumber(), userLoginDTO.getPassword());
            ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                    .success(true)
                    .message(translate(MessageKeys.LOGIN_SUCCESS))
                    .payload(loginResponse)
                    .build();
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<LoginResponse>builder().success(false)
                            .error(translate(MessageKeys.LOGIN_FAILED)).build()
            );
        }
    }

    // refreshToken
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        try {
            ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                    .success(true)
                    .payload(userService.refreshToken(refreshTokenDTO))
                    .build();
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                    .success(false)
                    .error(translate(MessageKeys.ERROR_REFRESH_TOKEN)).build();
            return ResponseEntity.ok().body(apiResponse);
        }
    }

    // Lấy ra thông tin chi tiết của người dùng thông qua token truyền vào
    @PostMapping("/details")
    public ResponseEntity<ApiResponse<?>> getUserDetails(
            @RequestHeader("Authorization") String token
    ) {
        try {
            String extractedToken = token.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            return ResponseEntity.ok(ApiResponse.<UserResponse>builder().success(true)
                    .payload(UserResponse.fromUser(user)).build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<UserResponse>builder().success(false)
                            .message(translate(MessageKeys.MESSAGE_ERROR_GET)).build()
            );
        }
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @Transactional
    @PutMapping("/details/{userId}")
    public ResponseEntity<ApiResponse<?>> updateUserDetails(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateUserDTO updateUserDTO,
            @RequestHeader("Authorization") String token
    ) {
        try {
            String extractedToken = token.substring(7);
            User user = userService.getUserDetailsFromToken(extractedToken);
            if (!user.getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            User updateUser = userService.updateUser(userId, updateUserDTO);
            return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                    .success(true)
                    .message(translate(MessageKeys.MESSAGE_UPDATE_GET))
                    .payload(UserResponse.fromUser(updateUser)).build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // translate message
    private String translate(String message) {
        return localizationUtils.getLocalizedMessage(message);
    }

    private String translate(String message, Object... listMessage) {
        return localizationUtils.getLocalizedMessage(message, listMessage);
    }

}
