package com.hoangtien2k3.shopappbackend.services;

import com.hoangtien2k3.shopappbackend.dtos.OrderDTO;
import com.hoangtien2k3.shopappbackend.models.Order;

import java.util.List;

public interface OrderService {
    Order createOrder(OrderDTO orderDTO) throws Exception;

    Order getOrderById(Long id);

    Order updateOrder(Long id, OrderDTO orderDTO);

    void deleteOrder(Long id);

    List<Order> findByUserId(Long userId);
}
