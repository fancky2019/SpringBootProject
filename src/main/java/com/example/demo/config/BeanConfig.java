package com.example.demo.config;

import com.example.demo.model.pojo.Student;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BeanConfig {

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
