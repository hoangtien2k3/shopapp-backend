package com.hoangtien2k3.shopappbackend.configurations;

import com.hoangtien2k3.shopappbackend.filters.JwtTokenFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final AuthenticationProvider authenticationProvider;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.GET,
                                String.format("%s/products/**", apiPrefix),
                                String.format("%s/products?*", apiPrefix),
                                String.format("%s/orders/**", apiPrefix),
                                String.format("%s/roles", apiPrefix),
                                String.format("%s/health-check/**", apiPrefix),
                                String.format("%s/actuator/**", apiPrefix),
                                String.format("%s/actuator/health", apiPrefix)
                        ).permitAll()
                        // swagger-ui
                        .requestMatchers(
                                String.format("%s/v2/api-docs", apiPrefix),
                                String.format("%s/v3/api-docs", apiPrefix),
                                String.format("%s/v3/api-docs/**", apiPrefix),
                                String.format("%s/swagger-resources/**", apiPrefix),
                                String.format("%s/swagger-ui.html", apiPrefix),
                                String.format("%s/webjars/**", apiPrefix),
                                String.format("%s/swagger-resources/configuration/ui", apiPrefix),
                                String.format("%s/swagger-resources/configuration/security", apiPrefix),
                                String.format("%s/swagger-ui.html/**", apiPrefix),
                                String.format("%s/swagger-ui/**", apiPrefix),
                                String.format("%s/swagger-ui.html/**", apiPrefix)
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                String.format("%s/users/register", apiPrefix),
                                String.format("%s/users/login", apiPrefix),
                                String.format("%s/users/refresh-token", apiPrefix)
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
                        })
                )
                .headers(headers -> headers
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                        .contentSecurityPolicy(csp -> csp  // Remove 'unsafe-inline'
                                .policyDirectives("default-src 'self'; img-src 'self' data:; script-src 'self'")
                        )
                        .permissionsPolicy(fp -> fp  // Adjust microphone/camera if needed
                                .policy("geolocation 'self'; microphone 'none'; camera 'none'")
                        )
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080")); // Define allowed origins
        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS")
        );
        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
