package com.hoangtien2k3.shopappbackend.repositories;

import com.hoangtien2k3.shopappbackend.models.Order;
import com.hoangtien2k3.shopappbackend.utils.ConfixSql;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // tìm đơn hàng của một user nào đó
    List<Order> findByUserId(Long userId);

    // lẩy ra tất cả các order
    @Query(ConfixSql.Order.GET_ALL_ORDER)
    Page<Order> findByKeyword(String keyword, Pageable pageable);
}
