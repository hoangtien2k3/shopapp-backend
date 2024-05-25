-- bảng coupons tính phiếu giảm giá cho đơn hàng
CREATE TABLE IF NOT EXISTS coupons
(
    id     INT PRIMARY KEY AUTO_INCREMENT,
    code   VARCHAR(50) NOT NULL,
    active BOOLEAN     NOT NULL DEFAULT true
);

-- discount
CREATE TABLE IF NOT EXISTS coupon_conditions
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    coupon_id       INT           NOT NULL,
    attribute       VARCHAR(255)  NOT NULL,
    operator        VARCHAR(10)   NOT NULL,
    value           VARCHAR(255)  NOT NULL,
    discount_amount DECIMAL(5, 2) NOT NULL,
    FOREIGN KEY (coupon_id) REFERENCES coupons (id)
);

ALTER TABLE orders
    ADD COLUMN coupon_id INT,
    ADD CONSTRAINT fk_orders_coupon
        FOREIGN KEY (coupon_id) REFERENCES coupons (id);

ALTER TABLE order_details
    ADD COLUMN coupon_id INT,
    ADD CONSTRAINT fk_order_details_coupon
        FOREIGN KEY (coupon_id) REFERENCES coupons (id);