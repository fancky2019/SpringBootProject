package com.example.demo.rabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ShutdownSignalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.ChannelListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitMQChannelListener implements ChannelListener {

    @Override
    public void onCreate(Channel channel, boolean transactional) {
        log.debug("Channel 创建: {}, 事务模式: {}", channel.getChannelNumber(), transactional);
    }

    @Override
    public void onShutDown(ShutdownSignalException signal) {
        // ✅ 这里能捕获到 Channel 关闭的详细原因！
        log.error("Channel 关闭！");
        log.error("关闭原因: {}", signal.getReason());
        log.error("是否由应用发起: {}", signal.isInitiatedByApplication());
        log.error("参考信息: {}", signal.getReference());

        // 存储错误信息供 ConfirmCallback 使用
        if (signal.getReason() != null) {
//            ChannelErrorHolder.setError(signal.getReason());
        }

//        // 根据错误类型判断
//        String reason = signal.getReason();
//        if (reason != null) {
//            if (reason.contains("NOT_FOUND")) {
//                log.error("⚠️ 交换机或队列不存在！");
//            } else if (reason.contains("ACCESS_REFUSED")) {
//                log.error("⚠️ 访问被拒绝！");
//            } else if (reason.contains("PRECONDITION_FAILED")) {
//                log.error("⚠️ 前置条件失败！");
//            }
//        }
    }
}
