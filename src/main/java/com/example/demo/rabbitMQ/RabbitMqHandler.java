package com.example.demo.rabbitMQ;

import com.rabbitmq.client.Channel;

/**
 * @author ruili
 */
@FunctionalInterface
public interface RabbitMqHandler<T> {
    /**
     * 处理任务回调
     *
     * @param mqMsg
     * @param channel
     */
    void handle(T mqMsg, Channel channel);
}
