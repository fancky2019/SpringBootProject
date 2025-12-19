package com.example.demo.aop.aspect.executeorder;

import com.example.demo.aop.aspect.NoRepeatSubmit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

/**
 * Bean 上 加 @Order 注解
 * @Order 主要用于控制 Spring 容器中 Bean 的初始化顺序
 *
 * 2. 切面（AOP）的执行顺序
 * 切面的执行顺序是通过 @Order 注解或实现 Ordered 接口来控制的。每个切面可以指定一个顺序值，值越小，优先级越高。
 *
 */
@Slf4j
@Aspect
@Component
@Order(201)
public class PointcutExecuteOrderOneAdvisor {
    @Autowired
    private HttpServletRequest httpServletRequest;

    //     // 切入带有@NoRepeatSubmit注解的方法。有@NoRepeatSubmit注解的方法才会拦截
    //    @Pointcut("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
//    @Transactional
    @Pointcut("@annotation(pointcutExecuteOrderOne)")
    public void pointCut(PointcutExecuteOrderOne pointcutExecuteOrderOne) {
    }

    @Around("pointCut(pointcutExecuteOrderOne)")
    public Object around(ProceedingJoinPoint pjp, PointcutExecuteOrderOne pointcutExecuteOrderOne) throws Throwable {
        String path=httpServletRequest.getRequestURI();
        log.info("pointcutExecuteOrderOne {}",path);
        return pjp.proceed();
    }
}
