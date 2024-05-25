package com.hoangtien2k3.shopappbackend.services;

public interface CouponService {
    double calculateCouponValue(String couponCode, double totalAmount);
}
