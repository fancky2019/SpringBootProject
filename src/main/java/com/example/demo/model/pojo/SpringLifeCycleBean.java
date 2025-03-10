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



 BeanNameAware: 设置bean 名称。bean 在springboot 容器中名称

 接口名称	作用
 BeanNameAware	            获取 Bean 的名称
 ApplicationContextAware	获取 Spring 容器（ApplicationContext）
 BeanFactoryAware	        获取 Bean 工厂（BeanFactory）
 EnvironmentAware	        获取环境配置（Environment）
 ResourceLoaderAware	    获取资源加载器（ResourceLoader）
 BeanNameAware 的执行时机

 BeanNameAware 的 setBeanName() 方法在以下时机被调用：
 在 Bean 的实例化之后：Spring 容器会先实例化 Bean。
 在依赖注入之前：setBeanName() 方法的调用早于依赖注入（即属性设置）。
 在 BeanPostProcessor 的 postProcessBeforeInitialization 之前：setBeanName() 方法的调用早于 BeanPostProcessor 的前置处理。




 BeanFactoryAware : 获取 Spring 容器中的 BeanFactory,然后通过BeanFactory 获取bean
 import org.springframework.beans.BeansException;
 import org.springframework.beans.factory.BeanFactory;
 import org.springframework.beans.factory.BeanFactoryAware;
 import org.springframework.stereotype.Component;

 @Component
 public class MyBean implements BeanFactoryAware {

 private BeanFactory beanFactory;

 @Override
 public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
 this.beanFactory = beanFactory; // 保存 BeanFactory 实例
 System.out.println("BeanFactoryAware: BeanFactory 已注入");
 }

 public void useBeanFactory() {
 // 使用 BeanFactory 获取其他 Bean
 AnotherBean anotherBean = beanFactory.getBean(AnotherBean.class);
 anotherBean.doSomething();
 }













 postProcessBeforeInitialization：在 Bean 的初始化方法（如 @PostConstruct、InitializingBean 的 afterPropertiesSet 方法）之前调用。
 postProcessAfterInitialization：在 Bean 的初始化方法之后调用。

 InitializingBean:
 Spring 容器会在 Bean 的属性设置完成后（即依赖注入完成后），调用其 afterPropertiesSet() 方法。
 推荐使用 @PostConstruct 注解来实现初始化逻辑。






 . 执行顺序
 以下是这些接口和扩展点的执行顺序：

 Bean 实例化：Spring 容器通过构造函数或工厂方法创建 Bean 的实例。

 BeanNameAware.setBeanName()：如果 Bean 实现了 BeanNameAware 接口，Spring 容器会调用 setBeanName() 方法，将 Bean 的名称注入到 Bean 中。

 BeanFactoryAware.setBeanFactory()：如果 Bean 实现了 BeanFactoryAware 接口，Spring 容器会调用 setBeanFactory() 方法，将 BeanFactory 注入到 Bean 中。

 ApplicationContextAware.setApplicationContext()：如果 Bean 实现了 ApplicationContextAware 接口，Spring 容器会调用 setApplicationContext() 方法，将 ApplicationContext 注入到 Bean 中。

 BeanPostProcessor.postProcessBeforeInitialization()：如果 Spring 容器中注册了 BeanPostProcessor，则会调用其 postProcessBeforeInitialization() 方法，在 Bean 初始化之前执行自定义逻辑。

 InitializingBean.afterPropertiesSet()：如果 Bean 实现了 InitializingBean 接口，Spring 容器会调用 afterPropertiesSet() 方法，执行初始化逻辑。

 自定义初始化方法（如 @PostConstruct 或 init-method）：如果 Bean 定义了其他初始化方法（如 @PostConstruct 注解或 XML 配置中的 init-method），Spring 容器会调用这些方法。

 BeanPostProcessor.postProcessAfterInitialization()：如果 Spring 容器中注册了 BeanPostProcessor，则会调用其 postProcessAfterInitialization() 方法，在 Bean 初始化之后执行自定义逻辑。

 */
public class SpringLifeCycleBean implements  BeanNameAware, BeanFactoryAware, ApplicationContextAware, BeanPostProcessor, InitializingBean, DisposableBean {

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

