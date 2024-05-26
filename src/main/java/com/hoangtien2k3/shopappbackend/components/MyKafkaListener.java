package com.hoangtien2k3.shopappbackend.components;

import com.hoangtien2k3.shopappbackend.models.Category;
import com.hoangtien2k3.shopappbackend.utils.Const;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@KafkaListener(id = Const.KAFKA_ID, topics = {Const.KAFKA_TOPIC_INSERT_CATEGORY, Const.KAFKA_TOPIC_GET_ALL_CATEGORY})
public class MyKafkaListener {

    @KafkaListener(topics = Const.KAFKA_TOPIC_INSERT_CATEGORY)
    public void listenerCategory(Category category) {
        System.out.println("Received Category: " + category);
    }

    @KafkaListener(topics = Const.KAFKA_TOPIC_GET_ALL_CATEGORY)
    public void listenListOfCategory(List<Category> categories) {
        System.out.println("Received list of Category: " + categories);
    }

    @KafkaHandler(isDefault = true)
    public void unknown(Category category) {
        System.out.println("Received unknown: " + category);
    }
}
