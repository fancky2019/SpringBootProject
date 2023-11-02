package com.example.demo.aop.aspect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;




//静态代理，jdk动态代理，cglib动态代理
/*
Spring AOP面向切面：
实现
一、JDK动态代理的两个核心接口(类)分别是InvocationHandler和Proxy。注意：只能代理接口。
    代码见Demo2019--Test2019--proxy目录下
二、CGLIB动态代理.派生类。


Spring 封装了JDK动态代理。DefaultAopProxyFactory


AspectJ 框架。编译时候确定
Spring AOP 属于运行时增强，而 AspectJ 是编译时增强. Spring AOP 基于代理(Proxying)，而 AspectJ 基于字节码操作(Bytecode Manipulation)。

org.springframework.core.annotation.Order，使用注解value属性指定优先级。
@Order数值越低，表明优先级越高，@Order 默认为最低优先级，即最大数值：
 */


/**

 执行顺序：Filter -> Interceptor -> ControllerAdvice -> Aspect -> Controller

 Filter:servlet采用回调的方式实现，可以获取request信息，获取不到请求的方法信息。
 Interceptor:采用反射动态代理实现，可以获取request信息，可以获取到请求的方法名称，获取不到方法的参数信息。
 Aspect:springboot 默认采用动态代理实现，获取不到request请求的信息，可以获取方法的参数

 preHandle-->postHandle-->afterCompletion
 preHandle return false 就不进入postHandle
 可以多个Interceptor
 */

/*
Spring中的拦截机制,如果出现异常的话，异常的顺序是从里面到外面一步一步的进行处理，如果到了最外层都没有进行处理的话，就会由tomcat容器抛出异常.

1.过滤器：Filter :可以获得Http原始的请求和响应信息，但是拿不到响应方法的信息

2.拦截器：Interceptor：可以获得Http原始的请求和响应信息，也拿得到响应方法的信息，但是拿不到方法响应中参数的值

3.ControllerAdvice（Controller增强，自spring3.2的时候推出）：主要是用于全局的异常拦截和处理,这里的异常可以使自定义异常也可以是JDK里面的异常，用于处理当数据库事务业务和预期不同的时候抛出封装后的异常，进行数据库事务回滚，并将异常的显示给用户

4.切片：Aspect：主要是进行公共方法的，可以拿得到方法响应中参数的值，但是拿不到原始的Http请求和相对应响应的方法
 */

/*
Filter: filters包下实例。
Interceptor：Interceptor包下,要在WebMvcConfigurer 方法里addInterceptors添加
ControllerAdvice：handler报下
Aspect：aspect包下LogAspect
 */

