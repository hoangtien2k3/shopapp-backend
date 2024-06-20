package com.hoangtien2k3.shopappbackend.utils;

public class ConfixSql {

    public interface Product {
        // Tìm kiếm sản phẩm theo từ khóa và id danh mục
        String SEARCH_PRODUCT_BY_KEYWORD = "SELECT p FROM Product p WHERE " +
                "(:categoryId IS NULL OR :categoryId = 0 OR p.category.id = :categoryId) " +
                "AND (:keyword IS NULL OR :keyword = '' OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))";

        // lấy ra chi tiết sản phẩm theo id
        String GET_DETAIL_PRODUCT = "SELECT p FROM Product p LEFT JOIN FETCH p.productImages where p.id = :productId";

        // tìm kiếm sản phẩm theo danh sách id
        String FIND_PRODUCT_BY_IDS = "SELECT p FROM Product p where p.id IN :productIds";
    }

    public interface Category {
        // lấy ra tất cả danh mục sản phẩm
        String GET_ALL_CATEGORY = "SELECT c FROM Category c WHERE c.active = true";

        // lấy ra danh mục sản phẩm theo id
        String GET_CATEGORY_BY_ID = "SELECT c FROM Category c WHERE c.id = :categoryId";
    }

    public interface User {
        // lấy ra tất cả user (ngoại trừ admin) với truyền admin
        String GET_ALL_USER = "SELECT o FROM User o WHERE o.active = true AND (:keyword IS NULL OR :keyword = '' " +
                "OR o.fullName LIKE %:keyword% " +
                "OR o.address LIKE %:keyword% " +
                "OR o.phoneNumber LIKE %:keyword%) " +
                "AND LOWER(o.role.name) = 'user'";
    }

    public interface Order {
        // lẩy ra tất cả các order
        String GET_ALL_ORDER = "SELECT o FROM Order o WHERE " +
                "(:keyword IS NULL OR :keyword = '' OR o.fullName LIKE %:keyword% OR o.address LIKE %:keyword% " +
                "OR o.note LIKE %:keyword%)";
    }

}
