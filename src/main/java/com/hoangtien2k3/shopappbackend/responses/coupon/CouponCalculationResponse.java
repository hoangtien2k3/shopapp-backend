package com.hoangtien2k3.shopappbackend.responses.coupon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CouponCalculationResponse {
    @JsonProperty("result")
    private Double result;

    // error code
    @JsonProperty("error_message")
    private String errorMessage;
}
