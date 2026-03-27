package com.example.demo.rabbitMQ;

import com.rabbitmq.client.ShutdownSignalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitMQConnectionListener implements ConnectionListener {

    @Override
    public void onCreate(Connection connection) {

    }

    @Override
    public void onClose(Connection connection) {
        log.error("连接关闭: {}", connection);
        // 可以获取关闭原因
    }

    @Override
    public void onShutDown(ShutdownSignalException signal) {
        log.error("连接关闭: reason={}, ref={}",
                signal.getReason(),
                signal.getReference());

        if (signal.isInitiatedByApplication()) {
            log.info("由应用主动关闭");
        } else {
            log.error("由服务端异常关闭: {}", signal.getMessage());
        }
    }

    @Override
    public void onFailed(Exception exception) {
        ConnectionListener.super.onFailed(exception);
    }
}
