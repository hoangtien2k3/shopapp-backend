package com.hoangtien2k3.shopappbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = MessageKeys.ORDER_ID_REQUIRED)
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = MessageKeys.PRODUCT_ID_REQUIRED)
    private Long productId;

    @Min(value = 0, message = MessageKeys.PRODUCT_PRICE_MIN_REQUIRED)
    private Float price;

    @JsonProperty("number_of_products")
    @Min(value = 1, message = MessageKeys.NUMBER_OF_PRODUCT_REQUIRED)
    private int numberOfProducts;

    @JsonProperty("total_money")
    @Min(value = 0, message = MessageKeys.TOTAL_MONEY_REQUIRED)
    private Float totalMoney;

    private String color;
}
