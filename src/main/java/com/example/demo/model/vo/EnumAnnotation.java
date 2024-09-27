package com.example.demo.model.vo;

import java.lang.annotation.*;

@Documented
// 作用在字段上
@Target(ElementType.FIELD)
// 运行时有效
@Retention(RetentionPolicy.RUNTIME)
public @interface EnumAnnotation {
    /**
     *
     * @return
     */
    Class<?> enumClass();
}
