package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.dtos.OrderDetailDTO;
import com.hoangtien2k3.shopappbackend.exceptions.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.models.Order;
import com.hoangtien2k3.shopappbackend.models.OrderDetail;
import com.hoangtien2k3.shopappbackend.models.Product;
import com.hoangtien2k3.shopappbackend.repositories.OrderDetailRepository;
import com.hoangtien2k3.shopappbackend.repositories.OrderRepository;
import com.hoangtien2k3.shopappbackend.repositories.ProductRepository;
import com.hoangtien2k3.shopappbackend.services.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) throws DataNotFoundException {
        // tìm xem orderId có tồn tại hay không
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Can't found order with id " + orderDetailDTO.getOrderId()));
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Can't found product with id " + orderDetailDTO.getProductId()));

        OrderDetail orderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .price(orderDetailDTO.getPrice())
                .color(orderDetailDTO.getColor())
                .build();

        // lưu vào DB
        return orderDetailRepository.save(orderDetail);
    }

    @Override
    public OrderDetail getOrderDetail(Long id) throws DataNotFoundException {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Can't found order with id " + id));
    }

    @Override
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) {
        // tìm xem orderDetail có tồn tại hay không
        OrderDetail existsOrderDetail = getOrderDetail(id);
        Order existsOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException("Can't found order with id " + orderDetailDTO.getOrderId()));
        Product existsProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException("Can't found order with id " + orderDetailDTO.getProductId()));

        existsOrderDetail.setProduct(existsProduct);
        existsOrderDetail.setNumberOfProducts(orderDetailDTO.getNumberOfProducts());
        existsOrderDetail.setTotalMoney(orderDetailDTO.getTotalMoney());
        existsOrderDetail.setPrice(orderDetailDTO.getPrice());
        existsOrderDetail.setColor(orderDetailDTO.getColor());
        existsOrderDetail.setId(id);
        existsOrderDetail.setOrder(existsOrder);
        return orderDetailRepository.save(existsOrderDetail);
    }

    @Override
    public void deleteOrderDetail(Long id) {
        orderDetailRepository.deleteById(id);
    }

    @Override
    public List<OrderDetail> findByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }
}
