package com.example.demo.model.pojo;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * https://blog.csdn.net/qinwuxian19891211/article/details/109004197?utm_medium=distribute.wap_relevant.none-task-blog-2~default~baidujs_baidulandingword~default-4-109004197-blog-104455686.wap_blog_relevant_default&spm=1001.2101.3001.4242.3&utm_relevant_index=5

执行结果：
 Constructor method invoked
 BeanNameAware setBeanName method inovked, name: lifeCycleBean
 BeanFactoryAware setBeanFactory method inovked, beanFactory: org.springframework.beans.factory.support.DefaultListableBeanFactory
 ApplicationContextAware setApplicationContext method inovked, applicationContext: org.springframework.web.context.support.GenericWebApplicationContext
 PostConstruct method invoked
 InitializingBean afterPropertiesSet method inovked
 customInit method invoked
---postProcessBeforeInitialization 没有执行 不知道为什么
 PreDestroy method invoked
 DisposableBean destroy method invoked
 customDestroy method invoked
 */
public class SpringLifeCycleBean implements BeanNameAware, BeanFactoryAware, ApplicationContextAware, BeanPostProcessor, InitializingBean, DisposableBean {

    /**
     * 构造函数执行完就有值
     */
    @Value("${demo.multiEnvironment}")
    private String multiEnvironment;

    public SpringLifeCycleBean() {
        System.out.println("Constructor method invoked");
    }


    @Override
    public void setBeanName(String s) {
        System.out.println("BeanNameAware setBeanName method invoked, name: " + s);
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("BeanFactoryAware setBeanFactory method invoked, beanFactory: " + beanFactory.getClass().getName());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("ApplicationContextAware setApplicationContext method invoked, applicationContext: " + applicationContext.getClass().getName());
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("lifeCycleBean")) {
            System.out.println("BeanPostProcessor postProcessBeforeInitialization method invoked, beanName: " + beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.equals("lifeCycleBean")) {
            System.out.println("BeanPostProcessor postProcessAfterInitialization method invoked, beanName: " + beanName);
        }

      return bean;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("InitializingBean afterPropertiesSet method invoked");

    }
    //region 类中声明注解    @PreDestroy
    @PostConstruct
    public void init() {
        System.out.println("PostConstruct method invoked");
    }


    @PreDestroy
    public void preDestroy() {
        System.out.println("PreDestroy method invoked");
    }
    //endregion

    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBean destroy method invoked");
    }


    //region 声明bean 配置时候使用
    public void customInit() {
        System.out.println("customInit method invoked");
    }

    public void customDestroy() {
        System.out.println("customDestroy method invoked");
    }
 //endregion

}

