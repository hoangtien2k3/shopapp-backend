package com.hoangtien2k3.shopappbackend.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    @JsonProperty("message")
    private List<String> message;

    @JsonProperty("token")
    private String token;
}
