package com.example.demo.config;

import com.example.demo.aop.Interceptor.SqlExecuteInterceptor;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisConfig {
    @Bean
    public SqlExecuteInterceptor sqlExecuteInterceptor() {
        return new SqlExecuteInterceptor();
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.addInterceptor(sqlExecuteInterceptor());
    }

}
