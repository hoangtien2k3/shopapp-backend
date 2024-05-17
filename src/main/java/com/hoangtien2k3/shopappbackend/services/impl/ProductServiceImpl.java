package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.dtos.ProductDTO;
import com.hoangtien2k3.shopappbackend.dtos.ProductImageDTO;
import com.hoangtien2k3.shopappbackend.exceptions.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.exceptions.InvalidParamException;
import com.hoangtien2k3.shopappbackend.mapper.ProductMapper;
import com.hoangtien2k3.shopappbackend.models.Category;
import com.hoangtien2k3.shopappbackend.models.Product;
import com.hoangtien2k3.shopappbackend.models.ProductImage;
import com.hoangtien2k3.shopappbackend.repositories.CategoryRepository;
import com.hoangtien2k3.shopappbackend.repositories.ProductImageRepository;
import com.hoangtien2k3.shopappbackend.repositories.ProductRepository;
import com.hoangtien2k3.shopappbackend.responses.product.ProductResponse;
import com.hoangtien2k3.shopappbackend.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existsCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + productDTO.getCategoryId()));

        Product savedProduct = productMapper.toProduct(productDTO);
        savedProduct.setCategory(existsCategory);
        return productRepository.save(savedProduct);
    }

    @Override
    public Product getProductById(Long productId) throws Exception {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productId));
    }

    @Override
    public Page<ProductResponse> getAllProducts(PageRequest pageable) {
        // lấy danh sách sản phẩm theo trang(page) và giới hạn(limit)
        return productRepository
                .findAll(pageable)
                .map(ProductResponse::fromProduct);
    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) throws Exception {
        Product existsProduct = getProductById(id);
        if (existsProduct != null) {
            // copy các thuộc tính từ DTO -> Product
            Category existsCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException("Category not found with id: " + productDTO.getCategoryId()));

            existsProduct.setName(productDTO.getName());
            existsProduct.setCategory(existsCategory);
            existsProduct.setPrice(productDTO.getPrice());
            existsProduct.setDescription(productDTO.getDescription());
            existsProduct.setThumbnail(productDTO.getThumbnail());
            return productRepository.save(existsProduct);
        }

        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        // tìm ra product
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsProduct(String name) {
        return productRepository.existsByName(name);
    }

    @Override
    public ProductImage createProductImage(Long productId,
                                            ProductImageDTO productImageDTO) {
        Product existsProduct = productRepository
                .findById(productId)
                .orElseThrow(() -> new DataNotFoundException("Product not found with id: " + productImageDTO.getProductId()));

        ProductImage productImage = ProductImage.builder()
                .product(existsProduct)
                .imageUrl(productImageDTO.getImageUrl())
                .build();

        // không cho insert quá 5 ảnh cho một sản phẩm
        int size = productImageRepository.findByProductId(productId).size();
        if (size >= ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
            throw new InvalidParamException("Number of images lest " +
                    ProductImage.MAXIMUM_IMAGES_PER_PRODUCT + " reached");
        }

        return productImageRepository.save(productImage);
    }

}
