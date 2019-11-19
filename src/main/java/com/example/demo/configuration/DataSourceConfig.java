//package com.example.demo.configuration;
//
//import com.example.demo.dynamicDataSource.DynamicDataSource;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.jdbc.DataSourceBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.core.annotation.Order;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Mybatis多数据源配置类
// *
// *
// * @Primary
// * 当一个类或接口有多个实例时候， @Autowired采用@Primary注解的默认注入
// *  @ Qualifier 注解里的别名要和类的别名一直。@Autowired()@Qualifier("baseDao")
// */
////@Configuration
//public class DataSourceConfig {
//    /*
//    @Bean的默认名称是方法名称，@Bean同一类型的不同实例，加Bean(实例)的别名来区分。
//    @Qualifier 注解的Bean不同类型的实例,通过在类型名称上加名称(@Service("UserService"))，用@Qualifier("typeAlia")区分
//     */
//
//HikariDataSource
//    /**
//     * 来源库
//     *
//     * @return
//     */
//    @Bean(name = "dataSourceWriter")
//    @ConfigurationProperties(prefix = "spring.datasource.writer")
//    public DataSource dataSourceWriter() {
//        return DataSourceBuilder.create().build();
//    }
//
//    /**
//     * 目标库
//     *
//     * @return
//     */
//    @Bean(name = "dataSourceReader")
//    @ConfigurationProperties(prefix = "spring.datasource.reader")
//    public DataSource dataSourceReader() {
//        return DataSourceBuilder.create().build();
//    }
//
//
//
//
//
//
//    /**
//     * 动态数据源: 通过AOP在不同数据源之间动态切换
//     * 将数据库实例写入到targetDataSources属性中，并且使用defaultTargetDataSource属性设置默认数据源。
//     * @Primary 注解用于标识默认使用的 DataSource Bean，并注入到SqlSessionFactory的dataSource属性中去。
//     *
//     * @return
//     */
//    @Primary
//    @Bean(name = "dynamicDataSource")
//    public DataSource dynamicDataSource() {
//        DynamicDataSource dynamicDataSource = new DynamicDataSource();
//        // 默认数据源
//        dynamicDataSource.setDefaultTargetDataSource(dataSourceWriter());
//        // 配置多数据源
//        Map<Object, Object> dsMap = new HashMap<>();
//        dsMap.put("dataSourceWriter", dataSourceWriter());
//        dsMap.put("dataSourceReader", dataSourceReader());
//        dynamicDataSource.setTargetDataSources(dsMap);
//        return dynamicDataSource;
//    }
//
//    /**
//     *
//     * 动态切换事务作用的数据库
//     *
//     * 配置@Transactional注解事物
//     * 使用dynamicDataSource作为transactionManager的入参来构造DataSourceTransactionManager
//     *
//     *
//     *
//     *Spring 4.2 利用@Order控制配置类的加载顺序，
//     * @Order指定执行顺序，值越小，越先执行
//     *
//     *
//     * 如果有多个实现类，而你需要他们按一定顺序执行的话，可以在实现类上加上@Order注解。@Order(value=整数值)。
//     * SpringBoot会按照@Order中的value值从小到大依次执行。
//     */
//
//    /*
//
//     */
//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        return new DataSourceTransactionManager(dynamicDataSource());
//    }
//
//
//}
