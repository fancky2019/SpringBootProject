package com.example.demo.rocketmq;

import com.alibaba.fastjson.JSON;
import com.example.demo.rabbitMQ.RabbitMQConfig;
import com.example.demo.rabbitMQ.RabbitMqMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Component
public class RocketMQProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public String send(String msgContent) {
        //发送端和接收端的消息格式要一样
        RabbitMqMessage msg = new RabbitMqMessage(msgContent);
        //  log.warn(JSON.toJSONString(msg));
        // rocketMQTemplate.send(RocketMQConfig.TOPIC, MessageBuilder.withPayload(msg).build());
        rocketMQTemplate.convertAndSend(RocketMQConfig.TOPIC, msg);
        return "SUCESS";
    }
//    /**
//     * 普通发送（这里的参数对象User可以随意定义，可以发送个对象，也可以是字符串等）
//     */
//    public void send(String msg) {
//        rocketMQTemplate.convertAndSend(RocketMQConfig.TOPIC + ":tag1", "rocketMs");
////        rocketMQTemplate.send(topic + ":tag1", MessageBuilder.withPayload(user).build()); // 等价于上面一行
//    }

    /**
     * 发送同步消息（阻塞当前线程，等待broker响应发送结果，这样不太容易丢失消息）
     * （msgBody也可以是对象，sendResult为返回的发送结果）
     */
    public SendResult sendMsg(String msgBody) {
        try {
            RabbitMqMessage msg = new RabbitMqMessage(msgBody);
            //同步发送
            SendResult sendResult = rocketMQTemplate.syncSend(RocketMQConfig.TOPIC, MessageBuilder.withPayload(msg).build(),30);
            //  log.info("【sendMsg】sendResult={}", JSON.toJSONString(sendResult));
            return sendResult;
        } catch (Exception e) {
            int m = 0;
            return null;
        }

    }

    /**
     * 发送异步消息（通过线程池执行发送到broker的消息任务，执行完后回调：在SendCallback中可处理相关成功失败时的逻辑）
     * （适合对响应时间敏感的业务场景）
     */
    public void sendAsyncMsg(String msgBody) {
        RabbitMqMessage msg = new RabbitMqMessage(msgBody);
        String uuid = msg.getMessageId();
        Message<RabbitMqMessage> message = MessageBuilder.withPayload(msg).build();
        //异步发送，在发送回调里处理生产成功失败。
        rocketMQTemplate.asyncSend(RocketMQConfig.TOPIC, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                //不是发送的消息的id
                String msgId = sendResult.getMsgId();
                String uuid1 = uuid;

                // 处理消息发送成功逻辑
                int m = 0;
            }

            @Override
            public void onException(Throwable throwable) {
                // 处理消息发送异常逻辑
                int m = 0;
            }
        });
    }

}
