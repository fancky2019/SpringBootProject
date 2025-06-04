package com.example.demo.config;

import com.example.demo.model.pojo.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Qualifier（指定 Bean 名称）的优先级高于 @Primary。
 *
 * 优先级顺序：
 * @Qualifier（明确指定 Bean 名称） → 最高优先级
 *
 * 如果使用 @Qualifier，Spring 会严格按名称匹配，忽略 @Primary。
 *
 * @Primary（标记为首选 Bean） → 次级优先级
 *
 * 如果没有 @Qualifier，但有多个同类型 Bean，Spring 会选择 @Primary 的 Bean。
 *
 * 变量名匹配（默认行为） → 最低优先级
 *
 * 如果既没有 @Qualifier 也没有 @Primary，Spring 会尝试按变量名匹配 Bean（若变量名与某个 Bean 名称一致）。
 */

@Configuration
public class BeanConfig {


    //    @Qualifier("upperObjectMapper")  // 明确指定名称
//    @Primary
    //@Bean 方法上使用 @Order 注解来控制 bean 的初始化顺序。
    @Bean(name = "studentF")
    public Student studentF() {

        Student student = new Student();
        student.setName("StudentF");
        return student;
    }

    @Bean("studentD")
    public Student studentD() {
        Student student = new Student();
        student.setName("studentD");
        return student;
    }


}
