package com.example.demo.listener.eventbus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 监听事件
 *默认使用 fanout 交换机，默认情况下，所有服务实例都会监听同一个 topic（springCloudBus），并且消息会广播给所有实例
 *
 *spring-cloud-starter-stream-rabbit:Spring Cloud Stream 默认使用 direct 交换机
 *
 * 如果mq 服务停止了，消息不会写入mq，可以监听到，分布式就监听不到了
 * 如果没有订阅：消息好像会自动ack 掉。在rabbitmq 管理页面没有看到消息
 *
 * binding key中可以存在两种特殊字符 * 与 # ，用于做模糊匹配，
 * 其中 * 用于匹配一个单词， # 用于匹配多个单词（可以是零个）
 */
@Slf4j
@Component
public class MyEventListener {

//    @Async("threadPoolExecutor") //使用异步和调用线程不在一个线程内
    //TransactionSynchronizationManager 事务成功之后发送
    @TransactionalEventListener //默认事务成功之后发送
//    @TransactionalEventListener  (phase = TransactionPhase.AFTER_COMMIT)
//    @EventListener  // 事务不成功也会检测到发送消息
    public void handleMyEvent(MyCustomEvent event) {
        //此处简单设计，失败了落表重试处理。或者重新设计本地消息表
        //ApplicationEventPublisher eventPublisher;
        //  eventPublisher.publishEvent(event);
        System.out.println("Received custom event: " + event);
    }


    //multiplier 2 ,每次重试时间间隔翻倍
    @Async("threadPoolExecutor")
    @EventListener
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void handleInventoryEvent(MyCustomEvent event) {
//        log.info("开始异步处理库存事件: {}", event.getInventoryItemDetailId());

        // 业务处理逻辑


        // 模拟业务处理
//        processBusinessLogic(event);

        log.info("异步处理库存事件完成: {}", event);
    }

    // 重试耗尽后的处理
    @Async("threadPoolExecutor")
    @EventListener
    public void handleInventoryEventFallback(MyCustomEvent event) {
        try {
            handleInventoryEvent(event);
        } catch (Exception e) {
            // 重试失败后的最终处理,将消息落到消息表

        }
    }
}

