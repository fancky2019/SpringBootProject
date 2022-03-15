package com.example.demo.config;

//import com.atomikos.jdbc.AtomikosDataSourceBean;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/*
具体参见workspace 的项目multipledatasource的MultipleDataSourceController
 */

//@Configuration
//指定该SqlSession对象对应的dao(basePackages , dao扫包  sqlSessionFactoryRef: SqlSessionFactory对象注入到该变量中)
//@MapperScan(basePackages = "com.fancky.multipledatasource.dao.demo", sqlSessionFactoryRef = "demoSqlSessionFactory")
public class DemoDataSourceConfig {

    //  HikariDataSource

    /**
     * SqlSession对象创建
     *
     * @param dataSource
     * @return
     * @throws Exception
     */
    @Bean(name = "demoSqlSessionFactory")
    public SqlSessionFactory demoSqlSessionFactory(@Qualifier("demoDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        //指定起别名的包, 这里注意, 设置该参数时打成jar启动会找不到该包下的类,目前未找到解决方案
        bean.setTypeAliasesPackage("com.example.springboot.pojo");

        bean.setDataSource(dataSource);
        //指定该SqlSession对应的mapper.xml文件位置
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/demo/*.xml"));
        SqlSessionFactory sqlSessionFactory = bean.getObject();
        return sqlSessionFactory;
    }

    @Bean("demoSqlSessionTemplate")
    public SqlSessionTemplate demoSqlSessionTemplate(@Qualifier("demoSqlSessionFactory") SqlSessionFactory sessionfactory) {
        return new SqlSessionTemplate(sessionfactory);
    }


    /**
     * 封装数据源对象创建, 该方法就已经将数据源的各个数据封装到该对象中
     * <p>
     * yml配置文件指定连接池类型，mybatis根据连接池生成的DataSource获取连接
     *
     * @return
     */
    @Bean(name = "demoDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.demo") //读取的数据源前缀, 跟yml文件对应
    public DataSource demoDataSource() {
        return DataSourceBuilder.create().build();
    }

//    @Bean(name = "demoDataSource")
//    public DataSource testDataSource(DemoDataSourceConfigParameters demoConfig) throws SQLException {
//        MysqlXADataSource mysqlXaDataSource = new MysqlXADataSource();
//        mysqlXaDataSource.setUrl(demoConfig.getJdbcUrl());
//        mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
//        mysqlXaDataSource.setPassword(demoConfig.getPassword());
//        mysqlXaDataSource.setUser(demoConfig.getUsername());
//        mysqlXaDataSource.setPinGlobalTxToPhysicalConnection(true);
//
//        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
//        xaDataSource.setXaDataSource(mysqlXaDataSource);
//        xaDataSource.setUniqueResourceName("demoDataSource");
//
//        xaDataSource.setMinPoolSize(demoConfig.getMinPoolSize());
//        xaDataSource.setMaxPoolSize(demoConfig.getMaxPoolSize());
//        xaDataSource.setMaxLifetime(demoConfig.getMaxLifetime());
//        xaDataSource.setBorrowConnectionTimeout(demoConfig.getBorrowConnectionTimeout());
//        xaDataSource.setLoginTimeout(demoConfig.getLoginTimeout());
//        xaDataSource.setMaintenanceInterval(demoConfig.getMaintenanceInterval());
//        xaDataSource.setMaxIdleTime(demoConfig.getMaxIdleTime());
//        xaDataSource.setTestQuery(demoConfig.getTestQuery());
//        return xaDataSource;
//    }


}
