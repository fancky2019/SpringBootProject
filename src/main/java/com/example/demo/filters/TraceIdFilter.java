package com.example.demo.filters;

import com.example.demo.utility.TraceIdHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.sleuth.TraceContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

/**
 * 通过url 访问添加traceId
 *
 *
 * xxljob默认不会经过 Filter：因为 XXL-JOB 默认使用自己的 Netty 服务器
 *
 *
 * 过滤器不用到WebMvcConfigurer 添加，拦截器需要到WebMvcConfigurer注册
 */
@Slf4j
@Configuration
@Order(1)
public class TraceIdFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(TraceIdFilter.class);
    private static final ThreadLocal<String> TRACEID = new ThreadLocal<>();

    // log4j内部 使用 ThreadContext 保证线程安全
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("初始化traceId Filter");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        String traceId = UUID.randomUUID().toString().replace("-", "");
//     //   TRACEID.set(traceId);
//        //MDC 线程安全，各个线程的traceId安全
//        MDC.put("traceId", traceId);
//        traceId = MDC.get("traceId");


//       //  sleuth 会把traceId 写入MDC ,可通过MDC获取traceId: MDC.get("traceId"),
//        TraceContext traceContext = (TraceContext) servletRequest.getAttribute(TraceContext.class.getName());
//        String traceId =  traceContext.traceId();
//        log.info("1链路跟踪测试{}",traceId);


        TraceIdHolder.setTraceId(UUID.randomUUID().toString().replace("-", ""));
        filterChain.doFilter(servletRequest, servletResponse);
        int n = 0;

//        try
//        {
//            MDC.put("traceId", traceId);
//            traceId = MDC.get("traceId");
//            filterChain.doFilter(servletRequest, servletResponse);
//        }
//        finally {
//            MDC.clear();
//        }
    }

    @Override
    public void destroy() {
        //  TRACEID.remove();
        MDC.clear();
        TraceIdHolder.removeTraceId();
    }
}
