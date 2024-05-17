package com.hoangtien2k3.shopappbackend.repositories;

import com.hoangtien2k3.shopappbackend.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // tìm đơn hàng của một user nào đó
    List<Order> findByUserId(Long userId);
}
