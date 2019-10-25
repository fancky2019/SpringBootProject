package com.example.demo.configuration;

import com.example.demo.dynamicDataSource.DynamicDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Mybatis多数据源配置类
 */
@Configuration
public class DataSourceConfig {
    /**
     * 默认数据源
     *
     * @return
     */
    @Bean(name = "dataSourceDefault")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSourceDefault() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 来源库
     *
     * @return
     */
    @Bean(name = "dataSourceWriter")
    @ConfigurationProperties(prefix = "spring.datasource.writer")
    public DataSource dataSourceWriter() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 目标库
     *
     * @return
     */
    @Bean(name = "dataSourceReader")
    @ConfigurationProperties(prefix = "spring.datasource.reader")
    public DataSource dataSourceReader() {
        return DataSourceBuilder.create().build();
    }

    /**
     * 动态数据源: 通过AOP在不同数据源之间动态切换
     * 将数据库实例写入到targetDataSources属性中，并且使用defaultTargetDataSource属性设置默认数据源。
     * @Primary 注解用于标识默认使用的 DataSource Bean，并注入到SqlSessionFactory的dataSource属性中去。
     *
     * @return
     */
    @Primary
    @Bean(name = "com/example/demo/dynamicDataSource")
    public DataSource dynamicDataSource() {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        // 默认数据源
        dynamicDataSource.setDefaultTargetDataSource(dataSourceDefault());
        // 配置多数据源
        Map<Object, Object> dsMap = new HashMap<>();
        dsMap.put("dataSourceDefault", dataSourceDefault());
        dsMap.put("dataSourceWriter", dataSourceWriter());
        dsMap.put("dataSourceReader", dataSourceReader());
        dynamicDataSource.setTargetDataSources(dsMap);
        return dynamicDataSource;
    }

    /**
     * 配置@Transactional注解事物
     * 使用dynamicDataSource作为transactionManager的入参来构造DataSourceTransactionManager
     *
     * @return
     */
    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dynamicDataSource());
    }
}
