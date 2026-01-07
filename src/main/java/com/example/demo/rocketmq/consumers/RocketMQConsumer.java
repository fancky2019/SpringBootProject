package com.example.demo.rocketmq.consumers;

import com.example.demo.rocketmq.RocketMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * RocketMQ没有真正意义的push，都是pull，虽然有push类，但实际底层实现采用的是长轮询机制，即拉取方式
 *
 *
 *
 *
 *
 *
 *
 * 一个消息只能被一个消费者组中一个消费者消费。
 * 消息模型 ：messageModel
 *1、集群消费模式：是消费者默认的消费方式。 消费者采用负载均衡方式消费消息，一个分组(Group)下的多个消费者共同消费队列消息，每个消费者处理的消息不同。
 * 一个Consumer Group中的各个Consumer实例分摊去消费消息，即一条消息只会投递到一个Consumer Group下面的一个实例。
 * 例如某个Topic有3个队列，其中一个Consumer Group 有 3 个实例，那么每个实例只消费其中的1个队列。
 * 。
 *
 *2、广播消费模式：中消息将对一个Consumer Group下的各个Consumer实例都投递一遍。即使这些 Consumer属于同一个Consumer
 *Group，消息也会被Consumer Group 中的每个Consumer都消费一次。实际上，是一个消费组下的每个消费者实例都获取到了
 *topic下面的每个Message Queue去拉取消费。所以消息会投递到每个消费者实例
 *
 */




@Slf4j
@Component
//,messageModel = MessageModel.BROADCASTING) //默认集群模式
//consumeMode() default ConsumeMode.CONCURRENTLY;

@RocketMQMessageListener(topic = RocketMQConfig.TOPIC, consumerGroup = RocketMQConfig.CONSUMER_GROUP)
//public class RocketMQConsumer implements RocketMQListener<RabbitMqMessage> {
public class RocketMQConsumer implements RocketMQListener<MessageExt> , RocketMQPushConsumerLifecycleListener {

    //    @Override
//    public void onMessage(RabbitMqMessage message) {
//        //默认 ConsumeConcurrentlyStatus.CONSUME_SUCCESS; 只要消费成功就确认
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String timeStr = LocalDateTime.now().format(formatter);
//        log.info("{} - 接受到消息: {}", timeStr, message.toString());
//        Integer m = Integer.parseInt("m");
//    }

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
