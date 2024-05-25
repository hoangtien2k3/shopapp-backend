package com.hoangtien2k3.shopappbackend.controllers;

import com.hoangtien2k3.shopappbackend.components.TranslateMessages;
import com.hoangtien2k3.shopappbackend.dtos.*;
import com.hoangtien2k3.shopappbackend.dtos.UserLoginDTO;
import com.hoangtien2k3.shopappbackend.mapper.UserMapper;
import com.hoangtien2k3.shopappbackend.models.Token;
import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.responses.ApiResponse;
import com.hoangtien2k3.shopappbackend.responses.user.LoginResponse;
import com.hoangtien2k3.shopappbackend.responses.user.UserPageResponse;
import com.hoangtien2k3.shopappbackend.responses.user.UserRegisterResponse;
import com.hoangtien2k3.shopappbackend.responses.user.UserResponse;
import com.hoangtien2k3.shopappbackend.services.UserService;
import com.hoangtien2k3.shopappbackend.services.impl.TokenServiceImpl;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController extends TranslateMessages {

    private final UserService userService;
    private final TokenServiceImpl tokenService;

    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Created"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<ApiResponse<?>> createUser(@RequestBody @Valid UserDTO userDTO,
                                                     BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(
                        ApiResponse.builder()
                                .message(translate(MessageKeys.ERROR_MESSAGE))
                                .errors(errorMessages.stream()
                                        .map(this::translate)
                                        .toList()).build()
                );
            }
            if (!userDTO.getPassword().equals(userDTO.getRetypePassword())) {
                return ResponseEntity.badRequest().body(ApiResponse.builder()
                        .message(translate(MessageKeys.ERROR_MESSAGE))
                        .error(translate(MessageKeys.PASSWORD_NOT_MATCH)).build()
                );
            }

            User newUser = userService.createUser(userDTO);
            return ResponseEntity.ok().body(
                    ApiResponse.builder().success(true)
                            .message(translate(MessageKeys.REGISTER_SUCCESS))
                            .payload(UserRegisterResponse.fromUser(newUser)).build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .error(e.getMessage())
                    .message(translate(MessageKeys.ERROR_MESSAGE)).error(e.getMessage()).build()
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> userLoginDTO(
            @Valid @RequestBody UserLoginDTO userLoginDTO,
            HttpServletRequest request,
            BindingResult bindingResult
    ) {
        try {
            if (bindingResult.hasErrors()) {
                List<String> errorMessages = bindingResult.getFieldErrors().stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
                return ResponseEntity.badRequest().body(
                        ApiResponse.<LoginResponse>builder()
                                .message(translate(MessageKeys.LOGIN_FAILED))
                                .errors(errorMessages.stream()
                                        .map(this::translate)
                                        .toList()).build()
                );
            }

            String tokenGenerator = userService.login(
                    userLoginDTO.getPhoneNumber(),
                    userLoginDTO.getPassword()
            );
            // check is mobile or web login
            String userAgent = request.getHeader("User-Agent");
            User user = userService.getUserDetailsFromToken(tokenGenerator);
            Token token = tokenService.addTokenEndRefreshToken(user, tokenGenerator, isMoblieDevice(userAgent));

            ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                    .success(true)
                    .message(translate(MessageKeys.LOGIN_SUCCESS))
                    .payload(LoginResponse.builder()
                            .token(token.getToken())
                            .refreshToken(token.getRefreshToken())
                            .build())
                    .build();
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<LoginResponse>builder()
                            .message(translate(MessageKeys.LOGIN_FAILED))
                            .error(e.getMessage()).build()
            );
        }
    }

    // refreshToken
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        try {
            ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                    .success(true)
                    .message(translate(MessageKeys.REFRESH_TOKEN_SUCCESS))
                    .payload(userService.refreshToken(refreshTokenDTO))
                    .build();
            return ResponseEntity.ok().body(apiResponse);
        } catch (Exception e) {
            ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                    .message(translate(MessageKeys.ERROR_REFRESH_TOKEN))
                    .error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(apiResponse);
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
                    ApiResponse.<UserResponse>builder()
                            .message(translate(MessageKeys.MESSAGE_ERROR_GET)).error(e.getMessage()).build()
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
            return ResponseEntity.badRequest().body(
                    ApiResponse.<UserResponse>builder()
                            .message(translate(MessageKeys.MESSAGE_ERROR_GET)).error(e.getMessage()).build()
            );
        }
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(defaultValue = "", name = "keyword", required = false) String keyword,
            @RequestParam(defaultValue = "0", name = "page") int page,
            @RequestParam(defaultValue = "10", name = "limit") int limit
    ) {
        try {
            PageRequest pageRequest = PageRequest.of(page, limit,
                    Sort.by("id").ascending());
            Page<UserResponse> usersPage = userService.findAllUsers(keyword, pageRequest)
                    .map(UserResponse::fromUser);
            List<UserResponse> userResponses = usersPage.getContent();

            return ResponseEntity.ok(UserPageResponse.builder()
                    .users(userResponses)
                    .pageNumber(page)
                    .totalElements(usersPage.getTotalElements())
                    .pageSize(usersPage.getSize())
                    .isLast(usersPage.isLast())
                    .totalPages(usersPage.getTotalPages())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .error(e.getMessage()).build());
        }
    }

    // reset password với quyen ADMIN
    @PutMapping("/reset-password/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> resetPassword(@Valid @PathVariable("userId") long id) {
        try {
            String newPasword = UUID.randomUUID().toString().substring(0, 8);
            userService.resetPassword(id, newPasword);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message(translate(MessageKeys.RESET_PASSWORD_SUCCESS))
                    .payload("New Password: " + newPasword)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.builder()
                    .message(translate(MessageKeys.RESET_PASSWORD_FAILED))
                    .error(e.getMessage()).build());
        }
    }

    @PutMapping("/block/{userId}/{active}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<?>> blockOrEnable(
            @Valid @PathVariable("userId") long id,
            @Valid @PathVariable("active") int active
    ) {
        try {
            userService.blockOrEnable(id, active > 0);
            if (active > 0) {
                return ResponseEntity.ok(ApiResponse.builder()
                        .success(true)
                        .message(translate(MessageKeys.USER_ID_UNLOCKED))
                        .build());
            }
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message(translate(MessageKeys.USER_ID_LOCKED))
                    .build());
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.builder()
                    .error(e.getMessage()).build());
        }
    }

    //kiểm tra xem thiết bị đang đăng nhập có phải mobile không
    private boolean isMoblieDevice(String userAgent) {
        return userAgent.toLowerCase().contains("mobile");
    }
}
