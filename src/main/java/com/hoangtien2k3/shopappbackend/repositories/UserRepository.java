package com.hoangtien2k3.shopappbackend.repositories;

import com.hoangtien2k3.shopappbackend.models.User;
import com.hoangtien2k3.shopappbackend.utils.ConfixSql;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<User> findByPhoneNumber(String phoneNumber);

    // lấy ra tất cả user (ngoại trừ admin) với truyền admin
    @Query(ConfixSql.User.GET_ALL_USER)
    Page<User> fillAll(@Param("keyword") String keyword, Pageable pageable);
}
