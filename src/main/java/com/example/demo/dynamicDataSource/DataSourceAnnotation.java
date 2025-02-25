package com.example.demo.dynamicDataSource;


import java.lang.annotation.*;

/**
 * 全局搜索使用
 * @DataSourceAnnotation(DataSourceStrings.WRITER)
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DataSourceAnnotation {
    String value() default DataSourceStrings.WRITER;
}
