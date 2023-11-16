package com.example.demo.rabbitMQ;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.service.demo.IMqMessageService;
import com.example.demo.utility.SpringApplicationContextHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PushConfirmCallback implements RabbitTemplate.ConfirmCallback {

    //无法注入 通过容器获取
//    @Autowired
//    IMqMessageService mqMessageService;
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String s) {
        try {

            String msgId = correlationData.getId();
            if (ack) {
                //发送消息时候指定的消息的id，根据此id设置消息表的消息状态为已发送

//                从容器中获取bean
                ApplicationContext applicationContext = SpringApplicationContextHelper.getApplicationContext();
                IMqMessageService mqMessageService = applicationContext.getBean(IMqMessageService.class);


//                LambdaQueryWrapper<MqMessage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//                lambdaQueryWrapper.eq(MqMessage::getMsgId, msgId);
//                mqMessageService.getOne(lambdaQueryWrapper);

                LambdaUpdateWrapper<MqMessage> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.set(MqMessage::getPublishAck, true);
                updateWrapper.eq(MqMessage::getMsgId, msgId);//条件
                mqMessageService.update(updateWrapper);

                //更新本地消息表，消息已经发送到mq
                log.info("消息 - {} 发送到交换机成功！", msgId);
                log.info("消息 - {} 发送到交换机成功！{}", msgId,"123");
            } else {
                log.info("消息 - {} 发送到交换机失败！ ", msgId);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
