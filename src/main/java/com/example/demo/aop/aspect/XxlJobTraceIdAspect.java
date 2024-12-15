package com.example.demo.aop.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
        //sleuth 不会在xxl-job中生成traceId 和spanId
        // 注入traceId
        String traceId = UUID.randomUUID().toString().replace("-", "");
     //   TRACEID.set(traceId);
        //MDC 线程安全，各个线程的traceId安全
        MDC.put("traceId", traceId);
        traceId = MDC.get("traceId");
    }

}
