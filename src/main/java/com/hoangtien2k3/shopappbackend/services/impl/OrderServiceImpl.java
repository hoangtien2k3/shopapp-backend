package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.dtos.OrderDTO;
import com.hoangtien2k3.shopappbackend.exceptions.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.models.Order;
import com.hoangtien2k3.shopappbackend.models.OrderStatus;
import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.repositories.OrderRepository;
import com.hoangtien2k3.shopappbackend.repositories.UserRepository;
import com.hoangtien2k3.shopappbackend.services.OrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public Order createOrder(OrderDTO orderDTO) throws DataNotFoundException {
        // kiểm tra xem userId có tồn tại hay không
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("User not found with id: " + orderDTO.getUserId()));

        // convert orderDTO -> order
        // using modelMapper
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));

        // cập nhật các trường của đơn hàng từ orderDTO
        Order order = new Order();
        modelMapper.map(orderDTO, order);
        order.setUser(user);
        order.setOrderDate(new Date()); // lấy thời điểm hiện tại
        order.setStatus(OrderStatus.PENDING);

        // Kiểm tra shipping date >= ngày hôm nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now()
                : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at lest today !");
        }

        order.setShippingDate(shippingDate); // set thời điểm giao hàng
        order.setActive(true); // trạng thái đơn hàng đã được active

        // lưu vào database
        orderRepository.save(order);
        return order;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Order is not found with id: " + id));
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Can't find order with id: " + id));
        User existsUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("Can't find order with id: " + id));

        // tạo một luồng ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // cập nhật các trường của đơn hàng từ orderDTO
        modelMapper.map(orderDTO, order);
        order.setUser(existsUser);
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(Long id) {
        // xoá mềm, không xoá cứng bản ghi trong DB
        // no hard-delete, please soft-delete
        Order order = orderRepository.findById(id).orElse(null);
        if (order != null) {
            order.setActive(false);
            orderRepository.save(order);
        }
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
