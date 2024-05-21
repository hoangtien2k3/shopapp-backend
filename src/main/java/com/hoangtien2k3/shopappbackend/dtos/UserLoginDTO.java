package com.hoangtien2k3.shopappbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @JsonProperty("phone_number")
    @NotBlank(message = MessageKeys.PHONE_NUMBER_REQUIRED)
    private String phoneNumber;

    @NotBlank(message = MessageKeys.PASSWORD_REQUIRED)
    private String password;

//    private int roleId; // quyền đăng nhập (1. user, 2. admin)
}
