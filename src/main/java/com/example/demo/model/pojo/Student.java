package com.example.demo.model.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/*
    注解：@Data
    @Data也是lombok提供的，免去了实体类中getter和setter方法，代码更简洁，编译的时候会自动生成getter和setter方法：

在类是继承父类的情况下：
EqualsAndHashCode实则就是在比较两个对象的属性；
当@EqualsAndHashCode(callSuper = false)时不会比较其继承的父类的属性可能会导致错误判断；
当@EqualsAndHashCode(callSuper = true)时会比较其继承的父类的属性；

    如果实体类报错找不到，重启Idea
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Student {
    private String name;
    private Integer age;
}
