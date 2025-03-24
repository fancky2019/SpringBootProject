package com.example.demo.aop.aspect.executeorder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD,})//注解目标，只加在类上
@Retention(RetentionPolicy.RUNTIME)
public @interface PointcutExecuteOrderTwo {
}
