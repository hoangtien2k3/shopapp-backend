package com.hoangtien2k3.shopappbackend.configurations;

import com.hoangtien2k3.shopappbackend.utils.Const;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
public class KafkaCofiguration {

    @Bean
    public CommonErrorHandler errorHandler(KafkaOperations<Object, Object> kafkaOperations) {
        return new DefaultErrorHandler(
                new DeadLetterPublishingRecoverer(kafkaOperations), new FixedBackOff(1000L, 2)
        );
    }

    @Bean
    public NewTopic insertACategoryTopic() {
        return new NewTopic(Const.KAFKA_TOPIC_INSERT_CATEGORY, 1, (short) 1);
    }

    @Bean
    public NewTopic getAllCategoryTopic() {
        return new NewTopic(Const.KAFKA_TOPIC_GET_ALL_CATEGORY, 1, (short) 1);
    }
}
