package com.example.demo.model.pojo;

import com.example.demo.controller.UtilityController;
import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * SpringLifeCycleBean
 * 测试方法在UtilityControllerTest
 */

//public class BeanLife {
public class BeanLife implements InitializingBean, DisposableBean {
    //Lombox 的注解 @Slf4j 相当于下面语句
    private static final Logger logger = LogManager.getLogger(BeanLife.class);
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        logger.info("BeanLife set property");
    }

    /**
     * 指定指定initMethod和destroyMethod的执行顺序：BeanLife constructor-->BeanLife init-->BeanLife destroy
     */
    public BeanLife() {
        logger.info("BeanLife constructor");
    }

    //  @Bean(initMethod = "init", destroyMethod = "destroy")
    public void init() {
        logger.info("BeanLife init");
    }


    @PostConstruct
    public void initPostConstruct(){
        System.out.println("PostConstruct method invoked");
    }


    @PreDestroy
    public void preDestroy(){
        System.out.println("PreDestroy method invoked");
    }
    @Override
    public void destroy() {
        logger.info("BeanLife destroy");
    }

    /*
    通过实现接口InitializingBean, DisposableBean
    执行顺序：constructor-->afterPropertiesSet--> destroy
     */

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("BeanLife afterPropertiesSet");
    }
}
