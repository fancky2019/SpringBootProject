package com.example.demo.eventbus;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 监听事件
 *
 * 如果mq 服务停止了，消息不会写入mq，可以监听到，分布式就监听不到了
 * 如果没有订阅：消息好像会自动ack 掉。在rabbitmq 管理页面没有看到消息
 *
 * binding key中可以存在两种特殊字符 * 与 # ，用于做模糊匹配，
 * 其中 * 用于匹配一个单词， # 用于匹配多个单词（可以是零个）
 */
@Component
public class MyEventListener {

    //TransactionSynchronizationManager 事务成功之后发送
    @TransactionalEventListener //事务成功之后发送
//    @EventListener  // 事务不成功也会检测到发送消息
    public void handleMyEvent(MyCustomEvent event) {
        System.out.println("Received custom event: " + event);
    }
}

