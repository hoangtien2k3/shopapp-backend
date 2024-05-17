create DATABASE shopapp;
USE shopapp;

-- QUẢN LÝ TÀI KHOẢN NGƯỜI DÙNG
CREATE TABLE users
(
    id                  INT PRIMARY KEY AUTO_INCREMENT,
    fullname            VARCHAR(100)          DEFAULT '',
    phone_number        VARCHAR(10)  NOT NULL,
    address             VARCHAR(200)          DEFAULT '',
    password            VARCHAR(100) NOT NULL DEFAULT '',
    created_at          DATETIME,
    updated_at          DATETIME,
    is_active           TINYINT(1)            DEFAULT 1,
    date_of_birth       DATE,
    facebook_account_id INT                   DEFAULT 0,
    google_account_id   INT                   DEFAULT 0
);

ALTER TABLE users
    ADD COLUMN role_id INT;

-- QUYỀN CỦA NGƯỜI DÙNG
CREATE TABLE roles
(
    id   INT PRIMARY KEY,
    name VARCHAR(20) NOT NULL
);

ALTER TABLE users
    ADD FOREIGN KEY (role_id) REFERENCES roles (id);

-- LƯU TRỮ TOKEN (JWT TOKEN)
CREATE TABLE tokens
(
    id              INT PRIMARY KEY AUTO_INCREMENT,
    token           VARCHAR(255) UNIQUE NOT NULL,
    token_type      VARCHAR(50)         NOT NULL,
    expiration_date DATETIME,
    revoked         TINYINT(1)          NOT NULL,
    expired         TINYINT(1)          NOT NULL,
    user_id         INT,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- HỖ TRỢ ĐĂNG NHẬP FACEBOOK VÀ GOOGLE
CREATE TABLE social_accounts
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    provider    VARCHAR(20)  NOT NULL comment 'Tên nhà social network',
    provider_id VARCHAR(50)  NOT NULL,
    email       VARCHAR(150) NOT NULL comment 'Email tài khoản',
    name        VARCHAR(100) NOT NULL comment 'Tên người dùng',
    user_id     INT,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- DANH MỤC SẢN PHẨM (CATEGORIES)
CREATE TABLE categories
(
    id   INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL DEFAULT '' comment 'Tên danh mục, vd: đồ điện tử'
);

-- BẢNG CHỨA SẢN PHẨM (PRODUCT)
CREATE TABLE products
(
    id          INT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(350) comment 'Tên sản phẩm',
    price       FLOAT NOT NULL CHECK (price >= 0),
    thumbnail   VARCHAR(300) DEFAULT '',
    description LONGTEXT,
    created_at  DATETIME,
    updated_at  DATETIME,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories (id)
);

-- BẢNG CHỨC DANH SÁCH CÁC ẢNH CỦA PRODUCT
CREATE TABLE product_images
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    CONSTRAINT fk_product_images_product_id
        FOREIGN KEY (product_id) REFERENCES products (id) ON delete CASCADE,
    image_url  VARCHAR(300)
);

-- ĐẶT HÀNG: ORDER
CREATE TABLE orders
(
    id           INT PRIMARY KEY AUTO_INCREMENT,
    user_id      INT,
    fullname     VARCHAR(100) DEFAULT '' comment 'Tên người đặt hàng khác (người đặt hàng khác)',
    email        VARCHAR(100) DEFAULT '' comment 'Email đặt hàng có khác',
    phone_number VARCHAR(20)  NOT NULL,
    address      VARCHAR(200) NOT NULL,
    note         VARCHAR(100) DEFAULT '',
    order_date   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    status       VARCHAR(20),
    total_money  FLOAT CHECK (total_money >= 0),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

ALTER TABLE orders
    ADD COLUMN shipping_method VARCHAR(100);
ALTER TABLE orders
    ADD COLUMN shipping_address VARCHAR(200);
ALTER TABLE orders
    ADD COLUMN shipping_date DATE;
ALTER TABLE orders
    ADD COLUMN tracking_number VARCHAR(100);
ALTER TABLE orders
    ADD COLUMN payment_method VARCHAR(100);

-- XOÁ 1 ĐƠN HÀNG => XOÁ MỀM => THÊM TRƯỜNG 'active'
ALTER TABLE orders
    ADD COLUMN active TINYINT(1);

-- TRẠNG THÁI ĐƠN HÀNG (status) -> CHỈ ĐƯỢC NHẬN MỘT SỐ GIÁ TRỊ CỤ THỂ
ALTER TABLE orders
    MODIFY COLUMN status ENUM ('pending', 'processing', 'shipped', 'delivered', 'canceled')
        COMMENT 'Trạng thái đơn hàng';

-- CHI TIẾT ĐƠN ĐẶT HÀNG
CREATE TABLE order_details
(
    id                 INT PRIMARY KEY AUTO_INCREMENT,
    order_id           INT,
    product_id         INT,
    price              FLOAT CHECK ( price >= 0 ),
    number_of_products INT CHECK ( number_of_products > 0 ),
    total_money        FLOAT CHECK ( total_money >= 0 ),
    color              VARCHAR(20) DEFAULT '',
    FOREIGN KEY (order_id) REFERENCES orders (id),
    FOREIGN KEY (product_id) REFERENCES products (id)
);
