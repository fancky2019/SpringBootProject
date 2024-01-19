package com.example.demo.listener;


import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
springboot 两种事件模式：
1、实现接口ApplicationListener<UserRegisterEvent>
2、@EventListener注解的方式

观察者模式 可以服务之间解耦，避免服务之间直接引用形成循环引用

 */
@Component
public class UserRegisterListener {

    /*
    先按照order 大小顺序执行，然后执行实现接口的方法
     */


    /*
       @Order(0) 数字越小越先执行
     */
    @EventListener
    @Order(0)
    public void sendMail(UserRegisterEvent event) {
        Object obj = event.getSource();
        int m = 0;
    }

    @EventListener
    @Order(1)
    public void sendCompon(UserRegisterEvent event) {

        int m = 0;
    }

    /**
     * 因为ApplicationEvent 是事件的公共基类，所有事件触发都会执行此方法。
     * 所以最好继承此基类，直接回调到具体的事件类型的Listener上。
     * @param event
     */
    @EventListener
    @Order(2)
    public void applicationEvent(ApplicationEvent event) {

        int m = 0;
    }
}
