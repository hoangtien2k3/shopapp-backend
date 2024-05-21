package com.hoangtien2k3.shopappbackend.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@Data
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "token_type", nullable = false, length = 50)
    private String tokenType;

    @Column(name = "expiration_time", nullable = false)
    private Instant expirationTime; // thời gian sống của token

    @Column(name = "refresh_expiration_time", nullable = false)
    private Instant refreshExpirationTime; // thời gian sống của refresh token

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "expired", nullable = false)
    private boolean expired;

    @Column(name = "is_mobile")
    private boolean isMobile;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
