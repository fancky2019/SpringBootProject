package com.example.demo.service.demo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.demo.dao.demo.MqMessageMapper;
import com.example.demo.model.entity.demo.MqMessage;
import com.example.demo.rabbitMQ.RabbitMQConfig;
import com.example.demo.service.demo.IMqMessageService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.utility.MqSendUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author author
 * @since 2023-11-15
 */
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements IMqMessageService {

    @Autowired
    private MqSendUtil mqSendUtil;

    /**
     * 生产失败的重新发布到mq，消费失败的
     */
    @Override
    public void mqOperation() {
        LambdaQueryWrapper<MqMessage> queryWrapper = new LambdaQueryWrapper<>();
        //没有消费确认=发布失败+消费失败
        queryWrapper.eq(MqMessage::getConsumeAck, false);
        List<MqMessage> mqMessageList = this.list(queryWrapper);
        List<MqMessage> unPushList = mqMessageList.stream().filter(p -> !p.getPublishAck()).collect(Collectors.toList());
        //可设计单独的job 处理消费失败
        List<MqMessage> consumerFailList = mqMessageList.stream().filter(p -> p.getPublishAck() && p.getConsumeFail()).collect(Collectors.toList());
        rePublish(unPushList);
        reConsume(consumerFailList);
    }

    private void rePublish(List<MqMessage> mqMessageList) {
        CompletableFuture.runAsync(() ->
        {
            publish(mqMessageList);
        });

    }

    private synchronized void publish(List<MqMessage> mqMessageList) {
        try {
            for (MqMessage message : mqMessageList) {
                mqSendUtil.send(message);
            }
        } catch (Exception ex) {
            log.error("", ex);
        }
    }

    private void reConsume(List<MqMessage> mqMessageList) {
        CompletableFuture.runAsync(() ->
        {
            consume(mqMessageList);
        });

    }

    private synchronized void consume(List<MqMessage> mqMessageList) {
        try {
            for (MqMessage message : mqMessageList) {
                switch (message.getQueue()) {
                    case RabbitMQConfig.BATCH_DIRECT_QUEUE_NAME:
                        //执行对应的service 消费代码
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception ex) {
            log.error("", ex);
        }
    }
}
