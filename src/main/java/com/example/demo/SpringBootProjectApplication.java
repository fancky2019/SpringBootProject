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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 springboot SpringCloud 版本对应 https://spring.io/projects/spring-cloud#overview




 ShiroConfiguration:defaultAdvisorAutoProxyCreator指定了 CGLIB代理。 defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
 # application.yml
 spring:
 aop:
 proxy-target-class: true
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
@EnableRabbit     //EnableRabbit 添加 spring-boot-starter-amqp  依赖，默认是自动开启的
//@EnableAsync     //启用异步,在 ThreadPoolExecutorConfig 上配置

//@EnableSelector
@EnableFanckyStarter

//SpringCloud ：组件
@EnableEurekaClient//注册中心
@EnableFeignClients//启用feign。微服务之间调用,服务发现
@EnableHystrixDashboard //开启HystrixDashBoard
@EnableCircuitBreaker//开启HystrixDashBoard
@EnableAspectJAutoProxy(exposeProxy = true)//开启spring注解aop配置的支持，获取当前代理对象 (PersonService) AopContext.currentProxy();
//@EnableAspectJAutoProxy(exposeProxy = true,
//        proxyTargetClass = true)//强制使用 CGLIB 代理)//开启spring注解aop配置的支持，获取当前代理对象 (PersonService) AopContext.currentProxy();

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

    //region 配置的动态刷新 参见 com.example.demo.config
    /*
    使用@RefreshScope注解
    对于使用Spring Boot但不依赖于Spring Cloud Config的应用，你可以通过@RefreshScope注解来使bean支持配置的动态刷新

    curl -X PosT http://localhost:8080/actuator/refresh
    */
    //endregion



    //    RedisAutoConfiguration re=new RedisAutoConfiguration();
    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext =
                SpringApplication.run(SpringBootProjectApplication.class, args);

        /*
           populateBean(beanName, mbd, instanceWrapper);
            会生成bean
			exposedObject = initializeBean(beanName, exposedObject, mbd);

// 这个lambda表达式就是放入三级缓存的ObjectFactory
addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));

原始对象、代理对象：代理对象要代理原始对象

动态代理提前创建：
在正常的非循环依赖流程中，这个工厂可能永远不会被调用。Bean会按部就班地走完属性填充、初始化，然后在初始化后阶段由 AbstractAutoProxyCreator 正常生成代理。
但是，一旦发生循环依赖（比如B需要A），“提前触发”的开关就被按下了：

正常流程（无循环依赖）： AOP处理器在“初始化后”阶段才工作。
“提前触发”流程（有循环依赖）： AOP处理器在B需要A的这一刻就被调用了。

初始化后 (postProcessAfterInitialization) - AOP代理在此阶段创建
三级缓存通过 ObjectFactory 将代理的生成时机，从“固定的初始化后阶段”动态地提前到“被其他Bean需要的那个瞬间”。

代理是在由Spring生命周期中的“初始化后”阶段创建，在spring 属性赋值之后创建，二级缓存拿到的是没有代理的半成品

二级缓存只能解决普通对象的循环依赖，而三级缓存通过引入“对象工厂”解决了 AOP 代理对象在循环依赖场景下的正确生成与注入问题。
	三级缓存不是二级缓存：
三级缓存引入了 singletonFactories（对象工厂），存储一个能生成对象（原始或代理）的工厂方法，延迟到真正需要时才生成。
解决 AOP 代理对象的循环依赖：通过 singletonFactories 存储的 ObjectFactory，在循环依赖发生时提前生成代理对象，
保证相互依赖的 Bean 拿到的都是增强后的代理，而非原始对象，从而避免事务、日志等 AOP 功能失效。



问题	                     两级缓存的答案	                                  三级缓存的答案
代理由谁创建？	             由Spring生命周期中的“初始化后”阶段创建。  	          同上。但三级缓存提供了“按需提前创建”的机制。
二级缓存存什么？	         存原始对象（半成品）                  。	         在三级缓存方案中，二级缓存用于存放提前生成的代理对象（早期单例）。
为什么两级缓存失败？	     因为代理的创建时机（初始化阶段）                      三级缓存中的ObjectFactory允许在循环依赖发生的时刻（即其他Bean需要它的时候），
                                                                          提前触发代理的生成，保证了所有依赖方拿到的都是最终形态（原始或代理）。
                          晚于其他Bean进行属性填充的时机
                       。导致其他Bean拿到的是原始对象，而不是最终的代理。


一句话总结二级缓存只是个仓库，它不创建代理。代理在初始化阶段创建，这个时机对解决带AOP的循环依赖来说太晚了。	三级缓存通过工厂模式，将代理的创建时机从“固定的初始化后”提前到“被需要时”，从而完美解决了问题。



所以，你的理解是对的：二级缓存本身不创建代理，它的存储能力也无法改变代理创建的时机问题。 这正是Spring引入三级缓存，通过工厂模式来控制代理生成时机的根本原因。



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

        //region 循环依赖为什么需要三级缓存
//        代理 Bean（有 @Transactional / @Async / 自定义 AOP）：
        /*
        三级缓存的作用
        缓存级别	存储内容	作用
        一级缓存	完整 Bean	已经初始化完成的 Bean
        二级缓存	早期暴露的 Bean	已经实例化但未初始化的 Bean（半成品）,只能存放原始对象（没有代理）
        三级缓存	Bean 工厂	延迟创建代理对象，解决 AOP 循环依赖，，ObjectFactory，用于代理 Bean。bean需要被代理（如存在 @Transactional 或 @Async）


        核心原因：
        1、二级缓存只能存储原始对象，无法处理 AOP 代理
        2、三级缓存存储工厂，可以在需要时才决定返回原始对象还是代理对象
        3、延迟代理创建：确保代理只创建一次，且在其他 Bean 需要时就能拿到正确的代理对象，bean需要被代理（如存在 @Transactional 或 @Async）

二级缓存遇到需要代理时候问题：
二级缓存（earlySingletonObjects）：只能存放原始对象（没有代理）
如果 Bean 有 AOP / CGLIB 代理（如 @Transactional, @Async），earlySingletonObjects 拿到的是“裸 Bean”
注入给其他 Bean 时，不会有代理效果

           // 2. 提前暴露：不是直接暴露对象，而是暴露一个工厂！
        // ★★★ 关键：如果只有二级缓存，这里只能暴露原始对象 ★★★
        // 但有了三级缓存，可以暴露一个工厂，在需要时才创建代理

        没有三级缓存会多次创建代理对象
        如果需要代理，这里创建代理并返回
        如果不需要代理，返回原始对象
        后续 initializeBean 时，发现已经创建了代理，就不会重复创建



缓存层	解决的问题
一级缓存 singletonObjects	完整初始化后的 Bean，正常单例使用
二级缓存 earlySingletonObjects	解决普通字段注入循环依赖，但拿不到代理
三级缓存 singletonFactories	解决代理 Bean 的循环依赖，保证 AOP/事务/异步生效




缓存级别	名称                 	存储内容         	       作用
一级缓存	singletonObjects	    完全初始化好的 Bean（成品）	   最终供业务使用的 Bean 实例
二级缓存	earlySingletonObjects	提前暴露的半成品 Bean	       已实例化但未完成属性填充和初始化
三级缓存	singletonFactories	    ObjectFactory 工厂	       存储能生成 Bean 实例的工厂对象

         */
        //endregion


//增强器
// 具体实现：
// - @Transactional → 转换为 TransactionAttributeSourceAdvisor
// - @Cacheable → 转换为 CacheAttributeSourceAdvisor
// - @Aspect 切面 → 转换为 InstantiationModelAwarePointcutAdvisor
// - 自定义 Advisor → 直接获取




        /*
        只能解决单例模式字段注入的循环依赖 ，无法解决构造函数和原型模式的Field依赖
          applicationContext.getBean("") 最终调用  DefaultSingletonBeanRegistry 的方法 getSingleton

  bean 实例化--》初始化。


          三个map 缓存:第三级缓存解决动态代理问题：返回代理对象
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


    // 一级缓存：单例池，存放完整的 Bean
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    // 二级缓存：早期暴露的 Bean，存放未完全初始化的 Bean（半成品）
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

    // 三级缓存：单例工厂，存放 Bean 工厂，用于生成早期暴露的 Bean
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

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

        //使用 Ctrl+C  关闭doc 窗体
        //Windows 控制台的特殊处理：点击 X 按钮会发送 CTRL_CLOSE_EVENT，而 JDK 1.8 默认不将其映射到 ShutdownHook
        //关闭事件 注册JVM关闭钩子.总会执行，x 掉dos 窗没有执行
        //Ctrl+F2 (Windows/Linux) 或 ⌘F2 (Mac) 终止调试会话时，确实不会执行通过
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("执行关闭钩子...注册JVM关闭钩子");
            applicationContext.close(); // 确保Spring上下文正确关闭
        }));
    }
}
