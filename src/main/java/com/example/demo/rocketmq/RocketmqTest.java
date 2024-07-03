package com.example.demo.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;


/**
 * 可用性：rabbitmq 生产者默认异步发送消息路由到绑定队列，暂时没找到同步发送。不像rocketmq和kafka 支生产者持同步发送异步发送
 */
@Slf4j
@Component
public class RocketmqTest {

    /**
    NameServer作用类似Zookeeper
    broker  负责消息存储

    broker负责接收并存储消息,发送消息指定topic,订阅也指定topic 相当于rabbitmq的queue.rabbitmq多了个exchange的概念，消息路由

    参见项目rocketmq demo

     //操作界面
     nameSrvAddr=127.0.0.1:7080

     消息可靠性：
     一、生成消息：1、同步发送，2、异步发送，回调确认机制
     二、broker 1、同步刷盘，将操作系统pageCache中的数据刷新到磁盘，2、主从模式，过半从同步
                 ## 默认情况为 ASYNC_FLUSH
                 flushDiskType = SYNC_FLUSH
     三、消费者消费确认 ConsumeConcurrentlyStatus.CONSUME_SUCCESS
     */

    @Autowired
    RocketMQProducer rocketMQProducer;
    public void test(String topic) {
//        CompletableFuture.runAsync(()->
//        {
        //不要开启线程调用否则子线程内的异常抛不出来
////            rocketMQProducer.send("rocketMqTest");
//            rocketMQProducer.sendMsg("rocketMqTest");
////            rocketMQProducer.sendAsyncMsg("rocketMqTest");
//        });


        if(StringUtils.isEmpty(topic))
        {
            rocketMQProducer.sendMsg("rocketMqTest");
        }else {
            rocketMQProducer.sendMsg("rocketMqTest",topic);
        }
    }
}
