package com.example.demo.rabbitMQ.producer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FanoutExchangeProducer {

    public static final String FANOUT_EXCHANGE_NAME = "FanoutExchangeSpringBoot";

//    @Autowired
//    private AmqpTemplate amqpTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void producer() {
        String msg="MSG_FanoutExchangeProducer";
         //参数：队列名称,RouteKey,消息内容（可以为可序列化的对象）
        // 注意， 这里的第2个参数为空。
        // 每个发送到交换器的消息都会被转发到与该交换器绑定的所有队列上
        this.rabbitTemplate.convertAndSend(FANOUT_EXCHANGE_NAME, "", msg );
        System.out.println("send: "+msg);
    }
}
