package com.example.demo.utility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
/*
声明注解，和C#的特性一样
 */
@Target({ElementType.TYPE, ElementType.METHOD,})//注解目标，只加在类上
@Retention(RetentionPolicy.RUNTIME)
public @interface Authorize {
    public AuthorizeType value() default AuthorizeType.UnAuthorize;
}
