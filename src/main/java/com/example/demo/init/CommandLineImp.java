package com.example.demo.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


//容器初始化完成执行：ApplicationRunner-->CommandLineRunner-->ApplicationReadyEvent

//@Order控制配置类的加载顺序，通过@Order指定执行顺序，值越小，越先执行
@Component
@Order(1)
public class CommandLineImp implements org.springframework.boot.CommandLineRunner {
    @Value("${config.configmodel.fist-Name}")
    private String fistName;
    private static Logger LOGGER = LogManager.getLogger(CommandLineImp.class);

    @Autowired
//    @Resource
    ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        String name = fistName;
        LOGGER.info("CommandLineImp");
        int m = 0;
    }
}
