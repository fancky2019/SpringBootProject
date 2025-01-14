package com.example.demo.config;

import cn.hutool.core.util.StrUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Configuration
public class GloabalWebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                String header = request.getHeader("Accept-Language");
                if (StrUtil.isBlank(header)) {
                    return true;
                } else if ("zh-CN".equals(header)) {
                    LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
                } else if ("en".equals(header)) {
                    LocaleContextHolder.setLocale(Locale.US);
                } else {
                    LocaleContextHolder.setLocale(Locale.US);
                }
                return true;
            }
        });
    }

}


