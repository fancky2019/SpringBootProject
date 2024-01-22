package com.example.demo.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

/**
 * 事件参数类
 *
 *
 * springboot  事件,本质就是回调，就像C#一样。简化编程用函数接口
 * 1、事件参数 继承自ApplicationEvent，内部object 保存事件对象
 * 2、注册事件添加Listener  a、实现ApplicationListener<T> 接口，T为时间参数。
 *                       b、加@EventListener注解的方法，方法参数为
 *   使用注解的方式代码简单点
 * 3、在事件触发地方调用 ApplicationEventPublisher  publishEvent() 方法
 */
public class UserRegisterEvent extends ApplicationEvent {

    private String userName;

    public UserRegisterEvent(Object source) {
        super(source);
//        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}

