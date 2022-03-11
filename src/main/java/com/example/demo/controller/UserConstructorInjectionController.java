package com.example.demo.controller;

import com.example.demo.service.interfaceAndImp.InterfaceTest;
import com.example.demo.service.interfaceAndImp.InterfaceTestImpA;
import com.example.demo.service.interfaceAndImp.InterfaceTestImpB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * spring 建议用构造器注入
 */
@RestController
@RequestMapping("/userConstructorInjection")
public class UserConstructorInjectionController {

    InterfaceTestImpA interfaceTestImpA;
//    InterfaceTestImpB interfaceTestImpB;

    InterfaceTest interfaceTestImpB;
    InterfaceTest interfaceTest;
    //获取配置文件的值
    @Value("${spring.datasource.username}")
    private String username;
    //
    @Value("${spring.application.name}")
    private String applicationName;

    private static Logger logger = LogManager.getLogger(UserController.class);

    /*
     *注意多个实现类时候加别名
     */
    @Autowired
    public UserConstructorInjectionController(InterfaceTestImpA interfaceTestImpA,
                                              InterfaceTestImpB interfaceTestImpB,
                                              @Qualifier("InterfaceTestImpA")InterfaceTest interfaceTest) {
        this.interfaceTestImpA = interfaceTestImpA;
        this.interfaceTestImpB = interfaceTestImpB;
        this.interfaceTest = interfaceTest;
    }

    @GetMapping("/autowiredTest")
    public String autowiredTest() {
        return this.interfaceTestImpA.fun() + ":" + interfaceTestImpB.fun()+":" + interfaceTest.fun();
    }
}
