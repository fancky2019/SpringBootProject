package com.example.demo.aop.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.demo.utility.TraceIdCreater;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.UUID;


/**
 * xxljob默认不会经过 Filter：因为 XXL-JOB 默认使用自己的 Netty 服务器
 *
 *xxl-job-admin（调度中心） 、执行器
 *
 *1. 标准 HTTP 执行器模式（默认）
 * 在标准 HTTP 模式下（使用内置的 Netty HTTP 服务器）：不会经过 Servlet Filter：因为 XXL-JOB
 * 执行器默认使用 Netty 直接处理 HTTP 请求，不经过 Servlet 容器
 *请求直接由 XxlJobExecutor 的 EmbedServer 处理
 * 执行器的 /run 接口由 EmbedHttpServerHandler 直接处理
 *默认不会经过 Filter：因为 XXL-JOB 默认使用自己的 Netty 服务器
 *
 *
 *2. 如果执行器部署在 Servlet 容器中
 * 当执行器以 WAR 包形式部署在 Tomcat/Jetty 等 Servlet 容器中：
 *
 * 会经过 Filter：因为请求会经过 Servlet 容器完整的处理链
 *
 */
@Slf4j
@Aspect
@Component
public class XxlJobTraceIdAspect {
    /**
     * 打印统一日志，注入traceId
     * @author 只有影子
     * @param joinPoint
     */
    @Before("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public void beforeMethod(JoinPoint joinPoint) {
//        //sleuth 不会在xxl-job中生成traceId 和spanId， MDC.get("traceId")为null
//        // 注入traceId
//        String traceId = UUID.randomUUID().toString().replace("-", "");
//        //   TRACEID.set(traceId);
//        //MDC 线程安全，各个线程的traceId安全
//        MDC.put("traceId", traceId);
//        traceId = MDC.get("traceId");
    }
    // 切入带有@NoRepeatSubmit注解的方法。有@NoRepeatSubmit注解的方法才会拦截
    @Pointcut("@annotation(com.xxl.job.core.handler.annotation.XxlJob)")
    public void xxlJobPointcut() {
    }

    @Around("xxlJobPointcut()")
    public Object traceXxlJob(ProceedingJoinPoint joinPoint)  {
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        XxlJob xxlJob = signature.getMethod().getAnnotation(XxlJob.class);
        // 生成 TraceId
//        String traceId = UUID.randomUUID().toString();

        String traceId =  TraceIdCreater.getTraceId();
        MDC.put("traceId", traceId);

        try {
            // 执行任务
            return joinPoint.proceed();
        } catch (Throwable e) {
//            XXL-Job 调度请求
//  → XXL-Job 服务端触发
//    → XXL-Job 客户端接收
//      → XXL-Job 拦截器链执行
//        → Spring AOP 事务拦截器
//          → 你的业务方法
//        ← Spring AOP 事务拦截器 (提交/回滚)
//      ← XXL-Job 拦截器链完成
//    ← XXL-Job 客户端返回结果
//  ← XXL-Job 服务端记录结果

            // @Transactional 已经执行回滚操作，才会执行到xxljob 的拦截器 异常处理
            //所以此处不必再抛出异常
            log.error("", e);
//            throw e;
        } finally {
//            MDC.clear();
            MDC.remove("traceId");
        }
        return null;
    }


}
