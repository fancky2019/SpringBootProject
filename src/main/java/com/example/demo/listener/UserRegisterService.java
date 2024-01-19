package com.example.demo.listener;

import com.example.demo.listener.UserRegisterEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

@Component
//public class UserRegisterService implements ApplicationEventPublisherAware {
public class UserRegisterService{

//        private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    /**
     * 负责用户注册及发布事件的功能
     *
     * @param userName 用户名
     */
    public void registerUser(String userName) {

        //用户注册(将用户信息入库等操作)
        System.out.println(String.format("用户【%s】注册成功", userName));
        //发布注册成功事件
//        this.applicationEventPublisher.publishEvent(new UserRegisterEvent(this, userName));
//        this.applicationEventPublisher.publishEvent(new ApplicationEvent(userName){});
        //可以设计成具体实现类,避免其他类事件触发
        this.applicationEventPublisher.publishEvent(new UserRegisterEvent(userName));
    }

//    @Override
//    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) { //@1
//        this.applicationEventPublisher = applicationEventPublisher;
//    }
}
