//package com.example.demo.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.transaction.support.TransactionSynchronizationManager;
//import javax.sql.DataSource;
//
//// 配置类：确保事务正确配置
//@Configuration
//@EnableTransactionManagement(proxyTargetClass = true)  // 强制使用CGLIB代理
//@EnableAspectJAutoProxy(exposeProxy = true)  // 暴露代理对象
//@Slf4j
//public class TransactionConfiguration {
//
//    @Bean
//    public PlatformTransactionManager transactionManager(DataSource dataSource) {
//        DataSourceTransactionManager transactionManager =
//                new DataSourceTransactionManager(dataSource);
//
//        // 设置事务超时
//        transactionManager.setDefaultTimeout(30);
//        // 设置嵌套事务允许
//        transactionManager.setNestedTransactionAllowed(true);
//
//        log.info("配置DataSourceTransactionManager: {}",
//                transactionManager.getClass().getName());
//
//        return transactionManager;
//    }
//
//
//}
