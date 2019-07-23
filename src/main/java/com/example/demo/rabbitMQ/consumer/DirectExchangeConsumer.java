package com.example.demo.rabbitMQ.consumer;


import com.alibaba.fastjson.JSON;
import com.example.demo.model.viewModel.Person;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
//@RabbitListener(queues = "DirectExchangeQueueSpringBoot")//参数为队列名称
public class DirectExchangeConsumer {
    public static final String DIRECT_QUEUE_NAME = "DirectExchangeQueueSpringBoot";

//    @RabbitHandler
//    @RabbitListener(queues = DIRECT_QUEUE_NAME)//参数为队列名称
//    public void receivedMsg(String msg){
//        System.out.println("DirectExchange Queue:"+DIRECT_QUEUE_NAME+" receivedMsg: "+msg);
//    }


    @RabbitHandler
    @RabbitListener(queues = DIRECT_QUEUE_NAME)//参数为队列名称
    public void receivedMsg(String receivedMessage, Channel channel, Message message) {
        try {
            //  System.out.println("DirectExchange Queue:" + DIRECT_QUEUE_NAME + " receivedMsg: " + receivedMessage);

            Person person = JSON.parseObject(receivedMessage, Person.class);
            System.out.println("DirectExchange Queue:" + DIRECT_QUEUE_NAME + " receivedMsg: " + receivedMessage);
            //手动Ack
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            e.printStackTrace();
            //丢弃这条消息
            //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
        }

    }


}
