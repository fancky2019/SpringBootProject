package com.example.demo.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/*
配置就使用springboot 的redis 的配置。
文档：https://github.com/redisson/redisson/tree/master/redisson-spring-boot-starter
 */
//@Configuration
public class RedissonConfig {
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() throws IOException {
        //1、创建配置
//        Config config = new Config();
//        config.useSingleServer()
//                .setAddress("127.0.0.1:6379")
//                .setPassword("fancky123456");
//        return Redisson.create(config);

        //默认数据库index:0
        //redisson-->connectionManager-->config-->database
        Config config = new Config();
        ((SingleServerConfig) config.useSingleServer().setTimeout(1000000))
                .setAddress("redis://127.0.0.1:6379")
                .setPassword("fancky123456");


//        // connects to 127.0.0.1:6379 by default
        return Redisson.create(config);
    }
}
