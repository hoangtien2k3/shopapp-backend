package com.hoangtien2k3.shopappbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hoangtien2k3.shopappbackend.responses.product.ProductResponse;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ProductRedisService {
    // clear cache data in redis
    void clear();

    List<ProductResponse> getAllProducts(String keyword,
                                         Long categoryId,
                                         PageRequest pageRequest,
                                         String sortField,
                                         String sortDirection
    ) throws JsonProcessingException;

    void saveAllProducts(List<ProductResponse> productResponses,
                         String keyword,
                         Long categoryId,
                         PageRequest pageRequest,
                         String sortField,
                         String sortDirection
    ) throws JsonProcessingException;
}
