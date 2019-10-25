package com.example.demo.dynamicDataSource;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})//注解目标，只加在方法上
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSourceAnnotation {
    public String value() default DataSourceStrings.WRITER;
}
