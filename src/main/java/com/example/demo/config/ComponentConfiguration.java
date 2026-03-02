package com.example.demo.config;

import com.example.fanckyspringbootstarter.service.ToolService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * 在 Spring 中，@Configuration 有两种模式：
 *
 * 模式	说明
 * Full 模式（默认）	CGLIB 代理整个配置类，保证同一个 @Bean 方法无论调用多少次都返回容器中同一个 Bean（单例保证）。
 * Lite 模式	配置类没有代理，@Bean 方法只是普通方法，调用就像普通 Java 调用一样。
 *
 *触发 Lite 模式的几种情况
 *
 * 1、你把配置类标记为 @Component（或者普通类通过 @Import 引入）
 * → 自动退化成 Lite 模式
 * 2、在 @Configuration 上显式关闭 CGLIB 代理：
 *
 *
 * Full 模式（默认 @Configuration）
 *
 */
//@Component 不适合做配置类。   @Bean内的方法不受ioc控制
//@Component
//@Configuration
public class ComponentConfiguration {
    //Lite 模式是 Spring 对 @Bean 方法的一种轻量级处理方式，发生在没有被 @Configuration 修饰的类中。
    //@Configuration 会从ioc 容器中获取,@Component 会直接new 对象。Lite 模式
//    @Bean
//    ToolService toolService() {
//        return new ToolService();
//    }

//    @Bean
//    OtherBean other() {
//        return new OtherBean(toolService()); // 这里会 new 两次
//    }

//    @Bean
//    public ToolService toolService() {
//        return new ToolService();
//    }
//
//    @Bean
//    public ToolHelper toolHelper() {
//    @Configuration 会从ioc 容器中获取
//        // ✅ 这里获取的是容器中的 toolService 单例
//        ToolService service = toolService();  // 被 CGLIB 增强，从容器获取
//        return new ToolHelper(service);
//    }
}
