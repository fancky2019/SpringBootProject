package com.example.demo.shiro;

import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LifecycleBeanPostProcessorConfiguration {


    //LifecycleBeanPostProcessor 会造成autowire bean  无法注入，不知道原因
    //从shiroConfiguration 中分离
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
