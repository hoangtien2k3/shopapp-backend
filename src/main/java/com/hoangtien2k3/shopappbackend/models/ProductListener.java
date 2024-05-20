package com.hoangtien2k3.shopappbackend.models;

import com.hoangtien2k3.shopappbackend.services.ProductRedisService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ProductListener {

    private final ProductRedisService productRedisService;

    @PrePersist
    public void onPrePersist(Product product) {
        log.info("onPrePersist: {}", product);
    }

    @PostPersist // save = persis
    public void onPostPersist(Product product) {
        log.info("onPostPersist: {}", product);
        productRedisService.clear();
    }

    @PreUpdate
    public void onPreUpdate(Product product) {
        // ApplicationEvenPublisher.instance().publishEvent(event);
        log.info("onPreUpdate: {}", product);
    }

    @PostUpdate
    public void onPostUpdate(Product product) {
        // update regis cache
        log.info("onPostUpdate: {}", product);
        productRedisService.clear();
    }

    @PreRemove
    public void onPreRemove(Product product) {
        // ApplicationEvenPublisher.instance().publishEvent(event);
        log.info("onPreRemove: {}", product);
    }

    @PostRemove
    public void onPostRemove(Product product) {
        log.info("onPostRemove: {}", product);
        productRedisService.clear();
    }

}
