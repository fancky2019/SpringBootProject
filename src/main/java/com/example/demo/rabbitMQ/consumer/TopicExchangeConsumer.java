package com.example.demo.rabbitMQ.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
//@RabbitListener(queues = "DirectExchangeQueueSpringBoot")//参数为队列名称
public class TopicExchangeConsumer {
    public static final String TOPIC_QUEUE_NAME = "TopicExchangeQueueSpringBoot";
    public static final String TOPIC_QUEUE_NAME1 = "TopicExchangeQueueSpringBoot1";

//    @RabbitHandler
//    @RabbitListener(queues = TOPIC_QUEUE_NAME)//参数为队列名称
//    public void receivedMsg(String msg){
//        System.out.println("TopicExchange Queue:"+TOPIC_QUEUE_NAME +" receivedMsg: "+msg);
//    }

    @RabbitHandler
    @RabbitListener(queues = TOPIC_QUEUE_NAME)//参数为队列名称
    public void receivedMsg(String receivedMessage, Channel channel, Message message) {
        try {
            System.out.println("TopicExchange Queue:" + TOPIC_QUEUE_NAME + " receivedMsg: " + receivedMessage);
            //手动Ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @RabbitHandler
//    @RabbitListener(queues = TOPIC_QUEUE_NAME1)//参数为队列名称
//    public void receivedMsg1(String msg){
//        System.out.println("TopicExchange Queue:"+TOPIC_QUEUE_NAME1 +" receivedMsg: "+msg);
//    }

    @RabbitHandler
    @RabbitListener(queues = TOPIC_QUEUE_NAME1)//参数为队列名称
    public void receivedMsg1(String receivedMessage, Channel channel, Message message) {
        try {
            System.out.println("TopicExchange Queue:" + TOPIC_QUEUE_NAME1 + " receivedMsg: " + receivedMessage);
            //手动Ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
