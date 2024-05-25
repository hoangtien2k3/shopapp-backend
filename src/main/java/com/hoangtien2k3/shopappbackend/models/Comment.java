package com.hoangtien2k3.shopappbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "comments")
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;

//    @Column(name = "created_at")
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at")
//    private LocalDateTime updatedAt;

    /*
    trường được đanh dấu là @PrePresist sẽ được JPA gọi trước khi mà INSERT Entity và DB
    => Vòng đơời của @PrePersist chỉ được gọi 1 lần

    + Một số annotation khác:
        @PostPersist: Được gọi sau khi entity được persist.
        @PreUpdate: Được gọi trước khi một entity được cập nhật.
        @PostUpdate: Được gọi sau khi một entity được cập nhật.
        @PreRemove: Được gọi trước khi một entity bị xóa.
        @PostRemove: Được gọi sau khi một entity bị xóa.
        @PostLoad: Được gọi sau khi một entity được tải.
    */
}
