package com.example.demo;


import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;



/*
 springboot springcloud 版本对应 https://spring.io/projects/spring-cloud#overview
 */




















//默认扫描的是启动类所在的包及其子包
//由于采用多数据源，禁用springboot默认的数据源配置，多数据源不适合微服务设计理念废弃。采用分布式事务。
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication(exclude = {RedissonAutoConfiguration.class})//不排除， redis 将采用redisson
//@SpringBootApplication

//@MapperScan("com.example.demo.dao")
//类不在主目录下需要指定扫描类完全限定名同时要加主类路径
@ComponentScan(basePackages = {"com.example.fanckyspringbootstarter",
                               "com.example.demo"})

@EnableCaching   //redis
@EnableScheduling //quartz
@EnableRabbit     //EnableRabbit
@EnableAsync     //启用异步


//SpringCloud ：组件
@EnableEurekaClient//注册中心
@EnableFeignClients//启用feign。微服务之间调用,服务发现
@EnableHystrixDashboard //开启HystrixDashBoard
@EnableCircuitBreaker//开启HystrixDashBoard
@EnableAspectJAutoProxy(exposeProxy = true)//开启spring注解aop配置的支持
//移动到SwaggerConfig 配置文件类处理
//@EnableSwagger2  //EnableSwagger2  http://localhost:8081/swagger-ui.html
public class SpringBootProjectApplication {

    /*
    @SpringBootApplication 继承多个注解

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan
    @Filter
     */



    public static void main(String[] args) {
        SpringApplication.run(SpringBootProjectApplication.class, args);
    }
}
