package com.hoangtien2k3.shopappbackend.services;

import com.hoangtien2k3.shopappbackend.dtos.ProductDTO;
import com.hoangtien2k3.shopappbackend.dtos.ProductImageDTO;
import com.hoangtien2k3.shopappbackend.models.Product;
import com.hoangtien2k3.shopappbackend.models.ProductImage;
import com.hoangtien2k3.shopappbackend.responses.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ProductService {
    Product createProduct(ProductDTO productDTO) throws Exception;
    Product getProductById(Long id) throws Exception;
    Page<ProductResponse> getAllProducts(PageRequest pageable);
    Product updateProduct(Long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(Long id);
    boolean existsProduct(String name);

    ProductImage createProductImage(Long productId,
                                    ProductImageDTO productImageDTO);
}