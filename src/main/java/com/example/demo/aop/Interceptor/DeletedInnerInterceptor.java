package com.example.demo.aop.Interceptor;

import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import javax.annotation.PostConstruct;
import java.sql.PreparedStatement;
import java.util.ArrayList;


public class DeletedInnerInterceptor implements InnerInterceptor {

    private ArrayList<String> tableNames;

    /**
     * 初始化方法
     */
    @PostConstruct
    public void initTable() {
        tableNames = new ArrayList<String>();
        tableNames.add("demo_product");
    }

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {


        String strSql = boundSql.getSql();
        // 检查是否为 SELECT 语句
        if (ms.getSqlCommandType() == SqlCommandType.SELECT) {
            String sql = ms.getBoundSql(parameter).getSql();
            if (!sql.contains("demo_product")) {
                return;
            }
            // 动态拼接 WHERE 条件
            if (!sql.toLowerCase().contains("where")) {
                sql += " WHERE 1=1";
            }


            if(sql.contains( "limit") )
            {
                sql.replace("limit","AND deleted = 0 limit");
            }
            else{
                //有limit  报错
                sql += " AND deleted = 0"; // 示例条件：只查询未删除的数据

            }
            // 更新 BoundSql 中的 SQL
//            MetaObject metaObject = SystemMetaObject.forObject(ms.getBoundSql(parameter));
            MetaObject metaObject = SystemMetaObject.forObject(boundSql);
            metaObject.setValue("sql", sql);

        }
    }
}
