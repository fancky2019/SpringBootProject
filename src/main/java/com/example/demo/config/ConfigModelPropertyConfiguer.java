package com.example.demo.config;


import org.springframework.boot.autoconfigure.amqp.RabbitProperties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * //EnableConfigurationProperties 或 Component 或 Configuration
 * //@Component
 * //需要在配置类上加启用注解一般和 Import 搭配使用，将配置类加入IOC
 * //@EnableConfigurationProperties(ConfigModelProperty.class)
 * //如果不配置，不报错，对象没有值
 *
 *Configuration: 内部@bean 声明
 *Configuer: 内部负责配置bean
 */
@Configuration
//通过
//@EnableConfigurationProperties({ConfigModelProperty.class})
//@Import({RabbitAnnotationDrivenConfiguration.class, RabbitStreamConfiguration.class})
public class ConfigModelPropertyConfiguer {
}
