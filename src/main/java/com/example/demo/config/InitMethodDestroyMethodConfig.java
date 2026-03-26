package com.example.demo.config;


/**
 * @PostConstruct  // 等价于 initMethod
 *  @PreDestroy  // 等价于 destroyMethod
 *
 *
 *
 * 方法要求
 * 方法名：可以任意（不一定叫 init/close）
 * 访问修饰符：通常用 public
 * 参数：必须无参数
 * 返回值：必须是 void
 * 异常：可以抛出异常
 *
 */
public class InitMethodDestroyMethodConfig {
    // 1. 定义类，包含 init() 和 close() 方法


    // 初始化方法 - 在 Bean 创建后、使用前被调用
    //@PostConstruct  // 等价于 initMethod
    public void init() {
        System.out.println("执行初始化逻辑");
        // 例如：建立连接、加载配置、启动线程等
    }

    // 销毁方法 - 在容器关闭时( context.close(); )被调用.
    // @PreDestroy  // 等价于 destroyMethod
    public void close() {
        System.out.println("执行清理逻辑");
        // 例如：关闭连接、释放资源、停止线程等
    }

    // 业务方法
    public void doSomething() {
        System.out.println("执行业务逻辑");
    }
}

//    // 2. 配置类中注册 Bean
//    @Configuration
//    public class AppConfig {
//
 //Bean 内使用@PostConstruct  @PreDestroy ，声明Bean不需要指定 initMethod 和 destroyMethod
//        @Bean(initMethod = "init", destroyMethod = "close")
//        public InitMethodDestroyMethodConfig myBean() {
//            return new InitMethodDestroyMethodConfig();  // 创建实例
//        }
//    }
//
//    // 3. 使用
//
//    public class Application {
//        public static void main(String[] args) {
//            AnnotationConfigApplicationContext context =
//                    new AnnotationConfigApplicationContext(AppConfig.class);
//
//            InitMethodDestroyMethodConfig myBean = context.getBean(InitMethodDestroyMethodConfig.class);
//            myBean.doSomething();  // 执行业务
//
//            context.close();  // 触发 close() 方法
//        }
//    }

