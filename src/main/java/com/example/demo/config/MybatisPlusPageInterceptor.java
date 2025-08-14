package com.example.demo.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.example.demo.aop.Interceptor.DeletedInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MybatisPlusPageInterceptor {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInterceptor=new PaginationInnerInterceptor();
        interceptor.addInnerInterceptor(paginationInterceptor);

        // 注册自定义拦截器
//        interceptor.addInnerInterceptor(new DeletedInnerInterceptor());
        return interceptor;
    }
}
