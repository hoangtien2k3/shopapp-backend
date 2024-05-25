package com.hoangtien2k3.shopappbackend.services.impl;

import com.hoangtien2k3.shopappbackend.models.Coupon;
import com.hoangtien2k3.shopappbackend.models.CouponCondition;
import com.hoangtien2k3.shopappbackend.repositories.CouponConditionRepository;
import com.hoangtien2k3.shopappbackend.repositories.CouponRespository;
import com.hoangtien2k3.shopappbackend.services.CouponService;
import com.hoangtien2k3.shopappbackend.utils.Const;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRespository couponRespository;
    private final CouponConditionRepository couponConditionRepository;

    @Override
    public double calculateCouponValue(String couponCode, double totalAmount) {
        Coupon coupon = couponRespository.findByCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("Coupon not found"));
        if (!coupon.isActive()) {
            throw new IllegalArgumentException("Coupon is not active");
        }
        double discount = calculateDiscount(coupon, totalAmount);
        return totalAmount - discount;
    }

    // tính toán coupon
    private double calculateDiscount(Coupon coupon, double totalAmount) {
        List<CouponCondition> conditions = couponConditionRepository.findByCouponId(coupon.getId());
        double discount = 0.0;
        double updateTotalAmount = totalAmount;
        for (CouponCondition condition : conditions) {
            // EAV (Entity - Attribute - Value) - Mô Hình
            String attribute = condition.getAttribute();
            String operator = condition.getOperator();
            String value = condition.getValue();
            double percenDiscount = Double.parseDouble(
                    String.valueOf(condition.getDiscountAmount()));

            if (attribute.equals(Const.MINIMUN_AMOUNT)) {
                if (operator.equals(">") && updateTotalAmount > Double.parseDouble(value)) {
                    discount += updateTotalAmount * percenDiscount / 100;
                }
            } else if (attribute.equals(Const.APPLICATION_DATE)) {
                LocalDate applicableDate = LocalDate.parse(value);
                LocalDate currentDate = LocalDate.now();
                if (operator.equalsIgnoreCase(Const.BETWEEN) && currentDate.isBefore(applicableDate)) {
                    discount += updateTotalAmount * percenDiscount / 100;
                }
            }

            // thêm nhiều điều kiện khác vào
            updateTotalAmount -= discount;
        }

        return discount;
    }

}
