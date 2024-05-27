package com.example.demo.filters;

import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import java.io.IOException;
import java.util.UUID;

/**
 * 通过url 访问添加traceId
 */
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
        String traceId = UUID.randomUUID().toString().replace("-", "");
     //   TRACEID.set(traceId);
        //MDC 线程安全，各个线程的traceId安全
        MDC.put("traceId", traceId);
        traceId = MDC.get("traceId");
        filterChain.doFilter(servletRequest, servletResponse);


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
    }
}
