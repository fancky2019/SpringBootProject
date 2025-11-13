package com.example.demo.listener.eventbus;

import lombok.Data;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;
import org.springframework.context.ApplicationEvent;

/**
 * 添加依赖 spring-cloud-starter-bus-amqp
 * 自定义事件对象
 */
public class MyCustomEvent extends RemoteApplicationEvent {
    private  String msg;
//public class MyCustomEvent extends ApplicationEvent {
    // 必须提供无参构造
    public MyCustomEvent() {
        // 反序列化需要默认构造函数
        super();
    }



    public MyCustomEvent(Object source, String originService,String msg) {
        super(source, originService);
        this.msg=msg;
    }


    //不能加@Data ，否则 必须添加 getter 和 setter（Jackson 序列化需要）.不实现getter 和 setter 无法发送到rabbitmq
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
