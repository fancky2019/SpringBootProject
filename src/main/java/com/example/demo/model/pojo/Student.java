package com.example.demo.model.pojo;

import lombok.Data;
/*
    注解：@Data
    @Data也是lombok提供的，免去了实体类中getter和setter方法，代码更简洁，编译的时候会自动生成getter和setter方法：



    如果实体类报错找不到，重启Idea
 */
@Data
public class Student {
    private String name;
    private Integer age;
}
