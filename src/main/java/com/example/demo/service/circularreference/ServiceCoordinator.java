package com.example.demo.service.circularreference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
使用观察者模式
 */
@Component
public class ServiceCoordinator {
    // 使用 @Lazy 延迟注入
    @Lazy
    @Autowired
    private ServiceA serviceA;

    @Lazy
    @Autowired
    private ServiceB serviceB;

    public void doWork() {
        // 按需调用

        serviceA.methodA();
        serviceB.methodB();

    }

}
