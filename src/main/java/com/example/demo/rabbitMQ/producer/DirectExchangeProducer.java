package com.example.demo.rabbitMQ.producer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DirectExchangeProducer {

    public static final String DIRECT_EXCHANGE_NAME = "DirectExchangeSpringBoot";
    public static final String DIRECT_ROUTING_KEY = "DirectExchangeRoutingKeySpringBoot.Direct";
    public static final String DIRECT_QUEUE_NAME = "DirectExchangeQueueSpringBoot";


    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Autowired
//    private AmqpTemplate amqpTemplate;


    public void producer() {
        String msg = "MSG_DirectExchangeProducer";
        //参数：队列名称,消息内容（可以为可序列化的对象）
//        this.amqpTemplate.convertAndSend(DIRECT_QUEUE_NAME, msg);
        rabbitTemplate.convertAndSend(DIRECT_QUEUE_NAME, msg);
    }

}
