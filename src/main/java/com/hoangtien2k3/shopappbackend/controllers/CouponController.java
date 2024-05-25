package com.hoangtien2k3.shopappbackend.controllers;

import com.hoangtien2k3.shopappbackend.responses.ApiResponse;
import com.hoangtien2k3.shopappbackend.responses.coupon.CouponCalculationResponse;
import com.hoangtien2k3.shopappbackend.services.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     Nếu đơn hàng có tổng giá trị là 120.0$ và áp dụng coupon 1
     Giá trị sau khi áp dụng giảm giá 10%
     120.0$ * (1 - 10/100) = 120.0$ * 0.9 = 108.0$

     Giảm giá sau khi áp dụng giảm giá 5%
     108$ * (1 - 5/100) = 108$ * 0.95$   = 102.6$
     **/
    @GetMapping("/calculate")
    public ResponseEntity<ApiResponse<CouponCalculationResponse>> calculateCouponValue(
            @RequestParam("coupon_code") String couponCode,
            @RequestParam("total_amount") double totalAmount) {
        try {
            double finalAmount = couponService.calculateCouponValue(couponCode, totalAmount);
            return ResponseEntity.ok(ApiResponse.<CouponCalculationResponse>builder()
                    .success(true)
                    .payload(CouponCalculationResponse.builder()
                            .result(finalAmount)
                            .errorMessage(null)
                            .build())
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<CouponCalculationResponse>builder()
                            .error(e.getMessage())
                            .payload(CouponCalculationResponse.builder()
                                    .result(totalAmount)
                                    .errorMessage(e.getMessage())
                                    .build())
                            .build()
            );
        }
    }

}
