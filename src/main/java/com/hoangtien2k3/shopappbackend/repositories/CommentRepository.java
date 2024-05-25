package com.hoangtien2k3.shopappbackend.repositories;

import com.hoangtien2k3.shopappbackend.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUserIdAndProductId(@Param("userId") Long userId,
                                           @Param("productId") Long productId);

    List<Comment> findByProductId(@Param("productId") Long productId);

}
