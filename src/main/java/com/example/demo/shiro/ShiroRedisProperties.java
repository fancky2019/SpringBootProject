package com.example.demo.shiro;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.redis")
@Data
public class ShiroRedisProperties {
//    @Value("${spring.redis.host}")
//    private String host = "127.0.0.1";
//
//    @Value("${spring.redis.port}")
//
//    private int port = 6379;
//    @Value("${spring.redis.timeout}")
//    private int timeout;
//
//    @Value("${spring.redis.password}")
//    private String password = "fancky123456";

    private String host ;
    private int port ;
    private int timeout;
    private String password;
}
