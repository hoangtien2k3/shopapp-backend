package com.hoangtien2k3.shopappbackend.controllers;

import com.hoangtien2k3.shopappbackend.dtos.*;
import com.hoangtien2k3.shopappbackend.dtos.UserLoginDTO;
import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.responses.user.LoginResponse;
import com.hoangtien2k3.shopappbackend.responses.user.RegisterResponse;
import com.hoangtien2k3.shopappbackend.services.UserService;
import com.hoangtien2k3.shopappbackend.utils.LocalizationUtils;
import com.hoangtien2k3.shopappbackend.utils.MessagKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                                .message(List.of(localizationUtils.getLocalizedMessage(MessagKeys.ERROR_MESSAGE, MessagKeys.PASSWORD_NOT_MATCH)))
                                .build()
                );
            }

            User newUser = userService.createUser(userDTO);
            return ResponseEntity.ok().body(
                    RegisterResponse.builder()
                            .message(List.of(localizationUtils.getLocalizedMessage(MessagKeys.REGISTER_SUCCESS)))
                            .user(newUser)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(RegisterResponse.builder()
                    .message(
                            List.of(localizationUtils.getLocalizedMessage(MessagKeys.ERROR_MESSAGE, e.getMessage()))
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
                            .message(List.of(localizationUtils.getLocalizedMessage(MessagKeys.LOGIN_SUCCESS)))
                            .token(token)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.builder()
                            .message(List.of(localizationUtils.getLocalizedMessage(MessagKeys.LOGIN_FAILED, e.getMessage())))
                            .token(null)
                            .build()
            );
        }
    }

}
