package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.example.demo.dao")
//@ComponentScan(basePackages = {"com.example.demo.service"})
//@ComponentScan({"com.operations.project.seed","com.operations.project.message.server"})
@EnableCaching   //redis
@EnableScheduling //quartz
@EnableRabbit     //EnableRabbit
@EnableAsync     //启用异步
@EnableSwagger2  //EnableSwagger2  http://localhost:8080/swagger-ui.html
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
