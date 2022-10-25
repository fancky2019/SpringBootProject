package com.example.demo.config;

import com.example.demo.model.pojo.BeanLife;
import com.example.demo.model.pojo.SpringLifeCycleBean;
import com.example.demo.shiro.ShiroRedisProperties;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanLifeTestConfig {

    //@ConfigurationProperties(prefix = "library")  读取并与 bean 绑定。

    /**
     * 指定initMethod和destroyMethod 在BeanLife内的方法
     * @return
     */
//    @Bean(initMethod = "init", destroyMethod = "destroy")
    @Bean
//    @Bean(name="beanLife") //默认方法名称
    public BeanLife beanLife() {
        return new BeanLife();
    }


    @Bean(destroyMethod = "customDestroy", initMethod = "customInit")
    public SpringLifeCycleBean lifeCycleBean(){
        SpringLifeCycleBean lifeCycleBean = new SpringLifeCycleBean();
        return lifeCycleBean;
    }

    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }
}
