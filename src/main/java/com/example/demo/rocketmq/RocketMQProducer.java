package com.example.demo.rocketmq;

import com.alibaba.fastjson.JSON;
import com.example.demo.model.entity.demo.ProductTest;
import com.example.demo.model.viewModel.MessageResult;
import com.example.demo.rabbitMQ.RabbitMQConfig;
import com.example.demo.rabbitMQ.RabbitMqMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 *
 * 消息发送方式
 * RocketMQ 支持 3 种消息发送方式：同步（sync）、 异步（async）、单向（oneway）。
 *
 * 同步（Sync Send）： 发送者向MQ执行发送消息API时，同步等待， 直到消息服务器返回发送结果。
 * 异步（Async Send）： 发送者向MQ执行发送消息API时，指定消息发送成功后的回掉函数，然后调 用消息发送API后，立即返回，消息发送者线程不阻塞，直到运行结束，消息发送成功或 失败的回调任务在一个新的线程中执行。
 * 单向（Oneway Send）：消息发送者向MQ执行发送消息API时，直接返回，不等待消息服务器的结果， 也不注册回调函数，简单地说，就是只管发，不在乎消息是否成功存储在消息服务器上。
 *
 * 强一致性需求 → 同步发送 + 重试机制。
 * 最终一致性 + 高吞吐 → 异步发送 + 回调补偿（如记录失败消息到DB）。
 * 可容忍丢失 + 极致性能 → 单向发送。
 *
 */
@Slf4j
@Component
public class RocketMQProducer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public String send(String msgContent) {
        //发送端和接收端的消息格式要一样
        RabbitMqMessage msg = new RabbitMqMessage(msgContent);
        //  log.warn(JSON.toJSONString(msg));
        // rocketMQTemplate.send(RocketMQConfig.TOPIC, MessageBuilder.withPayload(msg).build());
        // 内部最终 syncSend:  SendResult sendResult = this.syncSend(destination, message);
        //返回值丢失
//        rocketMQTemplate.convertAndSend(RocketMQConfig.TOPIC, msg);

        // 自动序列化为JSON
        SendResult sendResult = rocketMQTemplate.syncSend(RocketMQConfig.TOPIC, msg);
//        单项发送
//        rocketMQTemplate.sendOneWay(RocketMQConfig.TOPIC, msg);
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
            //同步发送  timeout 时间值调大，单位ms 3000
            SendResult sendResult = rocketMQTemplate.syncSend(RocketMQConfig.TOPIC, MessageBuilder.withPayload(msg).build());
            //  log.info("【sendMsg】sendResult={}", JSON.toJSONString(sendResult));rocketmq //            SendStatus
//            sendResult.getSendStatus()
            return sendResult;
        } catch (Exception e) {
            int m = 0;
            return null;
        }

    }

    public SendResult sendMsg(String msgBody, String topic) {
        try {
            RabbitMqMessage msg = new RabbitMqMessage(msgBody);
            //同步发送  timeout 时间值调大，单位ms 3000
            SendResult sendResult = rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload(msg).build());
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
                RabbitMqMessage rabbitMqMessage = msg;
                // 处理消息发送异常逻辑
                int m = 0;
            }
        });
    }


    public void transactionMessage(ProductTest productTest) throws JsonProcessingException {

        String msgBody = objectMapper.writeValueAsString(productTest);
        RabbitMqMessage msg = new RabbitMqMessage(msgBody);
        String uuid = msg.getMessageId();
        // 生成事务ID
        String transactionId = UUID.randomUUID().toString();
        // 构建消息
        Message<RabbitMqMessage> message = MessageBuilder
                .withPayload(msg)
                .setHeader(RocketMQHeaders.TRANSACTION_ID, transactionId)
                .setHeader(RocketMQHeaders.KEYS, UUID.randomUUID().toString())
                .setHeader("business_id", productTest.getId())
                .setHeader(RocketMQHeaders.TOPIC, RocketMQConfig.TRANSACTION_MESSAGE_TOPIC)
                .setHeader(RocketMQHeaders.TAGS, "ProductTest")
                //生产环境配置：多事务消息 + 多 Producer Group + 多 Template + 多 Listener
                .setHeader("tx_type", "PRODUCT_TEST_UPDATE")
                .build();
        Object arg = productTest;
        // 发送事务消息
        TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction(
                RocketMQConfig.TRANSACTION_MESSAGE_TOPIC,
                message,
                arg
        );

    }

}
