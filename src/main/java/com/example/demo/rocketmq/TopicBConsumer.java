package com.example.demo.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RocketMQMessageListener(topic = RocketMQConfig.TOPICB, consumerGroup = RocketMQConfig.CONSUMER_GROUP_B)
//public class RocketMQConsumer implements RocketMQListener<RabbitMqMessage> {
public class TopicBConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Override
    public void onMessage(MessageExt message ) {
        //默认 ConsumeConcurrentlyStatus.CONSUME_SUCCESS; 只要消费成功就确认
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeStr = LocalDateTime.now().format(formatter);

        int retryCount = message.getReconsumeTimes();
        String content = new String(message.getBody());

        log.info("{} - {} 接受到消息: {}", timeStr,retryCount, content);
        Integer m = Integer.parseInt("m");
    }

    //RocketMQPushConsumerLifecycleListener
    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        // 设置消费者重试次数 消息最大重试次数的设置对相同GroupID下的所有Consumer实例有效
        defaultMQPushConsumer.setMaxReconsumeTimes(3);
        // 实例名称-控制面板可以看到
        // defaultMQPushConsumer.setInstanceName("消费者1号");

        // defaultMQPushConsumer.setPullBatchSize(10);
    }
}
