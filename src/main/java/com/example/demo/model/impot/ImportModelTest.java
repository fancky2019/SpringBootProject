package com.example.demo.model.impot;

import com.example.demo.model.viewModel.Person;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
@Data
////如果不配置，不报错，对象没有值
@ConfigurationProperties(prefix = "config.config-model") //prefix= "配置项"   读取并与 bean 绑定。

//@ConditionalOnProperty(name = "config.config-model.conditional-on-property", matchIfMissing = false)// name 没有配置就不加载
//@ConditionalOnProperty(name = "config.config-model.conditional-on-property", havingValue = "true", matchIfMissing = true)// name + havingValue 控制配置bean 是否加载
//@ConditionalOnProperty(prefix = "config.config-model.conditional-on-property", value = "true", matchIfMissing = true)
//// 或者可以省略prefix前缀,没有找到配置项报异常不会加载配置类
//ConditionalOnProperty 用于根据配置文件（中的属性值来决定是否注册某个 Bean 或启用某个配置类。
//@ConditionalOnProperty(value = "config.config-model.conditional-on-property", havingValue = "true")
public class ImportModelTest {
    private String fistName;
    private  String address;
    private BigDecimal salary;
    private String[] array;
    private List<String> pets;
    private HashMap<String,String> maps;
    private List<Person> persons;
    private String conditionalOnProperty;
}
