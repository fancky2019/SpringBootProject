package com.example.demo.rocketmq.consumers;

import com.example.demo.rocketmq.RocketMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RocketMQMessageListener(topic = RocketMQConfig.TOPIC_DLQ, consumerGroup = RocketMQConfig.CONSUMER_GROUP_DLQ)


public class RocketMQConsumerDLQ implements RocketMQListener<MessageExt> {
    @Override
    public void onMessage(MessageExt message) {
        //一个消费者组订阅一个topic，这样死信队列对应的topic 就一样。每个消费者组只有一个死信队列
        String topic = message.getTopic();
        String content = new String(message.getBody());
        // 处理消息 签收了
        log.info("保存到 mysql 定时任务处理");
    }
}
