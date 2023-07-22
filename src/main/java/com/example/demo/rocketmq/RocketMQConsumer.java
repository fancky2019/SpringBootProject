package com.example.demo.rocketmq;

import com.example.demo.rabbitMQ.RabbitMQConfig;
import com.example.demo.rabbitMQ.RabbitMQTest;
import com.example.demo.rabbitMQ.RabbitMqMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;



@Slf4j
@Component
@RocketMQMessageListener(topic = RocketMQConfig.TOPIC, consumerGroup = RocketMQConfig.CONSUMERGROUP)
public class RocketMQConsumer implements RocketMQListener<RabbitMqMessage> {

    @Override
    public void onMessage(RabbitMqMessage message) {
        //默认 ConsumeConcurrentlyStatus.CONSUME_SUCCESS; 只要消费成功就确认
        log.info("接受到消息: {}", message.toString());
    }
}
