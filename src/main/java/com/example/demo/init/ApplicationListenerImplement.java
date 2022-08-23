package com.example.demo.init;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//容器初始化完成执行：ApplicationRunner-->CommandLineRunner-->ApplicationReadyEvent

//@Order控制配置类的加载顺序，通过@Order指定执行顺序，值越小，越先执行
@Component
@Order(1)
public class ApplicationListenerImplement implements ApplicationListener<ApplicationReadyEvent> {

    private static Logger LOGGER = LogManager.getLogger(ApplicationListenerImplement.class);
    @Value("${config.configmodel.fist-Name}")
    private String fistName;
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        String name=fistName;
        LOGGER.info("ApplicationListenerImplement");
        int m=0;
    }
}
