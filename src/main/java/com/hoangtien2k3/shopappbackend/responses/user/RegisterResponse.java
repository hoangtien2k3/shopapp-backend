package com.hoangtien2k3.shopappbackend.responses.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoangtien2k3.shopappbackend.models.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterResponse {
    @JsonProperty("message")
    private List<String> message;

    @JsonProperty("user")
    private User user;
}
