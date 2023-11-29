package com.example.demo.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class SendEmailListener implements ApplicationListener<UserRegisterEvent> {

//    @Override
//    public void onApplicationEvent(UserRegisterEvent event) {
//        System.out.println(String.format("给用户【%s】发送注册成功邮件!", event.getUserName()));
//
//    }

    @Override
    public void onApplicationEvent(UserRegisterEvent event) {
       Object obj= event.getSource();
       int m=0;
    }
}

