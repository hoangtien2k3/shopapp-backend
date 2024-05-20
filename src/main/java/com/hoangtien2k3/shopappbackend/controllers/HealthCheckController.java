package com.hoangtien2k3.shopappbackend.controllers;

import com.hoangtien2k3.shopappbackend.models.Category;
import com.hoangtien2k3.shopappbackend.services.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/health_check")
@AllArgsConstructor
public class HealthCheckController {

    private final CategoryService categoryService;

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        // perform addition health check here
        try {
            List<Category> categories = categoryService.getAllCategories();
            // get the computer name
            String computerName = InetAddress.getLocalHost().getHostName();
            return ResponseEntity.ok("Oke, computer name: " + computerName);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("failed");
        }
    }
}
