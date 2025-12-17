package com.example.demo.service.circularreference;

import com.example.demo.listener.eventbus.CustomEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ServiceB {

    @Autowired
    @Lazy
    private ServiceA serviceA;

    //使用接口无法解决循环依赖
//    @Autowired
//    private IService serviceAProxy;

//    @Autowired
//    @Lazy
//    public void setServiceB(ServiceA serviceA) {  // Setter注入
//        this.serviceA = serviceA;  // 此时ServiceB可能已完全初始化
//    }

//    public ServiceB( @Lazy ServiceA serviceA) {
//        this.serviceA = serviceA;
//    }

//    @Autowired
//    public void setServiceProxy(IService serviceProxy) {
//        this.serviceAProxy = serviceProxy;
//    }

    public void methodB() {
        System.out.println("ServiceB method");
        serviceA.methodA();
//        serviceAProxy.execute();
    }

    //在ServiceA 中发布
    //使用事件驱动
    @EventListener
    public void handleEvent(CustomEvent event) {
        System.out.println("ServiceB received: " + event.getMsg().getBusinessId());
    }

}
