package com.example.demo.config;

import com.example.demo.model.viewModel.Person;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/*
@import 导入第三方类库的类到ioc 容器，id 是全类名
@bean id 是方法名
 */


/*
使用@ConfigurationProperties注入对象，@value不能很好的解析复杂对象。
 定义数组或list时，将其进行初始化。如果不初始化，取到将会是null。
 */

/*
Prefix must be in canonical form :配置文件不要用駝峰写法 单词全小写或者用短横线连接 如：config.configmodel 或config.config-model
 */

// 缺少该配置属性时是否可以加载。如果为true，没有该配置属性时也会正常加载；反之则不会生效


/*
 *
 * name + havingValue = "true" 不相等，不加载，bean注入会报异常
 * */

/*
public @interface ConditionalOnProperty {

    // 数组，获取对应property名称的值，与name不可同时使用
    String[] value() default {};
    // 配置属性名称的前缀，比如spring.http.encoding
    String prefix() default "";
    // 数组，配置属性完整名称或部分名称
    // 可与prefix组合使用，组成完整的配置属性名称，与value不可同时使用
    String[] name() default {};
    // 可与name组合使用，比较获取到的属性值与havingValue给定的值是否相同，相同才加载配置
    String havingValue() default "";
    // 缺少该配置属性时是否可以加载。如果为true，没有该配置属性时也会正常加载；反之则不会生效
    boolean matchIfMissing() default false;
}
*/

/*
#配置文件不要用駝峰写法 单词全小写或者用短横线连接 如：config.configmodel 或config.config-model
#在竖线位置粘贴
 */

/**
 * @    @Value("${spring.datasource.username}") 配置文件不配置会报错，采用此种不配置就是用默认值不会报错
 */
@Data
//EnableConfigurationProperties 或 Component 或 Configuration
@Component
//不能在配置在加EnableConfigurationProperties，需要其他配置类上加启用注解一般和 Import 搭配使用，将配置类加入IOC
//@EnableConfigurationProperties(ConfigModelProperty.class)
//如果不配置，不报错，对象没有值
@ConfigurationProperties(prefix = "config.config-model") //prefix= "配置项"   读取并与 bean 绑定。

//@ConditionalOnProperty(name = "config.config-model.conditional-on-property", matchIfMissing = false)// name 没有配置就不加载
//@ConditionalOnProperty(name = "config.config-model.conditional-on-property", havingValue = "true", matchIfMissing = true)// name + havingValue 控制配置bean 是否加载
//@ConditionalOnProperty(prefix = "config.config-model.conditional-on-property", value = "true", matchIfMissing = true)
//// 或者可以省略prefix前缀，如果没有配置会报错
//@ConditionalOnProperty(value = "config.config-model.conditional-on-property", havingValue = "false")//配置项的值是否匹配控制是否加载bean
//@ConditionalOnClass(ConfigModel.class)
@RefreshScope
public class ConfigModelProperty {
    /*
    注意：要修改target 目录下的配置文件
    @RefreshScope 依赖，因为要手动调用   curl -X PosT http://localhost:8080/actuator/refresh 刷新配置
    返回值
    [
    "config.configmodel.fist-Name"
    ]

   1、
    <dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

2、
management:
  endpoints:
    web:
      exposure:
        include: "refresh"



3、不需要手动刷新，添加此依赖
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-context</artifactId>
</dependency>
     */



    private String fistName;
    //配置文件没有配置，就使用默认值，配置了就是用配置的值
    private Integer age = 27;
    //可设计一个default_field 字段设置字段的默认值
    private Integer DEFAULT_AGE = 27;
    private String address;
    private BigDecimal salary;
    private String[] array;
    private List<String> pets;
    private HashMap<String, String> maps;
    private List<Person> persons;
    private String conditionalOnProperty;

}
