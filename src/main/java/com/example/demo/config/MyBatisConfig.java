package com.example.demo.config;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor;
import com.example.demo.aop.Interceptor.SqlExecuteInterceptor;
import com.example.demo.aop.Interceptor.SqlUpdateInterceptor;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class MyBatisConfig {
    @Bean
    public SqlExecuteInterceptor sqlExecuteInterceptor() {
        return new SqlExecuteInterceptor();
    }

    @Bean
    public SqlUpdateInterceptor sqlUpdateInterceptor() {
        return new SqlUpdateInterceptor();
    }

//    /**
//     * mybatis-plus :3..5.6 版本有此类，3.5.9 没有此类，需要引入mybatis-plus-jsqlparser-4.9
//     */
//    @Bean
//    public DataChangeRecorderInnerInterceptor dataChangeRecorderInnerInterceptor(BaseMapper baseMapper){
//        DataChangeRecorderInnerInterceptor dataChangeRecorderInnerInterceptor = new DataChangeRecorderInnerInterceptor();
//        // 批量更新条数上限
//        dataChangeRecorderInnerInterceptor.setBatchUpdateLimit(1000);
//
//        return dataChangeRecorderInnerInterceptor;
//    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        //  return configuration -> configuration.addInterceptor(sqlExecuteInterceptor());
        ConfigurationCustomizer configuration = new ConfigurationCustomizer() {
            @Override
            public void customize(org.apache.ibatis.session.Configuration configuration) {
//                configuration.addInterceptor(sqlExecuteInterceptor());
                configuration.addInterceptor(sqlUpdateInterceptor());
            }
        };

        return configuration;

    }

}
