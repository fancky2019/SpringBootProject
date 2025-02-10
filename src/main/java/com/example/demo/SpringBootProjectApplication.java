package com.example.demo;


import com.example.demo.init.CommandLineImp;
import com.example.demo.model.impot.EnableSelector;
import com.example.demo.service.demo.DemoProductService;
import com.example.fanckyspringbootstarter.config.EnableFanckyStarter;
import org.redisson.spring.starter.RedissonAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/*
 springboot SpringCloud 版本对应 https://spring.io/projects/spring-cloud#overview
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
@EnableFanckyStarter

//SpringCloud ：组件
@EnableEurekaClient//注册中心
@EnableFeignClients//启用feign。微服务之间调用,服务发现
@EnableHystrixDashboard //开启HystrixDashBoard
@EnableCircuitBreaker//开启HystrixDashBoard
@EnableAspectJAutoProxy(exposeProxy = true)//开启spring注解aop配置的支持，获取当前代理对象 (PersonService) AopContext.currentProxy();
//移动到SwaggerConfig 配置文件类处理
@EnableSwagger2  //EnableSwagger2  http://localhost:8081/swagger-ui.html
@EnableRetry
public class SpringBootProjectApplication {

    /*
    @SpringBootApplication 继承多个注解

    @SpringBootConfiguration
    @EnableAutoConfiguration
    @ComponentScan
    @Filter
     */

    /* 优雅停机
    # 开启优雅停机，默认值：immediate 为立即关闭
server.shutdown=graceful

# 设置缓冲期，最大等待时间，默认：30秒
spring.lifecycle.timeout-per-shutdown-phase=60s
     */


    //    RedisAutoConfiguration re=new RedisAutoConfiguration();
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(SpringBootProjectApplication.class, args);

        /*
           populateBean(beanName, mbd, instanceWrapper);
            会生成bean
			exposedObject = initializeBean(beanName, exposedObject, mbd);

	三级缓存不是二级缓存：
如果没有AOP的话确实可以两级缓存就可以解决循环依赖的问题，如果加上AOP，两级缓存是无法解决的，不可能每次执行s
ingleFactory.getObject()方法都给我产生一个新的代理对象，所以还要借助另外一个缓存来保存产生的代理对象



			//bean 生成 执行步骤
			run 方法
			refreshContext(context);
			refresh
			registerBeanPostProcessors(beanFactory);
			PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors 里 调用beanFactory.getBean
			AbstractBeanFactory.getBean
			doGetBean:
                    1、doGetBean 里调用lambda表达式.注册工厂 ObjectFactory
                        sharedInstance = this.getSingleton(beanName, () -> {
                                try {
                                //创建bean
                                    return this.createBean(beanName, mbd, args);
                                } catch (BeansException var5) {
                                    BeansException ex = var5;
                                    this.destroySingleton(beanName);
                                    throw ex;
                                }
                            });
                    2、DefaultSingletonBeanRegistry.getSingleton
           AbstractAutowireCapableBeanFactory.createBean
          this.doCreateBean(beanName, mbdToUse, args);
             1、 instanceWrapper = this.createBeanInstance(beanName, mbd, args);  实例化
             2、 this.populateBean(beanName, mbd, instanceWrapper); //属性赋值
             //调用初始化方法，比如init-method、注入Aware对象、应用后处理器
		     3、exposedObject = initializeBean(beanName, exposedObject, mbd);


         */
        //RabbitMQConfig commandLineImp
//        Object cm= applicationContext.getBean("");
//        AnnotationConfigApplicationContext

        //region
        /*
        注册BeanDefinition : 入库run 方法内1、 context = createApplicationContext();
       2、
      	case SERVLET:
				return new AnnotationConfigServletWebServerApplicationContext();
         */
        //endregion


        // ConfigurableApplicationContext 实现beanFactory 接口，
        //beanFactory getBean 的内部实现还是通过 FactoryBean 的getObject 方法

        //region 循环依赖 三级缓存
        /*
        只能解决单例模式字段注入的循环依赖 ，无法解决构造函数和原型模式的Field依赖
          applicationContext.getBean("") 最终调用  DefaultSingletonBeanRegistry 的方法 getSingleton

  bean 实例化--》初始化。


          三个map 缓存:第三季缓存解决动态代理问题：返回代理对象
           判断该Bean是否需要被动态代理，两种返回结果：
                不需要代理，返回未属性注入、未初始化的半成品Bean
                 需要代理，返回未属性注入、未初始化的半成品Bean的代理对象

     1、
         Cache of singleton objects: bean name to bean instance.
        private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
3、
         Cache of singleton factories: bean name to ObjectFactory.
        private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
2、
         Cache of early singleton objects: bean name to bean instance.
        private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

一级缓存：singletonObject  存放已经经历了完整周期的Bean对象
二级缓存：earlySingletonObjects 存放早期暴露出来的Bean对象，Bean的生命周期未结束
三级缓存：singletonFactories 存放可以生成Bean的工厂
  private final Map<String, Object> singletonObjects = new ConcurrentHashMap(256);
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap(16);
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap(16);
         */

        //CommandLineImp.class.getName() 不能用这种全路径名
        //CommandLineImp
        String name = CommandLineImp.class.getSimpleName();
        Object obj = applicationContext.getBean("demoProductService");
        int m = 0;
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
