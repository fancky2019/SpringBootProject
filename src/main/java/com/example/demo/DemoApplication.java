package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

//由于采用多数据源，禁用springboot默认的数据源配置
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
//@SpringBootApplication
@MapperScan("com.example.demo.dao")
//@ComponentScan(basePackages = {"com.example.demo.service"})
//@ComponentScan({"com.operations.project.seed","com.operations.project.message.server"})
@EnableCaching   //redis
@EnableScheduling //quartz
@EnableRabbit     //EnableRabbit
@EnableAsync     //启用异步
@EnableEurekaClient//注册中心
@EnableFeignClients//微服务之间调用

@EnableHystrixDashboard //开启HystrixDashBoard
@EnableCircuitBreaker//开启HystrixDashBoard

@EnableSwagger2  //EnableSwagger2  http://localhost:8081/swagger-ui.html
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
