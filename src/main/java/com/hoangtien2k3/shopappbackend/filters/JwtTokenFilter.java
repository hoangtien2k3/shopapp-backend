package com.hoangtien2k3.shopappbackend.filters;

import com.hoangtien2k3.shopappbackend.components.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    @Value("${api.prefix}")
    private String apiPrefix;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            if (isByPassToken(request)) {
                filterChain.doFilter(request, response); // enable bypass
                return;
            }

            String authHeader = request.getHeader("Authorization");
            String token;
            String phoneNumber;
            if (authHeader == null && !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }

            token = authHeader.substring(7);
            phoneNumber = jwtTokenUtil.extractPhoneNumber(token);

            if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsService.loadUserByUsername(phoneNumber);
                if (jwtTokenUtil.isTokenValid(token, user)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    user,
                                    null,
                                    user.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    // isByPass: không yêu cầu token
    private boolean isByPassToken(@NotNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(String.format("%s/health-check/health", apiPrefix), "GET"),
                Pair.of(String.format("%s/actuator/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/actuator/health", apiPrefix), "GET"),
                Pair.of(String.format("%s/actuator", apiPrefix), "GET"),

                Pair.of(String.format("%s/roles/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/products/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/categories/**", apiPrefix), "GET"),
                Pair.of(String.format("%s/users/login", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/register", apiPrefix), "POST"),
                Pair.of(String.format("%s/users/refresh-token", apiPrefix), "POST"),

                // sagger-ui
                Pair.of("/v2/api-docs", "GET"),
                Pair.of("/v3/api-docs", "GET"),
                Pair.of("/v3/api-docs/**", "GET"),
                Pair.of("/swagger-resources/**", "GET"),
                Pair.of("/swagger-ui.html", "GET"),
                Pair.of("/webjars/**", "GET"),
                Pair.of("/swagger-resources/configuration/ui", "GET"),
                Pair.of("/swagger-resources/configuration/security", "GET"),
                Pair.of("/swagger-ui.html/**", "GET"),
                Pair.of("/swagger-ui/**", "GET"),
                Pair.of("/swagger-ui.html/**", "GET")
        );

        String requestPath = request.getServletPath();
        String requestMethod = request.getMethod();
        // check ở method get của order
        if (requestPath.equals(String.format("%s/orders", apiPrefix))
                && requestMethod.equals("GET")) {
            // check if the requestPath matches the desired pattern
            if (requestPath.matches(String.format("/%s/orders/\\d+", apiPrefix))) {
                return true;
            }
            // if the requestPath is just
            if (requestPath.matches(String.format("/%s/orders", apiPrefix))) {
                return true;
            }
        }

        for (Pair<String, String> bypassToken : bypassTokens) {
            String tokenPath = bypassToken.getFirst();
            String tokenMethod = bypassToken.getSecond();

            // check if the token path contains a wildcard character
            if (tokenPath.contains("**")) {
                // replate "**" with a regular expresion capturing any character
                String regexPath = tokenPath.replace("**", ".*");
                // create a patten to match the request path
                Pattern pattern = Pattern.compile(regexPath);
                Matcher matcher = pattern.matcher(requestPath);

                // check if the request path matcher the pattern and the request matches the token method
                if (matcher.matches() && requestMethod.equals(tokenMethod)) {
                    return true;
                }
            } else if (requestPath.equals(tokenPath) && requestMethod.equals(tokenMethod)) {
                return true;
            }
        }

        return false;
    }

}
