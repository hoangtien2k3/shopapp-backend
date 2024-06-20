package com.hoangtien2k3.shopappbackend.repositories;

import com.hoangtien2k3.shopappbackend.models.Product;
import com.hoangtien2k3.shopappbackend.utils.ConfixSql;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    Page<Product> findAll(Pageable pageable);

    @Query(ConfixSql.Product.SEARCH_PRODUCT_BY_KEYWORD)
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 @Param("categoryId") Long categoryId,
                                 Pageable pageable);

    @Query(ConfixSql.Product.GET_DETAIL_PRODUCT)
    Optional<Product> getDetailProducts(@Param("productId") Long productId);

    @Query(ConfixSql.Product.FIND_PRODUCT_BY_IDS)
    List<Product> findProductByIds(@Param("productIds") List<Long> productIds);

}
