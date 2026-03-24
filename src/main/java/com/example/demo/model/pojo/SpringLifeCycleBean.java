package com.example.demo.model.pojo;

import com.example.demo.init.CommandLineImp;
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
import javax.swing.*;

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

 @Component public class MyBean implements BeanFactoryAware {

 private BeanFactory beanFactory;

 @Override public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
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


 //region 循环依赖 三级缓存

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


 //CommandLineImp.class.getName() 不能用这种全路径名
 //CommandLineImp
 String name = CommandLineImp.class.getSimpleName();
 Object obj = applicationContext.getBean("demoProductService");
 int m = 0;
 //        AbstractAutowireCapableBeanFactory#doCreateBean()中：
 //endregion




 BeanFactoryPostProcessor 适用场景
 配置中心集成：在 Bean 实例化前，从配置中心拉取配置并修改 BeanDefinition
 动态注册 Bean：根据条件动态注册 BeanDefinition
 批量修改 Bean 属性：统一修改一批 Bean 的某个属性
 替换 Bean 实现类：例如根据环境（dev/test/prod）替换不同的实现类

 BeanPostProcessor 适用场景
 AOP 代理：为 Bean 创建代理对象，实现横切逻辑
 自定义注解处理：处理自定义注解，在 Bean 初始化时注入特定逻辑
 属性校验：在 Bean 初始化后校验属性是否合法
 日志记录：为 Bean 添加统一的日志记录功能



 执行顺序

 Bean 实例化
 ↓
 1. 构造方法 (Constructor)
 ↓
 2. 依赖注入 (Dependency Injection)
 ↓
 3. Bean 名称感知 (BeanNameAware.setBeanName) 优先使用依赖注入：
 ↓
 4. Bean 工厂感知 (BeanFactoryAware.setBeanFactory)
         能通过 @Autowired 解决的，就不要用 BeanFactoryAware
         userService = beanFactory.getBean(UserService.class);
         类似ApplicationContextAware ，从容其中获取bean

          BeanFactoryAware	ApplicationContextAware:获取对象	BeanFactory	ApplicationContext
          ApplicationContext 接口继承了BeanFactory
 ↓
 5. 应用上下文感知 (ApplicationContextAware.setApplicationContext)
 ↓
 6. Bean 后置处理器前置处理 (BeanPostProcessor.postProcessBeforeInitialization)
 ↓
 7. @PostConstruct 注解方法  ← 第1个初始化方法
 ↓
 8. InitializingBean.afterPropertiesSet()  ← 第2个初始化方法
 ↓
 9. 自定义 init-method  ← 第3个初始化方法
 ↓
 10. Bean 后置处理器后置处理 (BeanPostProcessor.postProcessAfterInitialization)
 ↓
 Bean 就绪

 1. 实例化 (Constructor)
 2. 依赖注入 (populateBean) - @Autowired
 3. 初始化前 (postProcessBeforeInitialization)
 4. 初始化方法 (invokeInitMethods)
 ├─ @PostConstruct
 ├─ afterPropertiesSet() ：InitializingBean 接口的方法。 在所有属性注入完成后执行
 └─ init-method  ： @Bean(initMethod = "customInit")
 5. 初始化后 (postProcessAfterInitialization) - AOP代理在此阶段创建
 6. Bean 准备就绪

 完整生命周期顺序
 对于普通Bean（非BeanPostProcessor）：
 1、构造函数
 2、依赖注入（@Autowired, @Value等）
 3、BeanNameAware
 4、BeanFactoryAware
 5、ApplicationContextAware
 6、BeanPostProcessor postProcessBeforeInitialization
 7、@PostConstruct
 8、InitializingBean afterPropertiesSet
 9、自定义init方法
 10、BeanPostProcessor postProcessAfterInitialization
 11、Bean准备就绪
 12、@PreDestroy
 13、DisposableBean destroy
 14、自定义destroy方法
 */
public class SpringLifeCycleBean implements BeanNameAware, BeanFactoryAware, ApplicationContextAware, BeanPostProcessor, InitializingBean, DisposableBean {

    /**
     * 构造函数执行完就有值
     */
    @Value("${demo.multiEnvironment}")
    private String multiEnvironment;

    public SpringLifeCycleBean() {
        System.out.println("SpringLifeCycleBean-1:Constructor method invoked");
    }


    @Override
    public void setBeanName(String s) {
        System.out.println("SpringLifeCycleBean-2:BeanNameAware setBeanName method invoked, name: " + s);
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        System.out.println("SpringLifeCycleBean-3:BeanFactoryAware setBeanFactory method invoked, beanFactory: " + beanFactory.getClass().getName());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("SpringLifeCycleBean-4:ApplicationContextAware setApplicationContext method invoked, applicationContext: " + applicationContext.getClass().getName());
    }

    /**
     * 当一个类实现了 BeanPostProcessor 接口时，它本身也会被作为一个特殊的 Bean 由 Spring 容器管理。但是，BeanPostProcessor 的执行时机和普通 Bean 不同：
     * BeanPostProcessor 自身不会应用它自己的后置处理器方法（避免无限循环）
     * BeanPostProcessor 在容器生命周期的早期就会被实例化，用于处理其他普通 Bean
     *
     *
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        if (beanName.equals("lifeCycleBean")) {
//            System.out.println("SpringLifeCycleBean:BeanPostProcessor postProcessBeforeInitialization method invoked, beanName: " + beanName);
//        }

//       非 SpringLifeCycleBean 类对象会进入此方法
        return bean;
    }

    /**
     * 当一个类实现了 BeanPostProcessor 接口时，它本身也会被作为一个特殊的 Bean 由 Spring 容器管理。但是，BeanPostProcessor 的执行时机和普通 Bean 不同：
     * BeanPostProcessor 自身不会应用它自己的后置处理器方法（避免无限循环）
     * BeanPostProcessor 在容器生命周期的早期就会被实例化，用于处理其他普通 Bean
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        if (beanName.equals("lifeCycleBean")) {
//            System.out.println("SpringLifeCycleBean:BeanPostProcessor postProcessAfterInitialization method invoked, beanName: " + beanName);
//        }
//        非 SpringLifeCycleBean 类对象会进入此方法
        return bean;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("SpringLifeCycleBean:InitializingBean afterPropertiesSet method invoked");

    }

    //region 类中声明注解    @PreDestroy
    @PostConstruct
    public void init() {
        System.out.println("SpringLifeCycleBean:PostConstruct method invoked");
    }


    @PreDestroy
    public void preDestroy() {
        System.out.println("SpringLifeCycleBean:PreDestroy method invoked");
    }
    //endregion

    @Override
    public void destroy() throws Exception {
        System.out.println("SpringLifeCycleBean:DisposableBean destroy method invoked");
    }


    //region 声明bean 配置时候使用
    public void customInit() {
        System.out.println("SpringLifeCycleBean:customInit method invoked");
    }

    public void customDestroy() {
        System.out.println("SpringLifeCycleBean:customDestroy method invoked");
    }
    //endregion

}

