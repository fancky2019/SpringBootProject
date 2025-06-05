package com.example.demo.listener.eventbus;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.context.ApplicationEvent;

/**
 * 添加依赖 spring-cloud-starter-bus-amqp
 * 自定义事件对象
 */
public class MyCustomEvent extends RemoteApplicationEvent {
//public class MyCustomEvent extends ApplicationEvent {
    // 必须提供无参构造
    public MyCustomEvent() {}

    public MyCustomEvent(Object source, String originService) {
        super(source, originService);
    }


//    public MyCustomEvent(Object source ) {
//        super(source);
//    }
}
