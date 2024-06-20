//package com.hoangtien2k3.shopappbackend.repositories;
//
//import com.hoangtien2k3.shopappbackend.models.Product;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//
//public interface ProductSearchRepository extends ElasticsearchRepository<Product, Long> {
//    Page<Product> findByNameContainingOrDescriptionContaining(String name, String description, Pageable pageable);
//}
