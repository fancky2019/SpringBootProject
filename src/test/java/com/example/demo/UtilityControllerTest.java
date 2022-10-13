package com.example.demo;

import com.example.demo.model.pojo.BeanLife;
import com.example.demo.model.pojo.SpringLifeCycleBean;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
public class UtilityControllerTest {
    @Test
    public void beanLifeTest() {
        try {
            AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(BeanLife.class);
            System.out.println("容器初始化");
            context.close();

            AnnotationConfigApplicationContext context1 = new AnnotationConfigApplicationContext(SpringLifeCycleBean.class);
            System.out.println("容器初始化");
            context1.close();
        } catch (Exception ex) {
        }
    }
}
