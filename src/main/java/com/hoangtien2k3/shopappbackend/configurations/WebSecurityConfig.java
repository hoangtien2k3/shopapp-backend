package com.hoangtien2k3.shopappbackend.configurations;

import com.hoangtien2k3.shopappbackend.filters.JwtTokenFilter;
import com.hoangtien2k3.shopappbackend.utils.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                String.format("%s/users/register", apiPrefix),
                                String.format("%s/users/login", apiPrefix),
                                String.format("%s/products/**", apiPrefix),
                                String.format("%s/products**", apiPrefix),
                                String.format("%s/orders/**", apiPrefix)
                        ).permitAll()

                        // Pre-authorization categories
                        .requestMatchers(HttpMethod.GET,
                                String.format("%s/categories?**", apiPrefix)).hasAnyRole(RoleType.USER, RoleType.ADMIN)
                        .requestMatchers(HttpMethod.POST,
                                String.format("%s/categories/**", apiPrefix)).hasRole(RoleType.ADMIN)
                        .requestMatchers(HttpMethod.PUT,
                                String.format("%s/categories/**", apiPrefix)).hasRole(RoleType.ADMIN)
                        .requestMatchers(HttpMethod.DELETE,
                                String.format("%s/categories/**", apiPrefix)).hasRole(RoleType.ADMIN)

                        // Pre-authorization products
                        .requestMatchers(HttpMethod.GET,
                                String.format("%s/products?**", apiPrefix)).hasAnyRole(RoleType.USER, RoleType.ADMIN)
                        .requestMatchers(HttpMethod.POST,
                                String.format("%s/products/**", apiPrefix)).hasRole(RoleType.ADMIN)
                        .requestMatchers(HttpMethod.PUT,
                                String.format("%s/products/**", apiPrefix)).hasRole(RoleType.ADMIN)
                        .requestMatchers(HttpMethod.DELETE,
                                String.format("%s/products/**", apiPrefix)).hasRole(RoleType.ADMIN)

                        // Pre-authorization orders
                        .requestMatchers(HttpMethod.POST,
                                String.format("%s/orders/**", apiPrefix)).hasRole(RoleType.USER)
                        .requestMatchers(HttpMethod.PUT,
                                String.format("%s/orders/**", apiPrefix)).hasRole(RoleType.ADMIN)
                        .requestMatchers(HttpMethod.DELETE,
                                String.format("%s/orders/**", apiPrefix)).hasRole(RoleType.ADMIN)

                        // Pre-authorization order_details
                        .requestMatchers(HttpMethod.POST,
                                String.format("%s/order_details/**", apiPrefix)).hasRole(RoleType.USER)
                        .requestMatchers(HttpMethod.PUT,
                                String.format("%s/order_details/**", apiPrefix)).hasRole(RoleType.ADMIN)
                        .requestMatchers(HttpMethod.GET,
                                String.format("%s/order_details/**", apiPrefix)).hasAnyRole(RoleType.USER, RoleType.ADMIN)
                        .requestMatchers(HttpMethod.DELETE,
                                String.format("%s/order_details/**", apiPrefix)).hasRole(RoleType.ADMIN)

                        .anyRequest().authenticated()
                );

        return http.build();
    }

}
