package com.example.demo.rabbitMQ;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.model.enums.MqMessageStatus;
import com.example.demo.service.demo.IMqMessageService;
//import com.example.demo.utility.ApplicationContextAwareImpl;
import com.example.demo.utility.ApplicationContextAwareImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/*
 * 1、确认模式（confirm）：可以监听消息是否从生产者成功传递到交换。
 * 2、退回模式（return）：可以监听消息是否从交换机成功传递到队列。
 * 3、消费者消息确认（Ack）：可以监听消费者是否成功处理消息。
 */


/**
 *
 * 确保发送到交换机，不确定路由到队列
 * 生产者 → Exchange → (路由匹配) → Queue → 消费者
 */
@Component
@Slf4j
public class PushConfirmCallback implements RabbitTemplate.ConfirmCallback {


    //null
//    @Autowired
//    ApplicationContext applicationContext;
    //无法注入 通过容器获取
//    @Autowired
//    IMqMessageService mqMessageService;


    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String s) {
        try {
            //生产线程和生产确认线程不是同一个线程
            long threadId = Thread.currentThread().getId();
            log.info("ProduceConfirm threadId - {}", threadId);
// s:channel error; protocol method: #method<channel.close>(reply-code=404, reply-text=NOT_FOUND - no exchange 'UnBindDirectExchange' in vhost '/', class-id=60, method-id=40)
            String msgId = correlationData.getId();
            ReturnedMessage returnedMessage = correlationData.getReturned();
            Message message = returnedMessage.getMessage();
            MessageProperties messageProperties = message.getMessageProperties();
            String messageId = messageProperties.getMessageId();
            if (ack) {
                //发送消息时候指定的消息的id，根据此id设置消息表的消息状态为已发送

//                从容器中获取bean
                ApplicationContext applicationContext = ApplicationContextAwareImpl.getApplicationContext();
                IMqMessageService mqMessageService = applicationContext.getBean(IMqMessageService.class);
                //本地消息表会存在重复投递情况，消费端要做幂等处理
                //rabbitMq 生产成功，更新小标状态失败。定时任务补偿重试，重复发送直到更新消息表更新成功
                //可采用事务消息（推荐）
                //使用 RocketMQ/Kafka 的事务消息 替代 RabbitMQ，它们原生支持 两阶段提交（2PC），能保证：
                //
                //Prepare 阶段：半消息发送到 Broker（但消费者不可见）。
                //
                //Commit 阶段：本地事务成功，消息正式投递；失败则回滚。
                //
                //但 RabbitMQ 没有原生事务消息支持，需自行实现补偿机制。

                LambdaQueryWrapper<MqMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(MqMessage::getMsgId, msgId);
                MqMessage mqMessage = mqMessageService.getOne(lambdaQueryWrapper);
                mqMessage.setStatus(1);
                mqMessage.setModifyTime(LocalDateTime.now());
                mqMessageService.updateById(mqMessage);

//                mqMessageService.updateByMsgId(msgId, MqMessageStatus.PRODUCE.getValue());

//                LambdaUpdateWrapper<MqMessage> updateWrapper = new LambdaUpdateWrapper<>();
//                updateWrapper.set(MqMessage::getStatus, 1);
//                updateWrapper.eq(MqMessage::getMsgId, msgId);//条件
//                mqMessageService.update(updateWrapper);

                //更新本地消息表，消息已经发送到mq
                log.info("消息 - {} 发送到交换机成功！", msgId);
//                log.info("消息 - {} 发送到交换机成功！{}", msgId,"123");
            } else {
                log.info("消息 - {} 发送到交换机失败！ ", msgId);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
