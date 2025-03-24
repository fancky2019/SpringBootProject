package com.example.demo.aop.aspect.executeorder;

import com.example.demo.model.pojo.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

//@Configuration
public class PointcutExecuteOrderConfig {

    //@Bean 方法上使用 @Order 注解来控制 bean 的初始化顺序。
    @Order(202)
    @Bean(name = "pointcutExecuteOrderOneAdvisor")
    public PointcutExecuteOrderOneAdvisor pointcutExecuteOrderOneAdvisor() {
        return  new PointcutExecuteOrderOneAdvisor();
    }

    @Order(201)
    @Bean(name = "pointcutExecuteOrderTwoAdvisor")
    public PointcutExecuteOrderTwoAdvisor pointcutExecuteOrderTwoAdvisor() {
        return  new PointcutExecuteOrderTwoAdvisor();
    }
}
