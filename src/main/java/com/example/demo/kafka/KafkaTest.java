package com.example.demo.kafka;

import com.example.demo.rabbitMQ.RabbitMQTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaTest {
    private static Logger logger = LogManager.getLogger(RabbitMQTest.class);
    @Autowired
    private KafkaConsumerClient kafkaConsumerClient;
    @Autowired
    private KafkaProducerClient kafkaProducerClient;

    public void test() {
        //https://github.com/spring-projects/spring-kafka
        try {
            kafkaProducerClient.producer("springbootKafka");
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
    }
}
