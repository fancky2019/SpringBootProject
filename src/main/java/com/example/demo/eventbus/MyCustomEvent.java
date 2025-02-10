package com.example.demo.eventbus;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.context.ApplicationEvent;

/**
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
