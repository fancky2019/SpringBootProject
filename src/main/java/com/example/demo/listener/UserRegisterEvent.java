package com.example.demo.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;


public class UserRegisterEvent extends ApplicationEvent {
    //用户名
    private String userName;

    public UserRegisterEvent(Object source) {
        super(source);
//        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}

