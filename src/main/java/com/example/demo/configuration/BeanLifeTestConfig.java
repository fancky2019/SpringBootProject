package com.example.demo.configuration;

import com.example.demo.model.entity.demo.Person;
import com.example.demo.model.pojo.BeanLife;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanLifeTestConfig {
    /**
     * 指定initMethod和destroyMethod 在BeanLife内的方法
     * @return
     */
//    @Bean(initMethod = "init", destroyMethod = "destroy")
    @Bean
    public BeanLife beanLife() {
        return new BeanLife();
    }
}
