package com.example.demo.listener;


import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/*
springboot 两种事件模式：
1、实现接口ApplicationListener<UserRegisterEvent>
2、@EventListener注解的方式


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
}
