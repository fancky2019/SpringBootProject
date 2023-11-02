package com.example.demo.kafka;

import com.example.demo.rabbitMQ.RabbitMQTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
共享消息队列模式和发布-订阅模型

kafka 没有提供同步刷盘机制，由操作系统刷盘，消息发到broker 没刷盘就可能丢失
 */
//@Component
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
