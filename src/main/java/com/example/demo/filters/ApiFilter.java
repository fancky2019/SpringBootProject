package com.example.demo.filters;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import java.io.IOException;
import java.util.Date;

/*
Filter:servlet采用回调的方式实现，可以获取request信息，获取不到方法的参数信息。
Interceptor:采用反射动态代理实现，可以获取request信息，获取不到方法的参数信息。
Aspect:springboot 默认采用动态代理实现，获取不到request请求的信息，可以获取方法的参数
 */
//@Component
public class ApiFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("TimeFilter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("TimeFilter start");
        // 开始时间
        long start = new Date().getTime();
        // 过滤的实际业务
        chain.doFilter(request, response);
        // 结束时间
        long end = new Date().getTime();
        System.out.println("过滤用时：" + (end - start));
        System.out.println("TimeFilter end");
    }

    @Override
    public void destroy() {
        System.out.println("TimeFilter destroy");
    }

}
