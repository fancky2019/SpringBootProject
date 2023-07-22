package com.example.demo.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
@Slf4j
@Component
public class RocketmqTest {


    /*
    在此项目中rocketmq 发送消息不成功，不知道是不是rabbitmq 冲突了， 但是可以订阅
    参见项目rocketmq demo
     */

    @Autowired
    RocketMQProducer rocketMQProducer;
    public void test() {
        CompletableFuture.runAsync(()->
        {
//            rocketMQProducer.send("rocketMqTest");
            rocketMQProducer.sendMsg("rocketMqTest");
//            rocketMQProducer.sendAsyncMsg("rocketMqTest");
        });
    }
}
