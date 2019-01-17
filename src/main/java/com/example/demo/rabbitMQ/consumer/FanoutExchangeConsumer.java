package com.example.demo.rabbitMQ.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RabbitListener
public class FanoutExchangeConsumer {
    public static final String FANOUT_QUEUE_NAME = "FanoutExchangeQueueSpringBoot";
    public static final String FANOUT_QUEUE_NAME1 = "FanoutExchangeQueueSpringBoot1";

//    @RabbitHandler
//    @RabbitListener(queues = FANOUT_QUEUE_NAME)//参数为队列名称
//    public void receivedMsg(String msg){
//        System.out.println("FanoutExchange queue:"+FANOUT_QUEUE_NAME+" receivedMsg: "+msg);
//    }

    @RabbitHandler
    @RabbitListener(queues = FANOUT_QUEUE_NAME)//参数为队列名称
    public void receivedMsg(String receivedMessage, Channel channel, Message message) {
        try {
            System.out.println("FanoutExchange Queue:" + FANOUT_QUEUE_NAME + " receivedMsg: " + receivedMessage);
            //手动Ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            //丢弃这条消息
            //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
        }
    }

//    @RabbitHandler
//    @RabbitListener(queues = FANOUT_QUEUE_NAME1)//参数为队列名称
//    public void receivedMsg1(String msg){
//        System.out.println("FanoutExchange queue:"+FANOUT_QUEUE_NAME1+" receivedMsg: "+msg);
//    }

    @RabbitHandler
    @RabbitListener(queues = FANOUT_QUEUE_NAME1)//参数为队列名称
    public void receivedMsg1(String receivedMessage, Channel channel, Message message) {
        try {
            System.out.println("FanoutExchange Queue:" + FANOUT_QUEUE_NAME1 + " receivedMsg: " + receivedMessage);
            //手动Ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
