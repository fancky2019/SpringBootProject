package com.example.demo;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
//springboot 2.2之后改动
//使用junit-jupiter，不再使用@RunWith，也没有替代类，也就是需不要这个注解了
//@RunWith(SpringRunner.class) //springboot 2.1.1

@SpringBootTest
public class DemoApplicationTests {


    @Test
    public void contextLoads() {
    }

}
