package com.example.demo.rocketmq;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class RocketMQPushConsumerLifecycleListenerImp  implements RocketMQPushConsumerLifecycleListener {
//    @Override
//    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
//        // 设置消费者重试次数
//        defaultMQPushConsumer.setMaxReconsumeTimes(3);
//        // 实例名称-控制面板可以看到
//        defaultMQPushConsumer.setInstanceName("消费者1号");
//        defaultMQPushConsumer.setPullBatchSize(10);
//    }
//}
