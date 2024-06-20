package com.hoangtien2k3.shopappbackend.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
/**
 * @MappedSuperclass
 * Chia sẻ thuộc tính: kế thừa các thuộc tính, phương thức, và ánh xạ từ lớp siêu lớp mà không cần định nghĩa lại.
 * Không tạo bảng riêng: Lớp được chú thích với @MappedSuperclass sẽ không tạo ra một bảng trong cơ sở dữ liệu.
 *                       Thay vào đó, các thuộc tính của nó sẽ được ánh xạ vào các bảng của các lớp con.
 * */
@MappedSuperclass
public class BaseEntity {
    @JsonProperty("created_at")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
