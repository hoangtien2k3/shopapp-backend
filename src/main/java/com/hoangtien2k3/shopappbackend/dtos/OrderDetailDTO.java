package com.hoangtien2k3.shopappbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "{order_id.required}")
    private Long orderId;

    @JsonProperty("product_id")
    @Min(value = 1, message = "{product_id.required}")
    private Long productId;

    @Min(value = 0, message = "{product_price_min.required}")
    private Float price;

    @JsonProperty("number_of_products")
    @Min(value = 1, message = "{number_of_product.required}")
    private int numberOfProducts;

    @JsonProperty("total_money")
    @Min(value = 0, message = "{total_money.required}")
    private Float totalMoney;

    private String color;
}
