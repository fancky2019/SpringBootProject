package com.example.demo.service.circularreference;

import com.example.demo.listener.eventbus.CustomEvent;
import com.example.demo.model.entity.demo.MqMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.bus.BusProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.swing.*;

@Lazy
@Service
public class ServiceA implements IService {

    /*@Lazy 注解的主要作用是延迟初始化，告诉 Spring 容器在第一次真正使用时才创建 Bean 实例。
    启动时：
    Spring 会为 @Lazy Bean 创建一个代理对象放入容器
    使用时：当首次调用代理对象的方法时，才会创建真实对象
    容器中：一直有代理对象，真实对象按需创建
    性能：加快启动速度，但首次调用有额外开销
    */

    // 代理对象，首次调用时才初始化真实对象
//    @Autowired
//    @Lazy
    private ServiceB serviceB;

    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private BusProperties busProperties;
//    @Autowired
    //    @Lazy
//    public void setServiceA(ServiceA serviceA) {
//        this.serviceA = serviceA;
//    }

//    @Autowired
    //    @Lazy
//    public ServiceA(@Lazy ServiceB serviceB) {
//        this.serviceB = serviceB;
//    }


    @PostConstruct
    public void init() {
        //@Lazy 注解时，Spring 会创建一个代理对象来延迟初始化
        //字段 即使被 @Lazy 注入，这个方法仍可能在启动时执行
        //要在类上加@Lazy 注解才会在启动时候不执行此初始化方法
        int n = 0;
    }


    public void methodA() {
        System.out.println("ServiceA method");
//        serviceB.methodB();
//        publisher.publishEvent(event);
        // 获取当前服务的ID springBootProject
        String serviceId = applicationContext.getId();
        // 或使用 busProperties.getId()
        // String serviceId = busProperties.getId();

        // 获取当前服务实例ID（通常与busProperties.getId()相同）  springBootProject:8088:f89b296d4ca5589865e70da7de918722
//        String originService = busProperties.getId();

        MqMessage mqMessage = new MqMessage();
        mqMessage.setBusinessId(1L);
        CustomEvent event = new CustomEvent(this, serviceId, mqMessage);
        //最好使用本地消息表
        //发送消息的时候可能崩溃，不能保证消息被消费。如果发送成功了，还要设计消息表兜底失败的消息
//        MyCustomEvent event = new MyCustomEvent(busProperties.getId());


        publisher.publishEvent(event);
    }

    @Override
    public void execute() {
        System.out.println("ServiceA executing");

    }
}
