package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.dtos.ProductDTO;
import com.hoangtien2k3.shopappbackend.dtos.ProductImageDTO;
import com.hoangtien2k3.shopappbackend.exceptions.payload.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.exceptions.payload.InvalidParamException;
import com.hoangtien2k3.shopappbackend.mapper.ProductMapper;
import com.hoangtien2k3.shopappbackend.models.Category;
import com.hoangtien2k3.shopappbackend.models.Product;
import com.hoangtien2k3.shopappbackend.models.ProductImage;
import com.hoangtien2k3.shopappbackend.repositories.CategoryRepository;
import com.hoangtien2k3.shopappbackend.repositories.ProductImageRepository;
import com.hoangtien2k3.shopappbackend.repositories.ProductRepository;
import com.hoangtien2k3.shopappbackend.responses.product.ProductResponse;
import com.hoangtien2k3.shopappbackend.services.ProductService;
import com.hoangtien2k3.shopappbackend.utils.LocalizationUtils;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductMapper productMapper;
    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category existsCategory = categoryRepository
                .findById(productDTO.getCategoryId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                translate(MessageKeys.CATEGORY_NOT_FOUND, productDTO.getCategoryId())
                        )
                );
        Product savedProduct = productMapper.toProduct(productDTO);
        savedProduct.setCategory(existsCategory);
        return productRepository.save(savedProduct);
    }

    @Override
    public Product getProductById(Long productId) throws DataNotFoundException {
        return productRepository
                .findById(productId)
                .orElseThrow(() -> new DataNotFoundException(translate(MessageKeys.PRODUCT_NOT_FOUND, productId)));
    }

    @Override
    public Page<ProductResponse> getAllProducts(String keyword,
                                                Long categoryId,
                                                PageRequest pageRequest) {
        // lấy danh sách sản phẩm theo trang(page) và giới hạn(limit)
        Page<Product> productPage;
        productPage = productRepository.searchProducts(keyword, categoryId, pageRequest);
        return productPage.map(ProductResponse::fromProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(Long id, ProductDTO productDTO) throws DataNotFoundException {
        Product existsProduct = getProductById(id);
        if (existsProduct != null) {
            // copy các thuộc tính từ DTO -> Product
            Category existsCategory = categoryRepository
                    .findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new DataNotFoundException(
                            translate(MessageKeys.CATEGORY_NOT_FOUND, productDTO.getCategoryId())
                    ));

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
    @Transactional
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
                .orElseThrow(() -> new DataNotFoundException(
                        translate(MessageKeys.CATEGORY_NOT_FOUND, productId))
                );

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

    @Override
    public Product getDetailProducts(long productId) throws DataNotFoundException {
        Optional<Product> optionalProduct = productRepository.getDetailProducts(productId);
        if (optionalProduct.isPresent()) {
            return optionalProduct.get();
        }
        throw new DataNotFoundException(translate(MessageKeys.PRODUCT_NOT_FOUND, productId));
    }

    @Override
    public List<Product> findProductsByIds(List<Long> productIds) {
        return productRepository.findProductByIds(productIds);
    }

    private String translate(String message, Object... listMessages) {
        return localizationUtils.getLocalizedMessage(message, listMessages);
    }

}
