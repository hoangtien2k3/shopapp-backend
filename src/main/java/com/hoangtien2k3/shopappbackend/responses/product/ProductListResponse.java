package com.hoangtien2k3.shopappbackend.responses.product;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductListResponse {
    List<ProductResponse> products;
    int totalPages;
}
