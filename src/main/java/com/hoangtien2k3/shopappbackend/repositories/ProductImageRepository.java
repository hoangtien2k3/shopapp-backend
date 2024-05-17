package com.hoangtien2k3.shopappbackend.repositories;

import com.hoangtien2k3.shopappbackend.models.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);
}
