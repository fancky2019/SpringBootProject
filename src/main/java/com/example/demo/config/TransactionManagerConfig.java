package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;



//@Configuration
////proxyTargetClass:直接开启 CGLIB 代理：,JDK 动态代理接口实现类上的事务注解失效
//@EnableTransactionManagement(proxyTargetClass = true)
public class TransactionManagerConfig {
    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager tm = new DataSourceTransactionManager(dataSource);
        // 显式开启事务同步（虽然默认就是开启的，但可以强制确认）
        //transactionSynchronization默认0
        tm.setTransactionSynchronization(DataSourceTransactionManager.SYNCHRONIZATION_ALWAYS);
        return tm;
    }
}
