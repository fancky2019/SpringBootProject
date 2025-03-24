package com.example.demo.aop.aspect.executeorder;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
//@Order(200)  //实现接口Ordered
public class PointcutExecuteOrderTwoAdvisor implements Ordered {
    @Autowired
    private HttpServletRequest httpServletRequest;

//    @Transactional
//    @Pointcut("@annotation(pointcutExecuteOrderTwo)")
//    public void pointCut(PointcutExecuteOrderTwo pointcutExecuteOrderTwo) {
//    }

//    @Around("pointCut(pointcutExecuteOrderTwo)")
//public Object around(ProceedingJoinPoint pjp, PointcutExecuteOrderTwo pointcutExecuteOrderTwo) throws Throwable {

    @Around("@annotation(com.example.demo.aop.aspect.executeorder.PointcutExecuteOrderTwo)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        String path=httpServletRequest.getRequestURI();
        log.info("pointcutExecuteOrderTwo {}",path);
        return pjp.proceed();
    }

    @Override
    public int getOrder() {
        return 200;
    }
}
