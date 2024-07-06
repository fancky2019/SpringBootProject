package com.example.demo.config;

import com.example.demo.aop.Interceptor.AuthenticationInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * extends WebMvcConfigurationSupport
 *
 * 添加拦截器
 */
@Slf4j
@Configuration
public class AuthenticationInterceptorConfig  implements WebMvcConfigurer {
    //extends WebMvcConfigurerAdapter 废弃了

//    @Autowired
//   private AuthenticationInterceptor authenticationInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authenticationInterceptor);
        registry.addInterceptor(authenticationInterceptor())
                .addPathPatterns("/**")
                //白名单
                .excludePathPatterns(
                        "/crm-backend/datacallback",
                        "/crm-backend/front/order/**");
        log.info("加入拦截器: {}", "TokenVerifyIntercepter");;
    }

    @Bean
    public AuthenticationInterceptor authenticationInterceptor() {
        return new AuthenticationInterceptor();
    }
}