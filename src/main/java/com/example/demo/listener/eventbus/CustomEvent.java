package com.example.demo.listener.eventbus;

import com.example.demo.model.entity.demo.MqMessage;
import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 *  添加依赖 spring-cloud-starter-bus-amqp
 *  自定义事件对象
 * RemoteApplicationEvent springCloud bus会落rabbitmq
 */
public class CustomEvent extends RemoteApplicationEvent {
    private MqMessage msg;
    //public class MyCustomEvent extends ApplicationEvent {
    // 必须提供无参构造
    public CustomEvent() {
        // 反序列化需要默认构造函数
//        super();
    }



    public CustomEvent(Object source, String originService,MqMessage msg) {
        super(source, originService);
        this.msg=msg;
    }


    //不能加@Data ，否则 必须添加 getter 和 setter（Jackson 序列化需要）.不实现getter 和 setter 无法发送到rabbitmq
    public MqMessage getMsg() {
        return msg;
    }

    public void setMsg(MqMessage msg) {
        this.msg = msg;
    }
}
