package com.example.demo.rocketmq.transactionmessage;

import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.rocketmq.RocketMQConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Slf4j
@Component
//,messageModel = MessageModel.BROADCASTING) //默认集群模式
//consumeMode() default ConsumeMode.CONCURRENTLY;

@RocketMQMessageListener(topic = RocketMQConfig.TRANSACTION_MESSAGE_TOPIC, consumerGroup = RocketMQConfig.TRANSACTION_MESSAGE_TOPIC_CONSUMER_GROUP)
//public class RocketMQConsumer implements RocketMQListener<RabbitMqMessage> {
public class TransactionMessageConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    @Autowired
     private ObjectMapper objectMapper;
    //    @Override
//    public void onMessage(RabbitMqMessage message) {
//        //默认 ConsumeConcurrentlyStatus.CONSUME_SUCCESS; 只要消费成功就确认
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String timeStr = LocalDateTime.now().format(formatter);
//        log.info("{} - 接受到消息: {}", timeStr, message.toString());
//        Integer m = Integer.parseInt("m");
//    }

    @Override
    public void onMessage(MessageExt message) {
        //默认 ConsumeConcurrentlyStatus.CONSUME_SUCCESS; 只要消费成功就确认
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timeStr = LocalDateTime.now().format(formatter);

        int retryCount = message.getReconsumeTimes();
        String content = new String(message.getBody());
        try {
            ProductTest productTest=objectMapper.readValue(content,ProductTest.class);
            int n=0;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        log.info("{} - {} 接受到消息: {}", timeStr, retryCount, content);
//  消费失败会重新投递
        //        Integer m = Integer.parseInt("m");
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
