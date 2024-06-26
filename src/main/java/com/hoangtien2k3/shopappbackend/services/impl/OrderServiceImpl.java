package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.components.TranslateMessages;
import com.hoangtien2k3.shopappbackend.dtos.CartItemDTO;
import com.hoangtien2k3.shopappbackend.dtos.OrderDTO;
import com.hoangtien2k3.shopappbackend.exceptions.payload.DataNotFoundException;
import com.hoangtien2k3.shopappbackend.models.*;
import com.hoangtien2k3.shopappbackend.repositories.OrderDetailRepository;
import com.hoangtien2k3.shopappbackend.repositories.OrderRepository;
import com.hoangtien2k3.shopappbackend.repositories.ProductRepository;
import com.hoangtien2k3.shopappbackend.repositories.UserRepository;
import com.hoangtien2k3.shopappbackend.responses.order.OrderResponse;
import com.hoangtien2k3.shopappbackend.services.OrderService;
import com.hoangtien2k3.shopappbackend.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends TranslateMessages
        implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws DataNotFoundException {
        // kiểm tra xem userId có tồn tại hay không
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(translate(MessageKeys.NOT_FOUND, orderDTO.getUserId())));

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
            throw new DataNotFoundException(translate(MessageKeys.TOKEN_EXPIRATION_TIME));
        }
        order.setShippingDate(shippingDate); // set thời điểm giao hàng
        order.setActive(true); // trạng thái đơn hàng đã được active
        order.setTotalMoney(orderDTO.getTotalMoney());
        orderRepository.save(order); // lưu vào database

        // tạo danh sách các đối tượng orderDetails
        List<OrderDetail> orderDetails = new ArrayList<>();
        for(CartItemDTO cartItemDTO : orderDTO.getCartItems()) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);

            // lấy thông tin sản phẩm từ cartItemDTO
            Long productId = cartItemDTO.getProductId();
            int quantity = cartItemDTO.getQuantity();

            // tìm thông tin sản phẩm từ cơ sở dữ liệu
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new DataNotFoundException(
                            translate(MessageKeys.PRODUCT_NOT_FOUND, productId))
                    );

            // Đặt thông tin cho orderDetails
            orderDetail.setProduct(product);
            orderDetail.setNumberOfProducts(quantity);
            orderDetail.setPrice(product.getPrice());

            // thêm orderDetails vào danh sách
            orderDetails.add(orderDetail);
        }

        // Lưu danh sách OrderDetails vào cơ sở dữ liệu
        orderDetailRepository.saveAll(orderDetails);
        return order;
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Order updateOrder(Long id, OrderDTO orderDTO) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(translate(MessageKeys.NOT_FOUND, id)));
        User existsUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(translate(MessageKeys.NOT_FOUND, orderDTO.getUserId())));

        // tạo một luồng ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // cập nhật các trường của đơn hàng từ orderDTO
        modelMapper.map(orderDTO, order);
        order.setUser(existsUser);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
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

    @Override
    public Page<OrderResponse> findByKeyword(String keyword, Pageable pageable) {
        // lấy danh sách sản phẩm theo trang(page) và giới hạn(limit)
        Page<Order> orderPage;
        orderPage = orderRepository.findByKeyword(keyword, pageable);
        return orderPage.map(OrderResponse::fromOrder);
    }

}
