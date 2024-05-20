package com.hoangtien2k3.shopappbackend.components;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class CustomHealthCheck implements HealthIndicator {
    @Override
    public Health health() {
        // implement your custom health logic here
        try {
            String computerName = InetAddress.getLocalHost().getHostName();
            return Health.up().withDetail("computerName", computerName).build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("Error: ", e.getMessage()).build();
        }
    }
}
