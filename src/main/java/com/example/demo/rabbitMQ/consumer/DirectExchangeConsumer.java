package com.example.demo.rabbitMQ.consumer;


import com.alibaba.fastjson.JSON;
import com.example.demo.controller.UserController;
import com.example.demo.model.viewModel.Person;
import com.rabbitmq.client.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
//@RabbitListener(queues = "DirectExchangeQueueSpringBoot")//参数为队列名称
public class DirectExchangeConsumer {

    private static Logger logger = LogManager.getLogger(DirectExchangeConsumer.class);

    public static final String DIRECT_QUEUE_NAME = "DirectExchangeQueueSpringBoot";

//    @RabbitHandler
//    @RabbitListener(queues = DIRECT_QUEUE_NAME)//参数为队列名称
//    public void receivedMsg(String msg){
//        System.out.println("DirectExchange Queue:"+DIRECT_QUEUE_NAME+" receivedMsg: "+msg);
//    }


    @RabbitHandler
    @RabbitListener(queues = DIRECT_QUEUE_NAME)//参数为队列名称
    public void receivedMsg(String receivedMessage, Channel channel, Message message) throws Exception {
        try {
            //  System.out.println("DirectExchange Queue:" + DIRECT_QUEUE_NAME + " receivedMsg: " + receivedMessage);

            Person person = JSON.parseObject(receivedMessage, Person.class);
            System.out.println("DirectExchange Queue:" + DIRECT_QUEUE_NAME + " receivedMsg: " + receivedMessage);

            Integer m = Integer.parseInt("m");
            //手动Ack listener.simple.acknowledge-mode: manual
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {


            //死信
            // channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);

            //  设置死信队列设置自动Ack. 否则不能进入死信队列。
            //将异常抛出，不能吞了，否则不能重试。和mybatis的事务回滚有点像，否则mybatis不能回滚。
            //  throw new AmqpRejectAndDontRequeueException(e.getMessage()) ;
            throw e;
            // e.printStackTrace();
//            logger.error(e.getMessage());
            //丢弃这条消息
            //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
        }

    }



    @RabbitHandler
    @RabbitListener(queues = {DIRECT_QUEUE_NAME},containerFactory = "customContainerFactory")
    public void consumerByMultiThread(String receivedMessage, Channel channel, Message message) throws Exception {
        try {
            //  System.out.println("DirectExchange Queue:" + DIRECT_QUEUE_NAME + " receivedMsg: " + receivedMessage);

            Person person = JSON.parseObject(receivedMessage, Person.class);
            System.out.println("DirectExchange Queue:" + DIRECT_QUEUE_NAME + " receivedMsg: " + receivedMessage);

            Integer m = Integer.parseInt("m");
            //手动Ack listener.simple.acknowledge-mode: manual
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {


            //死信
            // channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);

            //  设置死信队列设置自动Ack. 否则不能进入死信队列。
            //将异常抛出，不能吞了，否则不能重试。和mybatis的事务回滚有点像，否则mybatis不能回滚。
            //  throw new AmqpRejectAndDontRequeueException(e.getMessage()) ;
            throw e;
            // e.printStackTrace();
//            logger.error(e.getMessage());
            //丢弃这条消息
            //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false,false);
        }

    }

}
