package com.example.demo.aop.Interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//过滤器不用到WebMvcConfigurer 添加，拦截器需要到WebMvcConfigurer注册
@Slf4j
@Component
public class RequestTimingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
       /*
        Request Attribute 是绑定到 HttpServletRequest 对象上的键值对数据，具有以下特点：
        请求范围：只在当前请求生命周期内有效
        键值对存储：以 String 为键，Object 为值
        线程安全：每个请求有独立的 HttpServletRequest 实例
        */
        request.setAttribute("startTime", System.currentTimeMillis());
        // 移除单个属性
//        request.removeAttribute("startTime");
        log.info("请求开始1: {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //controller 执行完执行此方法
        log.info("处理器执行完成2，视图即将渲染");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("请求完成3: {} {} 耗时: {}ms",
                request.getMethod(),
                request.getRequestURI(),
                duration);
        log.info("Request: {} {} took {} ms",
                request.getMethod(),
                request.getRequestURI(),
                duration);
    }
}
