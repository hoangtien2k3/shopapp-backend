package com.hoangtien2k3.shopappbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImageDTO {
    @JsonProperty("product_id")
    @Min(value = 1, message = "{product_id_required}")
    private Long productId;

    @Size(min = 5, max = 300, message = "{image_size_required}")
    @JsonProperty("image_url")
    private String imageUrl;
}
