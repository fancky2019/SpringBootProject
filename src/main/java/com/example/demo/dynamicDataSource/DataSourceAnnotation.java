package com.example.demo.dynamicDataSource;


import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface DataSourceAnnotation {
     String value() default DataSourceStrings.WRITER;
}
