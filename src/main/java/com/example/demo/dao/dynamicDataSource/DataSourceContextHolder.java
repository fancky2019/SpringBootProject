package com.example.demo.dao.dynamicDataSource;

public class DataSourceContextHolder {
    /**
     * 默认数据源
     */
    public static final String DEFAULT_DS = "dataSourceDefault";

    /**
     * 使用ThreadLocal存储数据源
     */
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    /**
     * 设置数据源
     *
     * @param dbType
     */
    public static void setDB(String dbType) {
        contextHolder.set(dbType);
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public static String getDB() {
        return contextHolder.get();
    }

    /**
     * 清除数据源
     */
    public static void clearDB() {
        contextHolder.remove();
    }
}
