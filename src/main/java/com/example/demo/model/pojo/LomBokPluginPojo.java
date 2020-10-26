package com.example.demo.model.pojo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/*
一、安装LomBok Plugin 插件。
二、添加依赖 <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <!--            <version>1.18.12</version>-->
            <!--            <scope>provided</scope>-->
        </dependency>


        @Data相当于@Getter @Setter @RequiredArgsConstructor @ToString @EqualsAndHashCode这5个注解的合集

        在类是继承父类的情况下：
EqualsAndHashCode实则就是在比较两个对象的属性；
当@EqualsAndHashCode(callSuper = false)时不会比较其继承的父类的属性可能会导致错误判断；
当@EqualsAndHashCode(callSuper = true)时会比较其继承的父类的属性；


不需要编译其他类就可以访问到getter、setter
 */


import lombok.Data;

/**
 * @Auther fancky
 * @Date 2020-10-26 9:34
 * @Description
 */
@Data
 public class LomBokPluginPojo {

}

@Data
class  LomBokSuper
{
    private  Integer age;
    private  String name;
}

