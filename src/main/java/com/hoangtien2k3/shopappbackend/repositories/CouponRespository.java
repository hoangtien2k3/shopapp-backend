package com.hoangtien2k3.shopappbackend.repositories;

import com.hoangtien2k3.shopappbackend.models.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CouponRespository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String couponCode);
}
