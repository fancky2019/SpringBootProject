package com.example.demo.aop.Interceptor;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.Statement;
import java.util.Properties;

/**
 *MybatisPlus统一修改审计信息MetaObjectHandlerImp
 * mybatis sql执行拦截器
 *
 *
 * 过滤器不用到WebMvcConfigurer 添加，拦截器需要到WebMvcConfigurer注册
 */

//@Intercepts({
//        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
//        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class})
//})

//@Component
@Intercepts({
        @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class
        })
})
public class SqlExecuteInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = invocation.proceed();
        long endTime = System.currentTimeMillis();
        long timeElapsed = endTime - startTime;
//        invocation.proceed();


        String sql = ((StatementHandler) invocation.getTarget()).getBoundSql().getSql();

//        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
//        String sqlId = mappedStatement.getId();
        System.out.println("执行SQL【" + sql + "】耗时：" + timeElapsed + "ms");

        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 可以配置拦截器的属性
    }
}
