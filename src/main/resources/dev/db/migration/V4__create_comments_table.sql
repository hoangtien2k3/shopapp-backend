CREATE TABLE comments
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT,
    user_id    INT,
    content    VARCHAR(255),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (product_id) REFERENCES products (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);