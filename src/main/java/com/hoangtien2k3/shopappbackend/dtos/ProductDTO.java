package com.hoangtien2k3.shopappbackend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    @NotBlank(message = "{product_title_required}")
    @Size(min = 3, max = 200, message = "{product_title_size_required}")
    private String name;

    @Min(value = 0, message = "{product_price_min_required}")
    @Max(value = 100000000, message = "{product_price_max_required}")
    private Float price;

    private String thumbnail;
    private String description;

    @JsonProperty("category_id")
    private Long categoryId;
}
