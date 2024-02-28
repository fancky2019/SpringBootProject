package com.example.demo;


import com.example.demo.model.impot.EnableSelector;
import com.example.fanckyspringbootstarter.config.EnableFanckyStarter;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/*
 springboot springcloud 版本对应 https://spring.io/projects/spring-cloud#overview
 */




















//默认扫描的是启动类所在的包及其子包
//由于采用多数据源，禁用springboot默认的数据源配置，多数据源不适合微服务设计理念废弃。采用分布式事务。
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication(exclude = {RedissonAutoConfiguration.class})//不排除， redis 将采用redisson
//@SpringBootApplication

//@MapperScan("com.example.demo.dao")
//类不在主目录下需要指定扫描类完全限定名同时要加主类路径
//@ComponentScan(basePackages = {"com.example.fanckyspringbootstarter",
//                               "com.example.demo"})

@EnableCaching   //redis
@EnableScheduling //quartz
@EnableRabbit     //EnableRabbit
@EnableAsync     //启用异步
//@EnableSelector
//@EnableFanckyStarter

//SpringCloud ：组件
@EnableEurekaClient//注册中心
@EnableFeignClients//启用feign。微服务之间调用,服务发现
@EnableHystrixDashboard //开启HystrixDashBoard
@EnableCircuitBreaker//开启HystrixDashBoard
@EnableAspectJAutoProxy(exposeProxy = true)//开启spring注解aop配置的支持，获取当前代理对象 (PersonService) AopContext.currentProxy();
//移动到SwaggerConfig 配置文件类处理
//@EnableSwagger2  //EnableSwagger2  http://localhost:8081/swagger-ui.html
@EnableRetry
public class SpringBootProjectApplication {

    /*
    @SpringBootApplication 继承多个注解

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan
    @Filter
     */


    //    RedisAutoConfiguration re=new RedisAutoConfiguration();
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext=
        SpringApplication.run(SpringBootProjectApplication.class, args);

        // ConfigurableApplicationContext 实现beanFactory 接口，
        //beanFactory getBean 的内部实现还是通过 FactoryBean 的getObject 方法

        //region 循环依赖
        /*
        只能解决单例模式字段注入的循环依赖 ，无法解决构造函数和原型模式的Field依赖
          applicationContext.getBean("") 最终调用  DefaultSingletonBeanRegistry 的方法 getSingleton

          三个map 缓存:

         Cache of singleton objects: bean name to bean instance.
        private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

         Cache of singleton factories: bean name to ObjectFactory.
        private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

         Cache of early singleton objects: bean name to bean instance.
        private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);


         */

//        applicationContext.getBean("");
//        AbstractAutowireCapableBeanFactory#doCreateBean()中：
        //endregion


        //applicationContext.getBean("factoryBeanIml"); 获取到到的是factoryBeanIml 实现类对象
        //applicationContext.getBean("&factoryBeanIml");获取到到的是factoryBeanIml 实现类里getObject
        //方法的返回对象

//        SpringApplication application = new SpringApplication(Application.class);
//        // 添加 日志监听器，使 log4j2-spring.xml 可以间接读取到配置文件的属性
//        application.addListeners(new LoggingListener());
//        application.run(args);
    }
}
